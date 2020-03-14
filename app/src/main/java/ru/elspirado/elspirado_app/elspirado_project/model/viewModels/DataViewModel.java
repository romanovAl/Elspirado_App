package ru.elspirado.elspirado_app.elspirado_project.model.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.model.DBRecorder;
import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;
import ru.elspirado.elspirado_app.elspirado_project.model.TimeRange;

public class DataViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Recorder>>
            arrayListAllData = new MutableLiveData<>();

    private MutableLiveData<TimeRange>
            timeRangeData = new MutableLiveData<>();

    public LiveData<ArrayList<Recorder>> subscribeOnArrayListAllData(){
        return arrayListAllData;
    }

    public LiveData<TimeRange> subscribeOnTimeRangeData(){
        return timeRangeData;
    }

    public void setTimeRangeData(TimeRange timeRange){
        timeRangeData.setValue(timeRange);
    }

    public void updateArrayListAllData(){

        new DBRecorder().selectAll(new DBRecorder.CallbackSelectAllListener() {
            @Override
            public void DBRecorderCallingBackSelectAll(ArrayList<Recorder> arrayList) {
                arrayListAllData.setValue(arrayList);
            }
        });
    }

    public void deleteRecorderInSelectedData(Recorder recorder){

        TimeRange timeRange = timeRangeData.getValue();

        ArrayList<Recorder> arrayList = Objects.requireNonNull(timeRange).getArrayList();

        arrayList.remove(recorder);

        timeRange.setArrayList(arrayList);

        timeRangeData.setValue(timeRange);
    }

    public void addRecorderInSelectedData(Recorder recorder){

        TimeRange timeRange = timeRangeData.getValue();

        ArrayList<Recorder> arrayList = Objects.requireNonNull(timeRange).getArrayList();

        arrayList.add(recorder);

        Collections.sort(arrayList);//todo проверить хорошо ли это работает

        timeRange.setArrayList(arrayList);

        timeRangeData.setValue(timeRange);
    }

    public void deleteArrayListInSelectedData(ArrayList<Recorder> arrayList){

        TimeRange timeRange = timeRangeData.getValue();

        ArrayList<Recorder> arrayListAll = Objects.requireNonNull(timeRange).getArrayList();

        arrayListAll.removeAll(arrayList);

        timeRange.setArrayList(arrayListAll);

        timeRangeData.setValue(timeRange);
    }

    public void updateRecorderInSelectedData(Recorder newRecorder){ //todo в целом это всё большой костыль

        TimeRange timeRange = timeRangeData.getValue();

        ArrayList<Recorder> arrayListAll = Objects.requireNonNull(timeRange).getArrayList();

        for(int i = 0; i < arrayListAll.size(); i++){

            Recorder oldRecorder = arrayListAll.get(i);

            if(oldRecorder.getId().equals(newRecorder.getId())){

                arrayListAll.remove(oldRecorder);
                arrayListAll.add(newRecorder);
                Collections.sort(arrayListAll);//todo проверить хорошо ли это работает

                timeRange.setArrayList(arrayListAll);
            }
        }
    }

    public String getTimeRangeString(){
        return Objects.requireNonNull(timeRangeData.getValue()).getStringTimeRange();
    }
}