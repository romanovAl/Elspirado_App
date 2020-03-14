package ru.elspirado.elspirado_app.elspirado_project.model;

import androidx.annotation.NonNull;

public class ActivityMode {

    private int timeRangeMode = 0;

    private int fragmentMode = 0;

    public int getTimeRangeMode() {
        return timeRangeMode;
    }

    public void setTimeRangeMode(int timeRangeMode) {
        this.timeRangeMode = timeRangeMode;
    }

    public int getFragmentMode() {
        return fragmentMode;
    }

    public void setFragmentMode(int fragmentMode) {
        this.fragmentMode = fragmentMode;
    }

    @NonNull
    @Override
    public String toString() {
        return "timeRangeMode " + timeRangeMode + " fragmentMode " + fragmentMode;
    }
}