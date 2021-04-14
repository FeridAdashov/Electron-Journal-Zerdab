package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Teacher;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Teacher.Models.ManagerTeacherModel;
import com.ej.zerdabiyolu2.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ManagerRecyclerTeacherProfiles extends RecyclerView.Adapter<ManagerRecyclerTeacherProfiles.MyViewHolder> {

    private final Activity activity;
    private final ArrayList<String> searchNameList;
    private final HashMap<String, ManagerTeacherModel> map;


    public ManagerRecyclerTeacherProfiles(Activity activity,
                                          ArrayList<String> searchNameList,
                                          HashMap<String, ManagerTeacherModel> map) {
        this.activity = activity;
        this.searchNameList = searchNameList;
        this.map = map;
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
        holder.cardView.setOnClickListener(v -> openTeacherValuationDialog(username));
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
        if (lastCheckedTime == null) lastCheckedTime = "2000_01";
        else lastCheckedTime = lastCheckedTime.substring(0, lastCheckedTime.lastIndexOf("_"));

        try {
            DateFormat df = new SimpleDateFormat("yyyy_MM");
            Calendar calendar = Calendar.getInstance();
            int current = calendar.get(Calendar.MONTH);

            calendar.setTime(df.parse(lastCheckedTime));
            int last = calendar.get(Calendar.MONTH);

            return last == current;
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

    private void openTeacherValuationDialog(String username) {
        Intent intent = new Intent(activity, TeacherValuationActivity.class);
        intent.putExtra("user", username);
        activity.startActivity(intent);
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