package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;
import java.util.Map;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.DBRecorder;
import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;

public class ChartActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        final LineChart lineChart = findViewById(R.id.chart);


        new DBRecorder().selectAll(new DBRecorder.CallbackSelectAllListener() {
            @Override
            public void DBRecorderCallingBackSelectAll(ArrayList<Recorder> arrayList) {
                ;

                for(int i = 0; i < arrayList.size(); i++){
                    Recorder recorder = arrayList.get(i);
                    arrayList.remove(i);

                    recorder.setTime(i);

                    arrayList.add(recorder);
                }

                ArrayList<Entry> entryArrayList = new ArrayList<>();

                for(int i = 0; i < arrayList.size(); i++){
                    entryArrayList.add(new Entry(arrayList.get(i).getTime(),arrayList.get(i).getValue()));
                }

                LineDataSet dataSet = new LineDataSet(entryArrayList,"lol");

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);

                lineChart.setVisibleXRangeMaximum(15);//Сколько значений видно без пролистываения

                //lineChart.moveViewToX(arrayList.size() - 15); //К какому знаению пролистать

                lineChart.setScaleEnabled(false); //Отключает возможность масштабирования

                lineChart.setKeepPositionOnRotation(true);

                lineChart.setOnChartGestureListener(new OnChartGestureListener() {
                    @Override
                    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                    }

                    @Override
                    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                    }

                    @Override
                    public void onChartLongPressed(MotionEvent me) {

                    }

                    @Override
                    public void onChartDoubleTapped(MotionEvent me) {

                    }

                    @Override
                    public void onChartSingleTapped(MotionEvent me) { //Вот это понадобиться для обработки нажатий по графику
                        System.err.println("Тыцк по графику");
                    }

                    @Override
                    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

                    }

                    @Override
                    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

                    }

                    @Override
                    public void onChartTranslate(MotionEvent me, float dX, float dY) {

                    }
                });
            }
        });
    }
}
