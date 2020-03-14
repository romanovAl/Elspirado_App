package ru.elspirado.elspirado_app.elspirado_project.controller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

import ru.elspirado.elspirado_app.elspirado_project.R;

public class FragmentUser extends Fragment {

    private ImageView imageViewEmail, imageViewGoogle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        TextView textViewMail, textViewChangePassword, textViewDeleteAllData;

        MaterialButton buttonShareData = view.findViewById(R.id.button_share_data);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        textViewMail = view.findViewById(R.id.textView_mail);
        textViewMail.setText(Objects.requireNonNull(currentUser).getEmail());

        textViewChangePassword = view.findViewById(R.id.textView_change_password);
        textViewDeleteAllData = view.findViewById(R.id.textView_delete_all_data);

        imageViewEmail = view.findViewById(R.id.imageView_email);
        imageViewGoogle = view.findViewById(R.id.imageView_google);

        textViewChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.fragmentUserChangePasswordListener();
            }
        });

        textViewDeleteAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.fragmentUserDeleteAllDataListener();
            }
        });

        imageViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.fragmentUserAddEmailListener();
            }
        });

        imageViewGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.fragmentUserAddGoogleAccountListener();
            }
        });

        buttonShareData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.fragmentUserShareDataListener();
            }
        });

        checkProviders();

        return view;
    }

    void checkProviders(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        List<String> listProviders = Objects.requireNonNull(mAuth.getCurrentUser()).getProviders();

        imageViewEmail.setClickable(true);
        imageViewGoogle.setClickable(true);

        for(int i = 0; i < Objects.requireNonNull(listProviders).size(); i++){

            if(listProviders.get(i).equals("google.com")){ //Если человек зареган через гугл
                imageViewGoogle.setImageDrawable(Objects.requireNonNull(getActivity())
                        .getDrawable(R.drawable.ic_done_black_24dp));

                imageViewGoogle.setClickable(false);
            }

            if(listProviders.get(i).equals("password")){ //Если человек зареган через почу
                imageViewEmail.setImageDrawable(Objects.requireNonNull(getActivity()).
                        getDrawable(R.drawable.ic_done_black_24dp));

                imageViewEmail.setClickable(false);
            }
        }
    }

    public interface OnFragmentListener {

        void fragmentUserAddEmailListener();

        void fragmentUserAddGoogleAccountListener();

        void fragmentUserChangePasswordListener();

        void fragmentUserDeleteAllDataListener();

        void fragmentUserShareDataListener();
    }

    private OnFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentListener) {
            mListener = (OnFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentListener");
        }
    }
}