package ru.elspirado.elspirado_app.elspirado_project.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Recorder implements Serializable, Comparable<Recorder>  {

    @NonNull
    @PrimaryKey
    private String id;
    private int value;
    private String note;
    private long time;
    private int isMedicine;

    @Ignore
    public Recorder(){

    }

    public Recorder(String id, int value, String note, long time, int isMedicine) {
        this.id = id;
        this.value = value;
        this.note = note;
        this.time = time;
        this.isMedicine = isMedicine;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getIsMedicine() {
        return isMedicine;
    }

    public void setIsMedicine(int isMedicine) {
        this.isMedicine = isMedicine;
    }

    @Ignore
    @Override
    public String toString() {
        return "id " + id + " value " + value + " note " + note + " time " + time
                + " medicine " + isMedicine;
    }

    @Ignore
    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Recorder)) return false;
        Recorder recorder = (Recorder) obj;
        return (id == recorder.id)
                && (value == recorder.value)
                && (note.equals(recorder.note)
                &&(time == recorder.time)
                && isMedicine == recorder.isMedicine);
    }

    @Ignore
    @Override
    public int compareTo(Recorder o) {
        return Long.valueOf((long)getTime()).compareTo(Long.valueOf((long) o.getTime()));
    }
}