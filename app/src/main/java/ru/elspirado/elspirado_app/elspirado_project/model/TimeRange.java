package ru.elspirado.elspirado_app.elspirado_project.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TimeRange {

    private ArrayList<Recorder> arrayList; //Содержит в себе значения из временного отрезка

    private long fromTime, toTime;

    private String stringTimeRange;

    public TimeRange(long fromTime, long toTime, ArrayList<Recorder> arrayList){

        this.fromTime = fromTime;
        this.toTime = toTime;

        this.arrayList = arrayList;


        SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yy",
                Locale.getDefault());

        if (fromTime == toTime) {

            stringTimeRange = timeFormat.format(fromTime);

        } else {

            String fromTimeString = timeFormat.
                    format(fromTime);

            String toTimeString = timeFormat.
                    format(toTime);

            stringTimeRange = fromTimeString + " - " + toTimeString;

        }

        System.err.println("Создали строку " + stringTimeRange);
    }

    public ArrayList<Recorder> getArrayList() {
        return arrayList;
    }

    public String getStringTimeRange() {
        return stringTimeRange;
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    public void setArrayList(ArrayList<Recorder> arrayList) {
        this.arrayList = arrayList;
    }
}