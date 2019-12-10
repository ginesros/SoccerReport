package es.umu.soccerreport;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class PrincipalActivity extends Activity {
    private static final int code = 1;
    private EditText dia;
    private EditText mes;
    private EditText anyo;
    private EditText horas;
    private EditText minutos;
    private EditText equipoLocal;
    private EditText equipoVisitante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_principal);
    }

    @Override
    protected void onStart() {
        super.onStart();
        establecerValoresPantallaInicial();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    public void salir(View view) {
        finish();
    }

    private void establecerValoresPantallaInicial() {
        String horaInicial, minutoInicial, diaInicial, mesInicial, anyoInicial;
        int horaInicialInt, minutoInicialInt, diaInicialInt, mesInicialInt;

        //Inicialiar los ID de la vista
        dia = findViewById(R.id.dia);
        mes = findViewById(R.id.mes);
        anyo = findViewById(R.id.anyo);
        horas = findViewById(R.id.horas);
        minutos = findViewById(R.id.minutos);
        equipoLocal = findViewById(R.id.equipo1);
        equipoVisitante = findViewById(R.id.equipo2);

        //Asignar los valores por defecto a la pantalla
        Calendar cal = Calendar.getInstance();

        diaInicialInt = cal.get(Calendar.DAY_OF_MONTH);
        mesInicialInt = cal.get(Calendar.MONTH) + 1;
        horaInicialInt = cal.get(Calendar.HOUR_OF_DAY);
        minutoInicialInt = cal.get(Calendar.MINUTE);

        if (diaInicialInt < 10)
            diaInicial = "0" + Integer.toString(diaInicialInt);
        else
            diaInicial = Integer.toString(diaInicialInt);

        if (mesInicialInt < 10)
            mesInicial = "0" + Integer.toString(mesInicialInt);
        else
            mesInicial = Integer.toString(mesInicialInt);

        if (horaInicialInt < 10) {
            horaInicial = "0" + Integer.toString(horaInicialInt);
        }
        else {
            horaInicial = Integer.toString(horaInicialInt);
        }

        if (minutoInicialInt < 10) {
            minutoInicial = "0" + Integer.toString(minutoInicialInt);
        }
        else {
            minutoInicial = Integer.toString(minutoInicialInt);
        }

        anyoInicial = Integer.toString(cal.get(Calendar.YEAR));

        dia.setText(diaInicial);
        mes.setText(mesInicial);
        anyo.setText(anyoInicial);
        horas.setText(horaInicial);
        minutos.setText(minutoInicial);

        //equipoLocal.requestFocus();

    }

    /**
     * Comprueba que los campos introducidos son correctos además de guardarlos en las variables correspondientes
     * @return boolean Retorna true si los campos son correctos, false en otro caso
     */
    private boolean comprobarParametros() {
        //Comprobar equipos Locales y visitantes
        equipoLocal = findViewById(R.id.equipo1);
        equipoVisitante = findViewById(R.id.equipo2);

        if (equipoLocal.getText().toString().compareTo("") == 0 || equipoVisitante.getText().toString().compareTo("") == 0)
            return false;

        //Comprobar día, mes y año
        dia = findViewById(R.id.dia);
        mes = findViewById(R.id.mes);
        anyo = findViewById(R.id.anyo);

        if (dia.getText().toString().compareTo("") == 0 ||
                mes.getText().toString().compareTo("") == 0 ||
                anyo.getText().toString().compareTo("") == 0)
            return false;

        int mesActual = Integer.parseInt(mes.getText().toString());
        int diaActual = Integer.parseInt(dia.getText().toString());

        if (mesActual < 0 || mesActual > 12)
            return false;

        switch (mesActual) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (diaActual < 0 || diaActual > 31){
                return false;
            }
            break;
            case 2:
                if (diaActual < 0 || diaActual > 28) {
                    return false;
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (diaActual < 0 || diaActual > 30) {
                    return false;
                }
                break;
        }

        if (Integer.parseInt(anyo.getText().toString()) < 2012)
            return false;

        //Comprobar horas y minutos
        horas = findViewById(R.id.horas);
        minutos = findViewById(R.id.minutos);

        if (horas.getText().toString().compareTo("") == 0 ||
                horas.getText().toString().compareTo("") == 0)
            return false;

        int horasActuales = Integer.parseInt(horas.getText().toString());
        int minutosActuales = Integer.parseInt(minutos.getText().toString());

        return !(horasActuales < 0 || horasActuales > 24 || minutosActuales < 0 ||minutosActuales > 59);
    }

    public void cargarPartido(View view){
        List<Partido> partidos = AppDatabase.getInstance(getApplicationContext()).partidoDao().getAll();

        if(partidos != null) {
            final ArrayAdapter<String> titulos = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_desplegableitems);
            for(int i = 0; i<partidos.size(); i++){
                String titulo = partidos.get(i).getEquipoLocal() + " vs " + partidos.get(i).getEquipoVisitante() + "  " +
                        partidos.get(i).getAnyo() + "-" + partidos.get(i).getMes() + "-" + partidos.get(i).getDia() +
                        " " + partidos.get(i).getHoras() + ":" + partidos.get(i).getMinutos();
                titulos.add(titulo);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Seleccione el partido:")
                    .setSingleChoiceItems(titulos, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cargarPartido(which);
                        }
                    });

            builder.create().show();


        }else
            Toast.makeText(this, "Lista de partidos vacía\nAñada uno nuevo", Toast.LENGTH_SHORT).show();
    }

    private void cargarPartido(int seleccionado){
        Partido partido = AppDatabase.getInstance(getApplicationContext()).partidoDao().getAll().get(seleccionado);
        Intent intent1 = new Intent(getApplicationContext(), IncidenciaActivity.class);
        intent1.putExtra("team", partido);
        startActivityForResult(intent1, code);
        limpiarCampos();
    }

    private void limpiarCampos(){
        equipoLocal.setText("");
        equipoVisitante.setText("");
    }

    public void siguiente(View view) {
        int duration = Toast.LENGTH_LONG;

        if (!comprobarParametros()) {
            Toast toast = Toast.makeText(this, "Revise los campos", duration);
            toast.show();
        } else {
            Partido partido = new Partido(equipoLocal.getText().toString(), equipoVisitante.getText().toString(),
                    dia.getText().toString(), mes.getText().toString(), anyo.getText().toString(),
                    horas.getText().toString(), minutos.getText().toString());
            Intent intent1 = new Intent(getApplicationContext(), IncidenciaActivity.class);
            intent1.putExtra("team", partido);
            //int id = AppDatabase.getInstance(getApplicationContext()).partidoDao().getAll().size();
            int id = AppDatabase.getInstance(getApplicationContext()).partidoDao().getMaxIdPartido();
            id++;
            partido.idPartido = id;
            //startActivity(intent1);
            //AppDatabase.getInstance(getApplicationContext()).partidoDao().deleteAllData();
            startActivityForResult(intent1, code);
            limpiarCampos();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == code) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }
}
