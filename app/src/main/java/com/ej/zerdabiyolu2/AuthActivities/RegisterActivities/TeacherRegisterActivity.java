package com.ej.zerdabiyolu2.AuthActivities.RegisterActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.InformationClasses.TeacherInformation;
import com.ej.zerdabiyolu2.Profiles.Teacher.TeacherProfileActivity;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class TeacherRegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private CustomProgressDialog progressDialog;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register);

        activity = this;

        progressDialog = new CustomProgressDialog(this, getString(R.string.data_loading));

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = DatabaseFunctions.getDatabases(this).get(0);
        progressDialog.setMessage(getString(R.string.data_loading));

        Button buttonRegisterTeacher = findViewById(R.id.buttonRegisterTeacher);
        buttonRegisterTeacher.setOnClickListener(v -> {
            progressDialog.show();
            String schoolEmail = firebaseAuth.getCurrentUser().getEmail();
            if (schoolEmail != null)
                databaseReference.child("SCHOOL/" + schoolEmail.split("@")[0] + "/password").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String schoolPassword = snapshot.getValue(String.class);

                        if (schoolPassword != null) registerUser(schoolEmail, schoolPassword);
                        else showInternetError();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showInternetError();
                    }
                });
            else showInternetError();
        });


    }

    private void showInternetError() {
        progressDialog.dismiss();
        SharedClass.showSnackBar(activity, activity.getString(R.string.error_check_internet));
    }

    private void registerUser(String schoolEmail, String schoolPassword) {
        EditText editTextTeacherName = findViewById(R.id.editTextTeacherName);
        EditText editTextTeacherBiography = findViewById(R.id.editTextTeacherBiography);
        EditText editTextTeacherClassCount = findViewById(R.id.editTextTeacherClassCount);

        String email = getIntent().getStringExtra("email");
        final String password = getIntent().getStringExtra("password");
        final String name = editTextTeacherName.getText().toString().trim();
        final String biography = editTextTeacherBiography.getText().toString().trim();
        final int countOfClass;

        if (TextUtils.isEmpty(name)) {
            progressDialog.dismiss();
            SharedClass.showSnackBar(this, getString(R.string.enter_teacher_name));
            return;
        }

        try {
            countOfClass = Integer.parseInt(editTextTeacherClassCount.getText().toString());
        } catch (Exception e) {
            progressDialog.dismiss();
            SharedClass.showSnackBar(this, "Sinif sayını daxil edin");
            return;
        }

        if (TextUtils.isEmpty(biography)) {
            progressDialog.dismiss();
            SharedClass.showSnackBar(this, getString(R.string.enter_biography));
            return;
        }

        progressDialog.setMessage(getString(R.string.registering));
        progressDialog.setCancelable(false);

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                TeacherInformation obj = new TeacherInformation(
                        name,
                        countOfClass,
                        biography,
                        password
                );

                final String childName = firebaseAuth.getCurrentUser().getEmail().split("@")[0];
                databaseReference.child("TEACHERS/" + childName).setValue(obj);

                finishAffinity();
                Intent intent = new Intent(getBaseContext(), TeacherProfileActivity.class);
                intent.putExtra("username", schoolEmail);
                intent.putExtra("password", schoolPassword);
                startActivity(intent);
            } else {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), R.string.incorrect_email, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
