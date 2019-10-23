package es.umu.soccerreport;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;


public class PrincipalActivity extends Activity {
    private static final int code = 1;
    private Partido partido;
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
        setContentView(R.layout.principal);
        SurfaceView ss = (SurfaceView) findViewById(R.id.surfaceView1);
        ss.setBackgroundColor(Color.rgb(192, 192, 192));
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
        dia.setText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        mes.setText(Integer.toString(cal.get(Calendar.MONTH) + 1));
        anyo.setText(Integer.toString(cal.get(Calendar.YEAR)));
        horas.setText(Integer.toString(cal.get(Calendar.HOUR_OF_DAY)));
        minutos.setText(Integer.toString(cal.get(Calendar.MINUTE)));

        equipoLocal.requestFocus();

    }

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

        if (horasActuales < 0 || horasActuales > 24 || minutosActuales < 0 ||minutosActuales > 59)
            return false;

        return true;
    }

    public void siguiente(View view) {
        int duration = Toast.LENGTH_LONG;

        if (!comprobarParametros()) {
            Toast toast = Toast.makeText(this, "Revise los campos", duration);
            toast.show();
        } else {
            partido = new Partido(equipoLocal.getText().toString(), equipoVisitante.getText().toString(),
                    Integer.parseInt(dia.getText().toString()), Integer.parseInt(mes.getText().toString()),
                    Integer.parseInt(anyo.getText().toString()), Integer.parseInt(horas.getText().toString()),
                    Integer.parseInt(minutos.getText().toString()));
            Intent intent1 = new Intent(getApplicationContext(), IncidenciaActivity.class);
            intent1.putExtra("team", this.partido);
            //startActivity(intent1);
            startActivityForResult(intent1, code);
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
