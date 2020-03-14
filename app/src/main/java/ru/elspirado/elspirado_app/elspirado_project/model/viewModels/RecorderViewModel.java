package ru.elspirado.elspirado_app.elspirado_project.model.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;

public class RecorderViewModel extends ViewModel {

    private MutableLiveData<Recorder> recorderMutableLiveData = new MutableLiveData<>();

    public LiveData<Recorder> subscribe(){
        return recorderMutableLiveData;
    }

    public Recorder getRecorder() {
        return recorderMutableLiveData.getValue();
    }

    public void setRecorder(Recorder recorder) {
        recorderMutableLiveData.setValue(recorder);
    }
}