package es.umu.soccerreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class IncidenciaActivity extends Activity {
    private static final int code1 = 1;
    private Partido partido;
    private boolean local;
    private boolean visitante;

    //Situacion la utilizo para diferenciar entre incidencias de árbitro=0, AA1=1, AA=2
    private int situacion = 0;
    private Incidencia inci;

    //Añadimos para el cambio fijo
    private EditText jugador1c;
    private EditText jugador2c;

    //Añadimos para la descripción de la tarjeta amarilla
    private EditText motivot;
    private EditText jugadort;

    //Para saber si es el asistente 1 o 2
    private boolean asistente = true;

    //Añadimos para el cronometro
    private Chronometer cronometro;
    private Button botonCrono;
    private Long memoCronometro;
    private String estado = "inactivo";

    //Para la información de la parte del partido
    private TextView infoParte;

    //Añadimos para establecer el marcador
    private TextView equipol;
    private TextView equipov;
    private TextView goll;
    private TextView golv;

    //Para saber si es primera mitad o segunda mitad
    //Además se cambiará el título del botón
    private int parte = 1; //Si esta a 1 es la primera parte, si está a 2 es la segunda parte
    private Button botonParte;

    //Añadimos para ver el informe
    private TextView listado;

    /**
     * Inicializa todos los campos de la activity
     */
    private void iniciarCampos() {
        //Añadimos para el cambio fijo
        jugador1c = findViewById(R.id.etsalec);
        jugador2c = findViewById(R.id.etentrac);

        //Añadimos para la tarjeta amarilla, tarjeta roja, gol
        motivot = findViewById(R.id.motivo);
        jugadort = findViewById(R.id.jugadort);

        //Añadimos para el cronometro
        infoParte = findViewById(R.id.infoParte);
        cronometro = findViewById(R.id.chronometer1);
        botonCrono = findViewById(R.id.button7);

        //Añadimos para visualizar Equipo Local 0 Equipo Visitante 0
        equipol = findViewById(R.id.tvlocal);
        equipol.setText(this.partido.getEquipoLocal());
        goll = findViewById(R.id.tvgoll);
        goll.setText(Integer.toString(partido.getGolesLocal()));
        equipov = findViewById(R.id.tvvisitante);
        equipov.setText(this.partido.getEquipoVisitante());
        golv = findViewById(R.id.tvgolv);
        golv.setText(Integer.toString(partido.getGolesVisitante()));

        //Añadimos para ver el informe
        listado = findViewById(R.id.textView6);

        this.local = true;
        this.visitante = false;
    }

    private void iniciarSpinner() {
        final Spinner spinner = findViewById(R.id.desplegable);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.valores_desplegable, R.layout.spiner_itemseleccionado);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0)
                    Toast.makeText(parent.getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                spinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_incidencia);

        Intent i = this.getIntent();
        this.partido = (Partido) i.getSerializableExtra("team");
    }

    @Override
    protected void onStart() {
        super.onStart();
        iniciarCampos();
        iniciarSpinner();
    }

    /**
     * Limpia todos los campos utilizados y poner el foco en el jugador
     */
    private void limpiarCampos() {
        motivot.setText("");
        jugador1c.setText("");
        jugador2c.setText("");
        jugadort.setText("");
        jugadort.requestFocus();
    }

    public void cambio(View view) {
        boolean flag = false;
        int number1 = 0;
        int number2 = 0;

        if (jugador1c.getText().toString().compareTo("") == 0 || jugador2c.getText().toString().compareTo("") == 0) {
            flag = true;
        } else {
            number1 = Integer.parseInt(jugador1c.getText().toString());
            number2 = Integer.parseInt(jugador2c.getText().toString());

            if (number1 <= 0 || number2 <= 0 || number1 == number2)
                flag = true;
        }

        String tiempo = cronometro.getText().toString();

        if (!flag) {
            if (this.local)
                this.inci = new Incidencia("Cambio", TipoEquipo.Local, tiempo, number1, number2, "", parte);
            else
                this.inci = new Incidencia("Cambio", TipoEquipo.Visitante, tiempo, number1, number2, "", parte);

            this.partido.addIncidencia(inci);
            Toast.makeText(this, "Añadido evento Cambio", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(this, "Revise los campos", Toast.LENGTH_SHORT).show();
            jugador2c.requestFocus();
        }

    }

    private boolean comprobar_dorsal_jugador(String dorsal) {
        if(jugadort.getText().toString().equals(""))
            return false;
        int min = Integer.parseInt(dorsal);
        return (min >= 0 && min <= 99);
    }

    //Pasa por parámetro el motivo de la incidencia: "Tarjeta Amarilla","Tarjeta Roja","Gol"
    public boolean add_incidencia(String texto, String tiempo) {
        boolean flag = false;
        int min = -1;
        int dorsal;
        int number2 = 0;

        //Comprueba el jugador
        if (comprobar_dorsal_jugador(jugadort.getText().toString())) {
            dorsal = Integer.parseInt(jugadort.getText().toString());

        } else
            flag = true;

        if (!flag) {
            add_incidencia_simple(texto, tiempo);
            return true;

        } else {
            Toast.makeText(this, "Revise el número de jugador.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Pasa por parámetro el motivo de la incidencia: "Falta"
    public void add_incidencia_simple(String texto, String tiempo) {
        if (this.local) {
            this.inci = new Incidencia(texto, TipoEquipo.Local, tiempo, 0, 0, motivot.getText().toString(), parte);
        } else {
            this.inci = new Incidencia(texto, TipoEquipo.Visitante, tiempo, 0, 0, motivot.getText().toString(), parte);
        }

        this.partido.addIncidencia(inci);
        //Notificar que se ha añadido
        Toast.makeText(this, "Añadido evento " + texto, Toast.LENGTH_SHORT).show();
        limpiarCampos();
    }

    //Pasa por parámetro el motivo de la incidencia: "Falta"
    public void add_incidencia_arbitros(String texto, String tiempo) {
        switch (situacion) {
            case 0:
                this.inci = new Incidencia(texto, TipoEquipo.arbitro, tiempo, 0, 0, motivot.getText().toString(), parte);
                break;
            case 1:
                this.inci = new Incidencia(texto, TipoEquipo.AA1, tiempo, 0, 0, motivot.getText().toString(), parte);
                break;
            case 2:
                this.inci = new Incidencia(texto, TipoEquipo.AA2, tiempo, 0, 0, motivot.getText().toString(), parte);
                break;
            default:
                return;
        }


        this.partido.addIncidencia(inci);
        Toast.makeText(this, "Añadido evento " + texto, Toast.LENGTH_SHORT).show();
        limpiarCampos();
    }


    //-------------------------------------------------------------------------\\
    /*  Eventos sobre los equipos
    *   El usuario previamnte indica si es Local o Visitante  */

    public void golLocal(View view) {
        local = true;
        visitante = false;
        gol();
    }

    public void golVisitante(View view) {
        local = false;
        visitante = true;
        gol();
    }

    public void faltaLocal(View view) {
        local = true;
        visitante = false;
        falta();
    }

    public void faltaVisitante(View view) {
        local = false;
        visitante = true;
        falta();
    }

    public void tarjetaAmarillaLocal(View view) {
        local = true;
        visitante = false;
        tarjetaAmarilla();
    }

    public void tarjetaAmarillaVisitante(View view) {
        local = false;
        visitante = true;
        tarjetaAmarilla();
    }

    public void tarjetaRojaLocal(View view) {
        local = true;
        visitante = false;
        tarjetaRoja();
    }

    public void tarjetaRojaVisitante(View view) {
        local = false;
        visitante = false;
        tarjetaRoja();
    }

    public void jugadaAreaLocal(View view) {
        local = true;
        visitante = false;
        jugadaArea();
    }

    public void jugadaAreaVisitante(View view) {
        local = false;
        visitante = true;
        jugadaArea();
    }

    public void penaltiLocal(View view) {
        local = true;
        visitante = false;
        penalti();
    }

    public void penaltiVisitante(View view) {
        local = false;
        visitante = true;
        penalti();
    }

    private void gol(){
        String cadena = "Gol";
        if (add_incidencia(cadena, cronometro.getText().toString())) {
            //Actualizar el resultado
            partido.sumarGol(this.local);
            if (local)
                goll.setText(Integer.toString(partido.getGolesLocal()));
            else
                golv.setText(Integer.toString(partido.getGolesVisitante()));
        }
    }

    private void falta() {
        String cadena = "Falta";
        add_incidencia_simple(cadena, cronometro.getText().toString());
        partido.sumarFalta(true);
    }

    private void tarjetaAmarilla() {
        String cadena = "Tarjeta Amarilla";
        add_incidencia(cadena, cronometro.getText().toString());

    }

    private void tarjetaRoja() {
        String cadena = "Tarjeta Roja";
        add_incidencia(cadena, cronometro.getText().toString());

    }

    private void jugadaArea() {
        String cadena = "Jugada de area";
        add_incidencia(cadena, cronometro.getText().toString());
    }

    private void penalti() {
        String cadena = "Penalti";
        add_incidencia(cadena, cronometro.getText().toString());
    }

    //-------------------------------------------------------------------------\\

    public void ventaja(View view) {
        String cadena = "Ventaja";

        situacion = 0; //Se pone como incidencia del árbitro
        add_incidencia_arbitros(cadena, cronometro.getText().toString());
    }

    public void aceleracion(View view) {
        String cadena = "Aceleración";

        situacion = 0; //Se pone como incidencia del árbitro
        add_incidencia_arbitros(cadena, cronometro.getText().toString());
    }

    public void posicionamiento(View view) {
        String cadena = "Posicionamiento";

        situacion = 0; //Se pone como incidencia del árbitro
        add_incidencia_arbitros(cadena, cronometro.getText().toString());
    }

    public void otra(View view) {
        String cadena = "Otra";

        situacion = 0; //Se pone como incidencia del árbitro
        add_incidencia_arbitros(cadena, cronometro.getText().toString());
    }


    public void faltaaa1(View view) {
        //Hacer la de falta
        String cadena = "Falta";

        situacion = 1; //Se pone como incidencia del AA1
        add_incidencia_arbitros(cadena, cronometro.getText().toString());
    }

    public void noaa1(View view) {
        String cadena = "No";

        situacion = 1; //Se pone como incidencia del AA1
        add_incidencia_arbitros(cadena, cronometro.getText().toString());
    }

    public void fjaa1(View view) {
        String cadena = "FJ";

        situacion = 1; //Se pone como incidencia del AA1
        add_incidencia_arbitros(cadena, cronometro.getText().toString());
    }

    public void faltaaa2(View view) {
        //Hacer la de falta
        String cadena = "Falta";

        situacion = 2; //Se pone como incidencia del AA2
        add_incidencia_arbitros(cadena, cronometro.getText().toString());
    }

    public void noaa2(View view) {
        String cadena = "No";

        situacion = 2; //Se pone como incidencia del AA2
        add_incidencia_arbitros(cadena, cronometro.getText().toString());
    }

    public void fjaa2(View view) {
        String cadena = "FJ";

        situacion = 2; //Se pone como incidencia del AA1
        add_incidencia_arbitros(cadena, cronometro.getText().toString());
    }

    public void clickcrono(View view) {
        switch (estado) {
            case "inactivo":
                cronometro.setBase(SystemClock.elapsedRealtime());
                cronometro.start();
                estado = "primeraParte";
                infoParte.setText("1ª parte");
                botonCrono.setText("Fin 1ª parte");
                parte = 1;
                break;
            case "primeraParte":
                cronometro.stop();
                memoCronometro = SystemClock.elapsedRealtime() - cronometro.getBase();
                estado = "descanso";
                infoParte.setText("Descanso");
                botonCrono.setText("Continuar");
                break;
            case "descanso":
                cronometro.setBase(SystemClock.elapsedRealtime() - memoCronometro);
                cronometro.start();
                estado = "segundaParte";
                infoParte.setText("2ª Parte");
                botonCrono.setText("Fin partido");
                parte = 2;
                break;
            case "segundaParte":
                infoParte.setText("Finalizado");
                botonCrono.setVisibility(View.INVISIBLE);
                cronometro.stop();
                estado = "finPartido";
        }
    }

    public void salir(View view) {
        setResult(Activity.RESULT_OK, null);
        finish();
    }

    /*
    public void generarPdf(View view) {


        String nombre = "" + this.partido.getEquipoLocal() + "-" + this.partido.getEquipoVisitante();
        try {
            FileOutputStream file = new FileOutputStream("/sdcard/" + nombre + Integer.toString(cont1) + ".pdf");
            cont1++;
            Document document = new Document();

            PdfWriter.getInstance(document, file);
            document.open();
            document.add(new Paragraph("INFORME \n\n", FontFactory.getFont("Calibri", 22, Font.BOLD, BaseColor.BLACK)));
            String cadena = informe();
            document.add(new Paragraph(cadena));
            document.close();
            //Asigno el texto del pdf a listado
            listado.setText(cadena);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    */

    /**
     * Generará un PDF con la información actual del partido y la guardará en
     * /storage/emulated/0/SoccerReport
     * Como nombre del PDF tendrá la fecha y los equipos enfrentados.
     * @param view
     */
    public void guardarPdfPartido(View view) {
        //Solicitar permiso para el almacenamiento
        int requestCode = 0;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);

        //Crear el directorio en caso de que no exista
        File file = new File(Environment.getExternalStorageDirectory(), "SoccerReport");
        if(!file.exists())
            if (!file.mkdir())
                Toast.makeText(this, "Fallo al crear directorio\nVuelva a probar", Toast.LENGTH_SHORT).show();

        //Crear fichero para guardarlo en la carpeta


    }

    /**
     * Lanza un nuevo activity con el estado actual del partido
     * @param view
     */
    public void verEstadoPartido(View view){
        Intent intent = new Intent(getApplicationContext(), EstadoActualActivity.class);
        intent.putExtra("info", informe());
        startActivity(intent);
    }

    /*
    public void txt(View view) {

        String nombre = "" + this.partido.getEquipoLocal() + "-" + this.partido.getEquipoVisitante();
        try {
            FileOutputStream file = new FileOutputStream("/sdcard/" + nombre + "o" + Integer.toString(cont2) + ".pdf");
            cont2++;
            Document document = new Document();
            PdfWriter.getInstance(document, file);
            document.open();
            document.add(new Paragraph("INFORME" + nombre + "\n\n"));
            String cadena = informe_ordenado();
            document.add(new Paragraph(cadena));
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    public String informe() {
        String cadena = "";
        String golesl = "";
        String golesv = "";
        String tarjetal = "";
        String tarjetav = "";
        String rojal = "";
        String rojav = "";
        String cambiol = "";
        String cambiov = "";
        String faltasl = "";
        String faltasv = "";
        String ventaja = "";
        String aceleracion = "";
        String pos = "";
        String otra = "";
        String aa1f = "";
        String aa2f = "";
        String aa1no = "";
        String aa2no = "";
        String aa1fj = "";
        String aa2fj = "";
        int minutosA = 0;
        String segundosA = "";
        String mt;

        cadena = "  Equipo Local: " + partido.getEquipoLocal() + "\n  Equipo Visitante: " + partido.getEquipoVisitante() +
                "\n  Fecha: " + partido.getDia() + "-" + partido.getMes() + "-" + partido.getAnyo() + "\n  Hora: " + partido.getHoras() +
                ":" + partido.getMinutos() + "\n\n";

        cadena += "______________________________________________________________________________\n";
        cadena += "RESULTADO: " + goll.getText().toString() + " - " + golv.getText().toString() + "\n\n";

        //Inicializo el número total de faltas
        faltasl = Integer.toString(partido.getFaltasLocales());
        faltasv = Integer.toString(partido.getFaltasVisitantes());

        //Recorro la lista completa y voy analizando
        LinkedList<Incidencia> l1 = (LinkedList<Incidencia>) this.partido.getLista();
        for (Incidencia incidencia : l1) {
            mt = "";
            //Si es una incidencia de la segunda parte, le sumamos 45
            if (incidencia.getParte() == 2) {
                //Si la longitud es igual a 4: tipo 0:11, 2:14
                if (incidencia.getMinuto().length() == 4) {
                    minutosA = Integer.valueOf(incidencia.getMinuto().substring(0, 1)) + 45;
                    segundosA = incidencia.getMinuto().substring(2, 4);

                } else { //Longitud debe ser igual a 5: tipo 10:22, 45:11
                    minutosA = Integer.valueOf(incidencia.getMinuto().substring(0, 2)) + 45;
                    segundosA = incidencia.getMinuto().substring(3, 5);

                }
                mt = Integer.toString(minutosA) + ":" + segundosA;
            } else {
                mt = incidencia.getMinuto();
            }
            if (incidencia.getNombre().compareTo("Gol") == 0) {
                if (incidencia.getTipo() == TipoEquipo.Local) {
                    golesl += "Nº " + incidencia.getJugador1() + " Min: " + mt + " " + incidencia.getDescripcion() + " ";
                } else {
                    golesv += "Nº " + incidencia.getJugador1() + " Min: " + mt + " " + incidencia.getDescripcion() + " ";
                }
            }
            if (incidencia.getNombre().compareTo("Falta") == 0) {
                //Falta marcada por AA1
                if (incidencia.getTipo() == TipoEquipo.AA1) {
                    aa1f += " " + mt; //incidencia.getMinuto();
                }
                //Falta marcada por AA2
                if (incidencia.getTipo() == TipoEquipo.AA2) {
                    aa2f += " " + mt; //incidencia.getMinuto();
                }
                //Falta normal para un equipo u otro
                if (incidencia.getTipo() == TipoEquipo.Local) {
                    faltasl += mt + " ";//incidencia.getMinuto()+" ";
                }
                if (incidencia.getTipo() == TipoEquipo.Visitante) {
                    faltasv += mt + " ";//incidencia.getMinuto()+" ";
                }
            }
            //cadena+="- "+incidencia.getNombre()+"\n";
            if (incidencia.getNombre().compareTo("Tarjeta Amarilla") == 0) {
                if (incidencia.getTipo() == TipoEquipo.Local) {
                    tarjetal += "Nº " + incidencia.getJugador1() + " Min: " + mt + " " + incidencia.getDescripcion() + "\n";
                } else {
                    tarjetav += "Nº " + incidencia.getJugador1() + " Min: " + mt + " " + incidencia.getDescripcion() + "\n";
                }
            }
            if (incidencia.getNombre().compareTo("Tarjeta Roja") == 0) {
                if (incidencia.getTipo() == TipoEquipo.Local) {
                    rojal += incidencia.getJugador1() + " " + mt + " " + incidencia.getDescripcion() + "\n";
                } else {
                    rojav += incidencia.getJugador1() + " " + mt + " " + incidencia.getDescripcion() + "\n";
                }
            }

            if (incidencia.getNombre().compareTo("Ventaja") == 0) {
                ventaja += " " + mt;//incidencia.getMinuto();
            }
            if (incidencia.getNombre().compareTo("Aceleración") == 0) {
                aceleracion += " " + mt;//incidencia.getMinuto();
            }
            if (incidencia.getNombre().compareTo("Posicionamiento") == 0) {
                pos += " " + mt + " " + incidencia.getDescripcion();//incidencia.getMinuto();
            }
            if (incidencia.getNombre().compareTo("Otra") == 0) {
                otra += " " + mt + " " + incidencia.getDescripcion() + "\n";
            }
            if (incidencia.getNombre().compareTo("Cambio") == 0) {
                if (incidencia.getTipo() == TipoEquipo.Local) {
                    cambiol += "Nº " + incidencia.getJugador2() + " X " + "Nº " + incidencia.getJugador1() + " Min:" + mt + "\n";
                } else {
                    cambiov += "Nº " + incidencia.getJugador2() + " X " + "Nº " + incidencia.getJugador1() + " Min:" + mt + "\n";
                }
            }
            if (incidencia.getNombre().compareTo("No") == 0) {
                if (incidencia.getTipo() == TipoEquipo.AA1) {
                    aa1no += " " + mt + " " + incidencia.getDescripcion();//incidencia.getMinuto();
                } else {
                    aa2no += " " + mt + " " + incidencia.getDescripcion();//incidencia.getMinuto();
                }
            }
            if (incidencia.getNombre().compareTo("FJ") == 0) {
                if (incidencia.getTipo() == TipoEquipo.AA1) {
                    aa1fj += " " + mt + " " + incidencia.getDescripcion();//incidencia.getMinuto();
                } else {
                    aa2fj += " " + mt + " " + incidencia.getDescripcion();//incidencia.getMinuto();
                }
            }
        }
        cadena += "Goles local: " + golesl + "\nGoles visitante: " + golesv + "\n\nTA local: " + tarjetal + "\nTR local: " + rojal + "\nTA visitante: " + tarjetav + "\nTR visitante: " + rojav +
                "\n\nCambios local: " +
                cambiol + "Cambios visitante: " + cambiov + "\n\nFaltas local: " + faltasl + "\nFaltas visitante: " + faltasv + "\nVentaja: " + ventaja + "\nAceleración: " + aceleracion +
                "\nPosicionamiento: " + pos + "\n\nIncidencias: " + otra + "\n\nAA1: NO: " + aa1no + "\nFJ: " + aa1fj + "\nFALTAS: " + aa1f + "\n\nAA2: NO: " + aa2no + "\nFJ: " + aa2fj + "\nFALTAS: " + aa2f;
        cadena += "\n\n";
        cadena += "______________________________________________________________________________\n";

        cadena += "FINAL DEL PARTIDO";
        return cadena;
    }

    public String informe_ordenado() {

        String cadena = "";

        //Recorro la lista completa y voy analizando
        LinkedList<Incidencia> l1 = (LinkedList<Incidencia>) this.partido.getLista();
        for (Incidencia incidencia : l1) {

            cadena += incidencia.getMinuto() + " ";
            if ((incidencia.getNombre().compareTo("Gol") == 0) || (incidencia.getNombre().compareTo("Tarjeta Amarilla") == 0) || (incidencia.getNombre().compareTo("Tarjeta Roja") == 0)) {
                cadena += incidencia.getNombre() + " " + incidencia.getTipo() + " Nº " + incidencia.getJugador1() + " " + incidencia.getDescripcion();
            }
            if ((incidencia.getNombre().compareTo("Ventaja") == 0) || (incidencia.getNombre().compareTo("Aceleración") == 0)) {
                cadena += incidencia.getNombre();
            }

            if ((incidencia.getNombre().compareTo("Posicionamiento") == 0) || (incidencia.getNombre().compareTo("Otra") == 0)) {
                cadena += incidencia.getNombre() + " " + incidencia.getDescripcion();
            }

            if (incidencia.getNombre().compareTo("Cambio") == 0) {
                cadena += incidencia.getNombre() + " " + incidencia.getTipo() + " Nº " + incidencia.getJugador2() + " X " + "Nº " + incidencia.getJugador1();
            }

            if (incidencia.getNombre().compareTo("No") == 0) {
                cadena += "Deja continuar ajustado" + " " + incidencia.getTipo() + " " + incidencia.getDescripcion();
            }
            if (incidencia.getNombre().compareTo("FJ") == 0) {
                cadena += incidencia.getNombre() + " " + incidencia.getTipo() + " " + incidencia.getDescripcion();
            }

            if (incidencia.getNombre().compareTo("Falta") == 0) {
                cadena += incidencia.getNombre() + " " + incidencia.getTipo();
            }

            cadena += "\n";
        }

        cadena += "FINAL DEL PARTIDO";
        return cadena;
    }


    //Métodos para el cambio fijo que activa o desactiva la variable local2 a true si es equipo local o a false si es el equipo visitante
    public void clickLocalc(View view) {

        this.local = true;
        this.visitante = false;

    }

    public void clickVisitantec(View view) {

        this.local = false;
        this.visitante = true;
    }

    public void undo(View view) {
        Log.e("msg", "En undo");
        this.partido.quitarUltimoEvento();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code1) {
            if (resultCode == RESULT_OK) {
                setResult(Activity.RESULT_OK, null);
                finish();
            }
        }
    }
}
