package com.ej.zerdabiyolu2.Profiles.Student.Lesson;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.CustomDateTime;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.Helper.StorageFunctions;
import com.ej.zerdabiyolu2.ListAdapters.CustomExpandableListAdapter;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class StudentLessonsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private final ArrayList<String> listDataHeader = new ArrayList<>();
    private final HashMap<String, ArrayList<String>> listDataChild = new HashMap<>();
    private CustomProgressDialog progressDialog;

    private CustomExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private TextView beginDateTextView, endDateTextView;

    private DatabaseReference databaseReference;

    private Activity activity;
    private View view;
    private String username, studentClass, studentName, begin_date, end_date;
    private boolean dateStatus = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_student_lessons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        this.view = view;


        progressDialog = new CustomProgressDialog(activity, getString(R.string.data_loading));
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        username = firebaseAuth.getCurrentUser().getEmail().split("@")[0];
        databaseReference = DatabaseFunctions.getDatabases(activity).get(0);

        getStudentClass();
        configureSearchButton();
        configureDateTextView();
        configureExpandableList();
    }

    private void getStudentClass() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentClass = snapshot.child("STUDENT_CLASS/" + username).getValue(String.class);
                studentName = snapshot.child("STUDENTS/" + studentClass + "/" + username + "/name").getValue(String.class);
                load();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SharedClass.showSnackBar(activity, getString(R.string.maybe_profile_data_wrong));
            }
        });
    }

    private void configureExpandableList() {
        expListView = view.findViewById(R.id.listViewData);
        expListView.setOnGroupClickListener((expandableListView, view, i, l) -> {
            if (listDataChild.get(listDataHeader.get(i)).size() > 0) {
                if (expListView.isGroupExpanded(i))
                    expListView.collapseGroup(i);
                else {
                    for (int j = 0; j < listAdapter.getGroupCount(); j++)
                        expListView.collapseGroup(j);
                    expListView.expandGroup(i);
                }
            }
            expandableListView.smoothScrollToPosition(i);
            return true;
        });
        expListView.setOnChildClickListener((expandableListView, view, headerPos, childPos, l) -> {
            showDialog(headerPos, childPos);
            return true;
        });
        expListView.setOnItemLongClickListener((parent, view, position, id) -> {
            final Context context = parent.getContext();
            new AlertDialog.Builder(context)
                    .setTitle(getString(R.string.store_data))
                    .setPositiveButton(R.string.ok, (dialog, which) ->
                            StorageFunctions.storeStudentAllLessonTimeData(activity, databaseReference.child("LESSON_HISTORY/" + username + " " + studentName)))
                    .setNegativeButton(R.string.m_cancel, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        });
        listAdapter = new CustomExpandableListAdapter(activity, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

    }

    private void configureSearchButton() {
        TextView textViewSearch = view.findViewById(R.id.textViewSearch);
        textViewSearch.setOnClickListener(v -> load());
    }

    private void showDialog(int headerPos, int childPos) {
        progressDialog.show();

        String child = listDataChild.get(listDataHeader.get(headerPos)).get(childPos);
        String date = child.split(" ")[0].trim();
        String time = child.replace(date, "").trim();

        databaseReference.child("LESSON_HISTORY/" + username + " " + studentName + "/" + date + "/" + time)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();

                        ShowLessonDataDialog dialog = new ShowLessonDataDialog();
                        dialog.setDefaultValues(
                                dataSnapshot.child("lesson").getValue(String.class),
                                dataSnapshot.child("lessonSubject").getValue(String.class),
                                dataSnapshot.child("lessonRate").getValue(String.class),
                                dataSnapshot.child("behaviourRate").getValue(String.class),
                                dataSnapshot.child("extraInformation").getValue(String.class));
                        dialog.show(getChildFragmentManager(), "Example");
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void configureDateTextView() {
        beginDateTextView = view.findViewById(R.id.textViewBeginDate);
        endDateTextView = view.findViewById(R.id.textViewEndDate);

        begin_date = end_date = CustomDateTime.getDate(new Date());
        beginDateTextView.setText(begin_date);
        endDateTextView.setText(end_date);

        beginDateTextView.setOnClickListener(v -> {
            dateStatus = true;
            SharedClass.showDatePickerDialog(activity, this);
        });
        endDateTextView.setOnClickListener(v -> {
            dateStatus = false;
            SharedClass.showDatePickerDialog(activity, this);
        });
    }

    private void load() {
        if (studentClass == null) return;

        progressDialog.show();

        listDataHeader.clear();
        listDataChild.clear();
        expListView.clearChoices();

        databaseReference.child("LESSON_HISTORY/" + username + " " + studentName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotDate : snapshot.getChildren()) {
                    String date = snapshotDate.getKey();
                    if (SharedClass.checkDate(begin_date, date, end_date))
                        for (DataSnapshot snapshotTime : snapshotDate.getChildren()) {
                            String time = snapshotTime.getKey();

                            String s = snapshotTime.child("lesson").getValue(String.class);
                            if (!listDataHeader.contains(s)) {
                                listDataHeader.add(s);
                                ArrayList<String> al = new ArrayList<>();
                                al.add(date + "    " + time);
                                listDataChild.put(s, al);
                            } else
                                listDataChild.get(s).add(date + "    " + time);
                        }
                }
                Collections.reverse(listDataHeader);
                listAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

                if (listDataHeader.size() > 0) expListView.expandGroup(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        ++month;
        String m = month < 10 ? "0" + month : "" + month;
        String d = day < 10 ? "0" + day : "" + day;
        String date = year + "_" + m + "_" + d;

        if (dateStatus) {
            beginDateTextView.setText(date);
            begin_date = date;
        } else {
            endDateTextView.setText(date);
            end_date = date;
        }
    }
}
