package ru.elspirado.elspirado_app.elspirado_project.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Notification {

    @NonNull
    @PrimaryKey
    private String title;
    private String text;
    private int id;
    private long time;
    private boolean isRepeat;

    @Ignore
    public Notification(){
    }

    public Notification(String text, int id, long time, boolean isRepeat) {
        this.text = text;
        this.id = id;
        this.time = time;
        this.isRepeat = isRepeat;
    }

    @Ignore
    @Override
    public String toString() {
        return "Notification{" +
            "text='" + text + '\'' +
            ", id=" + id +
            ", time=" + time +
            ", isRepeat=" + isRepeat +
            '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }
}
