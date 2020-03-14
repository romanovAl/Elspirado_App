package ru.elspirado.elspirado_app.elspirado_project.model.roomStuff;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;
import ru.elspirado.elspirado_app.elspirado_project.model.roomStuff.RecorderDao;

@Database(entities = {Recorder.class}, version = 1, exportSchema = false)
public abstract class RecorderRoomDataBase extends RoomDatabase {
    public abstract RecorderDao recorderDao();
}