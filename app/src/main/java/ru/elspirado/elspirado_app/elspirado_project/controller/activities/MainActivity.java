package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.FRAGMENT_MODE_GRAPH;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.ACTIVITY_MODE_RANGE_All;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.ACTIVITY_MODE_RANGE_SELECTED;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.FRAGMENT_MODE_RECYCLER;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.FRAGMENT_MODE_RECYCLER_SELECTED;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES_IS_ENTERED;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker.RecurrenceOption;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.FragmentGraph;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.FragmentNote;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.FragmentRecyclerView;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.SublimePickerFragment;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.SublimePickerFragment.Callback;
import ru.elspirado.elspirado_app.elspirado_project.model.ActivityMode;
import ru.elspirado.elspirado_app.elspirado_project.model.NotificationPublisher;
import ru.elspirado.elspirado_app.elspirado_project.model.TimeRange;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialog;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialogSelectTimeRange;
import ru.elspirado.elspirado_app.elspirado_project.model.CustomBottomSheet;
import ru.elspirado.elspirado_app.elspirado_project.model.CustomBottomSheet.CustomBottomSheetInterface;
import ru.elspirado.elspirado_app.elspirado_project.model.CustomSwipeToRefresh;
import ru.elspirado.elspirado_app.elspirado_project.model.DBRecorder;
import ru.elspirado.elspirado_app.elspirado_project.model.ExcelWriter;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.ActivityModeViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.DataViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.RecorderViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.RecyclerViewAdapter;

