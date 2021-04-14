package com.ej.zerdabiyolu2.Helper;


import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseFunctions {

    public static int database_count = 1;

    private static final ArrayList<String> databaseIds = new ArrayList<>(Arrays.asList(
            "1:106 your id"   //Database 1
    ));

    private static final ArrayList<String> databaseApiKeys = new ArrayList<>(Arrays.asList(
            "AIza your key"         //Database 1
    ));

    private static final ArrayList<String> databaseURLs = new ArrayList<>(Arrays.asList(
            "your database url"        //Database 1
    ));


    public static FirebaseApp getMyFirebaseApp(Context context, int index) {
        --index;

        FirebaseApp app;
        boolean b = false;

        for (FirebaseApp a : FirebaseApp.getApps(context)) {
            if (a.getName().equals(String.valueOf(index)))
                b = true;
        }
        if (!b) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId(databaseIds.get(index)) // Required for Analytics.
                    .setApiKey(databaseApiKeys.get(index)) // Required for Auth.
                    .setDatabaseUrl(databaseURLs.get(index)) // Required for RTDB.
                    .build();
            app = FirebaseApp.initializeApp(context, options, String.valueOf(index));
        } else app = FirebaseApp.getInstance(String.valueOf(index));

        return app;
    }

    public static ArrayList<FirebaseApp> getAllFirebaseApps(Context context) {
        ArrayList<FirebaseApp> apps = new ArrayList<>();
        for (int i = 1; i <= databaseIds.size(); ++i)
            apps.add(getMyFirebaseApp(context, i));
        return apps;
    }

    public static ArrayList<DatabaseReference> getDatabases(Context context) {
        ArrayList<DatabaseReference> databases = new ArrayList<>();
        for (FirebaseApp app : DatabaseFunctions.getAllFirebaseApps(context))
            databases.add(FirebaseDatabase.getInstance(app).getReference());

        return databases;
    }

    public static void changeBudget(Activity activity, double amount, boolean isAdding) {
        final double _amount = isAdding ? amount : -amount;
        getDatabases(activity).get(0).child("BUDGET").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double budget = snapshot.getValue(Double.class);
                budget = budget == null ? _amount : budget + _amount;
                snapshot.getRef().setValue(SharedClass.twoDigitDecimal(budget));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
