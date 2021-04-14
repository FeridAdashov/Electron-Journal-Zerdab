package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.NoteLesson;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.CustomDateTime;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TeachersNoteLessonFragment extends Fragment {

    private final ArrayList<String> nameList = new ArrayList<>();
    private final ArrayList<String> searchTeacherNameList = new ArrayList<>();
    private final ArrayList<String> userNameList = new ArrayList<>();
    private final ArrayList<Integer> countOfClass = new ArrayList<>();
    private final ArrayList<Integer> noteCountList = new ArrayList<>();
    private final ArrayList<Boolean> isNoteList = new ArrayList<>();
    private CustomProgressDialog progressDialog;
    private RecyclerTeachersNoteLesson adapter;
    private DatabaseReference databaseReference;

    private View view;

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_teacher_profiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        this.view = view;

        databaseReference = DatabaseFunctions.getDatabases(activity).get(0);
        progressDialog = new CustomProgressDialog(activity, getString(R.string.data_loading));

        configureRefreshButton();
        configureListViewTeachers();
    }

    private void configureRefreshButton() {
        ImageButton refresh = view.findViewById(R.id.imageButtonRefresh);
        refresh.setOnClickListener(v -> loadUsers());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }

    private void configureSearchEditText() {
        EditText etSearch = view.findViewById(R.id.etSearch);
        SharedClass.configureSearchEditText(etSearch, nameList, searchTeacherNameList, adapter);
    }

    private void configureListViewTeachers() {
        adapter = new RecyclerTeachersNoteLesson(
                nameList, searchTeacherNameList, countOfClass,
                noteCountList, isNoteList);
        RecyclerView myView = view.findViewById(R.id.recyclerviewTeacher);
        myView.setHasFixedSize(true);
        myView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myView.setLayoutManager(llm);

        configureSearchEditText();
    }

    private void loadUsers() {
        progressDialog.show();

        nameList.clear();
        searchTeacherNameList.clear();

        databaseReference.child("TEACHERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot snapshotTeacher : snapshot.getChildren()) {
                        String user = snapshotTeacher.getKey();
                        userNameList.add(user);
                        nameList.add(snapshotTeacher.child("name").getValue(String.class));
                        countOfClass.add(snapshotTeacher.child("countOfClass").getValue(Integer.class));
                        isNoteList.add(false);
                        noteCountList.add(0);
                    }
                    searchTeacherNameList.addAll(nameList);
                    loadIsTeacherNote();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(activity, R.string.error_check_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadIsTeacherNote() {
        databaseReference.child("TEACHER_NOTE_LESSON").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String lastNoteTime = snapshot.child("lastNoteTime").getValue(String.class);

                    Integer noteCount = snapshot.child("noteCount").getValue(Integer.class);
                    if (noteCount == null) {
                        noteCount = 0;
                        snapshot.getRef().child("noteCount").setValue(noteCount);
                    }

                    boolean b = false;
                    if (lastNoteTime != null) {
                        long diffInMillies = Math.abs(new Date().getTime() - CustomDateTime.getDate(lastNoteTime).getTime());
                        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        b = diff > 6;
                    }

                    int position = userNameList.indexOf(snapshot.getKey());

                    if (lastNoteTime == null || (noteCount >= countOfClass.get(position) && b)) {
                        noteCount = 0;
                        snapshot.getRef().child("lastNoteTime").setValue(CustomDateTime.getDate(new Date()));
                        snapshot.getRef().child("noteCount").setValue(noteCount);

                        isNoteList.set(position, true);
                        noteCountList.set(position, noteCount);

                        configureListViewTeachers();
                        progressDialog.dismiss();
                        return;
                    }

                    isNoteList.set(position, !b);
                    noteCountList.set(position, noteCount);
                }

                configureListViewTeachers();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }
}
