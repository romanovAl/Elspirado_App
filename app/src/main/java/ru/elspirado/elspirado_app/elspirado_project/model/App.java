package ru.elspirado.elspirado_app.elspirado_project.model;

import android.app.Application;

import androidx.room.Room;

import ru.elspirado.elspirado_app.elspirado_project.model.roomStuff.RecorderRoomDataBase;


public class App extends Application {
    public static App instance;

    private RecorderRoomDataBase database;

    @Override
    public void onCreate() {
        instance = this;

        database = Room.databaseBuilder(this, RecorderRoomDataBase.class, "database")
                .build();

//        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("4965887b-a6a9-4967-a0b7-3127c762394d").build();
//        // Initializing the AppMetrica SDK.
//        YandexMetrica.activate(this, config);
//        // Automatic tracking of user activity.
//        YandexMetrica.enableActivityAutoTracking(this);
//        //Чекает локацию
//        YandexMetrica.setLocationTracking(true);

        super.onCreate();
    }

    public static App getInstance() {
        return instance;
    }

    public RecorderRoomDataBase getDatabase() {
        return database;
    }
}