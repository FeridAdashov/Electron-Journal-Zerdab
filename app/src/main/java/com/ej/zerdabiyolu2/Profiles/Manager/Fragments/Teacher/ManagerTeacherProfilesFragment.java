package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Teacher;


import android.app.Activity;
import android.os.Bundle;
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
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Teacher.Models.ManagerTeacherModel;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ManagerTeacherProfilesFragment extends Fragment {

    private final ArrayList<String> nameList = new ArrayList<>();
    private final ArrayList<String> searchNameList = new ArrayList<>();
    private final HashMap<String, ManagerTeacherModel> teacherMap = new HashMap<>();
    private CustomProgressDialog progressDialog;
    private ManagerRecyclerTeacherProfiles adapter;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        configureListViewManagers();
        loadUsers();
    }

    private void configureSearchEditText() {
        EditText etSearch = view.findViewById(R.id.etSearch);
        SharedClass.configureSearchEditText(etSearch, nameList, searchNameList, adapter);
    }

    private void configureListViewManagers() {
        adapter = new ManagerRecyclerTeacherProfiles(activity, searchNameList, teacherMap);
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
        searchNameList.clear();
        teacherMap.clear();

        databaseReference.child("TEACHERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot snapshotStudent : snapshot.getChildren()) {
                        String name = snapshotStudent.child("name").getValue(String.class);
                        nameList.add(name);
                        teacherMap.put(name, new ManagerTeacherModel(
                                snapshotStudent.getKey(),
                                snapshotStudent.child("lastCheckedTime").getValue(String.class),
                                snapshotStudent.child("active").getValue(Boolean.class)));
                    }
                    searchNameList.addAll(nameList);
                    configureListViewManagers();
                    progressDialog.dismiss();
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
}