public class MainActivity extends AppCompatActivity implements FragmentGraph.OnFragmentListener,
        FragmentNote.OnFragmentListener, CustomBottomSheetInterface, RecyclerViewAdapter.OnFragmentListener {

    private DBRecorder dataBase;

    private FragmentManager fragmentManager;
    private FragmentNote fragmentNote;
    private FragmentGraph fragmentGraph;
    private FragmentRecyclerView fragmentRecyclerView;
    private FloatingActionButton fab;

    protected Toolbar toolbar;
    private BottomAppBar bottomAppBar;
    private CustomBottomSheet customBottomSheet;

    private SublimePickerFragment pickerFrag;

    private SublimePickerFragment alertDialogPickerFrag;

    private View view;

    private RecorderViewModel recorderViewModel;
    private ActivityModeViewModel activityMode;
    private DataViewModel dataViewModel;

    private Intent settingsIntent, addRecordIntent, editIntent, intentShareFile;

    //================Всё, что напрямую связано с MainActivity и её жизненным циклом================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        if (!mSettings.getBoolean(APP_PREFERENCES_IS_ENTERED, false)) {
            Intent intent = new Intent(MainActivity.this, RegAndAuthActivity.class);
            startActivityForResult(intent, 2);
        } else {
            initUI();
        }
    }

    private void initUI() {

        //Объявление UI, установка слушателей
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_activity_toolbar_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId() == R.id.action_select_all){
                    fragmentRecyclerView.selectAllElementsInRecyclerView();
                }
                return false;
            }
        });

        view = findViewById(R.id.mainActivityLayout);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(fabClickListener);

        bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);

        //Объекты для работы с данными
        dataBase = new DBRecorder();

        createNotificationChannel();

        recorderViewModel = ViewModelProviders.of(this).get(RecorderViewModel.class);
        activityMode = ViewModelProviders.of(this).get(ActivityModeViewModel.class);
        dataViewModel = ViewModelProviders.of(this).get(DataViewModel.class);

        dataViewModel.updateArrayListAllData();

        //Установка фрагментов

        fragmentManager = getSupportFragmentManager();

        fragmentNote = (FragmentNote) fragmentManager.findFragmentByTag("fragmentNote");
        fragmentGraph = (FragmentGraph) fragmentManager.findFragmentByTag("fragmentGraph");
        fragmentRecyclerView = (FragmentRecyclerView) fragmentManager.findFragmentByTag("fragmentRecyclerView");

        if (fragmentNote == null) {
            fragmentNote = new FragmentNote();

            fragmentManager
                    .beginTransaction()
                    .add(R.id.tip_container, fragmentNote, "fragmentNote")
                    .commit();

            fragmentManager
                    .beginTransaction()
                    .hide(fragmentNote)
                    .commit();
        }

        if (fragmentGraph == null) {
            fragmentGraph = new FragmentGraph();

            fragmentManager
                    .beginTransaction()
                    .add(R.id.graph_container, fragmentGraph, "fragmentGraph")
                    .commit();
        }

        if (fragmentRecyclerView == null) {
            fragmentRecyclerView = new FragmentRecyclerView();

            fragmentManager
                    .beginTransaction()
                    .add(R.id.recycler_view_container, fragmentRecyclerView, "fragmentRecyclerView")
                    .commit();

            fragmentManager
                    .beginTransaction()
                    .hide(fragmentRecyclerView)
                    .commit();
        }

        //Всё остальное

        final CustomSwipeToRefresh customSwipeToRefresh = findViewById(R.id.swipeRefreshLayout);
        customSwipeToRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (hasConnection(getApplicationContext()) && FirebaseAuth.getInstance().getCurrentUser() != null) {

                            if(activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_All){
                                dataViewModel.updateArrayListAllData();
                            }

                            customSwipeToRefresh.setRefreshing(false);
                        } else {
                            if (FirebaseAuth.getInstance().getCurrentUser() == null) {

                                if(activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_All){
                                    dataViewModel.updateArrayListAllData();
                                }

                                Toast.makeText(getApplicationContext(), R.string.you_are_not_signed_up, Toast.LENGTH_SHORT).show();
                            } else {

                                if(activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_All){
                                    dataViewModel.updateArrayListAllData();
                                }
                                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }, 1000);
            }
        });

        customSwipeToRefresh.setColorSchemeResources(R.color.colorPrimary);

        bottomAppBar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                customBottomSheet = new CustomBottomSheet();
                customBottomSheet.show(fragmentManager, "customBottomSheet");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(activityMode == null)
            return;

        activityMode.subscribe().observe(this, new Observer<ActivityMode>() {

            @Override
            public void onChanged(ActivityMode activityMode) {

                switch (activityMode.getTimeRangeMode()){

                    case ACTIVITY_MODE_RANGE_All:

                        Objects.requireNonNull(toolbar).setTitle(R.string.Main_activity_title);

                        fab.setImageResource(R.drawable.ic_add_white_24dp);

                        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);

                        bottomAppBar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

                        break;

                    case ACTIVITY_MODE_RANGE_SELECTED:

                        Objects.requireNonNull(toolbar).setTitle(dataViewModel.getTimeRangeString());

                        fab.setImageResource(R.drawable.ic_undo_white_24dp);

                        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);

                        bottomAppBar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

                        break;
                }

                switch (activityMode.getFragmentMode()){

                    case FRAGMENT_MODE_GRAPH:

                        if(toolbar.getMenu().findItem(R.id.action_select_all).isVisible()){

                            toolbar.getMenu().findItem(R.id.action_select_all).setVisible(false);
                        }

                        resetChart()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CompletableObserver() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onComplete() {

                                        setChartMenu();
                                        fragmentGraph.setGraph();
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                });
                        break;

                    case FRAGMENT_MODE_RECYCLER:

                        if(toolbar.getMenu().findItem(R.id.action_select_all).isVisible()){

                            toolbar.getMenu().findItem(R.id.action_select_all).setVisible(false);
                        }

                        if(fragmentGraph.isVisible()){

                            fragmentManager
                                    .beginTransaction()
                                    .hide(fragmentGraph)
                                    .commit();
                        }

                        if(fragmentNote.isVisible()){

                            fragmentManager
                                    .beginTransaction()
                                    .hide(fragmentNote)
                                    .commit();
                        }

                        fragmentManager
                                .beginTransaction()
                                .show(fragmentRecyclerView)
                                .commit();

                        setRecyclerMenu();

                        break;

                    case FRAGMENT_MODE_RECYCLER_SELECTED:

                        toolbar.getMenu().findItem(R.id.action_select_all).setVisible(true);

                        if(activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_All){

                            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);

                            fab.setImageResource(R.drawable.ic_undo_white_24dp);
                        }

                        bottomAppBar.setNavigationIcon(null);

                        setRecyclerBackMenu();

                        break;
                }
            }
        });

    }

    private Completable resetChart() {

        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) {

                if(fragmentRecyclerView.isVisible()){

                    fragmentManager
                            .beginTransaction()
                            .hide(fragmentRecyclerView)
                            .commit();
                }

                fragmentManager
                        .beginTransaction()
                        .show(fragmentGraph)
                        .commit();

                emitter.onComplete();
            }
        });
    }

    @Override
    protected void onPause() {
        if (pickerFrag != null && pickerFrag.isVisible()) {
            pickerFrag.dismiss();
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        settingsIntent = null;
        addRecordIntent = null;
        editIntent = null;
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){

            if(activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_All){
                dataViewModel.updateArrayListAllData();

                return;
            }

            if(activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_SELECTED){

                Recorder recorder = (Recorder) Objects.requireNonNull(data).getSerializableExtra("editedRecorder");

                if(recorder == null)
                    return;

                dataViewModel.updateRecorderInSelectedData(recorder);

                return;
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //Слушатель Floating Action Button
    private View.OnClickListener fabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_SELECTED) {

                if (activityMode.getFragmentMode() == FRAGMENT_MODE_GRAPH) {

                    activityMode.setTimeRangeMode(ACTIVITY_MODE_RANGE_All);

                    dataViewModel.updateArrayListAllData();

                }

                if (activityMode.getFragmentMode() == FRAGMENT_MODE_RECYCLER) {

                    activityMode.setTimeRangeMode(ACTIVITY_MODE_RANGE_All);

                    dataViewModel.updateArrayListAllData();
                }

                if (activityMode.getFragmentMode() == FRAGMENT_MODE_RECYCLER_SELECTED) {

                    fragmentRecyclerView.cleanSelectionInRecyclerView();

                    activityMode.setFragmentMode(FRAGMENT_MODE_RECYCLER);
                }

                return;
            }

            if (activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_All) {

                if (activityMode.getFragmentMode() == FRAGMENT_MODE_GRAPH) {

                    if (addRecordIntent == null) {

                        fragmentManager
                                .beginTransaction()
                                .hide(fragmentNote)
                                .commit();

                        addRecordIntent = new Intent(MainActivity.this, AddRecordActivity.class);
                        startActivityForResult(addRecordIntent, 1);
                    }
                }

                if (activityMode.getFragmentMode() == FRAGMENT_MODE_RECYCLER) {

                    addRecordIntent = new Intent(MainActivity.this, AddRecordActivity.class);

                    startActivityForResult(addRecordIntent, 1);
                }

                if (activityMode.getFragmentMode() == FRAGMENT_MODE_RECYCLER_SELECTED) {

                    fragmentRecyclerView.cleanSelectionInRecyclerView();

                    activityMode.setFragmentMode(FRAGMENT_MODE_RECYCLER);
                }
            }
        }
    };

    //Обработка нажатий на аппаратную кнопку "назад"
    @Override
    public void onBackPressed() {

        if (activityMode.getTimeRangeMode() != ACTIVITY_MODE_RANGE_SELECTED) {
            super.onBackPressed();
        }

        switch (activityMode.getFragmentMode()){

            case FRAGMENT_MODE_GRAPH:

            case FRAGMENT_MODE_RECYCLER:

                activityMode.setTimeRangeMode(ACTIVITY_MODE_RANGE_All);

                dataViewModel.updateArrayListAllData();

                break;

            case FRAGMENT_MODE_RECYCLER_SELECTED:

                activityMode.setFragmentMode(FRAGMENT_MODE_RECYCLER);

                fragmentRecyclerView.cleanSelectionInRecyclerView();

                break;
        }
    }

    //Установка меню, и его обработка

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (activityMode.getFragmentMode() == FRAGMENT_MODE_GRAPH) {

            getMenuInflater().inflate(R.menu.main_menu_graph, menu);

            return true;
        }

        if (activityMode.getFragmentMode() == FRAGMENT_MODE_RECYCLER) {

            getMenuInflater().inflate(R.menu.main_menu_recycler, menu);

            return true;
        }

        if (activityMode.getFragmentMode() == FRAGMENT_MODE_RECYCLER_SELECTED) {

            getMenuInflater().inflate(R.menu.main_menu_recycler_back, menu);

            bottomAppBar.setNavigationIcon(null);
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            fab.setImageResource(R.drawable.ic_undo_white_24dp);

            return true;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Обработка нажатий в меню

        if (activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_SELECTED) {

            if (item.getItemId() == R.id.action_show_list) {
                recorderViewModel.setRecorder(null);
                activityMode.setFragmentMode(FRAGMENT_MODE_RECYCLER);

            }

            if (item.getItemId() == R.id.action_show_chart) {
                activityMode.setFragmentMode(FRAGMENT_MODE_GRAPH);
            }
        }

        if (activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_All) {

            if (item.getItemId() == R.id.action_show_list) {

                recorderViewModel.setRecorder(null);
                activityMode.setFragmentMode(FRAGMENT_MODE_RECYCLER);
            }

            if (item.getItemId() == R.id.action_show_chart) {

                activityMode.setFragmentMode(FRAGMENT_MODE_GRAPH);

            }
        }

        if (item.getItemId() == R.id.action_delete) {

            final CustomAlertDialog customAlertDialog = new CustomAlertDialog(MainActivity.this,
                    R.string.ad_positive_delete, R.string.ad_negative_cancel);

            int itemCount = fragmentRecyclerView.getRecyclerViewSelectedItemCount();

            if(itemCount == 1){
                customAlertDialog.setMessage(R.string.ad_delete_question);
            } else {
                customAlertDialog.setMessage("Удалить эти (" + itemCount + ") записи?"); //todo стринги
            }

            customAlertDialog.setTitle(R.string.ad_delete_title);
            customAlertDialog.setCancelable(true);

            customAlertDialog.setPositiveButtonClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentRecyclerView.deleteElementInRecyclerView();
                    customAlertDialog.dismiss();
                }
            });

            customAlertDialog.setNegativeButtonClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    customAlertDialog.dismiss();
                }
            });

            customAlertDialog.show();
        }

        if(item.getItemId() == R.id.action_share){
            fragmentRecyclerView.shareElementsInRecyclerView();
        }

        if (item.getItemId() == R.id.action_calendar) {

            if (pickerFrag == null) {
                pickerFrag = new SublimePickerFragment();
            }

            if (pickerFrag.isVisible()) {
                return true;
            }

            pickerFrag.setCallback(mFragmentCallback);

            // Options
            Pair<Boolean, SublimeOptions> optionsPair = getOptions();

            // Valid options
            Bundle bundle = new Bundle();
            bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
            pickerFrag.setArguments(bundle);

            pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

            pickerFrag.show(fragmentManager, "SUBLIME_PICKER");
        }

        return true;
    }

    //===============================Всё, что связано с FragmentGraph===============================

    @Override
    public void fragmentGraphTapOnChartListener() {
        fragmentGraph.hideCustomTextViewYearText();
        recorderViewModel.setRecorder(null);
    }

    @Override
    public void fragmentGraphNoGraph() {
        fragmentManager.beginTransaction().remove(fragmentNote).commit();
    }

    //===============================Всё, что связано с FragmentNote===============================

    @Override
    public void fragmentNoteChangeListener(CharSequence charSequence, Recorder recorder) {

        if(recorder == null){
            return;
        }

        recorder.setNote(charSequence.toString());

        new DBRecorder().update(recorder, new DBRecorder.CallbackUpdateListener() {
            @Override
            public void DBRecorderCallingBackUpdate() {

            }
        });
    }

    private Recorder deletedRecorder;

    @Override
    public void fragmentNoteDeleteListener(final Recorder recorder) {

        deleteRecorderWithToastMessage(recorder);
    }

    @Override
    public void fragmentNoteEditButtonClickListener(Recorder recorder) {

        if (editIntent == null) {

            fragmentManager
                    .beginTransaction()
                    .hide(fragmentNote)
                    .commit();

            editIntent = new Intent(getApplicationContext(), EditRecordActivity.class);
            editIntent.putExtra("objectRecorder", recorder);

            startActivityForResult(editIntent, 1);
        }

    }

    @Override
    public void fragmentNoteHideListener() {

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.note_exit, R.anim.note_exit)
                .hide(fragmentNote)
                .commit();
    }

    @Override
    public void fragmentNoteSetRecorderListener(Recorder recorder) {

        if (recorder != null) {

            Date recorderDate = new Date();
            recorderDate.setTime(recorder.getTime());

            Calendar calendarRecorder = Calendar.getInstance();
            calendarRecorder.setTime(recorderDate);

            Calendar calendarCurrentYear = Calendar.getInstance();
            calendarCurrentYear.setTime(new Date());

            if (calendarRecorder.get(Calendar.YEAR) < calendarCurrentYear.get(Calendar.YEAR)) {
                String timeString;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                timeString = dateFormat.format(calendarRecorder.getTime());

                fragmentGraph.setCustomTextViewYearText(timeString);
            } else {
                fragmentGraph.hideCustomTextViewYearText();
            }
        }

        if (fragmentNote.isVisible()) {
            fragmentManager.beginTransaction()
                    .show(fragmentNote)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.note_enter, R.anim.note_enter)
                    .show(fragmentNote)
                    .commit();
        }
    }

    //===============================Всё, что связано с RecyclerView================================

    @Override
    public void recyclerViewAdapterSelectedElementsListener(int size) {

        if (size > 0) {

            if (activityMode.getFragmentMode() != FRAGMENT_MODE_RECYCLER_SELECTED) {

                activityMode.setFragmentMode(FRAGMENT_MODE_RECYCLER_SELECTED);
            }

            Objects.requireNonNull(toolbar).setTitle("Выбрано " + size);//todo стринги

        } else {
            activityMode.setFragmentMode(FRAGMENT_MODE_RECYCLER);
        }

    }

    @Override
    public void recyclerViewAdapterDeleteListener(final ArrayList<Recorder> deletedArrayList) {

        ActivityMode mode = activityMode.getMode();

        if(mode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_All){

            DBRecorder dbRecorder = new DBRecorder();

            dbRecorder.deleteArrayList(deletedArrayList, new DBRecorder.CallbackDeleteArrayListListener() {
                @Override
                public void DBRecorderCallingBackDeleteArrayList() {
                    fragmentRecyclerView.cleanSelectionInRecyclerView();
                    dataViewModel.updateArrayListAllData();
                    activityMode.setFragmentMode(FRAGMENT_MODE_RECYCLER);
                }
            });
        }

        if(mode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_SELECTED){

            DBRecorder dbRecorder = new DBRecorder();

            dbRecorder.deleteArrayList(deletedArrayList, new DBRecorder.CallbackDeleteArrayListListener() {
                @Override
                public void DBRecorderCallingBackDeleteArrayList() {
                    dataViewModel.deleteArrayListInSelectedData(deletedArrayList);
                }
            });
        }
    }

    @Override
    public void recyclerViewAdapterShareListener(ArrayList<Recorder> arrayList) {

        new ExcelWriter().saveExcelFile(getApplicationContext(), getString(R.string.excel_file_name), arrayList, new ExcelWriter.Callback() {
            @Override
            public void excelWriterCallingBack(boolean success, String filePath) {
                fragmentRecyclerView.cleanSelectionInRecyclerView();
                shareResult(filePath);
            }
        });
    }

    @Override
    public void recyclerViewAdapterDeleteOneElementListener(Recorder recorder) {
        deleteRecorderWithToastMessage(recorder);
    }

    @Override
    public void recyclerViewAdapterEditElementListener(Recorder recorder) {

        if (editIntent == null) {

            fragmentManager
                    .beginTransaction()
                    .hide(fragmentNote)
                    .commit();

            editIntent = new Intent(getApplicationContext(), EditRecordActivity.class);
            editIntent.putExtra("objectRecorder", recorder);

            startActivityForResult(editIntent, 1);
        }
    }

    //================================Всё, что связано с Календарём=================================

    //Метод для настроек календаря
    private Pair<Boolean, SublimeOptions> getOptions() {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;

        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;

        options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);

        options.setDisplayOptions(displayOptions);

        options.setCanPickDateRange(true);

        options.setDateRange(Long.MIN_VALUE, new Date().getTime());

        return new Pair<>(Boolean.TRUE, options);
    }

    //Срабатывает при закрытии календаря
    SublimePickerFragment.Callback mFragmentCallback = new SublimePickerFragment.Callback() {

        @Override
        public void onCancelled() {
            fab.setImageResource(R.drawable.ic_add_white_24dp);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onDateTimeRecurrenceSet(final SelectedDate selectedDate, int hourOfDay, int minute,
                                            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                            String recurrenceRule) {
            fragmentManager
                    .beginTransaction()
                    .hide(fragmentNote)
                    .commit();

            final Date dateFirst = new Date();
            dateFirst.setTime(selectedDate.getFirstDate().getTimeInMillis());

            dateFirst.setHours(0);

            final Date dateSecond = new Date();
            dateSecond.setTime(selectedDate.getSecondDate().getTimeInMillis());

            dateSecond.setHours(24);

            dataBase.getTimeRange(dateFirst.getTime(), dateSecond.getTime(), new DBRecorder.CallbackSortedArrayListener() {
                @Override
                public void DBRecorderCallingBackSortedArray(ArrayList<Recorder> arrayList) {

                    if (arrayList.isEmpty()) {

                        Snackbar snackbar = Snackbar
                                .make(view, R.string.no_record_in_chosen_range,
                                        Snackbar.LENGTH_SHORT);
                        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                                snackbar.getView().getLayoutParams();
                        params.setMargins(8, 0, 8, bottomAppBar.getHeight());

                        snackbar.getView().setLayoutParams(params);

                        snackbar.show();
                        return;
                    }

                    long fromTime = selectedDate.getFirstDate().getTimeInMillis();
                    long toTime = selectedDate.getSecondDate().getTimeInMillis();

                    TimeRange timeRange = new TimeRange(fromTime,toTime,arrayList);

                    dataViewModel.setTimeRangeData(timeRange);//todo такое...
                    activityMode.setTimeRangeMode(ACTIVITY_MODE_RANGE_SELECTED);
                    dataViewModel.setTimeRangeData(timeRange);
                }
            });
        }
    };

    //===============================Всё, что связано с Нижним меню=================================

    @Override
    public void bottomSheetOnClickAccount() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {

            Intent intent = new Intent(MainActivity.this, SignUpForAnonymousActivity.class);
            startActivity(intent);
            customBottomSheet.dismiss();

        } else {

            Intent intent = new Intent(MainActivity.this, UserActivity.class); //TODO смотри как устроен intent ниже
            startActivityForResult(intent, 1);
            customBottomSheet.dismiss();
        }
    }

    @Override
    public void bottomSheetOnClickNotifications() {
//        Date date = Calendar.getInstance().getTime();
//        Long time = date.getTime() + 999;
//
//        scheduleNotification(getNotification(), time);

        Intent intentToNotificationsActivity = new Intent(MainActivity.this, NotificationsActivity.class);
        startActivity(intentToNotificationsActivity);
    }

    private static final String CHANNEL_ID = "Elspirado notify";

    private void scheduleNotification(Notification notification, Long time) {
        Intent notificationIntent = new Intent(getBaseContext(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, notificationIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    private Notification getNotification() {
        Intent intent = new Intent(getBaseContext(), NotificationPublisher.class);
        intent.putExtra("title", "title");
        intent.putExtra("text", "text");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon_background)
                .setContentTitle("title")
                .setContentText("text")
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        return builder.build();
    }

    @Override
    public void bottomSheetOnClickShare() {

        final CustomAlertDialogSelectTimeRange customAlertDialogSelectTimeRange =
                new CustomAlertDialogSelectTimeRange(MainActivity.this);

        customBottomSheet.dismiss();

        customAlertDialogSelectTimeRange.setCancelable(true);
        customAlertDialogSelectTimeRange.show();

        customAlertDialogSelectTimeRange.setPositiveButtonClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                customAlertDialogSelectTimeRange.dismiss();

                ExcelWriter excelWriter = new ExcelWriter();

                if (customAlertDialogSelectTimeRange.isAllRecords()) {
                    excelWriter.saveExcelFile(MainActivity.this, getString(R.string.excel_file_name),
                            new ExcelWriter.Callback() {
                                @RequiresApi(api = VERSION_CODES.LOLLIPOP_MR1)
                                @Override
                                public void excelWriterCallingBack(boolean success, String filePath) {
                                    if (success) {
                                        shareResult(filePath);
                                    } else {
                                        doToastError();
                                    }
                                }
                            });
                } else {
                    long fromTime = customAlertDialogSelectTimeRange.getFromTime();
                    long toTime = customAlertDialogSelectTimeRange.getToTime();
                    excelWriter.saveExcelFileInRange(MainActivity.this,
                            getString(R.string.excel_file_name), fromTime, toTime,
                            new ExcelWriter.Callback() {
                                @Override
                                public void excelWriterCallingBack(boolean success, String filePath) {
                                    if (success) {
                                        shareResult(filePath);
                                    } else {
                                        doToastError();
                                    }
                                }
                            });
                }


            }
        });

        customAlertDialogSelectTimeRange.setNegativeButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialogSelectTimeRange.dismiss();
            }
        });

        customAlertDialogSelectTimeRange.setEditTextDateClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialogSelectTimeRange.setCheckBoxAllRecords(false);

                if (alertDialogPickerFrag == null) {
                    alertDialogPickerFrag = new SublimePickerFragment();
                }

                if (alertDialogPickerFrag.isAdded()) {
                    return;
                }

                final long[] fromTime = new long[1];
                final long[] toTime = new long[1];

                SublimePickerFragment.Callback alertDialogCallback = new Callback() {
                    @Override
                    public void onCancelled() {
                        alertDialogPickerFrag.dismiss();
                    }

                    @Override
                    public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute,
                                                        RecurrenceOption recurrenceOption, String recurrenceRule) {

                        Date firstDate = selectedDate.getFirstDate().getTime();

                        Date secondDate = selectedDate.getSecondDate().getTime();

                        if (firstDate != secondDate) {
                            firstDate.setHours(0);
                            secondDate.setHours(24);
                        }

                        fromTime[0] = firstDate.getTime();
                        toTime[0] = secondDate.getTime();

                        System.out.println(fromTime[0] + toTime[0]);

                        customAlertDialogSelectTimeRange.setFromTime(fromTime[0]);
                        customAlertDialogSelectTimeRange.setToTime(toTime[0]);

                        customAlertDialogSelectTimeRange.setEditTextDate();

                    }
                };

                alertDialogPickerFrag.setCallback(alertDialogCallback);

                // Options
                Pair<Boolean, SublimeOptions> optionsPair = getOptions();

                // Valid options
                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
                alertDialogPickerFrag.setArguments(bundle);

                alertDialogPickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                alertDialogPickerFrag.show(fragmentManager, "alertDialogPickerFrag");
            }
        });
    }

    @Override
    public void bottomSheetOnClickSettings() {
        if (settingsIntent == null) {
            settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(settingsIntent, 1);
            customBottomSheet.dismiss();
        }
    }

    //====================================Все остальные методы=====================================

    //Методы для управления меню
    private void setChartMenu(){
        bottomAppBar.replaceMenu(R.menu.main_menu_graph);
    }

    private void setRecyclerMenu(){
        bottomAppBar.replaceMenu(R.menu.main_menu_recycler);
    }

    private void setRecyclerBackMenu(){
        bottomAppBar.replaceMenu(R.menu.main_menu_recycler_back);
    }

    private void doToastError() {
        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
    }

    private void shareResult(String filePath) {

        if (intentShareFile == null) {
            intentShareFile = new Intent(Intent.ACTION_SEND);
        }

        File fileWithinMyDir = new File(filePath);

        System.out.println(filePath);

        Uri fileUri = FileProvider.getUriForFile(
                MainActivity.this,
                "ru.elspirado.elspirado_app.elspirado_project.provider",
                fileWithinMyDir);

        if (fileWithinMyDir.exists()) {
            intentShareFile.setType("application/xls");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri);

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    getString(R.string.peak_flow_measurement_report));
            intentShareFile.putExtra(Intent.EXTRA_TEXT,
                    R.string.report_is_made_with_Elspirado);

            startActivity(Intent.createChooser(intentShareFile, getString(R.string.share_report)));

        }
    }

    //методы для удаления одной записи
    private void deleteRecorderWithToastMessage(final Recorder recorder){

        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(MainActivity.this,
                R.string.ad_positive_delete, R.string.ad_negative_cancel);

        customAlertDialog.setTitle(R.string.ad_delete_title);
        customAlertDialog.setMessage(R.string.ad_delete_question);
        customAlertDialog.setCancelable(true);

        customAlertDialog.setPositiveButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                customAlertDialog.dismiss();

                if(!fragmentNote.isVisible() && fragmentGraph.isVisible()){
                    return;
                }

                deletedRecorder =
                        new Recorder(recorder.getId(), recorder.getValue(), recorder.getNote(),
                                recorder.getTime(), recorder.getIsMedicine());

                fragmentGraphTapOnChartListener();

                dataBase.delete(recorder.getId(),callbackDeleteListener);
            }
        });

        customAlertDialog.setNegativeButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog.dismiss();
            }
        });

        customAlertDialog.show();
    }

    //Срабатывает при удалении записи
    private DBRecorder.CallbackDeleteListener callbackDeleteListener = new DBRecorder.CallbackDeleteListener() {
        @Override
        public void DBRecorderCallingBackDelete() {

            final Snackbar snackbar = Snackbar
                    .make(view, R.string.record_is_deleted, Snackbar.LENGTH_SHORT);

            snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimaryDark));

            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                    snackbar.getView().getLayoutParams();
            params.setMargins(8, 0, 8, bottomAppBar.getHeight());

            snackbar.getView().setLayoutParams(params);

            if(activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_All){

                updateUIWithoutSelectedRange();

                snackbar.setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        snackbar.setAction(null, null);
                        dataBase.insert(
                                deletedRecorder.getValue(),
                                deletedRecorder.getNote(),
                                deletedRecorder.getTime(),
                                deletedRecorder.getIsMedicine(),
                                callbackInsertListenerRangeAll);
                    }
                });

                snackbar.show();
            }

            if(activityMode.getTimeRangeMode() == ACTIVITY_MODE_RANGE_SELECTED){

                dataViewModel.deleteRecorderInSelectedData(deletedRecorder);

                snackbar.setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.setAction(null, null);
                        dataViewModel.addRecorderInSelectedData(deletedRecorder);
                    }
                });

                snackbar.show();
            }
        }
    };

    //Если в снекбаре выбрали отмену, и при этом мы НЕ в диапазоне
    private DBRecorder.CallbackInsertListener callbackInsertListenerRangeAll = new DBRecorder.CallbackInsertListener() {
        @Override
        public void DBRecorderCallingBackInsert() {
            updateUIWithoutSelectedRange();
        }
    };

    private void updateUIWithoutSelectedRange(){
        fragmentManager.beginTransaction().hide(fragmentNote).commit();

        dataViewModel.updateArrayListAllData();
    }

    private boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }

        wifiInfo = cm.getActiveNetworkInfo();

        return wifiInfo != null && wifiInfo.isConnected();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}