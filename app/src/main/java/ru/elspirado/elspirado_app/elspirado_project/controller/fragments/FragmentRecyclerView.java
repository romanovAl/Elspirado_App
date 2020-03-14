package ru.elspirado.elspirado_app.elspirado_project.controller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;
import ru.elspirado.elspirado_app.elspirado_project.model.RecyclerViewAdapter;
import ru.elspirado.elspirado_app.elspirado_project.model.TimeRange;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.ActivityModeViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.DataViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.RecyclerViewAdapterDataViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.RecyclerViewPositionViewModel;

import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.ACTIVITY_MODE_RANGE_All;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.ACTIVITY_MODE_RANGE_SELECTED;

public class FragmentRecyclerView extends Fragment{

    private RecyclerView recyclerView;

    private RecyclerViewAdapter recyclerViewAdapter;

    private RecyclerViewPositionViewModel lastFirstVisiblePosition;

    private RecyclerViewAdapterDataViewModel recyclerViewAdapterData;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        lastFirstVisiblePosition = ViewModelProviders.of(this).get(RecyclerViewPositionViewModel.class);
        recyclerViewAdapterData = ViewModelProviders.of(this).get(RecyclerViewAdapterDataViewModel.class);

        final ActivityModeViewModel activityMode = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(ActivityModeViewModel.class);

        DataViewModel dataViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).
                get(DataViewModel.class);

        dataViewModel.subscribeOnArrayListAllData().observe(this, new Observer<ArrayList<Recorder>>() {
            @Override
            public void onChanged(ArrayList<Recorder> arrayList) {

                if(activityMode.getMode().getTimeRangeMode() == ACTIVITY_MODE_RANGE_All){

                    setRecyclerView(arrayList,getActivity());
                }
            }
        });

        dataViewModel.subscribeOnTimeRangeData().observe(this, new Observer<TimeRange>() {
            @Override
            public void onChanged(TimeRange timeRange) {

                System.err.println("В ресайкле апдейт даты " + timeRange.getArrayList().toString());

                if(activityMode.getMode().getTimeRangeMode() == ACTIVITY_MODE_RANGE_SELECTED){

                    setRecyclerView(timeRange.getArrayList(),getActivity());
                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {

        //Сохраняем в ViewModel параметры для ресайкла
        try {
            lastFirstVisiblePosition.setPosition(((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager()))
                    .findLastCompletelyVisibleItemPosition());

            recyclerViewAdapterData.setIsOpenedFullArray(recyclerViewAdapter.getIsOpenedFullArray());
            recyclerViewAdapterData.setIsSelectedArray(recyclerViewAdapter.getIsSelectedArray());
            recyclerViewAdapterData.setCountSelected(recyclerViewAdapter.getCountSelected());
            recyclerViewAdapterData.setSelectedArrayList(recyclerViewAdapter.getSelectedArrayList());

        }catch (NullPointerException ignored){

        }

        super.onPause();
    }

    private void setRecyclerView(ArrayList<Recorder> list, Context context){

        System.err.println("FragmentRecyclerView - setRecyclerView - получили arrayList и уже перевернули " + list.toString());

        recyclerViewAdapter = new RecyclerViewAdapter(list,context);

        //Получаем список раскрытых полностью элементов в RecyclerView из ViewModel
        boolean[] isOpenedFullArrayViewModel = recyclerViewAdapterData.getIsOpenedFullArray();

        //И список выделенных элементов
        boolean[] isSelectedArrayViewModel = recyclerViewAdapterData.getIsSelectedArray();

        //Количество выделенных элементов
        int countSelectedViewModel = recyclerViewAdapterData.getCountSelected();

        //Наконец сами эти элементы
        ArrayList<Recorder> selectedArrayListViewModel = recyclerViewAdapterData.getSelectedArrayList();

        //Если этот список не null, и ничего нового не добавлилось/убавилось, мы его применяем
        if(isOpenedFullArrayViewModel != null && isOpenedFullArrayViewModel.length == list.size()){

            recyclerViewAdapter.setIsOpenedFullArray(isOpenedFullArrayViewModel);
            recyclerViewAdapter.setCountSelected(countSelectedViewModel);
            recyclerViewAdapter.setSelectedArrayList(selectedArrayListViewModel);

        } else { //Иначе создаём новый список, с обновлённой длинной
            recyclerViewAdapter.setIsOpenedFullArray(new boolean[list.size()]);
            recyclerViewAdapter.setCountSelected(0);
            recyclerViewAdapter.setSelectedArrayList(new ArrayList<Recorder>());
        }

        //аналогично
        if(isSelectedArrayViewModel != null && isSelectedArrayViewModel.length == list.size()){

            recyclerViewAdapter.setIsSelectedArray(isSelectedArrayViewModel);
            recyclerViewAdapter.setCountSelected(countSelectedViewModel);
            recyclerViewAdapter.setSelectedArrayList(selectedArrayListViewModel);

        } else {
            recyclerViewAdapter.setIsSelectedArray(new boolean[list.size()]);
            recyclerViewAdapter.setCountSelected(0);
            recyclerViewAdapter.setSelectedArrayList(new ArrayList<Recorder>());
        }

        recyclerViewAdapter.notifyDataSetChanged();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);

        try {
            ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager()))
                    .scrollToPositionWithOffset(lastFirstVisiblePosition.getPosition(),0);



        }catch (NullPointerException ignored){

        }

        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public void deleteElementInRecyclerView(){
        recyclerViewAdapter.deleteElements();
    }

    public void shareElementsInRecyclerView(){
        recyclerViewAdapter.shareElements();
    }

    public void selectAllElementsInRecyclerView(){
        recyclerViewAdapter.selectAllElements();
    }

    public void cleanSelectionInRecyclerView(){
        recyclerViewAdapter.cleanSelection();
    }

    public int getRecyclerViewSelectedItemCount(){
        return recyclerViewAdapter.getCountSelected();
    }
}