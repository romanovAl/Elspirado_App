package ru.elspirado.elspirado_app.elspirado_project.controller.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.db.chart.listener.OnEntryClickListener;
import com.libRG.CustomTextView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.CustomLineChartView;
import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.ActivityModeViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.DataViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.RecorderViewModel;

public class NewFragmentGraph extends Fragment {

    private CustomLineChartView chart;
    private HorizontalScrollView scrollView;
    private CustomTextView customTextViewYear;

    private ArrayList<Recorder> recorderArrayList;

    private RecorderViewModel recorderViewModel;
    private DataViewModel dataViewModel;
    private ActivityModeViewModel activityMode;

    private EditText editText;
    private ImageButton buttonDelete;
    private ImageButton buttonEdit;
    private TextView textTime;
    private TextView textIsMedicine;
    private CardView cardView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_fragment_graph, container, false);

        setRetainInstance(true);

        chart = view.findViewById(R.id.chart);
        scrollView = view.findViewById(R.id.horizontalScrollView);
        customTextViewYear = view.findViewById(R.id.customTextView_year);

        editText = view.findViewById(R.id.note_edit_text);
        buttonDelete = view.findViewById(R.id.note_button_delete);
        buttonEdit = view.findViewById(R.id.note_button_edit);
        textTime = view.findViewById(R.id.note_text_time);
        textIsMedicine = view.findViewById(R.id.note_text_isMedicine);
        cardView = view.findViewById(R.id.note_cardView);

        recorderViewModel = ViewModelProviders.of(this).get(RecorderViewModel.class);

        activityMode = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(ActivityModeViewModel.class);

        dataViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).
                get(DataViewModel.class);

        doNoteLogic();
        doGraphLogic();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        KeyboardVisibilityEvent.registerEventListener(
                Objects.requireNonNull(getActivity()),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {

                            editText.setCursorVisible(true);

                        } else {
                            editText.setCursorVisible(false);
                        }
                    }
                });

        // call this method when you don't need the event listener anymore
        //unregistrar.unregister();
    }

    private void doNoteLogic() {

        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setCursorVisible(false);

        editText.setSingleLine(false);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mListener.fragmentGraphTextChangeListener(charSequence, recorderViewModel.getRecorder());

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (null != editText.getLayout() && editText.getLayout().getLineCount() > 4) {
                    editText.getText()
                            .delete(editText.getText().length() - 1, editText.getText().length());
                }
            }
        });

        recorderViewModel.subscribe().observe(Objects.requireNonNull(getActivity()), new Observer<Recorder>() {
            @Override
            public void onChanged(Recorder recorder) {

                System.err.println("В графе поймали");

                if (recorder != null) {

                    openNote();
                    setCardData(recorder);

                } else {
                    closeNote();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.fragmentGraphDeleteListener(recorderViewModel.getRecorder());
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.fragmentGraphEditListener(recorderViewModel.getRecorder());
            }
        });
    }

    private void doGraphLogic() {

        chart.setHorizontalScrollBarEnabled(true);

        dataViewModel.subscribeOnArrayListAllData().observe(getActivity(), new Observer<ArrayList<Recorder>>() {
            @Override
            public void onChanged(ArrayList<Recorder> arrayList) {

                updateCurrentArrayList(arrayList);
                updateGraph();
                recorderViewModel.setRecorder(null);

            }
        });
    }

    private void updateCurrentArrayList(ArrayList<Recorder> arrayList) {

        this.recorderArrayList = arrayList;
    }

    private void updateGraph() {

        setGraph(this.recorderArrayList);
    }

    private void setGraph(final ArrayList<Recorder> arrayList) {

        resetChart();
        dismissTooltips();

        if (arrayList.isEmpty()) {
            return;
        }

        chart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {

//              System.err.println(arrayList.get(entryIndex).toString() + "Пришло в вьюМодел");
                recorderViewModel.setRecorder(arrayList.get(entryIndex));
            }
        });

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart.dismissAllTooltips();
                recorderViewModel.setRecorder(null);
            }
        });

        chart.setArrayList(arrayList, getActivity());

        scrollDialogDown();
    }

    private void setCardData(Recorder recorder) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        Date date = new Date();
        date.setTime(recorder.getTime());

        Calendar calendarRecorder = Calendar.getInstance();
        calendarRecorder.setTime(date);

        String timeString = timeFormat.format(calendarRecorder.getTime());

        String isMedicine;

        if (recorder.getIsMedicine() == 0) {
            isMedicine = Objects.requireNonNull(getContext()).getString(R.string.without_medicine);
        } else {
            isMedicine = Objects.requireNonNull(getContext()).getString(R.string.after_medicine);
        }
        editText.setText(recorder.getNote());
        textTime.setText(timeString);
        textIsMedicine.setText(isMedicine);
    }

    private int widthBetweenDots;
    private int fullWidth;
    private int currentPoint;

    private void scrollDialogDown() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    try {
                        currentPoint = scrollX/widthBetweenDots;
                    }catch (Exception ignored){}
                }
            });
        }

        scrollView.post(new Runnable() {

            @Override
            public void run() {

                //scrollView.setSmoothScrollingEnabled(false);
                scrollView.fullScroll(ScrollView.FOCUS_RIGHT);
               // scrollView.setSmoothScrollingEnabled(true);
                //fullWidth = chart.getMeasuredWidth();
                //widthBetweenDots = (int)((double)(fullWidth)
                 //       / (double)(recorderArrayList.size() + 1));

            }
        });
    }

    private void openNote(){

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.note_enter);

        if (cardView.getVisibility() == View.INVISIBLE) {
            cardView.startAnimation(animation);
            cardView.setVisibility(View.VISIBLE);
        }
    }

    private  void closeNote(){

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.note_exit);

        if (cardView.getVisibility() == View.VISIBLE) {
            cardView.startAnimation(animation);
            cardView.setVisibility(View.INVISIBLE);
        }
    }

    public void resetChart() {
        chart.reset();
    }

    public void dismissTooltips() {
        chart.dismissAllTooltips();
    }


    public interface OnFragmentListener {

        void fragmentGraphDeleteListener(Recorder recorder);

        void fragmentGraphEditListener(Recorder recorder);

        void fragmentGraphTextChangeListener(CharSequence charSequence, Recorder recorder);
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