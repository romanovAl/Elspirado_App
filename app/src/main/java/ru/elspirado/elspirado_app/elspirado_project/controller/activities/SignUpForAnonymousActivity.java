package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES_IS_ENTERED;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialog;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialogInfo;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialogProgress;
import ru.elspirado.elspirado_app.elspirado_project.model.DBRecorder;
import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;
import ru.elspirado.elspirado_app.elspirado_project.model.Utils;

public class SignUpForAnonymousActivity extends AppCompatActivity {

    private TextInputLayout loginInputLayout, passwordInputLayout;
    private TextInputEditText loginEditText, passwordEditText;

    private Toolbar toolbar;

    private MaterialButton googleSignInButton;

    private CustomAlertDialogProgress customAlertDialogProgress;

    private Button enterButton;

    private TextView titleTextView;

    private SharedPreferences.Editor editor;

    private View view;

    private FloatingActionButton fabBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_anonymous);
        view = findViewById(R.id.activity_sign_up_for_anonymous_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        loginInputLayout = findViewById(R.id.loginTextInputLayout);
        passwordInputLayout = findViewById(R.id.passwordTextInputLayout);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        enterButton = findViewById(R.id.enterButton);
        titleTextView = findViewById(R.id.titleTextView);
        googleSignInButton = findViewById(R.id.anonymousGoogleSignIn);
        fabBack = findViewById(R.id.fab_anonymous_back);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final CustomAlertDialogInfo customAlertDialogInfo = new CustomAlertDialogInfo(SignUpForAnonymousActivity.this,
                R.string.perfectly);

        customAlertDialogInfo.setTitle(R.string.good_news);
        customAlertDialogInfo.setMessage(R.string.we_transfer_your_data);
        customAlertDialogInfo.show();

        customAlertDialogInfo.setButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialogInfo.dismiss();
            }
        });

        customAlertDialogProgress = new CustomAlertDialogProgress(SignUpForAnonymousActivity.this);
        customAlertDialogProgress.setTitle(R.string.wait);
        customAlertDialogProgress
            .setMessage(getString(R.string.transfering_your_data_to_protected_server));

        SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        editor = mSettings.edit();

        titleTextView.setText(Html.fromHtml(
            getResources().getString(R.string.registration) + //Делает надпись двумя цветами
                "<font color = #ff9e80> " +
                getResources().getString(R.string.Main_activity_title) +
                "</font>"));

        enterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String email = Objects.requireNonNull(loginEditText.getText()).toString();
                    String password = Objects.requireNonNull(passwordEditText.getText()).toString();
                    signUpAnonymousUser(email, password);

                    customAlertDialogProgress.show();

                } catch (IllegalArgumentException e) {
                    if (Objects.requireNonNull(loginEditText.getText()).toString().isEmpty()) {
                        loginInputLayout.setError(getResources().getString(R.string.error_string));
                    } else {
                        loginInputLayout.setError(null);
                    }
                    if (Objects.requireNonNull(passwordEditText.getText()).toString().isEmpty()) {
                        passwordInputLayout
                            .setError(getResources().getString(R.string.error_string));
                    } else {
                        passwordInputLayout.setError(null);
                    }
                }
            }
        });

        loginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    loginInputLayout.setError(getResources().getString(R.string.error_string));
                } else {
                    loginInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    passwordInputLayout.setError(getResources().getString(R.string.error_string));
                } else {
                    passwordInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    String email = Objects.requireNonNull(loginEditText.getText()).toString();
                    String password = Objects.requireNonNull(passwordEditText.getText()).toString();
                    signUpAnonymousUser(email, password);

                    customAlertDialogProgress.show();


                } catch (Exception e) {
                    if (Objects.requireNonNull(loginEditText.getText()).toString().isEmpty()) {
                        loginInputLayout.setError(getResources().getString(R.string.error_string));
                    } else {
                        loginInputLayout.setError(null);
                    }
                    if (Objects.requireNonNull(passwordEditText.getText()).toString().isEmpty()) {
                        passwordInputLayout
                            .setError(getResources().getString(R.string.error_string));
                    } else {
                        passwordInputLayout.setError(null);
                    }
                }
                return false;
            }
        });

        googleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpAnonymousUserWithGoogle();

                customAlertDialogProgress.show();
            }
        });

        fabBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //устанавливаем созданое меню
        getMenuInflater().inflate(R.menu.sign_up_for_anonymous_activ_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_exit) {

            final CustomAlertDialog customAlertDialog = new CustomAlertDialog(SignUpForAnonymousActivity.this,
                    R.string.to_exit, R.string.undo);

            customAlertDialog.setTitle(R.string.you_are_not_signed_up);
            customAlertDialog.setMessage(R.string.exit_account_all_data_will_be_deleted);
            customAlertDialog.setCancelable(true);

            customAlertDialog.setPositiveButtonClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBRecorder dbRecorder = new DBRecorder();
                    dbRecorder.deleteAllWithRoom(new DBRecorder.CallbackDeleteAllListener() {
                        @Override
                        public void DBRecorderCallingBackDeleteAll() {
                            editor.putBoolean(APP_PREFERENCES_IS_ENTERED, false).apply();
                            customAlertDialog.hide();

                            Intent intent = new Intent(SignUpForAnonymousActivity.this, RegAndAuthActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            });

            customAlertDialog.setNegativeButtonClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    customAlertDialog.cancel();
                }
            });

            customAlertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        customAlertDialogProgress.dismiss();
        super.onDestroy();
    }

    private void signUpAnonymousUser(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        afterAuth();

                    } else {

                        customAlertDialogProgress.hide();

                        View view = findViewById(R.id.activity_sign_up_for_anonymous_layout);

                        try {
                            Snackbar.make(view,
                                Objects.requireNonNull(task.getException()).getLocalizedMessage(),
                                Snackbar.LENGTH_LONG).show();
                        } catch (NullPointerException ignored) {
                        }
                    }
                }
            });
    }

    private GoogleApiClient mGoogleApiClient;

    private void signUpAnonymousUserWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn
                .getClient(SignUpForAnonymousActivity.this, gso);

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

        startActivityForResult(signInIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                customAlertDialogProgress.show();

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(Objects.requireNonNull(account));
            } catch (ApiException e) {
                Toast.makeText(SignUpForAnonymousActivity.this, "Что-то пошло не так",
                    Toast.LENGTH_SHORT).show();
                customAlertDialogProgress.hide();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(SignUpForAnonymousActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            afterAuth();

                        } else {
                            customAlertDialogProgress.hide();

                            try {
                                Snackbar.make(view, Objects.requireNonNull(task.getException())
                                        .getLocalizedMessage(),
                                    Snackbar.LENGTH_LONG).show();
                            } catch (NullPointerException ignored) {
                            }
                        }
                    }
                });
    }

    private void afterAuth(){

        new DBRecorder().selectAllWithRoom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ArrayList<Recorder>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ArrayList<Recorder> arrayList) {
                        for(int i = 0; i < arrayList.size(); i++){
                            System.err.println("Чек записей" + arrayList);
                        }

                        DBRecorder dbRecorder = new DBRecorder();

                        dbRecorder.insert(arrayList);
                        dbRecorder.deleteAllWithRoom(new DBRecorder.CallbackDeleteAllListener() {
                            @Override
                            public void DBRecorderCallingBackDeleteAll() {
                                customAlertDialogProgress.hide();

                                editor.putBoolean(APP_PREFERENCES_IS_ENTERED, true).apply();

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                Utils.getDatabase()
                                        .getReference()
                                        .child("users")
                                        .child(Objects.requireNonNull(currentUser).getUid())
                                        .child("userInfo")
                                        .child("email").setValue(currentUser.getEmail());

                                Intent intent = new Intent(SignUpForAnonymousActivity.this,//СМОТРИ СЮДА
                                        MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivityFromChild(SignUpForAnonymousActivity.this, intent, 2);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}