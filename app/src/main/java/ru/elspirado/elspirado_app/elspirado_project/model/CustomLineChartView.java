package ru.elspirado.elspirado_app.elspirado_project.model;

import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.db.chart.model.LineSet;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.util.Tools;
import com.db.chart.view.LineChartView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.R;

public class CustomLineChartView extends LineChartView {

    private ArrayList<Recorder> arrayList;

    private Activity activity;

    public CustomLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //-------Работа со всплывающими подсказками-------

        final Tooltip tip = new Tooltip(getContext(), R.layout.tool, R.id.value);
        tip.setDimensions(75, 40);

        tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(80);

        tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(80);

        tip.setBackgroundColor(Color.parseColor("#ff9e80"));

        tip.setVerticalAlignment(Tooltip.Alignment.CENTER);//Назначение левитации подсказочки над точечкой
        tip.setDimensions((int) Tools.fromDpToPx(36),
                (int) Tools.fromDpToPx(20));//менять размер подсказочки

        super.setTooltips(tip);

        super.setClickablePointRadius(40); //зона кликанья
    }

    public void setArrayList(ArrayList<Recorder> arrayList , Activity activity){
        this.activity = activity;
        this.arrayList = arrayList;
        setChartSettings();
    }

    private void setChartSettings(){

        String[] stringDate = new String[arrayList.size()];
        float[] values = new float[arrayList.size()];

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale
                .getDefault()); //http://qaru.site/questions/11806/get-current-time-and-date-on-android

        for (int i = 0; i < arrayList.size(); i++) {

            Date date = new Date();
            date.setTime(arrayList.get(i).getTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            stringDate[i] = dateFormat.format(calendar.getTime());

            values[i] = arrayList.get(i).getValue();
        }

        LineSet dataSet = new LineSet(stringDate, values);

        dataSet
                .setColor(Color.parseColor("#616161"))
                .setDotsColor(Color.parseColor("#616161"))
                .setDotsRadius((int) Tools.fromDpToPx(4)) //Менять радиус точки
                .setSmooth(true) //Настраивает сглаживание
                .setThickness(Tools.fromDpToPx(3));//Настройка тонкости графика

        super.addData(dataSet);

        //-------Работа с внешним видом графика-------

        Recorder recorderMin = getMinValue(arrayList);
        Recorder recorderMax = getMaxValue(arrayList);

        if (recorderMin.getValue() < 10) {
            super.setAxisBorderValues(0, recorderMax.getValue() + 10, 10);
        } else {
            super.setAxisBorderValues(recorderMin.getValue() - 10, recorderMax.getValue() + 10, 10);
        }

        if (recorderMax.getValue() - recorderMin.getValue() > 300) {
            super.setStep(20);
        }

        if (recorderMax.getValue() - recorderMin.getValue() > 500) {
            super.setStep(40);
        }

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#ffd0b0"));
        gridPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        super.setGrid((recorderMax.getValue() - recorderMin.getValue() + 10) / 10, 0,
                gridPaint);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Objects.requireNonNull(activity).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        if (arrayList.size() > 10) {

            if (width < (values.length * width
                    / 10)) {
                super.setLayoutParams(new LinearLayout.LayoutParams((values.length * width / 10),
                        super.getLayoutParams().height));
            } else {
                super.setLayoutParams(
                        new LinearLayout.LayoutParams(width, super.getLayoutParams().height));
            }
        }

        super.show();
    }

    private Recorder getMinValue(ArrayList<Recorder> arrayList) {
        Recorder recorderMin = arrayList.get(0);

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getValue() < recorderMin.getValue()) {
                recorderMin = arrayList.get(i);
            }
        }

        return recorderMin;
    }

    private Recorder getMaxValue(ArrayList<Recorder> arrayList) {
        Recorder recorderMax = arrayList.get(0);

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getValue() > recorderMax.getValue()) {
                recorderMax = arrayList.get(i);
            }
        }

        return recorderMax;
    }

}