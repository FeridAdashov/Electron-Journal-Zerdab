package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.ExamResult;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.R;

import java.util.ArrayList;

public class RecyclerTeachersForExamResult extends RecyclerView.Adapter<RecyclerTeachersForExamResult.MyViewHolder> {

    private final Activity activity;
    private final ArrayList<String> nameList;
    private final ArrayList<String> searchNameList;
    private final ArrayList<String> userNameList;
    private final ArrayList<Integer> teacherCountOfClass;
    private final ArrayList<Integer> givenResultCountList;
    private final ArrayList<Boolean> giveResultList;


    public RecyclerTeachersForExamResult(Activity activity,
                                         ArrayList<String> nameList,
                                         ArrayList<String> searchNameList,
                                         ArrayList<String> userNameList,
                                         ArrayList<Integer> teacherCountOfClass,
                                         ArrayList<Integer> givenResultCountList,
                                         ArrayList<Boolean> isGivenResultList) {
        this.activity = activity;
        this.nameList = nameList;
        this.searchNameList = searchNameList;
        this.userNameList = userNameList;
        this.teacherCountOfClass = teacherCountOfClass;
        this.givenResultCountList = givenResultCountList;
        this.giveResultList = isGivenResultList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_school_teacher_profile, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        int position = nameList.indexOf(searchNameList.get(pos));

        String name = nameList.get(position);
        String username = userNameList.get(position);
        int countOfClass = teacherCountOfClass.get(position);
        int givenResultCount = givenResultCountList.get(position);
        boolean isResultGiven = giveResultList.get(position);

        holder.name.setText(name + "   ( " + givenResultCount + " / " + countOfClass + " )");
        holder.name.setTextColor(Color.parseColor(isResultGiven ? "#01294B" : "#FFFFFF"));
        holder.name.setBackgroundColor(Color.parseColor(isResultGiven ? "#FFFFFF" : "#FF5722"));
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, GiveExamResultActivity.class);
            intent.putExtra("teacher", name);
            intent.putExtra("teacherUsername", username);
            intent.putExtra("givenResultCount", givenResultCount);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return searchNameList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView name;

        MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewTeacherProfile);
            name = itemView.findViewById(R.id.cardTextViewTeacherName);
        }
    }
}