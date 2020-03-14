package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialog;
import ru.elspirado.elspirado_app.elspirado_project.model.DBRecorder;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.SublimePickerFragment;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public abstract class AddAndEditRecordActivity extends AppCompatActivity {

    EditText
        valueEditText, noteEditText,
        dateEditText, timeEditText;

    CheckBox checkBox;

    TextInputLayout valueTextInputLayout, noteTextInputLayout;

    SublimePickerFragment pickerFragDate;

    SublimePickerFragment pickerFragTime;

    SelectedDate mySelectedDate, mySelectedTime;
    int myHours, myMinutes;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_edit_record);

        initUI();

        noteEditText.setSingleLine(false);
        noteEditText.addTextChangedListener(
            new TextWatcher() { //Настраивает ограничение в 4 строки в заметке
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (null != noteEditText.getLayout()
                        && noteEditText.getLayout().getLineCount() > 4) {
                        noteEditText.getText().delete(noteEditText.getText().length() - 1,
                            noteEditText.getText().length());
                    }

                }
            });

        valueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (valueEditText.getText().toString().isEmpty()) {
                    valueTextInputLayout.setError(getResources().getString(R.string.error_string));
                } else {
                    valueTextInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pickerFragDate == null){
                    pickerFragDate = new SublimePickerFragment();
                }

                if(pickerFragDate.isVisible()){
                    return;
                }

                pickerFragDate.setCallback(dateCallback);

                // Options
                Pair<Boolean, SublimeOptions> optionsPair = getDateOptions();

                // Valid options
                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
                pickerFragDate.setArguments(bundle);

                pickerFragDate.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFragDate.show(getSupportFragmentManager(), "SUBLIME_PICKER");
            }
        });

        timeEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pickerFragTime == null){
                    pickerFragTime = new SublimePickerFragment();
                }

                if(pickerFragTime.isVisible()){
                    return;
                }

                pickerFragTime.setCallback(timeCallback);

                // Options
                Pair<Boolean, SublimeOptions> optionsPair = getTimeOptions();

                // Valid options
                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
                pickerFragTime.setArguments(bundle);

                pickerFragTime.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFragTime.show(getSupportFragmentManager(), "SUBLIME_PICKER");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        KeyboardVisibilityEvent.registerEventListener(
            this,
            new KeyboardVisibilityEventListener() {
                @Override
                public void onVisibilityChanged(boolean isOpen) {
                    if (isOpen) {
                        noteEditText.setCursorVisible(true);

                    } else {
                        noteEditText.setCursorVisible(false);
                        noteTextInputLayout
                            .setHint(getResources().getString(R.string.tap_to_add_note));
                    }
                }
            });
    }

    @Override
    public void onBackPressed() {
        if (valueEditText.getText().toString().isEmpty()) {
            setResult(RESULT_OK);
            finish();
        } else {
            setAlertDialog();
        }
    }

    void setAlertDialog() {

        CustomAlertDialog customAlertDialog = new CustomAlertDialog(AddAndEditRecordActivity.this,
                R.string.ad_positive_save, R.string.ad_negative_cancel);

        customAlertDialog.setTitle(R.string.ad_save_question);
        customAlertDialog.setCancelable(true);

        customAlertDialog.setPositiveButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int value;

                try {
                    value = Integer.valueOf(valueEditText.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(AddAndEditRecordActivity.this, "Value is empty!",
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                String note = noteEditText.getText().toString();

                Date date = new Date();
                try {
                    mySelectedDate.getFirstDate().getTime().setHours(myHours);
                    mySelectedDate.getFirstDate().getTime().setMinutes(myMinutes);
                    date.setTime(mySelectedDate.getFirstDate().getTime().getTime());
                    date.setHours(myHours);
                    date.setMinutes(myMinutes);
                } catch (NullPointerException ignored) {
                }

                int isMedicine = 0;

                if (checkBox.isChecked()) {
                    isMedicine = 1;
                }

                DBRecorder dbRecorder = new DBRecorder();
                dbRecorder.insert(value, note, date.getTime(), isMedicine, new DBRecorder.CallbackInsertListener() {
                    @Override
                    public void DBRecorderCallingBackInsert() {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });

        customAlertDialog.setNegativeButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        customAlertDialog.show();
    }

    //Метод для настроек календаря
    Pair<Boolean, SublimeOptions> getDateOptions() {
        SublimeOptions dateOptions = new SublimeOptions();

        int displayDateOptions = 0;

        displayDateOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        displayDateOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;

        dateOptions.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);

        dateOptions.setDisplayOptions(displayDateOptions);

        dateOptions.setCanPickDateRange(false);

        dateOptions.setDateRange(Long.MIN_VALUE, new Date().getTime());

        return new Pair<>(displayDateOptions != 0 ? Boolean.TRUE : Boolean.FALSE, dateOptions);
    }

    Pair<Boolean, SublimeOptions> getTimeOptions() {
        SublimeOptions dateOptions = new SublimeOptions();

        int displayDateOptions = 0;

        displayDateOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
        displayDateOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;

        dateOptions.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);

        dateOptions.setDisplayOptions(displayDateOptions);

        dateOptions.setCanPickDateRange(false);

        dateOptions.setDateRange(Long.MIN_VALUE, new Date().getTime());

        return new Pair<>(displayDateOptions != 0 ? Boolean.TRUE : Boolean.FALSE, dateOptions);
    }


    //Срабатывает при закрытии календаря
    SublimePickerFragment.Callback dateCallback = new SublimePickerFragment.Callback() {
        @Override
        public void onCancelled() {

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate,

            int hourOfDay, int minute,
            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
            String recurrenceRule) {

            mySelectedDate = selectedDate;

            Date date = new Date();
            date.setHours(hourOfDay);
            date.setMinutes(minute);

            myHours = hourOfDay;
            myMinutes = minute;

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy ",
                Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedDate.getFirstDate().getTime());

            dateEditText.setText(dateFormat.format(calendar.getTime()));
            if (mySelectedTime == null){
                timeEditText.setText(timeFormat.format(date));
            }


        }
    };

    //Срабатывает при закрытии календаря
    SublimePickerFragment.Callback timeCallback = new SublimePickerFragment.Callback() {
        @Override
        public void onCancelled() {

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate,

            int hourOfDay, int minute,
            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
            String recurrenceRule) {

            mySelectedTime = selectedDate;

            Date date = new Date();
            date.setHours(hourOfDay);
            date.setMinutes(minute);

            myHours = hourOfDay;
            myMinutes = minute;

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy ",
                Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedDate.getFirstDate().getTime());

            if (mySelectedDate == null){
                dateEditText.setText(dateFormat.format(calendar.getTime()));
            }
            timeEditText.setText(timeFormat.format(date));

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initUI() {
        valueEditText = findViewById(R.id.valueEditText);
        noteEditText = findViewById(R.id.noteEditText);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        valueTextInputLayout = findViewById(R.id.valueTextInputLayout);
        noteTextInputLayout = findViewById(R.id.noteTextInputLayout);
        checkBox = findViewById(R.id.checkBox);
    }
}