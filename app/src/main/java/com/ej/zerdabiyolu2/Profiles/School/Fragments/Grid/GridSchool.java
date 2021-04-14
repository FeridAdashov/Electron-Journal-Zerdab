package com.ej.zerdabiyolu2.Profiles.School.Fragments.Grid;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class GridSchool extends Fragment {

    private final ArrayList<TextView> dayTextViewList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private CustomProgressDialog progressDialog;
    private View view;
    private Activity activity;
    private ArrayList<String> listClasses = new ArrayList<>();
    private int selectedClass = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        activity = getActivity();

        databaseReference = DatabaseFunctions.getDatabases(getActivity()).get(0);
        progressDialog = new CustomProgressDialog(activity, activity.getString(R.string.data_loading));

        configureTextViews();
        loadClasses();
    }

    private void configureTextViews() {
        dayTextViewList.add(view.findViewById(R.id.textViewOne));
        dayTextViewList.add(view.findViewById(R.id.textViewTwo));
        dayTextViewList.add(view.findViewById(R.id.textViewThree));
        dayTextViewList.add(view.findViewById(R.id.textViewFour));
        dayTextViewList.add(view.findViewById(R.id.textViewFive));
        dayTextViewList.add(view.findViewById(R.id.textViewSix));
        dayTextViewList.add(view.findViewById(R.id.textViewSeven));

        for (int i = 0; i < dayTextViewList.size(); ++i) {
            int finalI = i;
            dayTextViewList.get(i).setOnClickListener(v -> {
                if (selectedClass == 0) SharedClass.showSnackBar(activity, "Sinif seçin");
                else openGrid(listClasses.get(selectedClass), finalI + 1);
            });
        }
    }

    private void openGrid(String sClass, int day) {
        progressDialog.show();
        databaseReference.child("GRID/" + sClass + "/" + day).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();

                String grid = snapshot.getValue(String.class);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Sinif: " + sClass + ",  Gün: " + day);
                EditText editText = new EditText(activity);
                editText.setText(grid);
                editText.setTextColor(Color.parseColor("#01294b"));
                editText.setHint("Cədvəli daxil edin");
                builder.setView(editText);
                builder.setPositiveButton(R.string.save, (dialog, which) -> {
                    snapshot.getRef().setValue(editText.getText().toString());
                });
                builder.setNegativeButton(R.string.m_cancel, (dialog, which) -> dialog.cancel());
                builder.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                        listClasses.add(0, "Sinif Seçin");
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
                selectedClass = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
