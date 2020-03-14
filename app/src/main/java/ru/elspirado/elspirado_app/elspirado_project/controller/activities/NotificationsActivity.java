package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.Date;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.model.Notification;
import ru.elspirado.elspirado_app.elspirado_project.model.RecyclerNotificationsAdapter;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private RecyclerNotificationsAdapter adapter;

    private Toolbar toolbar;

    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.titleNotifications);

        recyclerView = findViewById(R.id.notificationRecyclerView);

        fab = findViewById(R.id.fabNotifications);

        ArrayList<Notification> arrayList = new ArrayList<>();
        arrayList.add(new Notification("писосик", 23, new Date().getTime(), true));
        arrayList.add(new Notification("писосик намба два", 23, new Date().getTime(), false));
        arrayList.add(new Notification("писосик намба ту", 23, new Date().getTime(), false));

        adapter = new RecyclerNotificationsAdapter(arrayList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}