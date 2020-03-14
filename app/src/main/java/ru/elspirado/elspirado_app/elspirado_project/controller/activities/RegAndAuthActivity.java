package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES;
import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES_IS_ENTERED;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.FragmentEntry;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.FragmentReg;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialog;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialogProgress;
import ru.elspirado.elspirado_app.elspirado_project.model.Utils;

public class RegAndAuthActivity extends AppCompatActivity implements
        FragmentReg.OnFragmentListener, FragmentEntry.OnFragmentListener {

    private FragmentManager fragmentManager;

    private FragmentReg fragmentReg;
    private FragmentEntry fragmentEntry;

    private FirebaseAuth mAuth;

    private SharedPreferences.Editor editor;

    private CustomAlertDialogProgress customAlertDialogProgress;

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_and_auth);
        view = findViewById(R.id.regAndAuthLayout);

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        editor = mSettings.edit();

        customAlertDialogProgress = new CustomAlertDialogProgress(RegAndAuthActivity.this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fragmentManager = getSupportFragmentManager();

        fragmentEntry = new FragmentEntry();

        fragmentReg = (FragmentReg) fragmentManager.findFragmentByTag("fragmentReg");
        fragmentEntry = (FragmentEntry) fragmentManager.findFragmentByTag("fragmentEntry");

        if (fragmentReg == null) {
            fragmentReg = new FragmentReg();

            fragmentManager
                    .beginTransaction()
                    .add(R.id.regAndAuthContainer, fragmentReg, "fragmentReg")
                    .commit();

            if (!fragmentReg.isFragmentIsVisible()) {
                fragmentManager
                        .beginTransaction()
                        .hide(fragmentReg);
            }
        }

        if (fragmentEntry == null) {
            fragmentEntry = new FragmentEntry();

            fragmentManager
                    .beginTransaction()
                    .add(R.id.regAndAuthContainer, fragmentEntry, "fragmentEntry")
                    .hide(fragmentEntry)
                    .commit();

            if (!fragmentEntry.isFragmentIsVisible()) {
                fragmentManager
                        .beginTransaction()
                        .hide(fragmentEntry);
            }
        }

    }

    @Override
    protected void onDestroy() {
        customAlertDialogProgress.dismiss();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    private void signUp(String login, String password) { //регистрация
        mAuth.createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            editor.putBoolean(APP_PREFERENCES_IS_ENTERED, true).apply();

                            customAlertDialogProgress.hide();

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                            Utils.getDatabase()
                                    .getReference()
                                    .child("users")
                                    .child(currentUser.getUid())
                                    .child("userInfo")
                                    .child("email").setValue(currentUser.getEmail());

                            System.out.println("Пользователь зарегался " + currentUser.getEmail());

                            Intent intent = new Intent(RegAndAuthActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityFromChild(RegAndAuthActivity.this, intent, 2);

                        } else {
                            // If sign in fails, display a message to the user.

                            customAlertDialogProgress.hide();

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


    private void sighIn(String login, String password) { //вход
        mAuth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            editor.putBoolean(APP_PREFERENCES_IS_ENTERED, true).apply();

                            customAlertDialogProgress.hide();

                            Intent intent = new Intent(RegAndAuthActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityFromChild(RegAndAuthActivity.this, intent, 2);


                        } else {
                            // If sign in fails, display a message to the user.

                            customAlertDialogProgress.hide();

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

    private void signUpAnonimously() {

        final CustomAlertDialog customAlertDialog = new CustomAlertDialog(RegAndAuthActivity.this,
                R.string.undo, R.string.to_continue);

        customAlertDialog.setTitle(R.string.to_skip);
        customAlertDialog.setMessage(R.string.you_will_lose_synchronizaton);

        customAlertDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog.cancel();
            }
        });

        customAlertDialog.setNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog.hide();
                editor.putBoolean(APP_PREFERENCES_IS_ENTERED, true).apply();

                Intent intent = new Intent(RegAndAuthActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityFromChild(RegAndAuthActivity.this, intent, 2);
            }
        });

        customAlertDialog.show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(RegAndAuthActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            customAlertDialogProgress.hide();

                            editor.putBoolean(APP_PREFERENCES_IS_ENTERED, true).apply();

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                            Utils.getDatabase()
                                    .getReference()
                                    .child("users")
                                    .child(currentUser.getUid())
                                    .child("userInfo")
                                    .child("email").setValue(currentUser.getEmail());

                            Intent intent = new Intent(RegAndAuthActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityFromChild(RegAndAuthActivity.this, intent, 2);

                        } else {
                            customAlertDialogProgress.hide();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                customAlertDialogProgress.setTitle(R.string.wait);
                customAlertDialogProgress.setMessage(getString(R.string.signin_up_you_in_system));
                customAlertDialogProgress.show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(Objects.requireNonNull(account));
            } catch (ApiException e) {
                Toast.makeText(RegAndAuthActivity.this, R.string.something_went_wrong,
                        Toast.LENGTH_SHORT).show();
                customAlertDialogProgress.hide();
            }
        }
    }


    @Override
    public void fragmentRegSignUp(String login, String password) {
        customAlertDialogProgress.setTitle(R.string.wait);
        customAlertDialogProgress.setMessage(getString(R.string.signin_up_you_in_system));
        customAlertDialogProgress.show();
        signUp(login, password);
    }

    @Override
    public void fragmentEntrySignIn(String login, String password) {
        customAlertDialogProgress.setTitle(R.string.wait);
        customAlertDialogProgress.setMessage(getString(R.string.downloading_your_data));
        customAlertDialogProgress.show();
        sighIn(login, password);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void goToFragmentReg() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit)
                .hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("fragmentEntry")))
                .show(Objects.requireNonNull(fragmentManager.findFragmentByTag("fragmentReg")))
                .addToBackStack("fragmentEntry")
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void goToFragmentEntry() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("fragmentReg")))
                .show(Objects.requireNonNull(fragmentManager.findFragmentByTag("fragmentEntry")))
                .addToBackStack("fragmentReg")
                .commit();
    }


    @Override
    public void fragmentRegContinueWithGoogle() {
        continueWithGoogle();
    }

    @Override
    public void fragmentEntryContinueWithGoogle() {
      continueWithGoogle();
    }

    private GoogleApiClient mGoogleApiClient;

    private void continueWithGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn
                .getClient(RegAndAuthActivity.this, gso);

        try {//Этот код вызывает диалоговое окно с выбором аккаунта
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
    public void fragmentRegContinueWithoutRegistration() {
        signUpAnonimously();
    }

    @Override
    public void fragmentEntryСontinueWithoutEntry() {
        signUpAnonimously();
    }
}