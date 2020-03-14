package ru.elspirado.elspirado_app.elspirado_project.model.customDialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import ru.elspirado.elspirado_app.elspirado_project.R;

public class CustomAlertDialog extends AlertDialog {

    private Context context;

    private Button buttonNegative, buttonPositive;

    public CustomAlertDialog(Context context, int positiveButtonTitle, int negativeButtonTitle) {
        super(context);

        this.context = context;

        @SuppressLint("InflateParams")
        View customAlertDialogView = getLayoutInflater().inflate(R.layout.fragment_custom_alert_dialog,null);

        buttonPositive = customAlertDialogView.findViewById(R.id.button_positive);
        buttonNegative = customAlertDialogView.findViewById(R.id.button_negative);

        buttonPositive.setText(context.getResources().getString(positiveButtonTitle));
        buttonNegative.setText(context.getResources().getString(negativeButtonTitle));

        super.setView(customAlertDialogView);
    }

    public void setTitle(int id){
        super.setTitle(context.getResources().getString(id));
    }

    public void setMessage(int id){
        super.setMessage(context.getResources().getString(id));
    }

    public void setPositiveButtonClickListener(View.OnClickListener onClickListener){
        buttonPositive.setOnClickListener(onClickListener);
    }

    public void setNegativeButtonClickListener(View.OnClickListener onClickListener){
        buttonNegative.setOnClickListener(onClickListener);
    }
}