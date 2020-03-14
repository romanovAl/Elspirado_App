package ru.elspirado.elspirado_app.elspirado_project.model.customDialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import ru.elspirado.elspirado_app.elspirado_project.R;

public class CustomAlertDialogSelectTimeRange extends AlertDialog {

    private Button buttonNegative, buttonPositive;

    private EditText editTextDate;

    private CheckBox checkBoxAllRecords;

    private long fromTime, toTime;

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    public CustomAlertDialogSelectTimeRange(@NonNull Context context) {
        super(context);

        @SuppressLint("InflateParams")
        View customAlertDialogView = getLayoutInflater()
            .inflate(R.layout.fragment_custom_alert_dialog_time_range, null);
        setTitle(context.getResources().getString(R.string.select_time_range));

        buttonNegative = customAlertDialogView.findViewById(R.id.button_negative);
        buttonPositive = customAlertDialogView.findViewById(R.id.button_positive);

        editTextDate = customAlertDialogView.findViewById(R.id.alert_dialog_edit_text_time_range);

        checkBoxAllRecords = customAlertDialogView.findViewById(R.id.checkBox_all_records);

        checkBoxAllRecords.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editTextDate.setText("Выбраны все даты"); //TODO стринги тут и в layout
                    buttonPositive.setEnabled(true);
                } else {
                    buttonPositive.setEnabled(false);
                    editTextDate.setText("Выберите диапазон, чтобы продолжить");//TODO стринги
                }
            }
        });

        super.setView(customAlertDialogView);
    }


    public void setPositiveButtonClickListener(View.OnClickListener onClickListener) {
        buttonPositive.setOnClickListener(onClickListener);
    }

    public void setNegativeButtonClickListener(View.OnClickListener onClickListener) {
        buttonNegative.setOnClickListener(onClickListener);
    }

    public void setEditTextDateClickListener(View.OnClickListener onClickListener) {
        editTextDate.setOnClickListener(onClickListener);
    }

    public void setCheckBoxAllRecords(boolean isChecked) {
        checkBoxAllRecords.setChecked(isChecked);
    }

    public boolean isAllRecords() {
        return checkBoxAllRecords.isChecked();
    }

    public void setEditTextDate() {

        buttonPositive.setEnabled(true);

        Date dateFirst = new Date();
        dateFirst.setTime(fromTime);


        Date dateSecond = new Date();
        dateSecond.setTime(toTime);

        if (dateFirst.getTime() != dateSecond.getTime()){
            dateFirst.setHours(0);
            dateSecond.setHours(24);
        }


        java.text.SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yy",
            Locale.getDefault());

        if (fromTime == toTime){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            editTextDate.setText(dateFormat.format(dateFirst));
        }else {
            String timeRange = timeFormat.format(fromTime)
                + " - "
                + timeFormat.format(toTime);
            editTextDate.setText(timeRange);
        }
    }

}