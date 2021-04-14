package com.ej.zerdabiyolu2.AuthActivities.RegisterActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ej.zerdabiyolu2.CustomDialogs.ChangeValueDialog;
import com.ej.zerdabiyolu2.Helper.StorageFunctions;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.InformationClasses.SchoolInformation;
import com.ej.zerdabiyolu2.Profiles.School.SchoolProfileActivity;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class SchoolRegisterActivity extends AppCompatActivity implements View.OnClickListener, ChangeValueDialog.ChangeValueDialogListener {

    private final ArrayList<String> listLessons = new ArrayList<>();
    private final ArrayList<String> listClasses = new ArrayList<>();
    private EditText editTextSchoolName, editTextLessonName, editTextClassName, editTextMoneyPerMonth;
    private Spinner spinnerClasses, spinnerLessons;
    private ImageButton buttonAddLessonName, buttonAddClassName;
    private Button buttonRegisterSchool;
    private ArrayAdapter<String> adapterLessons, adapterClasses;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_register);

        updateView();
    }

    private void reLoadSeed() {
        editTextSchoolName = findViewById(R.id.editTextSchoolName);
        editTextMoneyPerMonth = findViewById(R.id.editTextMoneyPerMonth);
        editTextLessonName = findViewById(R.id.editTextLessonName);
        editTextClassName = findViewById(R.id.editTextClassName);

        listLessons.add(getString(R.string.leeson_list));
        listClasses.add(getString(R.string.class_list));

        databaseReference = DatabaseFunctions.getDatabases(this).get(0).child("SCHOOL");

        spinnerLessons = findViewById(R.id.spinnerLessons);
        spinnerClasses = findViewById(R.id.spinnerClasses);

        adapterLessons = new ArrayAdapter<>(this, R.layout.layout_spinner_item, listLessons);
        spinnerLessons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    ChangeValueDialog dialog = new ChangeValueDialog();
                    dialog.setValue(listLessons.get(i), i, 1);
                    dialog.show(getSupportFragmentManager(), "");
                    spinnerLessons.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerLessons.setAdapter(adapterLessons);

        adapterClasses = new ArrayAdapter<>(this, R.layout.layout_spinner_item, listClasses);
        spinnerClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    ChangeValueDialog dialog = new ChangeValueDialog();
                    dialog.setValue(listClasses.get(i), i, 2);
                    dialog.show(getSupportFragmentManager(), "");
                    spinnerClasses.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerClasses.setAdapter(adapterClasses);

        buttonAddLessonName = findViewById(R.id.buttonAddLessonName);
        buttonAddClassName = findViewById(R.id.buttonAddClassName);
        buttonRegisterSchool = findViewById(R.id.buttonRegisterSchool);

        buttonAddLessonName.setOnClickListener(this);
        buttonAddClassName.setOnClickListener(this);
        buttonRegisterSchool.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void updateView() {
        setContentView(R.layout.activity_school_register);
        reLoadSeed();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonAddLessonName) {
            String name = editTextLessonName.getText().toString().trim().toUpperCase();
            if (!name.equals("") && !listLessons.contains(name)) {
                listLessons.add(name);
                editTextLessonName.setText("");
            }
        }

        if (view == buttonAddClassName) {
            String name = editTextClassName.getText().toString().trim().toUpperCase();
            if (!name.equals("") && !listClasses.contains(name)) {
                listClasses.add(name);
                editTextClassName.setText("");
            }
        }

        if (view == buttonRegisterSchool) {
            registerUser();
        }
    }

    private void registerUser() {
        String email = getIntent().getStringExtra("email");
        final String password = getIntent().getStringExtra("password");
        final String name = editTextSchoolName.getText().toString().trim();
        final String moneyPerMonth = editTextMoneyPerMonth.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.enter_school_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(moneyPerMonth)) {
            Toast.makeText(this, R.string.enter_money_month, Toast.LENGTH_SHORT).show();
            return;
        }

        if (listLessons.size() <= 1) {
            Toast.makeText(this, R.string.enter_lessons, Toast.LENGTH_SHORT).show();
            return;
        }

        if (listClasses.size() <= 1) {
            Toast.makeText(this, R.string.enter_classes, Toast.LENGTH_SHORT).show();
            return;
        }

        listClasses.remove(0);
        listLessons.remove(0);
        StorageFunctions.sortArray(listClasses);
        StorageFunctions.sortArray(listLessons);

        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                SchoolInformation obj = new SchoolInformation(
                        name,
                        Double.parseDouble(moneyPerMonth),
                        listLessons,
                        listClasses,
                        password);

                final String childName = firebaseAuth.getCurrentUser().getEmail().split("@")[0];

                databaseReference.child(childName).setValue(obj);

                finishAffinity();
                startActivity(new Intent(getBaseContext(), SchoolProfileActivity.class));
            } else {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), R.string.could_not_register, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getData(String value, int index, int id, int status) {
        switch (status) {
            case 0:
                switch (id) {
                    case 1:
                        listLessons.remove(index);
                        adapterLessons.notifyDataSetChanged();
                        break;
                    case 2:
                        listClasses.remove(index);
                        adapterClasses.notifyDataSetChanged();
                        break;
                }
                break;
            case 1:
                switch (id) {
                    case 1:
                        listLessons.set(index, value);
                        adapterLessons.notifyDataSetChanged();
                        break;
                    case 2:
                        listClasses.set(index, value);
                        adapterClasses.notifyDataSetChanged();
                        break;
                }
                break;
        }

    }
}
