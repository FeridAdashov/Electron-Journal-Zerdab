package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.ExamResult;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class TeachersForExamResultFragment extends Fragment {

    private final ArrayList<String> nameList = new ArrayList<>();
    private final ArrayList<String> searchTeacherNameList = new ArrayList<>();
    private final ArrayList<String> userNameList = new ArrayList<>();
    private final ArrayList<Integer> countOfClass = new ArrayList<>();
    private final ArrayList<Integer> givenResultCountList = new ArrayList<>();
    private final ArrayList<Boolean> isGivenResultList = new ArrayList<>();
    private CustomProgressDialog progressDialog;
    private RecyclerTeachersForExamResult adapter;
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

        configureListViewTeachers();
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
        adapter = new RecyclerTeachersForExamResult(activity,
                nameList, searchTeacherNameList,
                userNameList, countOfClass,
                givenResultCountList, isGivenResultList);
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
                        isGivenResultList.add(false);
                        givenResultCountList.add(0);
                    }
                    searchTeacherNameList.addAll(nameList);
                    loadIsTeacherGiveResult();
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

    private void loadIsTeacherGiveResult() {
        databaseReference.child("TEACHER_GIVE_RESULT").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String lastGiveTime = snapshot.child("lastGiveTime").getValue(String.class);
                    if (TextUtils.isEmpty(lastGiveTime)) continue;

                    Integer givenResultCount = snapshot.child("givenResultCount").getValue(Integer.class);
                    if (givenResultCount == null) continue;

                    Date checkDate = CustomDateTime.getDate(lastGiveTime);
                    Date currentDate = new Date();

                    long diffInMillies = Math.abs(currentDate.getTime() - checkDate.getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    int position = userNameList.indexOf(snapshot.getKey());

                    if (givenResultCount >= countOfClass.get(position)) {
                        givenResultCount = 0;
                        isGivenResultList.set(position, true);
                        snapshot.child("givenResultCount").getRef().setValue(0);
                        snapshot.child("lastGiveTime").getRef().setValue(CustomDateTime.getDate(currentDate));
                    } else if (diff < 14) isGivenResultList.set(position, true);

                    givenResultCountList.set(position, givenResultCount);
                }

                configureListViewTeachers();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
