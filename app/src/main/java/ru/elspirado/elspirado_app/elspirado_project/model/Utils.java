package ru.elspirado.elspirado_app.elspirado_project.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Utils {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_IS_ENTERED = "false";

    public static final int FRAGMENT_MODE_GRAPH = 0;
    public static final int FRAGMENT_MODE_RECYCLER = 1;
    public static final int FRAGMENT_MODE_RECYCLER_SELECTED = 2;
    public static final int FRAGMENT_MODE_ACCOUNT = 3;

    public static final int ACTIVITY_MODE_RANGE_All = 0;
    public static final int ACTIVITY_MODE_RANGE_SELECTED = 1;

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {

        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
            mDatabase.getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).keepSynced(true);
        }
        return mDatabase;
    }

}