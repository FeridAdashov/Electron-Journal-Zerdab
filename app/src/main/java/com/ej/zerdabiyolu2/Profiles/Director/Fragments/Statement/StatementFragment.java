package com.ej.zerdabiyolu2.Profiles.Director.Fragments.Statement;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class StatementFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private CustomProgressDialog progressDialog;
    private final ArrayList<String> names = new ArrayList<>();

    private DatabaseReference databaseReference;

    private TextView beginDate, endDate, result;
    private RadioButton radioProfit;
    private View view;

    private Activity activity;
    private double allMoney = 0.;
    private int dateStatus = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_statement, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        activity = getActivity();

        progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.data_loading));

        databaseReference = DatabaseFunctions.getDatabases(getActivity()).get(0);

        loadSeed();
    }

    private void loadSeed() {
        result = view.findViewById(R.id.textViewResult);

        beginDate = view.findViewById(R.id.beginDate);
        beginDate.setOnClickListener(view -> {
            dateStatus = 0;
            showDatePickerDialog();
        });

        endDate = view.findViewById(R.id.endDate);
        endDate.setOnClickListener(view -> {
            dateStatus = 1;
            showDatePickerDialog();
        });

        radioProfit = view.findViewById(R.id.radioProfit);

        Button calculate = view.findViewById(R.id.calculate);
        calculate.setOnClickListener(view -> {
            if (!beginDate.getText().toString().contains("_") || !endDate.getText().toString().contains("_")) {
                SharedClass.showSnackBar(activity, getString(R.string.select_beginDate_and_endDate));
                return;
            }

            loadStatement();
        });

        configureListView();
    }

    private void configureListView() {
        ListView listViewStatement = view.findViewById(R.id.listViewStatement);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getContext(), R.layout.list_group, R.id.lblListHeader, names);
        listViewStatement.setAdapter(listAdapter);
    }

    private void loadStatement() {
        progressDialog.show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                names.clear();
                allMoney = 0d;

                try {
                    String begin = beginDate.getText().toString();
                    String end = endDate.getText().toString();

                    if (radioProfit.isChecked()) {
                        addValueToList(snapshot.child("REPORT/StudentPayment"), begin, end, 1);
                        addValueToList(snapshot.child("REPORT/OtherPayment"), begin, end, 2);
                    } else
                        addValueToList(snapshot.child("REPORT/Expenditure"), begin, end, 3);

                    result.setText(String.valueOf(SharedClass.twoDigitDecimal(allMoney)));

                    Collections.sort(names);
                    Collections.reverse(names);
                    configureListView();
                } catch (Exception e) {
                    Log.d("AAAAA", e.toString());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.error_check_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addValueToList(DataSnapshot snapshot, String begin, String end, int status) {
        for (DataSnapshot snapshotDateTime : snapshot.getChildren()) {
            String date = snapshotDateTime.getKey().split(" ")[0];
            if (SharedClass.checkDate(begin, date, end)) {
                Double value = snapshotDateTime.getValue(Double.class);

                String name = "";
                if (status == 1) name = " - " + snapshotDateTime.getKey().split(" ")[2].trim();
                else if (status == 2) name = "  -  Digər";

                names.add(snapshotDateTime.getKey().split(" ")[0]
                        + name + "   :      " + value);
                allMoney += value;
            }
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.m_cancel), datePickerDialog);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        ++month;
        String m = month < 10 ? "0" + month : "" + month;
        String d = day < 10 ? "0" + day : "" + day;
        String date = year + "_" + m + "_" + d;

        if (dateStatus == 0) beginDate.setText(date);
        else endDate.setText(date);
    }
}
