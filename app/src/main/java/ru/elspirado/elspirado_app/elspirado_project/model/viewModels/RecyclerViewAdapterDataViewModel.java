package ru.elspirado.elspirado_app.elspirado_project.model.viewModels;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;

public class RecyclerViewAdapterDataViewModel extends ViewModel {

    private ArrayList<Recorder>
            selectedArrayList; //Хранит выбранные для взаимодействия элементы

    private boolean[]
            isSelectedArray, //Хранит данные о элементах, выбранных для взаимодействия
            isOpenedFullArray; //Хранит данные о раскрытах полностью элементах

    private int countSelected = 0;

    public ArrayList<Recorder> getSelectedArrayList() {
        return selectedArrayList;
    }

    public void setSelectedArrayList(ArrayList<Recorder> selectedArrayList) {
        this.selectedArrayList = selectedArrayList;
    }

    public boolean[] getIsSelectedArray() {
        return isSelectedArray;
    }

    public void setIsSelectedArray(boolean[] isSelectedArray) {
        this.isSelectedArray = isSelectedArray;
    }

    public boolean[] getIsOpenedFullArray() {
        return isOpenedFullArray;
    }

    public void setIsOpenedFullArray(boolean[] isOpenedFullArray) {
        this.isOpenedFullArray = isOpenedFullArray;
    }

    public int getCountSelected() {
        return countSelected;
    }

    public void setCountSelected(int countSelected) {
        this.countSelected = countSelected;
    }
}
