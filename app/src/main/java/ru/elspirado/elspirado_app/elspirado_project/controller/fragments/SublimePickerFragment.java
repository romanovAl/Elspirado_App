package ru.elspirado.elspirado_app.elspirado_project.controller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import ru.elspirado.elspirado_app.elspirado_project.R;

public class SublimePickerFragment extends DialogFragment {

    // Date & Time formatter used for formatting
    // text on the switcher button
    DateFormat mDateFormatter, mTimeFormatter;

    // Picker
    private SublimePicker mSublimePicker;

    // Callback to activity
    private Callback mCallback;

    SublimeListenerAdapter mListener = new SublimeListenerAdapter() {
        @Override
        public void onCancelled() {
            if (mCallback != null) {
                mCallback.onCancelled();
            }

            // Should actually be called by activity inside `Callback.onCancelled()`
            dismiss();
        }

        @Override
        public void onDateTimeRecurrenceSet(SublimePicker sublimeMaterialPicker,
            SelectedDate selectedDate,
            int hourOfDay, int minute,
            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
            String recurrenceRule) {
            if (mCallback != null) {
                mCallback.onDateTimeRecurrenceSet(selectedDate,
                    hourOfDay, minute, recurrenceOption, recurrenceRule);
            }

            // Should actually be called by activity inside `Callback.onCancelled()`
            dismiss();
        }
// You can also override 'formatDate(Date)' & 'formatTime(Date)'
        // to supply custom formatters.
    };

    public SublimePickerFragment() {
        // Initialize formatters
        mDateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        mTimeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        mTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }

    // Set activity callback
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        mSublimePicker = (SublimePicker) Objects.requireNonNull(getActivity())
            .getLayoutInflater().inflate(R.layout.sublime_picker, container);

        // Retrieve SublimeOptions
        Bundle arguments = getArguments();
        SublimeOptions options = null;

        // Options can be null, in which case, default
        // options are used.
        if (arguments != null) {
            options = arguments.getParcelable("SUBLIME_OPTIONS");
        }

        mSublimePicker.initializePicker(options, mListener);
        return mSublimePicker;
    }

    // For communicating with the activity
    public interface Callback {

        void onCancelled();

        void onDateTimeRecurrenceSet(SelectedDate selectedDate,
            int hourOfDay, int minute,
            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
            String recurrenceRule);
    }
}