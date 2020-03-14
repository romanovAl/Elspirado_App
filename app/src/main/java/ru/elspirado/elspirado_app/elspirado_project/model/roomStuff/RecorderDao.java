package ru.elspirado.elspirado_app.elspirado_project.model.roomStuff;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.elspirado.elspirado_app.elspirado_project.model.Recorder;

@Dao
public interface RecorderDao {

    @Query("SELECT * FROM recorder ORDER BY time")
    List<Recorder> selectAll();

    @Query("SELECT * FROM recorder WHERE time BETWEEN :fromTime AND :toTime ORDER BY time")
    List<Recorder> selectTimeRange(long fromTime, long toTime);

    @Query("SELECT * FROM recorder WHERE id = :id")
    Recorder select(String id);

    @Query("DELETE FROM recorder")
    void deleteAll();

    @Insert
    void insert(Recorder recorder);

    @Update
    void update(Recorder recorder);

    @Delete
    void delete(Recorder recorder);
}