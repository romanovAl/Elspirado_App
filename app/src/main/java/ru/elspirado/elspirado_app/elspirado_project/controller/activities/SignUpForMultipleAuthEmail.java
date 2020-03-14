package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.customDialogs.CustomAlertDialogProgress;

public class SignUpForMultipleAuthEmail extends AppCompatActivity {

    private TextInputLayout loginInputLayout, passwordInputLayout;
    private TextInputEditText loginEditText, passwordEditText;

    private CustomAlertDialogProgress customAlertDialogProgress;

    private Button enterButton;

    private TextView titleTextView;

    private FloatingActionButton fabBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_for_multiple_auth_email);

        loginInputLayout = findViewById(R.id.loginTextInputLayout);
        passwordInputLayout = findViewById(R.id.passwordTextInputLayout);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        enterButton = findViewById(R.id.enterButton);
        titleTextView = findViewById(R.id.titleTextView);
        fabBack = findViewById(R.id.fab_anonymous_back);

        titleTextView.setText(Html.fromHtml(
                getResources().getString(R.string.registration) + //Делает надпись двумя цветами
                        "<font color = #ff9e80> " +
                        getResources().getString(R.string.Main_activity_title) +
                        "</font>"));

        customAlertDialogProgress = new CustomAlertDialogProgress(SignUpForMultipleAuthEmail.this);

        customAlertDialogProgress.setTitle(R.string.wait);
        customAlertDialogProgress.setMessage(getString(R.string.signin_up_you_in_system)); //TODO нужно другое сообщение

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String email = Objects.requireNonNull(loginEditText.getText()).toString();
                    String password = Objects.requireNonNull(passwordEditText.getText()).toString();
                    signUpWithEmail(email, password);

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

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void signUpWithEmail(String email, String password){
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                          customAlertDialogProgress.hide();

                            Intent intent = new Intent(SignUpForMultipleAuthEmail.this,
                                    UserActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityFromChild(SignUpForMultipleAuthEmail.this, intent, 2);
                        } else {
                            View view = findViewById(R.id.activity_sign_up_for_anonymous_layout);
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
    protected void onDestroy() {
        customAlertDialogProgress.dismiss();
        super.onDestroy();
    }
}