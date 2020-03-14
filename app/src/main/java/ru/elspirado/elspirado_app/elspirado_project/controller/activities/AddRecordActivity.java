package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES_IS_ENTERED;

import android.content.SharedPreferences;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.DBRecorder;
import shortbread.Shortcut;

@Shortcut(id = "addRecord", icon = R.drawable.ic_add_circle_outline_primary_light_24dp, shortLabel = "Добавить запись")
public class AddRecordActivity extends AddAndEditRecordActivity {

    @RequiresApi(api = VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);//Если пользователь не вошел, он не сможет открыть шорткат
        if (!mSettings.getBoolean(APP_PREFERENCES_IS_ENTERED, false)) {
            Toast.makeText(AddRecordActivity.this, R.string.you_are_not_signed_up, Toast.LENGTH_SHORT).show();
            finish();
        }

        super.onCreate(savedInstanceState);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        Toolbar toolbar = findViewById(R.id.toolbar);
        Objects.requireNonNull(toolbar).setTitle(R.string.Add_activity_title);

        Date date = new Date();
        date.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String timeString = timeFormat.format(calendar.getTime());
        String dateString = dateFormat.format(calendar.getTime());

        dateEditText.setText(dateString);
        timeEditText.setText(timeString);

        final BottomAppBar bar = findViewById(R.id.bottom_app_bar_add_and_edit);
        setSupportActionBar(bar);

        bar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valueEditText.getText().toString().isEmpty()) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    setAlertDialog();
                }

            }
        });

        final FloatingActionButton fab = findViewById(R.id.fab_add_and_edit);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int value;
                String note = noteEditText.getText().toString();
                Date date = new Date();
                int isMedicine = 0;

                try {
                    value = Integer.valueOf(valueEditText.getText().toString());
                    if (valueEditText.getText().toString().isEmpty()) {
                        valueTextInputLayout
                                .setError(getResources().getString(R.string.error_string));
                    }
                } catch (NumberFormatException e) {

                    View view = findViewById(R.id.add_and_edit_constraint);

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

                if (checkBox.isChecked()) {
                    isMedicine = 1;
                }

                DBRecorder dbRecorder = new DBRecorder();
                dbRecorder.insert(value, note, date.getTime(), isMedicine, new DBRecorder.CallbackInsertListener() {
                    @Override
                    public void DBRecorderCallingBackInsert() {
                        fab.setClickable(false);

                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_record_activ_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}