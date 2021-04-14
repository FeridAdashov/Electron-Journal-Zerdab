package com.ej.zerdabiyolu2.AuthActivities.RegisterActivities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.InformationClasses.StudentInformation;
import com.ej.zerdabiyolu2.Profiles.Student.StudentProfileActivity;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StudentRegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText editTextStudentName, editTextStudentBiography,
            editTextStudentMoneyPerMonth, editTextStudentDebt, editTextStudentPayed;
    private TextView textViewRegisterDate;
    private Spinner spinnerClassNames;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ArrayList<String> listClasses = new ArrayList<>();

    private CustomProgressDialog progressDialog;
    private Activity activity;

    private String schoolEmail;
    private String schoolPassword;
    private String paymentTime, registerTime = "";
    private final Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        activity = this;

        progressDialog = new CustomProgressDialog(this, getString(R.string.data_loading));

        databaseReference = DatabaseFunctions.getDatabases(this).get(0);
        firebaseAuth = FirebaseAuth.getInstance();
        schoolEmail = firebaseAuth.getCurrentUser().getEmail();

        loadClasses();
    }

    private void loadClasses() {
        listClasses.clear();

        GenericTypeIndicator<ArrayList<String>> s = new GenericTypeIndicator<ArrayList<String>>() {
        };

        progressDialog.show();

        if (schoolEmail != null) {
            databaseReference.child("SCHOOL/" + schoolEmail.split("@")[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    schoolPassword = snapshot.child("password").getValue(String.class);

                    if (schoolPassword != null) {
                        listClasses = snapshot.child("classes").getValue(s);
                        listClasses.add(0, getString(R.string.class_list));

                        initViews();
                    } else showInternetError();

                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showInternetError();
                }
            });
        } else showInternetError();
    }

    private void initViews() {
        Button buttonRegisterStudent = findViewById(R.id.buttonRegisterStudent);
        buttonRegisterStudent.setOnClickListener(v -> registerUser());

        editTextStudentName = findViewById(R.id.editTextStudentName);
        editTextStudentBiography = findViewById(R.id.editTextStudentBiography);
        editTextStudentDebt = findViewById(R.id.editTextStudentDebt);
        editTextStudentPayed = findViewById(R.id.editTextStudentPayed);
        editTextStudentMoneyPerMonth = findViewById(R.id.editTextStudentMoneyPerMonth);
        textViewRegisterDate = findViewById(R.id.textViewRegisterDate);
        textViewRegisterDate.setOnClickListener(v -> {
            SharedClass.showDatePickerDialog(activity, this);
        });

        addTextChangeListener(editTextStudentPayed, editTextStudentDebt);
        addTextChangeListener(editTextStudentDebt, editTextStudentPayed);

        spinnerClassNames = findViewById(R.id.spinnerClassNames);
        ArrayAdapter<String> adapterClasses = new ArrayAdapter<>(this, R.layout.layout_spinner_item, listClasses);
        spinnerClassNames.setAdapter(adapterClasses);
    }

    private void addTextChangeListener(TextView one, TextView two) {
        one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0)
                    two.setVisibility(View.GONE);
                else
                    two.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showInternetError() {
        progressDialog.dismiss();
        SharedClass.showSnackBar(activity, activity.getString(R.string.error_check_internet));
    }

    private void registerUser() {
        final String email = getIntent().getStringExtra("email");
        final String password = getIntent().getStringExtra("password");
        final String name = editTextStudentName.getText().toString().trim();
        final String biography = editTextStudentBiography.getText().toString().trim();
        double moneyPerMonth, debt, payed;

        if (TextUtils.isEmpty(name)) {
            SharedClass.showSnackBar(this, getString(R.string.enter_student_name));
            return;
        }

        if (TextUtils.isEmpty(biography)) {
            SharedClass.showSnackBar(this, getString(R.string.enter_phone_number));
            return;
        }

        try {
            moneyPerMonth = Double.parseDouble(editTextStudentMoneyPerMonth.getText().toString());
        } catch (Exception e) {
            SharedClass.showSnackBar(this, getString(R.string.money_per_month));
            return;
        }

        try {
            debt = Double.parseDouble(editTextStudentDebt.getText().toString());
        } catch (Exception e) {
            debt = 0.0;
        }

        try {
            payed = Double.parseDouble(editTextStudentPayed.getText().toString());
        } catch (Exception e) {
            payed = 0.0;
        }

        if (TextUtils.isEmpty(registerTime)) {
            SharedClass.showSnackBar(this, getString(R.string.registration_date));
            return;
        }

        if (spinnerClassNames.getSelectedItemId() == 0) {
            SharedClass.showSnackBar(this, getString(R.string.select_class_name));
            return;
        }

        if (debt > 0) {
            int un_payed_months = (int) (debt / moneyPerMonth);
            payed = moneyPerMonth - debt % moneyPerMonth;

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) - un_payed_months;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            paymentTime = year + "_"
                    + (month < 10 ? "0" + month : "" + month) + "_"
                    + (day < 10 ? "0" + day : "" + day);
        }

        progressDialog = new CustomProgressDialog(this, getString(R.string.registering));
        progressDialog.show();

        double finalPayed = payed;
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(StudentRegisterActivity.this, task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {

                try {
                    StudentInformation obj = new StudentInformation(
                            name,
                            biography,
                            password,
                            registerTime
                    );

                    final String user = firebaseAuth.getCurrentUser().getEmail().split("@")[0];
                    final String childName = "STUDENTS/" + spinnerClassNames.getSelectedItem().toString() + "/" + user;

                    databaseReference.child("STUDENT_CLASS/" + user).setValue(spinnerClassNames.getSelectedItem().toString());

                    databaseReference.child(childName).setValue(obj);
                    databaseReference.child(childName + "/PaymentInfo/moneyPerMonth").setValue(moneyPerMonth);
                    databaseReference.child(childName + "/PaymentInfo/paymentTime").setValue(paymentTime);
                    databaseReference.child(childName + "/PaymentInfo/payed").setValue(finalPayed);
                    databaseReference.child(childName + "/PaymentInfo/commonPayed").setValue(finalPayed);//TODO commonPayed

                    finishAffinity();
                    Intent intent = new Intent(getBaseContext(), StudentProfileActivity.class);
                    intent.putExtra("username", schoolEmail);
                    intent.putExtra("password", schoolPassword);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), R.string.incorrect_email, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String d = day < 10 ? "0" + day : "" + day;
        ++month;
        String m = month < 10 ? "0" + month : "" + month;

        registerTime = year + "_" + m + "_" + d;
        paymentTime = registerTime;
        textViewRegisterDate.setText(registerTime);
    }
}
