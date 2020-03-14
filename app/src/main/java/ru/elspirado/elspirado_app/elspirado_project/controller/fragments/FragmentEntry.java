package ru.elspirado.elspirado_app.elspirado_project.controller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.SignInButton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import ru.elspirado.elspirado_app.elspirado_project.R;

public class FragmentEntry extends Fragment {

    private TextInputLayout loginInputLayout,
        passwordInputLayout;

    private TextInputEditText loginEditText,
        passwordEditText;

    private Button enterButton, registerButton,skipRegButton;

    private TextView titleTextView;

    private MaterialButton signInButtonGoogle;

    private boolean fragmentIsVisible = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_entry, container, false);

        loginInputLayout = view.findViewById(R.id.loginTextInputLayout);
        passwordInputLayout = view.findViewById(R.id.passwordTextInputLayout);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginEditText = view.findViewById(R.id.loginEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        enterButton = view.findViewById(R.id.enterButton);
        registerButton = view.findViewById(R.id.registerButton);
        skipRegButton = view.findViewById(R.id.skipEntryButton);

        titleTextView = view.findViewById(R.id.titleTextView);

        signInButtonGoogle = view.findViewById(R.id.fragmentEntryGoogleSignIn);

        titleTextView.setText(Html.fromHtml(getResources().getString(R.string.to_enter_account) + //Делает надпись двумя цветами
                "<font color = #ff9e80> " +
                getResources().getString(R.string.Main_activity_title) +
                "</font>"));

        setRetainInstance(true);

        signInButtonGoogle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.fragmentEntryContinueWithGoogle();
            }
        });

        enterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String login = Objects.requireNonNull(loginEditText.getText()).toString();
                    String password = Objects.requireNonNull(passwordEditText.getText()).toString();
                    mListener.fragmentEntrySignIn(login, password);
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

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragmentIsVisible(false);
                mListener.goToFragmentReg();
            }
        });

        skipRegButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.fragmentEntryСontinueWithoutEntry();
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
                    String login = Objects.requireNonNull(loginEditText.getText()).toString();
                    String password = Objects.requireNonNull(passwordEditText.getText()).toString();
                    mListener.fragmentEntrySignIn(login, password);
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

        return view;
    }

    public boolean isFragmentIsVisible() {
        return fragmentIsVisible;
    }

    public void setFragmentIsVisible(boolean fragmentIsVisible) {
        this.fragmentIsVisible = fragmentIsVisible;
    }

    public interface OnFragmentListener {

        void fragmentEntrySignIn(String login, String password);

        void goToFragmentReg();

        void fragmentEntryСontinueWithoutEntry();

        void fragmentEntryContinueWithGoogle();
    }

    private OnFragmentListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FragmentReg.OnFragmentListener) {
            mListener = (OnFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnFragmentListener");
        }
    }
}