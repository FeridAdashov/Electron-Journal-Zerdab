package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Teacher;


import android.app.Activity;
import android.os.Bundle;
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
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherProfilesFragment extends Fragment {

    private final ArrayList<String> nameList = new ArrayList<>();
    private final ArrayList<String> searchNameList = new ArrayList<>();
    private final ArrayList<String> userNameList = new ArrayList<>();
    private CustomProgressDialog progressDialog;
    private SchoolRecyclerTeacherProfiles adapter;
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
        configureListViewManagers();
        loadUsers();
    }

    private void configureRefreshButton() {
        ImageButton refresh = view.findViewById(R.id.imageButtonRefresh);
        refresh.setOnClickListener(v -> loadUsers());
    }

    private void configureSearchEditText() {
        EditText etSearch = view.findViewById(R.id.etSearch);
        SharedClass.configureSearchEditText(etSearch, nameList, searchNameList, adapter);
    }

    private void configureListViewManagers() {
        adapter = new SchoolRecyclerTeacherProfiles(activity, getChildFragmentManager(), nameList, searchNameList, userNameList);
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

        databaseReference.child("TEACHERS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot snapshotStudent : snapshot.getChildren()) {
                        nameList.add(snapshotStudent.child("name").getValue(String.class));
                        userNameList.add(snapshotStudent.getKey());
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
