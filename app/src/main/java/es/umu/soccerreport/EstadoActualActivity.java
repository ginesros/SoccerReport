package es.umu.soccerreport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EstadoActualActivity extends Activity {
    String informacionEstado;
    TextView contenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_estadoactual);

        Intent i = this.getIntent();
        this.informacionEstado = (String) i.getSerializableExtra("info");
    }

    @Override
    protected void onStart() {
        super.onStart();
        iniciarCampos();
        mostrarInfo();
    }

    private void iniciarCampos(){
        contenido = findViewById(R.id.contenido);
    }

    private void mostrarInfo(){
        contenido.setText(informacionEstado);
    }

    public void volver(View view) {
        setResult(Activity.RESULT_OK, null);
        finish();
    }
}
