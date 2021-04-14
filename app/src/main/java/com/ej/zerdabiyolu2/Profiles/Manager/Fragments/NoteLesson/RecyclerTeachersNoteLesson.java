package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.NoteLesson;

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

import com.ej.zerdabiyolu2.Profiles.Manager.Fragments.ExamResult.GiveExamResultActivity;
import com.ej.zerdabiyolu2.R;

import java.util.ArrayList;

public class RecyclerTeachersNoteLesson extends RecyclerView.Adapter<RecyclerTeachersNoteLesson.MyViewHolder> {

    private final ArrayList<String> nameList;
    private final ArrayList<String> searchNameList;
    private final ArrayList<Integer> teacherCountOfClass;
    private final ArrayList<Integer> givenResultCountList;
    private final ArrayList<Boolean> giveResultList;

    public RecyclerTeachersNoteLesson(
                                      ArrayList<String> nameList,
                                      ArrayList<String> searchNameList,
                                      ArrayList<Integer> teacherCountOfClass,
                                      ArrayList<Integer> noteCountList,
                                      ArrayList<Boolean> isNoteList) {
        this.nameList = nameList;
        this.searchNameList = searchNameList;
        this.teacherCountOfClass = teacherCountOfClass;
        this.givenResultCountList = noteCountList;
        this.giveResultList = isNoteList;
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
        int countOfClass = teacherCountOfClass.get(position);
        int givenResultCount = givenResultCountList.get(position);
        boolean isNote = giveResultList.get(position);

        holder.name.setText(name + "   ( " + givenResultCount + " / " + countOfClass + " )");
        holder.name.setTextColor(Color.parseColor(isNote ? "#01294B" : "#FFFFFF"));
        holder.name.setBackgroundColor(Color.parseColor(isNote ? "#FFFFFF" : "#FF5722"));
    }

    @Override
    public int getItemCount() {
        return searchNameList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;

        MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cardTextViewTeacherName);
        }
    }
}