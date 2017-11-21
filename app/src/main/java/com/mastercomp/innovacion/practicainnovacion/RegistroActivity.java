package com.mastercomp.innovacion.practicainnovacion;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mastercomp.innovacion.practicainnovacion.sqlite.AdminSQLiteOpenHelper;
import com.mastercomp.innovacion.practicainnovacion.utilidades.Constantes;

import java.util.UUID;

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

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                crearUsuario();
                Intent intent= new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void crearUsuario() {
        AdminSQLiteOpenHelper conn=new AdminSQLiteOpenHelper(this,"bd_usuarios",null,1);
        SQLiteDatabase db=conn.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(Constantes.CAMPO_ID, UUID.randomUUID().toString());
        values.put(Constantes.CAMPO_NOMBRE,txtNombre.getText().toString());
        values.put(Constantes.CAMPO_APELLIDO,txtApellido.getText().toString());

        db.insert(Constantes.TABLA_USUARIO,Constantes.CAMPO_ID,values);

        Toast.makeText(getApplicationContext(),"Usuario guardado correctamente",Toast.LENGTH_SHORT).show();
        db.close();
    }
}
