package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.FragmentPagerAdapter;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.FragmentUser;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.NewFragmentGraph;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.SublimePickerFragment;
import ru.elspirado.elspirado_app.elspirado_project.model.CustomViewPager;
import ru.elspirado.elspirado_app.elspirado_project.model.DBRecorder;
import ru.elspirado.elspirado_app.elspirado_project.model.ExcelWriter;
import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;
import ru.elspirado.elspirado_app.elspirado_project.model.RecyclerViewAdapter;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialog;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialogDeleteAll;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialogProgress;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialogSelectTimeRange;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.ActivityModeViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.DataViewModel;
import ru.elspirado.elspirado_app.elspirado_project.model.viewModels.RecorderViewModel;

import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.FRAGMENT_MODE_ACCOUNT;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.FRAGMENT_MODE_GRAPH;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.FRAGMENT_MODE_RECYCLER;

public class NewMainActivity extends  AppCompatActivity implements
        RecyclerViewAdapter.OnFragmentListener,
        FragmentUser.OnFragmentListener,
        NewFragmentGraph.OnFragmentListener {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FragmentPagerAdapter fragmentPagerAdapter;

    private CustomViewPager viewPager;

    private SublimePickerFragment alertDialogPickerFrag;

    private FragmentManager fragmentManager;

    private DataViewModel dataViewModel;

    private View view;

    private FloatingActionButton fab;

    private DBRecorder dataBase;

    private RecorderViewModel recorderViewModel;
    private ActivityModeViewModel activityMode;

    private Intent addRecordIntent, editIntent, intentShareFile, intentAddEmail;

    //================Всё, что напрямую связано с MainActivity и её жизненным циклом================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        view = findViewById(R.id.view);

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.new_main_activity_toolbar_menu);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        Objects.requireNonNull(toolbar).setTitle(R.string.Main_activity_title);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(onFloatingActionButtonClickListener);

        dataViewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        dataViewModel.updateArrayListAllData();

        recorderViewModel = ViewModelProviders.of(this).get(RecorderViewModel.class);
        activityMode = ViewModelProviders.of(this).get(ActivityModeViewModel.class);

        dataBase = new DBRecorder();

        fragmentManager = getSupportFragmentManager();

        viewPager = findViewById(R.id.view_pager);

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),
                androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);


        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(fragmentPagerAdapter);

        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                bottomNavigationView.getMenu().getItem(position).setChecked(true);

                if(position == 0){

                    toolbar.getMenu().findItem(R.id.action_calendar).setVisible(true);
                    toolbar.getMenu().findItem(R.id.action_exit).setVisible(false);

                    activityMode.setFragmentMode(FRAGMENT_MODE_GRAPH);

                    fab.show();
                }

                if(position == 1){

                    toolbar.getMenu().findItem(R.id.action_calendar).setVisible(true);
                    toolbar.getMenu().findItem(R.id.action_exit).setVisible(false);

                    activityMode.setFragmentMode(FRAGMENT_MODE_RECYCLER);

                    fab.show();
                }

                if(position == 2){

                    toolbar.getMenu().findItem(R.id.action_calendar).setVisible(false);
                    toolbar.getMenu().findItem(R.id.action_exit).setVisible(true);

                    activityMode.setFragmentMode(FRAGMENT_MODE_ACCOUNT);

                    fab.hide();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int itemId = menuItem.getItemId();

                if(itemId == R.id.bottomNavigationGraph){
                    viewPager.setCurrentItem(0);

                }

                if(itemId == R.id.bottomNavigationList){
                    viewPager.setCurrentItem(1);
                }

                if(itemId == R.id.bottomNavigationSettings){
                    viewPager.setCurrentItem(2);
                }

                return true;
            }
        });

    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if(item.getItemId() == R.id.action_share)
                fragmentPagerAdapter.shareSelectedElementsInFragmentRecyclerView();


            if(item.getItemId() == R.id.action_select_all)
                fragmentPagerAdapter.selectAllElementsInFragmentRecyclerView();

            if(item.getItemId() == R.id.action_delete){
                final CustomAlertDialog customAlertDialog = new CustomAlertDialog(NewMainActivity.this,
                        R.string.ad_positive_delete, R.string.ad_negative_cancel);

                int itemCount = fragmentPagerAdapter.getSelectedItemCountInFragmentRecyclerView();

                if(itemCount == 1){
                    customAlertDialog.setMessage(R.string.ad_delete_question);
                } else {
                    customAlertDialog.setMessage("Удалить эти (" + itemCount + ") записи?"); //todo стринги
                }

                customAlertDialog.setTitle(R.string.ad_delete_title);
                customAlertDialog.setCancelable(true);

                customAlertDialog.setPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragmentPagerAdapter.deleteSelectedElementsInFragmentRecyclerView();
                        customAlertDialog.dismiss();
                    }
                });

                customAlertDialog.setNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customAlertDialog.dismiss();
                    }
                });

                customAlertDialog.show();
            }

            return false;
        }
    };

    private View.OnClickListener onFloatingActionButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(addRecordIntent == null){
                addRecordIntent = new Intent(NewMainActivity.this,AddRecordActivity.class);
                startActivityForResult(addRecordIntent,1);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK) { //Добавили/отрдактировали запись
            dataViewModel.updateArrayListAllData();
            return;
        }

        if (requestCode == 2) { //Вошли в Google Account

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                customAlertDialogProgress.setTitle(R.string.wait);
                customAlertDialogProgress.setMessage(getString(R.string.signin_up_you_in_system));
                customAlertDialogProgress.show();

                GoogleSignInAccount account = task.getResult(ApiException.class);
                addGoogleAccount(Objects.requireNonNull(account));

            } catch (ApiException e) {

                Toast.makeText(NewMainActivity.this, R.string.something_went_wrong,
                        Toast.LENGTH_SHORT).show();
                customAlertDialogProgress.hide();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        addRecordIntent = null;
        editIntent = null;
        intentShareFile = null;
        intentAddEmail = null;

        super.onStop();
    }

    //===============================Всё, что связано с RecyclerView================================

    private int selectedElementsCount;

    @Override
    public void recyclerViewAdapterSelectedElementsListener(int size) {

        selectedElementsCount = size;

        if (size > 0) {

            bottomNavigationView.setVisibility(View.GONE);
            viewPager.setSwipeable(false);
            fab.hide();

            Objects.requireNonNull(toolbar).setTitle("Выбрано " + size);//todo стринги

            toolbar.getMenu().findItem(R.id.action_calendar).setVisible(false);
            toolbar.getMenu().findItem(R.id.action_delete).setVisible(true);
            toolbar.getMenu().findItem(R.id.action_share).setVisible(true);
            toolbar.getMenu().findItem(R.id.action_select_all).setVisible(true);

        } else {

            bottomNavigationView.setVisibility(View.VISIBLE);
            viewPager.setSwipeable(true);
            fab.show();

            Objects.requireNonNull(toolbar).setTitle(R.string.Main_activity_title);

            toolbar.getMenu().findItem(R.id.action_calendar).setVisible(true);
            toolbar.getMenu().findItem(R.id.action_delete).setVisible(false);
            toolbar.getMenu().findItem(R.id.action_share).setVisible(false);
            toolbar.getMenu().findItem(R.id.action_select_all).setVisible(false);
        }
    }

    @Override
    public void recyclerViewAdapterDeleteListener(ArrayList<Recorder> arrayList) {
        DBRecorder dbRecorder = new DBRecorder();

        dbRecorder.deleteArrayList(arrayList, new DBRecorder.CallbackDeleteArrayListListener() {
            @Override
            public void DBRecorderCallingBackDeleteArrayList() {
                dataViewModel.updateArrayListAllData();
            }
        });
    }

    @Override
    public void recyclerViewAdapterShareListener(ArrayList<Recorder> arrayList) {

        new ExcelWriter().saveExcelFile(getApplicationContext(), getString(R.string.excel_file_name), arrayList, new ExcelWriter.Callback() {
            @Override
            public void excelWriterCallingBack(boolean success, String filePath) {
                shareResult(filePath);
                fragmentPagerAdapter.cleanSelectionInFragmentRecyclerView();
            }
        });
    }

    @Override
    public void recyclerViewAdapterDeleteOneElementListener(Recorder recorder) {
        deleteOneRecorder(recorder);
    }

    @Override
    public void recyclerViewAdapterEditElementListener(Recorder recorder) {
        editOneRecorder(recorder);
    }

    //=============================Всё, что связано с FragmentSettings==============================

    @Override
    public void fragmentUserAddEmailListener() {

        if(intentAddEmail == null){
            intentAddEmail = new Intent(NewMainActivity.this, SignUpForMultipleAuthEmail.class);
            startActivity(intentAddEmail);
        }

    }

    private GoogleApiClient mGoogleApiClient;

    private CustomAlertDialogProgress customAlertDialogProgress;

    @Override
    public void fragmentUserAddGoogleAccountListener() {

        customAlertDialogProgress = new CustomAlertDialogProgress(NewMainActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn
                .getClient(NewMainActivity.this, gso);

        try { //Этот код вызывает диалоговое окно с выбором аккаунта
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    })
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            mGoogleApiClient.clearDefaultAccountAndReconnect();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    }).build();
        } catch (IllegalStateException ignored){

        }

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, 2);
    }

    private void addGoogleAccount(GoogleSignInAccount acct){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            customAlertDialogProgress.hide();
                            fragmentPagerAdapter.checkProvidersInFragmentUser();

                            Toast.makeText(NewMainActivity.this, "Вы добавили аккаунт Google!", //TODO стринги
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            customAlertDialogProgress.hide();
                            Toast.makeText(NewMainActivity.this, "Что-то пошло не так", //TODO стринги
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void fragmentUserChangePasswordListener() {

        final String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(NewMainActivity.this,
                R.string.ad_good, R.string.ad_negative_cancel);

        customAlertDialog.setCancelable(true);
        customAlertDialog.setTitle(R.string.ad_changing_password);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            customAlertDialog.setMessage(Html.fromHtml(getResources().getString(R.string.ad_we_send_you_email) + " " + "<b>"+email+"</b>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            customAlertDialog.setMessage(Html.fromHtml(getResources().getString(R.string.ad_we_send_you_email) + " " + "<b>"+email+"</b>"));
        }

        customAlertDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customAlertDialog.dismiss();

                FirebaseAuth.getInstance().sendPasswordResetEmail(Objects.requireNonNull(email));

                Snackbar snackbar = Snackbar
                        .make(view, "Письмо отправлено на " + email,//TODO стринги
                                Snackbar.LENGTH_SHORT);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                        snackbar.getView().getLayoutParams();
                params.setMargins(8, 0, 8,
                        bottomNavigationView.getHeight() + fab.getHeight() - 60);
                snackbar.getView().setLayoutParams(params);

                snackbar.show();
            }
        });

        customAlertDialog.setNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog.dismiss();
            }
        });

        customAlertDialog.show();
    }

    @Override
    public void fragmentUserDeleteAllDataListener() {

        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(NewMainActivity.this,
                R.string.next_registration,R.string.undo); //TODO поменять id первой надписи

        customAlertDialog.setTitle(R.string.ad_alert);
        customAlertDialog.setMessage("Вы собираетесь удалить все ваши записи. Это действие нельзя будет отменить. Вы уверены?"); //TODO стринги

        customAlertDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog.dismiss();

                final CustomAlertDialogDeleteAll customAlertDialogDeleteAll = new CustomAlertDialogDeleteAll(NewMainActivity.this,
                        R.string.delete_all,R.string.undo);

                customAlertDialogDeleteAll.setTitle("Внимание!");
                customAlertDialogDeleteAll.setMessage("Вы должны подтвердить свои намеренья, введя в поле ниже слово \"Подтверждаю\".");

                customAlertDialogDeleteAll.setPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DBRecorder().deleteAllWithFirebase();

                        customAlertDialogDeleteAll.dismiss();
                        dataViewModel.updateArrayListAllData();

                        Snackbar snackbar = Snackbar
                                .make(view, "Все данные были успешно удалены",//TODO стринги
                                        Snackbar.LENGTH_SHORT);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                                snackbar.getView().getLayoutParams();
                        params.setMargins(8, 0, 8,
                                bottomNavigationView.getHeight() + fab.getHeight() - 60);
                        snackbar.getView().setLayoutParams(params);

                        snackbar.show();
                    }
                });

                customAlertDialogDeleteAll.setNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customAlertDialogDeleteAll.dismiss();
                    }
                });

                customAlertDialogDeleteAll.show();
            }
        });

        customAlertDialog.setNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog.dismiss();
            }
        });

        customAlertDialog.show();
    }

    @Override
    public void fragmentUserShareDataListener() {
        final CustomAlertDialogSelectTimeRange customAlertDialogSelectTimeRange =
                new CustomAlertDialogSelectTimeRange(NewMainActivity.this);

        customAlertDialogSelectTimeRange.setCancelable(true);
        customAlertDialogSelectTimeRange.show();

        customAlertDialogSelectTimeRange.setPositiveButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                customAlertDialogSelectTimeRange.dismiss();

                ExcelWriter excelWriter = new ExcelWriter();

                if (customAlertDialogSelectTimeRange.isAllRecords()) {
                    excelWriter.saveExcelFile(NewMainActivity.this, getString(R.string.excel_file_name),
                            new ExcelWriter.Callback() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
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
                    excelWriter.saveExcelFileInRange(NewMainActivity.this,
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

        customAlertDialogSelectTimeRange.setNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialogSelectTimeRange.dismiss();
            }
        });

        customAlertDialogSelectTimeRange.setEditTextDateClickListener(new View.OnClickListener() {
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

                SublimePickerFragment.Callback alertDialogCallback = new SublimePickerFragment.Callback() {
                    @Override
                    public void onCancelled() {
                        alertDialogPickerFrag.dismiss();
                    }

                    @Override
                    public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute,
                                                        SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {

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
                NewMainActivity.this,
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

    //==============================Всё, что связано с FragmentGraph================================

    @Override
    public void fragmentGraphDeleteListener(Recorder recorder) {//переименовать
        deleteOneRecorder(recorder);
    }

    @Override
    public void fragmentGraphEditListener(Recorder recorder) {
        editOneRecorder(recorder);
    }

    @Override
    public void fragmentGraphTextChangeListener(CharSequence charSequence, Recorder recorder) {

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

    //====================================Все остальные методы======================================

    Recorder deletedRecorder;

    private void deleteOneRecorder(final Recorder recorder){

        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(NewMainActivity.this,
                R.string.ad_positive_delete, R.string.ad_negative_cancel);

        customAlertDialog.setTitle(R.string.ad_delete_title);
        customAlertDialog.setMessage(R.string.ad_delete_question);
        customAlertDialog.setCancelable(true);

        customAlertDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customAlertDialog.dismiss();

                deletedRecorder =
                        new Recorder(recorder.getId(), recorder.getValue(), recorder.getNote(),
                                recorder.getTime(), recorder.getIsMedicine());

                recorderViewModel.setRecorder(null);

                dataBase.delete(recorder.getId(),callbackDeleteListener);
            }
        });

        customAlertDialog.setNegativeButtonClickListener(new View.OnClickListener() {
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

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                    snackbar.getView().getLayoutParams();
            params.setMargins(8, 0, 8, bottomNavigationView.getHeight());

            snackbar.getView().setLayoutParams(params);

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
    };

    private void updateUIWithoutSelectedRange(){

        dataViewModel.updateArrayListAllData();
    }

    private DBRecorder.CallbackInsertListener callbackInsertListenerRangeAll = new DBRecorder.CallbackInsertListener() {
        @Override
        public void DBRecorderCallingBackInsert() {
            updateUIWithoutSelectedRange();
        }
    };

    private void editOneRecorder(Recorder recorder){

        if (editIntent == null) {

            editIntent = new Intent(getApplicationContext(), EditRecordActivity.class);
            editIntent.putExtra("objectRecorder", recorder);

            startActivityForResult(editIntent, 1);
        }
    }
}
