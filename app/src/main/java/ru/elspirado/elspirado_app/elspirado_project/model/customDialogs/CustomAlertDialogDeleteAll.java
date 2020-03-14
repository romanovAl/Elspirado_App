package ru.elspirado.elspirado_app.elspirado_project.model.customDialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ru.elspirado.elspirado_app.elspirado_project.R;

public class CustomAlertDialogDeleteAll extends AlertDialog {

    private Context context;

    private Button buttonNegative, buttonPositive;

    public CustomAlertDialogDeleteAll(Context context, int positiveButtonTitle, int negativeButtonTitle) {
        super(context);

        this.context = context;

        final String password = context.getResources().getString(R.string.delete_all_password); //TODO id скорее всего не должен быть таким)

        @SuppressLint("InflateParams")
        View customAlertDialogView = getLayoutInflater().inflate(R.layout.fragment_custom_alert_dialog_delete_all,null);

        buttonPositive = customAlertDialogView.findViewById(R.id.button_positive);
        buttonNegative = customAlertDialogView.findViewById(R.id.button_negative);

        buttonPositive.setText(context.getResources().getString(positiveButtonTitle));
        buttonNegative.setText(context.getResources().getString(negativeButtonTitle));
        buttonPositive.setEnabled(false);

        EditText editText = customAlertDialogView.findViewById(R.id.editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string = s.toString();

                if(string.equals(password)){
                    buttonPositive.setEnabled(true);
                } else {
                    buttonPositive.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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