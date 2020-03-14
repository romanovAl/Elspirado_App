package ru.elspirado.elspirado_app.elspirado_project.controller.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.RecorderViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class FragmentNote extends Fragment {

    private EditText editText;
    private TextView timeTextView, isMedicineTextView;
    private Recorder recorder;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tip_et, container, false);

        setRetainInstance(true);

        editText = view.findViewById(R.id.fragment_tip_et);
        timeTextView = view.findViewById(R.id.text_view_time);
        isMedicineTextView = view.findViewById(R.id.text_view_is_medicine);

        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setCursorVisible(false);

        editText.setSingleLine(false);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mListener.fragmentNoteChangeListener(charSequence, recorder);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (null != editText.getLayout() && editText.getLayout().getLineCount() > 4) {
                    editText.getText()
                        .delete(editText.getText().length() - 1, editText.getText().length());
                }
            }
        });

        ImageButton buttonDelete = view.findViewById(R.id.btn_delete);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.fragmentNoteDeleteListener(recorder);
            }
        });

        ImageButton buttonEdit = view.findViewById(R.id.btn_edit);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.fragmentNoteEditButtonClickListener(recorder);

            }
        });

        RecorderViewModel recorderViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                .get(RecorderViewModel.class);

        recorderViewModel.subscribe().observe(getActivity(), new Observer<Recorder>() {
            @Override
            public void onChanged(Recorder recorder) {

                if (recorder != null) {

                    setRecorder(recorder);
                    setTextView();

                    mListener.fragmentNoteSetRecorderListener(recorder);

                } else {
                    mListener.fragmentNoteHideListener();
                }
            }
        });

        return view;
    }

    private void setRecorder(Recorder recorder){
        this.recorder = recorder;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

    private void setTextView() {

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
        timeTextView.setText(timeString);
        isMedicineTextView.setText(isMedicine);
    }

    public interface OnFragmentListener {

        void fragmentNoteChangeListener(CharSequence charSequence, Recorder recorder);

        void fragmentNoteHideListener();

        void fragmentNoteSetRecorderListener(Recorder recorder);

        void fragmentNoteDeleteListener(Recorder recorder);

        void fragmentNoteEditButtonClickListener(Recorder recorder);
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