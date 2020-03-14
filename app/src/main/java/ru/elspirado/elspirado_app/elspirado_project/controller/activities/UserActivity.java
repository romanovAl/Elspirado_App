package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;
import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialog;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialogDeleteAll;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialogProgress;
import ru.elspirado.elspirado_app.elspirado_project.model.DBRecorder;

import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES_IS_ENTERED;

public class UserActivity extends AppCompatActivity {

    private TextView textViewMail, textViewChangePassword, textViewDeleteAllData;

    private ImageView imageViewEmail, imageViewGoogle;

    private CustomAlertDialogProgress customAlertDialogProgress;

    private FirebaseAuth mAuth;

    private SharedPreferences mSettings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ваш аккаунт"); //TODO стринги

        setSupportActionBar(toolbar);

        textViewMail = findViewById(R.id.textView_mail);
        textViewMail.setText(Objects.requireNonNull(currentUser).getEmail());

        imageViewEmail = findViewById(R.id.imageView_email);
        imageViewGoogle = findViewById(R.id.imageView_google);

        customAlertDialogProgress = new CustomAlertDialogProgress(UserActivity.this);

        mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        editor = mSettings.edit();

        final FloatingActionButton fab = findViewById(R.id.fab_user);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        textViewChangePassword = findViewById(R.id.textView_change_password);

        textViewChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

                final CustomAlertDialog customAlertDialog = new CustomAlertDialog(UserActivity.this,
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

                        View view = findViewById(R.id.activity_user_view);
                        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar_user);

                        Snackbar snackbar = Snackbar
                                .make(view, "Письмо отправлено на " + email,//TODO стринги
                                        Snackbar.LENGTH_SHORT);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                                snackbar.getView().getLayoutParams();
                        params.setMargins(8, 0, 8,
                                bottomAppBar.getHeight() + fab.getHeight() - 60);
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
        });

        textViewDeleteAllData = findViewById(R.id.textView_delete_all_data);

        textViewDeleteAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomAlertDialog customAlertDialog = new CustomAlertDialog(UserActivity.this,
                        R.string.next_registration,R.string.undo); //TODO поменять id первой надписи

                customAlertDialog.setTitle(R.string.ad_alert);
                customAlertDialog.setMessage("Вы собираетесь удалить все ваши записи. Это действие нельзя будет отменить. Вы уверены?"); //TODO стринги

                customAlertDialog.setPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customAlertDialog.dismiss();

                        final CustomAlertDialogDeleteAll customAlertDialogDeleteAll = new CustomAlertDialogDeleteAll(UserActivity.this,
                                R.string.delete_all,R.string.undo);

                        customAlertDialogDeleteAll.setTitle("Внимание!");
                        customAlertDialogDeleteAll.setMessage("Вы должны подтвердить свои намеренья, введя в поле ниже слово \"Подтверждаю\".");

                        customAlertDialogDeleteAll.setPositiveButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new DBRecorder().deleteAllWithFirebase();

                                customAlertDialogDeleteAll.dismiss();

                                View view = findViewById(R.id.activity_user_view);
                                BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar_user);

                                Snackbar snackbar = Snackbar
                                        .make(view, "Все данные были успешно удалены",//TODO стринги
                                                Snackbar.LENGTH_SHORT);
                                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
                                        snackbar.getView().getLayoutParams();
                                params.setMargins(8, 0, 8,
                                        bottomAppBar.getHeight() + fab.getHeight() - 60);
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
        });

        imageViewGoogle.setOnClickListener(new View.OnClickListener() {

            //Этот onClickListener
            //управляются методом checkProviders, поэтому срабатывают не всегда

            @Override
            public void onClick(View v) {
                continueWithGoogle();
            }
        });

        imageViewEmail.setOnClickListener(new View.OnClickListener() {

            //Этот OnClickListener
            //управляются методом checkProviders, поэтому срабатывают не всегда

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, SignUpForMultipleAuthEmail.class);
                startActivity(intent);
            }
        });

        checkProviders();
    }

    private void checkProviders(){

        List<String> listProviders = Objects.requireNonNull(mAuth.getCurrentUser()).getProviders();

        imageViewEmail.setClickable(true);
        imageViewGoogle.setClickable(true);

        for(int i = 0; i < Objects.requireNonNull(listProviders).size(); i++){

            if(listProviders.get(i).equals("google.com")){ //Если человек зареган через гугл
                imageViewGoogle.setImageDrawable(getDrawable(R.drawable.ic_done_black_24dp));

                imageViewGoogle.setClickable(false);
            }

            if(listProviders.get(i).equals("password")){ //Если человек зареган через почу
                imageViewEmail.setImageDrawable(getDrawable(R.drawable.ic_done_black_24dp));

                imageViewEmail.setClickable(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //устанавливаем созданое меню
        getMenuInflater().inflate(R.menu.user_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_exit) {

            final CustomAlertDialog customAlertDialog = new CustomAlertDialog(UserActivity.this,
                    R.string.exit, R.string.undo);

            customAlertDialog.setTitle(R.string.exit);
            customAlertDialog.setMessage(R.string.do_you_want_to_exit_an_account);
            customAlertDialog.setCancelable(true);

            customAlertDialog.setPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    editor.putBoolean(APP_PREFERENCES_IS_ENTERED, false).apply();
                    Intent intent = new Intent(UserActivity.this, RegAndAuthActivity.class);
                    customAlertDialog.dismiss();
                    startActivity(intent);
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

        return super.onOptionsItemSelected(item);
    }

    private GoogleApiClient mGoogleApiClient;

    private void continueWithGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn
                .getClient(UserActivity.this, gso);

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

    private void addGoogleAccount(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            customAlertDialogProgress.hide();
                            checkProviders();

                            Toast.makeText(UserActivity.this, "Вы добавили аккаунт Google!", //TODO стринги
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            customAlertDialogProgress.hide();
                            Toast.makeText(UserActivity.this, "Что-то пошло не так", //TODO стринги
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                customAlertDialogProgress.setTitle(R.string.wait);
                customAlertDialogProgress.setMessage(getString(R.string.signin_up_you_in_system));
                customAlertDialogProgress.show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                addGoogleAccount(Objects.requireNonNull(account));
            } catch (ApiException e) {
                Toast.makeText(UserActivity.this, R.string.something_went_wrong,
                        Toast.LENGTH_SHORT).show();
                customAlertDialogProgress.hide();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(1, intent);
        finish();
        super.onBackPressed();
    }
}