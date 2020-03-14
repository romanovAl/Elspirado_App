package ru.elspirado.elspirado_app.elspirado_project.model.customDialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import ru.elspirado.elspirado_app.elspirado_project.R;

public class CustomAlertDialogInfo extends AlertDialog {

    private Button button;

    private Context context;

    public CustomAlertDialogInfo(Context context, int buttonTitle) {
        super(context);

        this.context = context;

        @SuppressLint("InflateParams")
        View customAlertDialogView = getLayoutInflater().inflate(R.layout.fragment_custom_alert_dialog_info,null);

        button = customAlertDialogView.findViewById(R.id.button);
        button.setText(context.getResources().getString(buttonTitle));

        super.setCancelable(false);
        super.setView(customAlertDialogView);
    }

    public void setTitle(int id){
        super.setTitle(context.getResources().getString(id));
    }

    public void setMessage(int id){
        super.setMessage(context.getResources().getString(id));
    }

    public void setButtonClickListener(View.OnClickListener onClickListener){
        button.setOnClickListener(onClickListener);
    }
}