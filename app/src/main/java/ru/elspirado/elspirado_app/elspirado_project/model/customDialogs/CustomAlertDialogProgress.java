package ru.elspirado.elspirado_app.elspirado_project.model.customDialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import ru.elspirado.elspirado_app.elspirado_project.R;

public class CustomAlertDialogProgress extends AlertDialog {

    public CustomAlertDialogProgress(Context context) {
        super(context);

        @SuppressLint("InflateParams")
        View customAlertDialogView = getLayoutInflater().inflate(R.layout.fragment_custom_alert_dialog_progress,null);

        super.setCancelable(false);

        super.setView(customAlertDialogView);
    }
}