package es.umu.soccerreport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.room.Room;

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

    //Para mostrar el estado del partido
    private TextView contenidoScrol;

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
        //Para mostrar el informe en la tablet
        contenidoScrol = findViewById(R.id.contenido);
        actualizarInformeScroll();

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


    private void actualizarInformeScroll() {
        contenidoScrol.setText(informe());
        AppDatabase.getInstance(getApplicationContext()).partidoDao().insertPartido(partido);
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
                if(position != 0) {
                    add_incidencia_spinner(parent.getItemAtPosition(position).toString());
                    spinner.setSelection(0);
                }
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
        AppDatabase.getInstance(getApplicationContext()).partidoDao().insertPartido(partido);
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
                this.inci = new Incidencia("Cambio", TipoEquipo.Local, tiempo, number1, number2, motivot.getText().toString(), parte);
            else
                this.inci = new Incidencia("Cambio", TipoEquipo.Visitante, tiempo, number1, number2, motivot.getText().toString(), parte);

            this.partido.addIncidencia(inci);
            Toast.makeText(this, "Añadido evento Cambio", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            actualizarInformeScroll();
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
        int dorsal = 0;
        int number2 = 0;

        //Comprueba el jugador
        if (comprobar_dorsal_jugador(jugadort.getText().toString())) {
            dorsal = Integer.parseInt(jugadort.getText().toString());

        } else
            flag = true;

        if (!flag) {
            add_incidencia_simple(texto, tiempo, dorsal);
            return true;

        } else {
            Toast.makeText(this, "Revise el número de jugador.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //Pasa por parámetro el motivo de la incidencia: "Falta"
    public void add_incidencia_simple(String texto, String tiempo, int dorsal) {
        if (this.local) {
            this.inci = new Incidencia(texto, TipoEquipo.Local, tiempo, dorsal, 0, motivot.getText().toString(), parte);
        } else {
            this.inci = new Incidencia(texto, TipoEquipo.Visitante, tiempo, dorsal, 0, motivot.getText().toString(), parte);
        }

        this.partido.addIncidencia(inci);
        //Notificar que se ha añadido
        Toast.makeText(this, "Añadido evento " + texto, Toast.LENGTH_SHORT).show();
        limpiarCampos();
        actualizarInformeScroll();
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
        actualizarInformeScroll();
    }

    private void add_incidencia_spinner(String cadena) {
        this.inci = new Incidencia(cadena, TipoEquipo.INCSPINNER, cronometro.getText().toString(), 0, 0, motivot.getText().toString(), parte);
        this.partido.addIncidencia(inci);
        Toast.makeText(this, "Añadido evento " + cadena, Toast.LENGTH_SHORT).show();
        limpiarCampos();
        actualizarInformeScroll();

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

        actualizarInformeScroll();
    }

    private void falta() {
        String cadena = "Falta";
        add_incidencia(cadena, cronometro.getText().toString());
        partido.sumarFalta(true);
        actualizarInformeScroll();
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


    /**
     * Generará un PDF con la información actual del partido y la guardará en
     * /storage/emulated/0/SoccerReport
     * Como nombre del PDF tendrá la fecha y los equipos enfrentados.
     * @param view
     */
    public void guardarPdfPartido(View view) {
        //Solicitar permiso para el almacenamiento
        if(partido.hayIncidencias()) {
            int requestCode = 0;
            boolean error = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);

            //Crear el directorio en caso de que no exista
            File file = new File(Environment.getExternalStorageDirectory(), "SoccerReport");
            String fecha = partido.getAnyo() + "-" + partido.getMes() + "-" + partido.getDia();
            File dir = new File(file.getPath(), fecha);
            if (!dir.exists())
                if (!dir.mkdirs()) {
                    Toast.makeText(this, "Fallo al crear directorio\nVuelva a probar", Toast.LENGTH_SHORT).show();
                    error = true;
                }

            if (!error) {
                //Crear fichero para guardarlo en la carpeta
                String nombre_informe = "/informe_" + partido.getEquipoLocal() + "-" + partido.getEquipoVisitante() + "-" + partido.getHoras() + ":" + partido.getHoras() + ".pdf";
                String nombre_informe_ordenado = "/informe_ordenado_" + partido.getEquipoLocal() + "-" + partido.getEquipoVisitante() + "-" + partido.getHoras() + ":" + partido.getHoras() + ".pdf";


                try {
                    //Para el informe normal
                    FileOutputStream output_informe = new FileOutputStream(dir.getAbsolutePath() + nombre_informe);
                    Document documento_informe = new Document();
                    PdfWriter.getInstance(documento_informe, output_informe);
                    documento_informe.open();
                    documento_informe.add(new Paragraph("INFORME \n\n", FontFactory.getFont("Calibri", 22, Font.BOLD, BaseColor.BLACK)));
                    String contenido = informe();
                    documento_informe.add(new Paragraph(contenido));
                    documento_informe.close();

                    //Para el informe ordenado
                    FileOutputStream output_informe_ordenado = new FileOutputStream(dir.getAbsolutePath() + nombre_informe_ordenado);
                    Document documento_informe_ordenado = new Document();
                    PdfWriter.getInstance(documento_informe_ordenado, output_informe_ordenado);
                    documento_informe_ordenado.open();
                    documento_informe_ordenado.add(new Paragraph("INFORME ORDENADO \n\n", FontFactory.getFont("Calibri", 22, Font.BOLD, BaseColor.BLACK)));
                    String contenido_ordenado = informe_ordenado();
                    documento_informe_ordenado.add(new Paragraph(contenido_ordenado));
                    documento_informe_ordenado.close();


                    Toast.makeText(this, "Generados informes\n"+dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(this, "No se ha podido generar el informe\nVuelva a probar", Toast.LENGTH_SHORT).show();
                }
            }

        } else
            Toast.makeText(this, "Añada elementos al partido", Toast.LENGTH_SHORT).show();
    }

    /**
     * Exporta el contenido indicado a CSV
     * @param view
     */
    public void exportarCSV(View view) {
        if(partido.hayIncidencias()) {

            //Crear el diálogo para seleccionar los eventos
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final ArrayList<Integer> seleccionados = new ArrayList();
            builder.setTitle("Seleccione los eventos:")

                    .setMultiChoiceItems(R.array.valores_dialogo, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                seleccionados.add(which);
                            } else if (seleccionados.contains(which)) {
                                // Else, if the item is already in the array, remove it
                                seleccionados.remove(Integer.valueOf(which));
                            }

                        }
                    });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    List<String> cont = Arrays.asList(getResources().getStringArray(R.array.valores_dialogo));
                    ArrayList<String> filtro = new ArrayList<>();
                    for(int i=0;i<seleccionados.size();i++) {
                        if (cont.get(seleccionados.get(i)).compareTo("Lista desplegable") == 0)
                            filtro.add("Lista desplegable");
                        else if (cont.get(seleccionados.get(i)).compareTo("Goles") == 0)
                            filtro.add("Gol");
                        else if (cont.get(seleccionados.get(i)).compareTo("Faltas") == 0)
                            filtro.add("Falta");
                        else if (cont.get(seleccionados.get(i)).compareTo("Tarjetas Amarillas") == 0)
                            filtro.add("Tarjeta Amarilla");
                        else if (cont.get(seleccionados.get(i)).compareTo("Tarjetas Rojas") == 0)
                            filtro.add("Tarketa Roja");
                        else if (cont.get(seleccionados.get(i)).compareTo("Jugada de área") == 0)
                            filtro.add("Jugada de area");
                        else if (cont.get(seleccionados.get(i)).compareTo("Penalti") == 0)
                            filtro.add("Penalti");
                        else if (cont.get(seleccionados.get(i)).compareTo("Ventaja") == 0)
                            filtro.add("Ventaja");
                        else if (cont.get(seleccionados.get(i)).compareTo("Posición") == 0)
                            filtro.add("Posicionamiento");
                        else if (cont.get(seleccionados.get(i)).compareTo("Aceleración") == 0)
                            filtro.add("Aceleración");
                        else if (cont.get(seleccionados.get(i)).compareTo("Otra") == 0)
                            filtro.add("Otra");
                        else if (cont.get(seleccionados.get(i)).compareTo("Goles") == 0)
                            filtro.add("Gol");
                        else if (cont.get(seleccionados.get(i)).compareTo("Fuera de juego") == 0)
                            filtro.add("FJ");
                        else if (cont.get(seleccionados.get(i)).compareTo("Dejar continuar") == 0)
                            filtro.add("No");
                        else if (cont.get(seleccionados.get(i)).compareTo("Cambios") == 0)
                            filtro.add("Cambio");
                    }

                    generarCSV(filtro);

                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            builder.create().show();

        }
        else
            Toast.makeText(this, "Añada elementos al partido", Toast.LENGTH_SHORT).show();
    }


    private void generarCSV(ArrayList<String> filtro){
        int requestCode = 0;
        boolean error = false;
        //Solicitar permiso para el almacenamiento
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);

        //Crear el directorio en caso de que no exista
        File file = new File(Environment.getExternalStorageDirectory(), "SoccerReport");
        String fecha = partido.getAnyo() + "-" + partido.getMes() + "-" + partido.getDia();
        File dir = new File(file.getPath(), fecha);
        if (!dir.exists())
            if (!dir.mkdirs()) {
                Toast.makeText(this, "Fallo al crear directorio\nVuelva a probar", Toast.LENGTH_SHORT).show();
                error = true;
            }

        if (!error) {
            ArrayList<Incidencia> incidenciasFiltradas = partido.getIncidenciasFiltradas(filtro);
            String nombre_csv = "/csv_" + partido.getEquipoLocal() + "-" + partido.getEquipoVisitante() + "-" + partido.getHoras() + ":" + partido.getHoras() + ".csv";

            try{
                FileOutputStream output_csv = new FileOutputStream(dir.getAbsolutePath() + nombre_csv);
                String data_csv = "Minuto, Evento, Descripción\n";
                for(int i=0; i<incidenciasFiltradas.size();i++){
                    data_csv += incidenciasFiltradas.get(i).getMinuto() + "," + incidenciasFiltradas.get(i).getNombre() + "," + incidenciasFiltradas.get(i).getDescripcion() + "\n" ;
                }
                output_csv.write(data_csv.getBytes());
                output_csv.close();
                Toast.makeText(this, "Generado CSV\n"+dir.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            } catch (Exception e){
                Toast.makeText(this, "No se ha podido generar el csv\nVuelva a probar", Toast.LENGTH_SHORT).show();
            }

        } else
            Toast.makeText(this, "No se ha podido generar el directorio\nVuelva a probar", Toast.LENGTH_SHORT).show();



    }

    /**
     * Lanza un nuevo activity con el estado actual del partido
     * @param view
     */
    public void verEstadoPartido(View view){
        if (partido.hayIncidencias()) {
            Intent intent = new Intent(getApplicationContext(), EstadoActualActivity.class);
            intent.putExtra("info", informe());
            startActivity(intent);
        }else
            Toast.makeText(this, "Añada elementos al partido", Toast.LENGTH_SHORT).show();
    }

    public String informe() {
        String cadena = "";

        cadena = "  Equipo Local: " + partido.getEquipoLocal() +
                "\n  Equipo Visitante: " + partido.getEquipoVisitante() +
                "\n  Fecha: " + partido.getDia() + "-" + partido.getMes() + "-" + partido.getAnyo() +
                "\n  Hora: " + partido.getHoras() + ":" + partido.getMinutos() + "\n\n";

        cadena += "______________________________________________________________________________\n\n";
        cadena += "RESULTADO: " + partido.getGolesLocal() + " - " + partido.getGolesVisitante() + "\n\n";


        //Tratar los goles
        ArrayList<Incidencia> igoles = partido.getIncidenciasGoles();
        ArrayList<Incidencia> igolesLocal = partido.filtrarLocal(igoles);
        ArrayList<Incidencia> igolesVisitante = partido.filtrarVisitante(igoles);

        cadena += "Goles local:\n";
        for (int i=0; i<igolesLocal.size(); i++)
            cadena += "\t Nº " + igolesLocal.get(i).getJugador1() + " Min: " + igolesLocal.get(i).getMinuto() +
                 "  " + igolesLocal.get(i).getDescripcion() +"\n";



        cadena += "\nGoles visitante:\n";
        for (int i=0; i<igolesVisitante.size(); i++)
            cadena += "\t Nº " + igolesVisitante.get(i).getJugador1() + " Min: " +  igolesVisitante.get(i).getMinuto() +
                    "  " + igolesVisitante.get(i).getDescripcion() + "\n";

        //Tratar las tarjetas amarillas
        ArrayList<Incidencia> iTA = partido.getIncidenciasTA();
        ArrayList<Incidencia> iTALocal = partido.filtrarLocal(iTA);
        ArrayList<Incidencia> iTAVisitante = partido.filtrarVisitante(iTA);

        cadena += "\nTarjetas amarillas local:\n";
        for (int i=0; i<iTALocal.size(); i++)
            cadena += "\t Nº " + iTALocal.get(i).getJugador1() + " Min: " +  iTALocal.get(i).getMinuto() +
                    "  " + iTALocal.get(i).getDescripcion() + "\n";


        cadena += "\nTarjetas amarillas visitante:\n";
        for (int i=0; i<iTAVisitante.size(); i++)
            cadena += "\t Nº " + iTAVisitante.get(i).getJugador1() + " Min: " +  iTAVisitante.get(i).getMinuto() +
                    "  " + iTAVisitante.get(i).getDescripcion() + "\n";

        //Tratar las tarjetas rojas
        ArrayList<Incidencia> iTR = partido.getIncidenciasTR();
        ArrayList<Incidencia> iTRLocal = partido.filtrarLocal(iTR);
        ArrayList<Incidencia> iTRVisitante = partido.filtrarVisitante(iTR);

        cadena += "\nTarjetas rojas local:\n";
        for (int i=0; i<iTRLocal.size(); i++)
            cadena += "\t Nº " + iTRLocal.get(i).getJugador1() + " Min: " +  iTRLocal.get(i).getMinuto() +
                    "  " + iTRLocal.get(i).getDescripcion() + "\n";


        cadena += "\nTarjetas rojas visitante:\n";
        for (int i=0; i<iTRVisitante.size(); i++)
            cadena += "\t Nº " + iTRVisitante.get(i).getJugador1() + " Min: " +  iTRVisitante.get(i).getMinuto() +
                    "  " + iTRVisitante.get(i).getDescripcion() + "\n";

        //Tratar Jugadas de área
        ArrayList<Incidencia> iJugadaA = partido.getIncidenciasJugadaArea();
        ArrayList<Incidencia> iJALocal = partido.filtrarLocal(iJugadaA);
        ArrayList<Incidencia> iJAVisitante = partido.filtrarVisitante(iJugadaA);

        cadena += "\nJugadas de área local:\n";
        for (int i=0; i<iJALocal.size(); i++)
            cadena += "\t Nº " + iJALocal.get(i).getJugador1() + " Min: " +  iJALocal.get(i).getMinuto() +
                    "  " + iJALocal.get(i).getDescripcion() + "\n";

        cadena += "\nJugadas de área visitante:\n";
        for (int i=0; i<iJAVisitante.size(); i++)
            cadena += "\t Nº " + iJAVisitante.get(i).getJugador1() + " Min: " +  iJAVisitante.get(i).getMinuto() +
                    "  " + iJAVisitante.get(i).getDescripcion() + "\n";

        //Tratar penaltiles
        ArrayList<Incidencia> iPenalti = partido.getIncidenciasPenalti();
        ArrayList<Incidencia> iPenaltiLocal = partido.filtrarLocal(iPenalti);
        ArrayList<Incidencia> iPenaltiVisitante = partido.filtrarVisitante(iPenalti);

        cadena += "\nPenalti local:\n";
        for (int i=0; i<iPenaltiLocal.size(); i++)
            cadena += "\t Nº " + iPenaltiLocal.get(i).getJugador1() + " Min: " +  iPenaltiLocal.get(i).getMinuto() +
                    "  " + iPenaltiLocal.get(i).getDescripcion() + "\n";

        cadena += "\nPenalti visitante:\n";
        for (int i=0; i<iPenaltiVisitante.size(); i++)
            cadena += "\t Nº " + iPenaltiVisitante.get(i).getJugador1() + " Min: " +  iPenaltiVisitante.get(i).getMinuto() +
                    "  " + iPenaltiVisitante.get(i).getDescripcion() + "\n";

        //Tratar los cambios
        ArrayList<Incidencia> iCambios = partido.getIncidenciasCambios();
        ArrayList<Incidencia> iCambiosLocal = partido.filtrarLocal(iCambios);
        ArrayList<Incidencia> iCambiosVisitante = partido.filtrarVisitante(iCambios);

        cadena += "\nCambios local:\n";
        for (int i=0; i<iCambiosLocal.size(); i++)
            cadena += "\t Nº " + iCambiosLocal.get(i).getJugador2()+ " x " + iCambiosLocal.get(i).getJugador1() +
                    " Min: " +  iCambiosLocal.get(i).getMinuto() + "  " + iCambiosLocal.get(i).getDescripcion() + "\n";


        cadena += "\nCambios visitante:\n";
        for (int i=0; i<iCambiosVisitante.size(); i++)
            cadena += "\t Nº " + iCambiosVisitante.get(i).getJugador2() + " x " + iCambiosVisitante.get(i).getJugador1() +
                    " Min: " +  iCambiosVisitante.get(i).getMinuto() + "  " + iCambiosVisitante.get(i).getDescripcion() + "\n";

        //Tratar Faltas
        ArrayList<Incidencia> iFaltas = partido.getIncidenciasFaltas();
        ArrayList<Incidencia> iFaltasLocal = partido.filtrarLocal(iFaltas);
        ArrayList<Incidencia> iFaltasVisitante = partido.filtrarVisitante(iFaltas);

        cadena += "\nFalta local: " + Integer.toString(partido.getFaltasLocales()) + "\n";
        for (int i=0; i<iFaltasLocal.size(); i++)
            cadena += "\t Nº " + iFaltasLocal.get(i).getJugador1()+ " Min: " +  iFaltasLocal.get(i).getMinuto() +
                    "  " + iFaltasLocal.get(i).getDescripcion() + "\n";


        cadena += "\nFalta visitante: " + Integer.toString(partido.getFaltasVisitantes()) + "\n";
        for (int i=0; i<iFaltasVisitante.size(); i++)
            cadena += "\t Nº " + iFaltasVisitante.get(i).getJugador1()+ " Min: " +  iFaltasVisitante.get(i).getMinuto() +
                    "  " + iFaltasVisitante.get(i).getDescripcion() + "\n";

        //Tratar ventaja, aceleración, Posicionamiento
        ArrayList<Incidencia> iVen = partido.getIncidenciasVentaja();
        ArrayList<Incidencia> iAce = partido.getIncidenciasAceleracion();
        ArrayList<Incidencia> iPos = partido.getIncidenciasPosicionamiento();

        cadena += "\nVentaja: \n";
        for (int i=0; i<iVen.size(); i++)
            cadena += "\t" + iVen.get(i).getMinuto() + "  " + iVen.get(i).getDescripcion() + "\n";

        cadena += "\nAceleración: \n";
        for (int i=0; i<iAce.size(); i++)
            cadena += "\t" + iAce.get(i).getMinuto() + "  " + iAce.get(i).getDescripcion() + "\n";

        cadena += "\nPosición: \n";
        for (int i=0; i<iPos.size(); i++)
            cadena += "\t" + iPos.get(i).getMinuto() + "  " + iPos.get(i).getDescripcion() + "\n";

        //Tratar otras
        ArrayList<Incidencia> iOtras = partido.getIncidenciasOtra();
        cadena += "\nOtras: \n";
        for (int i=0; i<iOtras.size(); i++)
            cadena += "\t" + iOtras.get(i).getMinuto() + "  " + iOtras.get(i).getDescripcion() + "\n";

        //Tratar eventos del spinner
        ArrayList<Incidencia> iSpinner = partido.getIncidenciasSpinner();
        cadena += "\nIncidencias del desplegable: \n";
        for (int i=0; i<iSpinner.size(); i++)
            cadena += "\t"+ iSpinner.get(i).getNombre() + " " + iSpinner.get(i).getMinuto() + "  " + iSpinner.get(i).getDescripcion() + "\n";

        //Tratar incidencias AAA1
        ArrayList<Incidencia> iNO = partido.getIncidenciasNO();
        ArrayList<Incidencia> iFJ = partido.getIncidenciasFJ();
        ArrayList<Incidencia> iFaltasAAA1 = partido.filtrarAAA1(iFaltas);
        ArrayList<Incidencia> iNoAAA1 = partido.filtrarAAA1(iNO);
        ArrayList<Incidencia> iFjAAA1 = partido.filtrarAAA1(iFJ);

        cadena += "\nAAA1: \n";
        cadena += "\n\tFaltas:\n";
        for (int i=0; i<iFaltasAAA1.size(); i++)
            cadena += "\t\tMin: " +  iFaltasAAA1.get(i).getMinuto() +
                    "  " + iFaltasAAA1.get(i).getDescripcion() + "\n";

        cadena += "\n\tNO:\n";
        for (int i=0; i<iNoAAA1.size(); i++)
            cadena += "\t\tMin: " +  iNoAAA1.get(i).getMinuto() +
                    "  " + iNoAAA1.get(i).getDescripcion() + "\n";

        cadena += "\n\tFJ:\n";
        for (int i=0; i<iFjAAA1.size(); i++)
            cadena += "\t\tMin: " +  iFjAAA1.get(i).getMinuto() +
                    "  " + iFjAAA1.get(i).getDescripcion() + "\n";


        //Tratar incidencias AAA2
        ArrayList<Incidencia> iFaltasAAA2 = partido.filtrarAAA2(iFaltas);
        ArrayList<Incidencia> iNoAAA2 = partido.filtrarAAA2(iNO);
        ArrayList<Incidencia> iFjAAA2 = partido.filtrarAAA2(iFJ);
        cadena += "\nAAA2:\n";
        cadena += "\n\tFaltas:\n";
        for (int i=0; i<iFaltasAAA2.size(); i++)
            cadena += "\t\tMin: " +  iFaltasAAA2.get(i).getMinuto() +
                    "  " + iFaltasAAA2.get(i).getDescripcion() + "\n";

        cadena += "\n\tNO:\n";
        for (int i=0; i<iNoAAA2.size(); i++)
            cadena += "\t\tMin: " +  iNoAAA2.get(i).getMinuto() +
                    "  " + iNoAAA2.get(i).getDescripcion() + "\n";

        cadena += "\n\tFJ:\n";
        for (int i=0; i<iFjAAA2.size(); i++)
            cadena += "\tM\tin: " +  iFjAAA2.get(i).getMinuto() +
                    "  " + iFjAAA2.get(i).getDescripcion() + "\n";


        cadena += "______________________________________________________________________________\n";

        return cadena;
    }

    public String informe_ordenado() {

        String cadena = "";

        //Recorro la lista completa y voy analizando
        LinkedList<Incidencia> l1 = (LinkedList<Incidencia>) this.partido.getLista();
        for (Incidencia incidencia : l1) {

            cadena += incidencia.getMinuto() + " ";
            if (incidencia.getTipo() == TipoEquipo.INCSPINNER) {
                cadena += incidencia.getNombre() + " " + incidencia.getDescripcion();
            }
            else if ((incidencia.getNombre().compareTo("Gol") == 0) || (incidencia.getNombre().compareTo("Tarjeta Amarilla") == 0) || (incidencia.getNombre().compareTo("Tarjeta Roja") == 0)) {
                cadena += incidencia.getNombre() + " " + incidencia.getTipo() + " Nº " + incidencia.getJugador1() + " " + incidencia.getDescripcion();
            }
            else if ((incidencia.getNombre().compareTo("Jugada de area") == 0) || (incidencia.getNombre().compareTo("Penalti") == 0)) {
                cadena += incidencia.getNombre() + " " + incidencia.getTipo() + " Nº " + incidencia.getJugador1() + " " + incidencia.getDescripcion();
            }
            else if ((incidencia.getNombre().compareTo("Ventaja") == 0) || (incidencia.getNombre().compareTo("Aceleración") == 0)) {
                cadena += incidencia.getNombre() + " " + incidencia.getDescripcion();
            }

            else if ((incidencia.getNombre().compareTo("Posicionamiento") == 0) || (incidencia.getNombre().compareTo("Otra") == 0)) {
                cadena += incidencia.getNombre() + " " + incidencia.getDescripcion();
            }

            else if (incidencia.getTipo() == TipoEquipo.INCSPINNER) {
                cadena += incidencia.getNombre() + " " + incidencia.getNombre() + " " + incidencia.getDescripcion();
            }

            else if (incidencia.getNombre().compareTo("Falta") == 0) {
                if(incidencia.getTipo() == TipoEquipo.AA1 || incidencia.getTipo() == TipoEquipo.AA2)
                    cadena += incidencia.getNombre() + " " + incidencia.getTipo() + " " + incidencia.getDescripcion();
                else
                    cadena += incidencia.getNombre() + " " + incidencia.getTipo() + " Nº " + incidencia.getJugador1() + " " + incidencia.getDescripcion();
            }

            else if (incidencia.getNombre().compareTo("Cambio") == 0) {
                cadena += incidencia.getNombre() + " " + incidencia.getTipo() + " Nº " + incidencia.getJugador2() + " X " + "Nº " + incidencia.getJugador1();
            }

            else if (incidencia.getNombre().compareTo("No") == 0) {
                cadena += "Deja continuar ajustado" + " " + incidencia.getTipo() + " " + incidencia.getDescripcion();
            }
            else if (incidencia.getNombre().compareTo("FJ") == 0) {
                cadena += incidencia.getNombre() + " " + incidencia.getTipo() + " " + incidencia.getDescripcion();
            }



            cadena += "\n\n";
        }

        cadena += "\nFINAL DEL PARTIDO";
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
        Incidencia eliminada = this.partido.quitarUltimoEvento();
        if(eliminada != null) {
            Toast.makeText(this, inci.getNombre() + " eliminado", Toast.LENGTH_SHORT).show();
            goll.setText(Integer.toString(partido.getGolesLocal()));
            golv.setText(Integer.toString(partido.getGolesVisitante()));
            actualizarInformeScroll();
        }else
            Toast.makeText(this, "Añada elementos al partido", Toast.LENGTH_SHORT).show();

        actualizarInformeScroll();
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
