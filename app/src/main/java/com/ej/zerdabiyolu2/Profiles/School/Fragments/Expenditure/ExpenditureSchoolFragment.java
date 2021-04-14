package com.ej.zerdabiyolu2.Profiles.School.Fragments.Expenditure;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class ExpenditureSchoolFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private final ArrayList<String> nameList = new ArrayList<>();
    private final ArrayList<String> searchNameList = new ArrayList<>();
    private final ArrayList<String> dateList = new ArrayList<>();
    private final ArrayList<Double> amountList = new ArrayList<>();
    public String begin_date, end_date;
    private CustomProgressDialog progressDialog;
    private SchoolRecyclerExpenditure adapter;
    private TextView endDateTextView, beginDateTextView;
    private DatabaseReference databaseReference;
    private Activity activity;
    private View view;
    private boolean dateStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_other_profit_school, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        this.view = view;

        databaseReference = DatabaseFunctions.getDatabases(activity).get(0).child("EXPENDITURE");
        progressDialog = new CustomProgressDialog(activity, getString(R.string.data_loading));

        configureListViewExpenditure();
        configureSearchEditText();
        configureBtnOther();
        configureBeginEndTextViews();
        configureImageButtonRefresh();
    }

    private void configureImageButtonRefresh() {
        ImageButton refresh = view.findViewById(R.id.imageButtonRefresh);
        refresh.setOnClickListener(v -> loadExpenditureHistory());
    }

    private void configureBeginEndTextViews() {
        beginDateTextView = view.findViewById(R.id.textViewBeginDate);
        endDateTextView = view.findViewById(R.id.textViewEndDate);

        begin_date = end_date = CustomDateTime.getDate(new Date());
        beginDateTextView.setText(begin_date);
        endDateTextView.setText(begin_date);

        beginDateTextView.setOnClickListener(v -> {
            dateStatus = true;
            SharedClass.showDatePickerDialog(activity, this);
        });

        endDateTextView.setOnClickListener(v -> {
            dateStatus = false;
            SharedClass.showDatePickerDialog(activity, this);
        });
    }

    private void loadExpenditureHistory() {
        progressDialog.show();

        nameList.clear();
        searchNameList.clear();
        dateList.clear();
        amountList.clear();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotDate : snapshot.getChildren()) {
                    String date = snapshotDate.getKey();

                    if (date != null && SharedClass.checkDate(begin_date, date, end_date)) {
                        for (DataSnapshot snapshotPayment : snapshotDate.getChildren()) {
                            Double amount = snapshotPayment.getValue(Double.class);

                            dateList.add(date);
                            nameList.add(snapshotPayment.getKey());
                            searchNameList.add(snapshotPayment.getKey());
                            amountList.add(amount);
                        }
                    }
                }
                progressDialog.dismiss();
                configureListViewExpenditure();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configureBtnOther() {
        ImageButton btnOther = view.findViewById(R.id.other_profit_btn);
        btnOther.setOnClickListener(view12 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.layout_profit_dialog, null);
            builder.setView(dialogView);

            final EditText nameOfExpenditure = dialogView.findViewById(R.id.nameOfProfit);
            final EditText amountOfExpenditure = dialogView.findViewById(R.id.amountOfProfit);

            builder.setTitle(getString(R.string.special_profit));
            builder.setPositiveButton(R.string.ok, (di, i) -> {
                String date = CustomDateTime.getDate(new Date());
                String time = CustomDateTime.getTime(new Date());
                final String aop = amountOfExpenditure.getText().toString();

                if (aop.equals("") || aop.equals(".")) return;
                double amount = Double.parseDouble(aop);

                String name = nameOfExpenditure.getText().toString();

                dateList.add(date);
                nameList.add(name);
                searchNameList.add(name);
                amountList.add(amount);
                adapter.notifyDataSetChanged();

                databaseReference.child(date + "/" + name).setValue(amount);
                DatabaseFunctions.getDatabases(activity).get(0).child("REPORT/Expenditure/" + date + " " + time).setValue(amount);

                DatabaseFunctions.changeBudget(activity, amount, false);
            });
            builder.setNegativeButton(R.string.m_cancel, (di, i) -> di.dismiss());
            builder.create().show();
        });
    }

    private void configureSearchEditText() {
        EditText etSearch = view.findViewById(R.id.etSearch);
        SharedClass.configureSearchEditText(etSearch, nameList, searchNameList, adapter);
    }

    private void configureListViewExpenditure() {
        adapter = new SchoolRecyclerExpenditure(nameList, searchNameList, dateList, amountList);
        RecyclerView myView = view.findViewById(R.id.recyclerviewOtherProfits);
        myView.setHasFixedSize(true);
        myView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myView.setLayoutManager(llm);
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
