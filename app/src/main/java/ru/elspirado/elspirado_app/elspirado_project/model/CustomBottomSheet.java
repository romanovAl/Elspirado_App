package ru.elspirado.elspirado_app.elspirado_project.model;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ru.elspirado.elspirado_app.elspirado_project.R;

public class CustomBottomSheet extends BottomSheetDialogFragment {

    private CustomBottomSheetInterface mListener;

    private Button buttonAccount, buttonSettings, buttonShare, buttonPush;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        buttonAccount = view.findViewById(R.id.bottom_sheet_button_account);
        buttonSettings = view.findViewById(R.id.bottom_sheet_button_settings);
        buttonShare = view.findViewById(R.id.bottom_sheet_button_share);
        buttonPush = view.findViewById(R.id.bottom_sheet_button_notification);

        buttonAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.bottomSheetOnClickAccount();
            }
        });

        buttonSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.bottomSheetOnClickSettings();
            }
        });

        buttonShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.bottomSheetOnClickShare();
            }
        });

        buttonPush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.bottomSheetOnClickNotifications();
            }
        });

        return view;
    }

    public interface CustomBottomSheetInterface{

        void bottomSheetOnClickAccount();

        void bottomSheetOnClickSettings();

        void bottomSheetOnClickShare();

        void bottomSheetOnClickNotifications();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (CustomBottomSheetInterface)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement CustomBottomSheetInterface");
        }
    }
}
