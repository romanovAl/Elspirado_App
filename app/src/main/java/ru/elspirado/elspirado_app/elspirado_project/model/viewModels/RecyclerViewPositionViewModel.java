package ru.elspirado.elspirado_app.elspirado_project.model.viewModels;

import androidx.lifecycle.ViewModel;

public class RecyclerViewPositionViewModel extends ViewModel {

    private int position = -1;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
