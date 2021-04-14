package com.ej.zerdabiyolu2.Profiles.Director.Fragments.Profit;


import android.app.Activity;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Student.ProfitModel;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class StudentProfitDirectorFragment extends Fragment {

    private TextView textViewCommonPayed, textViewCommonDebt;

    private final ArrayList<String> nameList = new ArrayList<>();
    private final ArrayList<String> searchNameList = new ArrayList<>();
    private final HashMap<String, ProfitModel> map = new HashMap<>();
    private CustomProgressDialog progressDialog;
    private DirectorRecyclerStudentPayment adapter;
    private ArrayList<String> listClasses = new ArrayList<>();

    private DatabaseReference databaseReference;

    private View view;

    private Activity activity;
    private String selectedClass = "";
    private Double moneyPerMonth;
    private boolean isDirector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_student_profit_school, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();
        this.view = view;
        
        databaseReference = DatabaseFunctions.getDatabases(activity).get(0);
        progressDialog = new CustomProgressDialog(activity, getString(R.string.data_loading));

        configureCommonTextViews();
        configureListViewStudents();
        loadClasses();
    }

    private void configureCommonTextViews() {
        textViewCommonPayed = view.findViewById(R.id.textViewPayed);
        textViewCommonDebt = view.findViewById(R.id.textViewDebt);
    }

    private void configureSearchEditText() {
        EditText etSearch = view.findViewById(R.id.etSearch);
        SharedClass.configureSearchEditText(etSearch, nameList, searchNameList, adapter);
    }

    private void configureListViewStudents() {
        adapter = new DirectorRecyclerStudentPayment(activity,
                textViewCommonPayed, textViewCommonDebt, searchNameList, map);
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
        listClasses.add(0, "Sinif seç");
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
                        listClasses.add(0, "Sinif seç");

                        try {
                            notifyClassesSpinner();
                        } catch (Exception e) {
                            Log.d("AAAAAA", e.toString());
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

        map.clear();
        nameList.clear();
        searchNameList.clear();

        databaseReference.child("STUDENTS/" + selectedClass).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot snapshotStudent : snapshot.getChildren()) {
                        String name = snapshotStudent.child("name").getValue(String.class);

                        moneyPerMonth = snapshotStudent.child("PaymentInfo/moneyPerMonth").getValue(Double.class);

                        Double commonPayed = snapshotStudent.child("PaymentInfo/commonPayed").getValue(Double.class);
                        if(commonPayed == null) commonPayed = 0.;

                        Double payed = snapshotStudent.child("PaymentInfo/payed").getValue(Double.class);
                        if (payed == null) payed = 0d;

                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_MONTH, 1);

                        DateFormat df = new SimpleDateFormat("yyyy_MM_dd");

                        Date current_date = df.parse(df.format(cal.getTime()));
                        Date cd = df.parse(df.format(cal.getTime()));

                        String lastPaymentTime = snapshotStudent.child("PaymentInfo/paymentTime").getValue(String.class);
                        Date last_payment_time = df.parse(lastPaymentTime);

                        double debt;

                        int unpayed_months = -1;
                        while (last_payment_time.before(cd)) {
                            unpayed_months++;
                            cd.setMonth(cd.getMonth() - 1);
                        }
                        debt = unpayed_months * moneyPerMonth;

                        current_date.setMonth(current_date.getMonth() - 1);
                        try {
                            if (last_payment_time.before(current_date)) {
                                if (payed >= moneyPerMonth) {
                                    payed -= moneyPerMonth;
                                    debt = 0.0;

                                    last_payment_time.setMonth(last_payment_time.getMonth() + 1);

                                    snapshotStudent.getRef().child("PaymentInfo/paymentTime").setValue(df.format(last_payment_time));
                                    snapshotStudent.getRef().child("PaymentInfo/payed").setValue(payed);

                                    DateFormat dft = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
                                    String time = dft.format(Calendar.getInstance().getTime());

                                    DatabaseReference dr = databaseReference.child("PROFIT/STUDENT_PAYMENT/" + snapshotStudent.getKey());
                                    dr.child(time + "/amount").setValue(SharedClass.twoDigitDecimal(moneyPerMonth));
                                    dr.child(time + "/commonPayed").setValue(SharedClass.twoDigitDecimal(commonPayed + moneyPerMonth));
                                    dr.child(time + "/debt").setValue(debt);
                                    dr.child(time + "/payed").setValue(SharedClass.twoDigitDecimal(payed));

                                    DatabaseFunctions.changeBudget(activity, moneyPerMonth, true);
                                }
                            }
                            nameList.add(name);
                            searchNameList.add(name);
                            map.put(name, new ProfitModel(snapshotStudent.getKey(), last_payment_time, moneyPerMonth, payed, commonPayed, debt));
                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                    configureCommonTextViews();
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
