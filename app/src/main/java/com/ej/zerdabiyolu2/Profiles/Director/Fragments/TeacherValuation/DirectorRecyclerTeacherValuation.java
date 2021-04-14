package com.ej.zerdabiyolu2.Profiles.Director.Fragments.TeacherValuation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Teacher.Models.ManagerTeacherModel;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DirectorRecyclerTeacherValuation extends RecyclerView.Adapter<DirectorRecyclerTeacherValuation.MyViewHolder> {

    private final Activity activity;
    private final ArrayList<String> searchNameList;
    private final HashMap<String, ManagerTeacherModel> map;

    private final CustomProgressDialog progressDialog;


    public DirectorRecyclerTeacherValuation(Activity activity,
                                            ArrayList<String> searchNameList,
                                            HashMap<String, ManagerTeacherModel> map) {
        this.activity = activity;
        this.searchNameList = searchNameList;
        this.map = map;

        progressDialog = new CustomProgressDialog(activity, activity.getString(R.string.data_loading));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_manager_teacher_profile, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        String name = searchNameList.get(pos);

        ManagerTeacherModel model = map.get(name);

        if (model == null) return;

        String username = model.username;
        String lastCheckedTime = model.lastCheckedTime;
        boolean isActive = model.activeness;

        holder.name.setText(name);
        holder.cardView.setOnClickListener(v -> showTeacherValuation(model.username));
        holder.switchMaterial.setChecked(isActive);
        configureSwitch(holder.switchMaterial);
        holder.switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            model.activeness = isChecked;
            DatabaseFunctions.getDatabases(activity).get(0).child("TEACHERS/" + username + "/active").setValue(isChecked);
        });

        if (!check(lastCheckedTime))
            holder.linearLayout.setBackgroundColor(Color.parseColor("#9DFF5722"));
    }

    private boolean check(String lastCheckedTime) {
        if (lastCheckedTime == null) lastCheckedTime = "2000_01_01";
        try {
            DateFormat df = new SimpleDateFormat("yyyy_MM_dd");
            Calendar calendar = Calendar.getInstance();

            int current_month = calendar.get(Calendar.MONTH);

            calendar.setTime(df.parse(lastCheckedTime));

            int last_month = calendar.get(Calendar.MONTH);

            return last_month == current_month;
        } catch (Exception e) {
            Log.d("AAAAAAAAA", e.toString());
            return false;
        }
    }

    private void configureSwitch(SwitchCompat switchCompat) {
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
        };

        int[] thumbColors = new int[]{Color.BLACK, Color.GREEN,};

        int[] trackColors = new int[]{Color.DKGRAY, Color.LTGRAY,};

        DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.getTrackDrawable()), new ColorStateList(states, trackColors));
    }

    private void showTeacherValuation(String username) {
        progressDialog.show();

        DatabaseReference dr = DatabaseFunctions.getDatabases(activity).get(0).child("TEACHER_VALUATION/" + username);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_singlechoice);
                for (DataSnapshot snapshotTime : snapshot.getChildren())
                    arrayAdapter.add(snapshotTime.getKey());
                showTimesAlertDialog(dr, arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    private void showTimesAlertDialog(DatabaseReference dr, ArrayAdapter<String> arrayAdapter) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
        builderSingle.setTitle("Gün seçin");

        builderSingle.setNegativeButton(activity.getString(R.string.m_cancel), (dialog, which) -> dialog.dismiss());
        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            String time = arrayAdapter.getItem(which);

            progressDialog.show();
            dr.child(time).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String text = "Sinif:  " + dataSnapshot.child("className").getValue(String.class) + "\n\n";
                    text += "Dərs:  " + dataSnapshot.child("lessonName").getValue(String.class) + "\n\n";
                    text += "Dərsə Vaxtında Başlanıldımı:  " + dataSnapshot.child("startInTime").getValue(String.class) + "\n\n";
                    text += "Ev Tapşırıqlarını Yoxlanıldımı:  " + dataSnapshot.child("checkHomework").getValue(String.class) + "\n\n";
                    text += "Geyim Balı:  " + dataSnapshot.child("suitableDressed").getValue(String.class) + "\n\n";
                    text += "Səhvlər Üzərində İşlənildimi:  " + dataSnapshot.child("workOnMistakes").getValue(String.class) + "\n\n";
                    text += "Sorğu/Sual Edildimi:  " + dataSnapshot.child("questionAnswer").getValue(String.class) + "\n\n";
                    text += "Vaxtın İdarə Olunma Balı:  " + dataSnapshot.child("manageTime").getValue(String.class) + "\n\n";
                    text += "Yeni Dərsi Öyrədildimi:  " + dataSnapshot.child("teachNewSubject").getValue(String.class) + "\n\n";
                    text += "Ev Tapşırığı Verildimi:  " + dataSnapshot.child("givingHomework").getValue(String.class) + "\n\n";
                    text += "Müasir Texnologiyadan İstifadə:  " + dataSnapshot.child("usingTechnology").getValue(String.class) + "\n\n";
                    text += "Əyani Vəsaitdən İstifadə:  " + dataSnapshot.child("usingVisualAids").getValue(String.class) + "\n\n";
                    text += "Əlavə Məlumat:  " + dataSnapshot.child("extraInformation").getValue(String.class) + "\n\n";

                    progressDialog.dismiss();

                    AlertDialog.Builder builderInner = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK);
                    builderInner.setMessage(text);
                    builderInner.setPositiveButton(activity.getString(R.string.ok), (dialog1, which1) -> dialog1.dismiss());
                    builderInner.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                }
            });

        });
        builderSingle.show();
    }

    @Override
    public int getItemCount() {
        return searchNameList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final LinearLayout linearLayout;
        private final TextView name;
        private final SwitchCompat switchMaterial;

        MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewTeacherProfile);
            linearLayout = itemView.findViewById(R.id.cardViewLinear);
            name = itemView.findViewById(R.id.cardTextViewTeacherName);
            switchMaterial = itemView.findViewById(R.id.cardSwitchTeacherActiveness);
        }
    }
}