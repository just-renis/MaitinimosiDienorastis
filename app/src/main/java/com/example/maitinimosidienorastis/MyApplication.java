package com.example.maitinimosidienorastis;

import android.app.Application;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "PERSISTENCE ENABLED", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
