package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Student.ShowProfit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.HashMap;

public class ShowProfitHistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String begin_date, end_date;
    private final ArrayList<String> timeList = new ArrayList<>();
    private final ArrayList<String> searchTimeList = new ArrayList<>();
    private final HashMap<String, PaymentModel> map = new HashMap<>();
    private CustomProgressDialog progressDialog;
    private SchoolRecyclerPayment adapter;
    private TextView endDateTextView, beginDateTextView;
    private DatabaseReference databaseReference;
    private Activity activity;
    private boolean dateStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profit_history);

        String user = getIntent().getStringExtra("user");

        if (user != null) {
            activity = this;
            databaseReference = DatabaseFunctions.getDatabases(activity).get(0).child("PROFIT/STUDENT_PAYMENT/" + user);
            progressDialog = new CustomProgressDialog(activity, getString(R.string.data_loading));

            configureBeginEndTextViews();
            configureImageButtonRefresh();
        }
    }

    private void configureImageButtonRefresh() {
        ImageButton refresh = findViewById(R.id.imageButtonRefresh);
        refresh.setOnClickListener(v -> loadPaymentHistory());
    }

    private void loadPaymentHistory() {
        progressDialog.show();
        timeList.clear();
        searchTimeList.clear();
        map.clear();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshotDate : snapshot.getChildren()) {
                    String date = snapshotDate.getKey();

                    if (date != null && SharedClass.checkDate(begin_date, date, end_date)) {
                        Double amount = snapshotDate.child("amount").getValue(Double.class);
                        Double payed = snapshotDate.child("payed").getValue(Double.class);
                        Double debt = snapshotDate.child("debt").getValue(Double.class);

                        if (amount == null || payed == null || debt == null) return;

                        timeList.add(date);
                        searchTimeList.add(date);
                        map.put(date, new PaymentModel(amount, payed, debt));
                    }
                }
                progressDialog.dismiss();
                configureListViewPayments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configureBeginEndTextViews() {
        beginDateTextView = findViewById(R.id.textViewBeginDate);
        endDateTextView = findViewById(R.id.textViewEndDate);

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

    private void configureListViewPayments() {
        adapter = new SchoolRecyclerPayment(searchTimeList, map);
        RecyclerView myView = findViewById(R.id.recyclerviewStudentPayment);
        myView.setHasFixedSize(true);
        myView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myView.setLayoutManager(llm);

        configureSearchEditText();
    }

    private void configureSearchEditText() {
        EditText etSearch = findViewById(R.id.etSearch);
        SharedClass.configureSearchEditText(etSearch, timeList, searchTimeList, adapter);
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