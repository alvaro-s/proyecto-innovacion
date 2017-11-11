package com.mastercomp.innovacion.practicainnovacion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegistroActivity extends AppCompatActivity {
    private Button btnContinuar;
    private EditText txtNombre;
    private EditText txtApellido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        btnContinuar = (Button) findViewById(R.id.btnContinuar);
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtApellido = (EditText) findViewById(R.id.txtApellido);

        //GUARDAR ARCHIVO
        final Context context=this;//crear una variable context para guaradr los datos
        final SharedPreferences sharprefs=getSharedPreferences("ArchivoSP",context.MODE_PRIVATE);

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor=sharprefs.edit();
                editor.putString("nombre",txtNombre.getText().toString());
                editor.putString("apellido",txtApellido.getText().toString());
                editor.commit();

                Intent intent= new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
