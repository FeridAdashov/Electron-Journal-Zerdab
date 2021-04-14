package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Student;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolStudentProfilesFragment extends Fragment {

    private final ArrayList<String> nameList = new ArrayList<>();
    private final ArrayList<String> searchNameList = new ArrayList<>();
    private final ArrayList<String> userNameList = new ArrayList<>();
    private CustomProgressDialog progressDialog;
    private SchoolRecyclerStudentProfiles adapter;
    private ArrayList<String> listClasses = new ArrayList<>();

    private DatabaseReference databaseReference;

    private View view;

    private Activity activity;
    private String selectedClass = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_student_profiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        this.view = view;

        databaseReference = DatabaseFunctions.getDatabases(activity).get(0);
        progressDialog = new CustomProgressDialog(activity, getString(R.string.data_loading));

        configureRefreshButton();
        configureListViewStudents();
        loadClasses();
    }

    private void configureRefreshButton() {
        ImageButton refresh = view.findViewById(R.id.imageButtonRefresh);
        refresh.setOnClickListener(v -> loadUsers());
    }

    private void configureSearchEditText() {
        EditText etSearch = view.findViewById(R.id.etSearch);
        SharedClass.configureSearchEditText(etSearch, nameList, searchNameList, adapter);
    }

    private void configureListViewStudents() {
        adapter = new SchoolRecyclerStudentProfiles(
                activity,
                getChildFragmentManager(),
                nameList,
                searchNameList,
                userNameList,
                listClasses,
                selectedClass);
        RecyclerView myView = view.findViewById(R.id.recyclerviewStudents);
        myView.setHasFixedSize(true);
        myView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myView.setLayoutManager(llm);

        configureSearchEditText();
    }

    private void loadClasses() {
        listClasses.clear();
        listClasses.add(0, getString(R.string.class_list));
        notifyClassesSpinner();

        GenericTypeIndicator<ArrayList<String>> s = new GenericTypeIndicator<ArrayList<String>>() {
        };

        progressDialog.show();
        databaseReference.child("SCHOOL").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotSchool : snapshot.getChildren()) {
                    listClasses = snapshotSchool.child("classes").getValue(s);
                    if (listClasses != null) {
                        listClasses.add(0, activity.getString(R.string.class_list));
                        try {
                            notifyClassesSpinner();
                        } catch (Exception e) {
                            Log.d("AAAAAAAA", e.toString());
                        }
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void notifyClassesSpinner() {
        Spinner spinnerClassNames = view.findViewById(R.id.spinnerClassNames);
        ArrayAdapter<String> adapterClasses = new ArrayAdapter<>(getActivity(), R.layout.layout_spinner_item, listClasses);
        spinnerClassNames.setAdapter(adapterClasses);
        spinnerClassNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedClass = listClasses.get(position);
                    loadUsers();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadUsers() {
        progressDialog.show();

        nameList.clear();
        searchNameList.clear();
        userNameList.clear();

        databaseReference.child("STUDENTS/" + selectedClass).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot snapshotStudent : snapshot.getChildren()) {
                        nameList.add(snapshotStudent.child("name").getValue(String.class));
                        userNameList.add(snapshotStudent.getKey());
                    }
                    searchNameList.addAll(nameList);
                    configureListViewStudents();
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
