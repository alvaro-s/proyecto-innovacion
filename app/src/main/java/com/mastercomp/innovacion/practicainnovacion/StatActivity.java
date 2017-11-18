package com.mastercomp.innovacion.practicainnovacion;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;


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
        SharedPreferences sharpref=getSharedPreferences("ArchivoSP", this.MODE_PRIVATE);
        String tiempo = sharpref.getString("MiTiem","No hay Dato");//tiempo
        String hini = sharpref.getString("HIni", "No hay Dato"); //hora inicio
        String hfin = sharpref.getString("HFin", "No hay Dato"); //hora inicio
        String inte=sharpref.getString("MiInte","No hay Dato");//interrupciones

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
}
