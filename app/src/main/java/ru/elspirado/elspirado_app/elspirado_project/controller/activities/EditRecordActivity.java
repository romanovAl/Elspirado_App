package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.DBRecorder;
import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EditRecordActivity extends AddAndEditRecordActivity {

    private Recorder recorder, firstRecorder;

    private Date date;

    private BottomAppBar bar;
    private FloatingActionButton fab;

    @RequiresApi(api = VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Objects.requireNonNull(toolbar).setTitle(R.string.Edit_activity_title);

        recorder = (Recorder) getIntent().getSerializableExtra("objectRecorder");

        System.err.println(recorder.toString());

        firstRecorder = new Recorder
                (recorder.getId(), recorder.getValue(), recorder.getNote(), recorder.getTime(),
                        recorder.getIsMedicine());

        System.err.println(firstRecorder.toString());

        bar = findViewById(R.id.bottom_app_bar_add_and_edit);
        setSupportActionBar(bar);

        fab = findViewById(R.id.fab_add_and_edit);

        fab.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DBRecorder dbRecorder = new DBRecorder();
                        int value;
                        String note = noteEditText.getText().toString();
                        int isMedicine = 0;
                        View view = findViewById(R.id.add_and_edit_constraint);

                        if (checkBox.isChecked()) {
                            isMedicine = 1;
                        }

                        try {
                            value = Integer.valueOf(valueEditText.getText().toString());
                            if (valueEditText.getText().toString().isEmpty()) {
                                valueTextInputLayout
                                        .setError(getResources().getString(R.string.error_string));
                            }
                        } catch (NumberFormatException e) {

                            Snackbar snackbar = Snackbar
                                    .make(view, R.string.value_is_empty_error, Snackbar.LENGTH_SHORT);
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                                    snackbar.getView().getLayoutParams();

                            params.setMargins(8, 0, 8, bar.getHeight() + fab.getHeight() - 60);
                            snackbar.getView().setLayoutParams(params);

                            snackbar.show();
                            return;
                        }

                        try {
                            mySelectedDate.getFirstDate().getTime().setHours(myHours);
                            mySelectedDate.getFirstDate().getTime().setMinutes(myMinutes);
                            date.setTime(mySelectedDate.getFirstDate().getTime().getTime());
                            date.setHours(myHours);
                            date.setMinutes(myMinutes);


                        } catch (NullPointerException ignored) {
                        }

                        final Recorder editedRecorder = new Recorder(recorder.getId(), value, note,
                                date.getTime(), isMedicine);

                        dbRecorder.update(editedRecorder, new DBRecorder.CallbackUpdateListener() {
                            @Override
                            public void DBRecorderCallingBackUpdate() {
                                fab.setClickable(false);
                                saveAndExit(editedRecorder);
                            }
                        });
                    }
                });

        if (recorder.getIsMedicine() == 1) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        date = new Date();
        date.setTime(recorder.getTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        valueEditText.setText(String.valueOf(recorder.getValue()));
        dateEditText.setText(dateFormat.format(date));
        timeEditText.setText(timeFormat.format(date));

        valueEditText.setSelection(
                Integer.toString(recorder.getValue()).length()); //устанавливает курсор в конец et

        try {
            noteEditText.setText(recorder.getNote());
        } catch (NullPointerException ignored) {

        }

        valueEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getText().toString().isEmpty()) {
                    valueTextInputLayout.setError(getResources().getString(R.string.error_string));
                } else {
                    valueTextInputLayout.setError(null);
                }
                return false;
            }
        });

        bar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.setValue(Integer.valueOf(valueEditText.getText().toString()));
                recorder.setNote(noteEditText.getText().toString());
                if (checkBox.isChecked()) {
                    recorder.setIsMedicine(1);
                }
                try {
                    mySelectedDate.getFirstDate().getTime().setHours(myHours);
                    mySelectedDate.getFirstDate().getTime().setMinutes(myMinutes);
                    date.setTime(mySelectedDate.getFirstDate().getTime().getTime());
                    date.setHours(myHours);
                    date.setMinutes(myMinutes);
                } catch (NullPointerException ignored) {
                }
                recorder.setTime(date.getTime());
                if (valueEditText.getText().toString().isEmpty()) {
                    saveAndExit(recorder);
                } else {
                    if (firstRecorder.equals(recorder)) {
                        saveAndExit(recorder);
                    } else {
                        setAlertDialog();
                    }

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        recorder.setValue(Integer.valueOf(valueEditText.getText().toString()));
        recorder.setNote(noteEditText.getText().toString());
        if (checkBox.isChecked()) {
            recorder.setIsMedicine(1);
        }
        try {
            mySelectedDate.getFirstDate().getTime().setHours(myHours);
            mySelectedDate.getFirstDate().getTime().setMinutes(myMinutes);
            date.setTime(mySelectedDate.getFirstDate().getTime().getTime());
            date.setHours(myHours);
            date.setMinutes(myMinutes);
        } catch (NullPointerException ignored) {
        }
        recorder.setTime(date.getTime());
        if (valueEditText.getText().toString().isEmpty()) {
            saveAndExit(recorder);
        } else {
            if (firstRecorder.equals(recorder)) {
                saveAndExit(recorder);
            } else {
                setAlertDialog();
            }

        }
    }

    @Override
    void setAlertDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(EditRecordActivity.this);
        ad.setTitle(R.string.ad_save_question);
        ad.setPositiveButton(R.string.ad_positive_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DBRecorder dbRecorder = new DBRecorder();
                int value;
                String note = noteEditText.getText().toString();
                int isMedicine = 0;
                View view = findViewById(R.id.add_and_edit_constraint);

                if (checkBox.isChecked()) {
                    isMedicine = 1;
                }

                try {
                    value = Integer.valueOf(valueEditText.getText().toString());
                    if (valueEditText.getText().toString().isEmpty()) {
                        valueTextInputLayout
                                .setError(getResources().getString(R.string.error_string));
                    }
                } catch (NumberFormatException e) {

                    Snackbar snackbar = Snackbar
                            .make(view, R.string.value_is_empty_error, Snackbar.LENGTH_SHORT);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                            snackbar.getView().getLayoutParams();

                    params.setMargins(8, 0, 8, bar.getHeight() + fab.getHeight() - 60);
                    snackbar.getView().setLayoutParams(params);

                    snackbar.show();
                    return;
                }

                try {
                    mySelectedDate.getFirstDate().getTime().setHours(myHours);
                    mySelectedDate.getFirstDate().getTime().setMinutes(myMinutes);
                    date.setTime(mySelectedDate.getFirstDate().getTime().getTime());
                    date.setHours(myHours);
                    date.setMinutes(myMinutes);


                } catch (NullPointerException ignored) {
                }

                final Recorder editedRecorder = new Recorder(recorder.getId(), value, note,
                        date.getTime(), isMedicine);

                dbRecorder.update(editedRecorder, new DBRecorder.CallbackUpdateListener() {
                    @Override
                    public void DBRecorderCallingBackUpdate() {
                        fab.setClickable(false);
                        saveAndExit(editedRecorder);
                    }
                });
            }
        });
        ad.setNegativeButton(R.string.ad_negative_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        ad.setCancelable(true);
        ad.show();
    }

    private void saveAndExit(Recorder editedRecorder){
        Intent intent = new Intent();
        intent.putExtra("editedRecorder", editedRecorder);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_record_activ_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}