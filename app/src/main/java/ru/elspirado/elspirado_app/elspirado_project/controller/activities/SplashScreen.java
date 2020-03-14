package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import ru.elspirado.elspirado_app.elspirado_project.R;
import shortbread.Shortbread;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        Shortbread.create(this);

        Button btnOld, btnNew;

        btnOld = findViewById(R.id.button_old);
        btnNew = findViewById(R.id.button_new);

        btnOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                SplashScreen.this.startActivity(intent);
                //SplashScreen.this.finish();
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreen.this,NewMainActivity.class);
                SplashScreen.this.startActivity(intent);
                //SplashScreen.this.finish();
            }
        });



//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
//                SplashScreen.this.startActivity(intent);
//                SplashScreen.this.finish();
//            }
//        },2000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}