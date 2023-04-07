package com.ej.zerdabiyolu2.AuthActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Profiles.Director.DirectorProfileActivity;
import com.ej.zerdabiyolu2.Profiles.Manager.ManagerProfileActivity;
import com.ej.zerdabiyolu2.Profiles.School.SchoolProfileActivity;
import com.ej.zerdabiyolu2.Profiles.Student.StudentProfileActivity;
import com.ej.zerdabiyolu2.Profiles.Teacher.TeacherProfileActivity;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private CustomProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new CustomProgressDialog(this, getString(R.string.signing));
        databaseReference = DatabaseFunctions.getDatabases(this).get(0);
        firebaseAuth = FirebaseAuth.getInstance();

        loadSeed();

        Log.d("AAAAAAAAAA", "LLLLLLLLLLL");
    }

    private void loadSeed() {
        TextView textViewSignIn = findViewById(R.id.buttonSignIn);
        textViewSignIn.setOnClickListener(v -> userLogIn());

        TextView textViewSignUp = findViewById(R.id.textViewSignUp);
        textViewSignUp.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), SignUpActivity.class)));

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        if (checkMyPermissions() && firebaseAuth.getCurrentUser() != null) goToProfile();
    }

    private void goToProfile() {
        progressDialog.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                Integer version = dataSnapshot.child("DatabaseInformation/version").getValue(Integer.class);
                if (version == null || version != 6) {
                    firebaseAuth.signOut();
                    Toast.makeText(getBaseContext(), "Proqramı Güncəlləyin!", Toast.LENGTH_LONG).show();
                    return;
                }

                String user = firebaseAuth.getCurrentUser().getEmail().split("@")[0];
                String studentClass = dataSnapshot.child("STUDENT_CLASS/" + user).getValue(String.class);
                String school = "SCHOOL/" + user;
                String student = "STUDENTS/" + studentClass + "/" + user;
                String teacher = "TEACHERS/" + user;
                String director = "DIRECTOR/" + user;
                String manager = "MANAGERS/" + user;

                Intent intent;
                if (dataSnapshot.child(school).exists())
                    intent = new Intent(getBaseContext(), SchoolProfileActivity.class);
                else if (dataSnapshot.child(student).exists())
                    intent = new Intent(getBaseContext(), StudentProfileActivity.class);
                else if (dataSnapshot.child(teacher).exists())
                    intent = new Intent(getBaseContext(), TeacherProfileActivity.class);
                else if (dataSnapshot.child(director).exists())
                    intent = new Intent(getBaseContext(), DirectorProfileActivity.class);
                else if (dataSnapshot.child(manager).exists())
                    intent = new Intent(getBaseContext(), ManagerProfileActivity.class);
                else {
                    Toast.makeText(getBaseContext(), R.string.maybe_profile_data_wrong, Toast.LENGTH_LONG).show();
                    firebaseAuth.signOut();
                    return;
                }

                finishAffinity();
                startActivity(intent);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private boolean checkMyPermissions() {
        int permissionWriteExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionReadExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        if (permissionReadExternal != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (firebaseAuth.getCurrentUser() != null) {
                        progressDialog.show();
                        goToProfile();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.write_permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (firebaseAuth.getCurrentUser() != null) {
                        progressDialog.show();
                        goToProfile();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.read_permission_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void userLogIn() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.enter_email, Toast.LENGTH_SHORT).show();
            return;
        } else email += "@mail.ru";

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) goToProfile();
            else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.incorrect_email, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
