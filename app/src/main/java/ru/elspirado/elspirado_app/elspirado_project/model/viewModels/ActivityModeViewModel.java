package ru.elspirado.elspirado_app.elspirado_project.model.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.model.ActivityMode;

public class ActivityModeViewModel extends ViewModel {

    private MutableLiveData<ActivityMode> activityModeLiveData = new MutableLiveData<>();

    public ActivityModeViewModel(){
        activityModeLiveData.setValue(new ActivityMode());
    }

    public ActivityMode getMode() {
        if(activityModeLiveData.getValue() == null){
            return new ActivityMode();
        }
        return activityModeLiveData.getValue();
    }

    public LiveData<ActivityMode> subscribe(){
        return activityModeLiveData;
    }

    public void setFragmentMode(int fragmentMode) {

        ActivityMode activityMode = activityModeLiveData.getValue();

        Objects.requireNonNull(activityMode).setFragmentMode(fragmentMode);
        activityModeLiveData.setValue(activityMode);
    }

    public int getFragmentMode(){
        return Objects.requireNonNull(activityModeLiveData.getValue()).getFragmentMode();
    }

    public int getTimeRangeMode() {
        return Objects.requireNonNull(activityModeLiveData.getValue()).getTimeRangeMode();
    }

    public void setTimeRangeMode(int timeRangeMode) {
        ActivityMode activityMode = activityModeLiveData.getValue();

        Objects.requireNonNull(activityMode).setTimeRangeMode(timeRangeMode);
        activityModeLiveData.setValue(activityMode);
    }
}
