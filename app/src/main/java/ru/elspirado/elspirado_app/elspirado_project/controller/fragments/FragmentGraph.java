package ru.elspirado.elspirado_app.elspirado_project.controller.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.db.chart.listener.OnEntryClickListener;
import com.libRG.CustomTextView;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.ActivityMode;
import ru.elspirado.elspirado_app.elspirado_project.model.CustomLineChartView;
import ru.elspirado.elspirado_app.elspirado_project.model.DBRecorder;
import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;
import ru.elspirado.elspirado_app.elspirado_project.model.TimeRange;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.ActivityModeViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.DataViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.RecorderViewModel;

import java.util.ArrayList;
import java.util.Objects;

import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.FRAGMENT_MODE_GRAPH;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.ACTIVITY_MODE_RANGE_All;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.ACTIVITY_MODE_RANGE_SELECTED;

public class FragmentGraph extends Fragment implements DBRecorder.CallbackSelectAllListener {

    private CustomLineChartView chart;
    private HorizontalScrollView scrollView;
    private CustomTextView customTextViewYear;

    private ArrayList<Recorder> arrayList;

    private RecorderViewModel recorderViewModel;

    private DataViewModel dataViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        setRetainInstance(true);

        chart = view.findViewById(R.id.chart);
        scrollView = view.findViewById(R.id.horizontalScrollView);
        customTextViewYear = view.findViewById(R.id.customTextView_year);

        chart.setHorizontalScrollBarEnabled(true);

        final ActivityModeViewModel activityMode = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(ActivityModeViewModel.class);

        recorderViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(RecorderViewModel.class);

        dataViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).
                get(DataViewModel.class);

        dataViewModel.subscribeOnArrayListAllData().observe(getActivity(), new Observer<ArrayList<Recorder>>() {
            @Override
            public void onChanged(ArrayList<Recorder> arrayList) {

                System.err.println("FragmentGraph - OnCreateView - получили лист " + arrayList.toString());

                ActivityMode mode = activityMode.getMode();

                if(mode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_All){

                    updateCurrentArrayList(arrayList);

                    if(mode.getFragmentMode() == FRAGMENT_MODE_GRAPH){

                        updateGraph();
                    }
                }
            }
        });

        dataViewModel.subscribeOnTimeRangeData().observe(getActivity(), new Observer<TimeRange>() {
            @Override
            public void onChanged(TimeRange timeRange) {

                ActivityMode mode = activityMode.getMode();

                if(mode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_SELECTED){
                    updateCurrentArrayList(timeRange.getArrayList());
                    updateGraph();
                }
            }
        });

        return view;
    }

    private void updateCurrentArrayList(ArrayList<Recorder> arrayList){

        this.arrayList = arrayList;

    }

    private void updateGraph(){

        setGraph(this.arrayList);
    }

    @Override
    public void DBRecorderCallingBackSelectAll(ArrayList<Recorder> arrayList) {

        if (arrayList != null) {
            setGraph(arrayList);
        } else {
            mListener.fragmentGraphNoGraph();
        }
    }

    public void setGraph(){
        if(arrayList != null)
            setGraph(arrayList);
    }

    private void setGraph(final ArrayList<Recorder> arrayList) {

        resetChart();
        dismissTooltips();

        if(arrayList.isEmpty()){
            return;
        }

        chart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {

                Recorder recorder = arrayList.get(entryIndex);
                System.err.println("FragmentGraph - Тыкнули на точку " + recorder.toString());

                recorderViewModel.setRecorder(recorder);
            }
        });

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.dismissAllTooltips();
                mListener.fragmentGraphTapOnChartListener();
            }
        });

        chart.setArrayList(arrayList,getActivity());

        scrollDialogDown();
    }

    private void scrollDialogDown() {
        scrollView.post(new Runnable() {

            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });

    }

    public void setCustomTextViewYearText(String yearText){
        customTextViewYear.setVisibility(View.VISIBLE);
        customTextViewYear.setText(yearText);
    }

    public void hideCustomTextViewYearText(){
        customTextViewYear.setVisibility(View.INVISIBLE);
    }

    public void resetChart() {
        chart.reset();
    }

    public void dismissTooltips() {
        chart.dismissAllTooltips();
    }


    public interface OnFragmentListener {

        void fragmentGraphTapOnChartListener();

        void fragmentGraphNoGraph();
    }

    private OnFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentListener) {
            mListener = (OnFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnFragmentListener");
        }
    }
}