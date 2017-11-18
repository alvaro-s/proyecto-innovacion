package com.mastercomp.innovacion.practicainnovacion;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.mastercomp.innovacion.practicainnovacion.entidades.Sesion;
import com.mastercomp.innovacion.practicainnovacion.sqlite.AdminSQLiteOpenHelper;
import com.mastercomp.innovacion.practicainnovacion.utilidades.Constantes;

import java.util.ArrayList;
import java.util.List;


public class StatActivity extends AppCompatActivity {

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //estas son caracteristicas del piechart
        pieChart = (PieChart) findViewById(R.id.idPieChart);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setDrawEntryLabels(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);

        addDataSet();
    }
    private void addDataSet() {
       /* SharedPreferences sharpref=getSharedPreferences("ArchivoSP", this.MODE_PRIVATE);
        String tiempo = sharpref.getString("MiTiem","No hay Dato");//tiempo
        String hini = sharpref.getString("HIni", "No hay Dato"); //hora inicio
        String hfin = sharpref.getString("HFin", "No hay Dato"); //hora inicio
        String inte=sharpref.getString("MiInte","No hay Dato");//interrupciones*/

        List<Sesion> listSesiones= consultarSesiones();//lista de sesiones gurdadas en la base
        Sesion sesion=listSesiones.get(listSesiones.size()-1);//Toma de la lista el último registro

        String tiempo = sesion.getTiempo_estudio();//tiempo
        String hini = sesion.getHoraInicio(); //hora inicio
        String hfin = sesion.getHoraFin(); //hora inicio
        String inte = String.valueOf(sesion.getInterrupciones());//interrupciones

        String [] tiempoSplit = tiempo.split(":");
        float fTiempoAprovechado = Integer.parseInt(tiempoSplit[0])*60 + Integer.parseInt(tiempoSplit[1]);

        String [] hiniSplit = hini.split(":");
        String [] hfinSplit = hfin.split(":");

        float fini = Integer.parseInt(hiniSplit[0])*3600 + Integer.parseInt(hiniSplit[1])*60 + Integer.parseInt(hiniSplit[2]);
        float ffin = Integer.parseInt(hfinSplit[0])*3600 + Integer.parseInt(hfinSplit[1])*60 + Integer.parseInt(hfinSplit[2]);

        float fTiempoTotal = ffin - fini;

        String[] xData = new String[]{"Tiempo aprovechado", "Tiempo perdido"};
        float[] yData = {(fTiempoAprovechado/fTiempoTotal) * 100, (1-(fTiempoAprovechado/fTiempoTotal)) * 100};

        TextView interruptions = (TextView) findViewById(R.id.interruptCounter2);
        interruptions.setText(inte);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (int i = 0; i < yData.length; i++){
            pieEntries.add(new PieEntry(yData[i], xData[i]));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueTextColor(Color.BLACK);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        pieDataSet.setColors(colors);

        //create pie legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    //Consulta en la base todos los registros de sesiones y devuelve una lista
    private List<Sesion> consultarSesiones() {
        List<Sesion> sesionList= new ArrayList<>();

        AdminSQLiteOpenHelper conn = new AdminSQLiteOpenHelper(this,
                "bd_usuarios", null, 1);

        SQLiteDatabase bd = conn.getReadableDatabase();

        Cursor cursor = bd.rawQuery("select * from sesion", null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Sesion sesion= cursorToEntity(cursor);
                sesionList.add(sesion);
                cursor.moveToNext();
            }
            cursor.close();
        }

        bd.close();
        return sesionList;
    }

    //Realiza el paso de cursor a la clase sesion para facilidad de uso
    protected Sesion cursorToEntity(Cursor cursor) {
        Sesion sesion = new Sesion();
        int idIndex;
        int fechaIndex;
        int horaInicioIndex;
        int horaFinIndex;
        int tiempoEstudioIndex;
        int interrupcionesIndex;
        int idUsuarioIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(Constantes.CAMPO_ID_SESION) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_ID_SESION);
                sesion.setIdSesion(cursor.getString(idIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_FECHA) != -1) {
                fechaIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_FECHA);
                sesion.setFecha(cursor.getString(fechaIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_HORA_INICIO) != -1) {
                horaInicioIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_HORA_INICIO);
                sesion.setHoraInicio(cursor.getString(horaInicioIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_HORA_FIN) != -1) {
                horaFinIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_HORA_FIN);
                sesion.setHoraFin(cursor.getString(horaFinIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_TIEMPO_ESTUDIO) != -1) {
                tiempoEstudioIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_TIEMPO_ESTUDIO);
                sesion.setTiempo_estudio(cursor.getString(tiempoEstudioIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_INTERRUPCIONES) != -1) {
                interrupcionesIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_INTERRUPCIONES);
                sesion.setInterrupciones(cursor.getInt(interrupcionesIndex));
            }
            if (cursor.getColumnIndex(Constantes.CAMPO_ID_USUARIO) != -1) {
                idUsuarioIndex = cursor.getColumnIndexOrThrow(Constantes.CAMPO_ID_USUARIO);
                sesion.setIdUsuario(cursor.getString(idUsuarioIndex));
            }

        }
        return sesion;
    }
}
