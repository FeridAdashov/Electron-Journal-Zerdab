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
import com.ej.zerdabiyolu2.InformationClasses.ManagerInformation;
import com.ej.zerdabiyolu2.Profiles.Manager.ManagerProfileActivity;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ManagerRegisterActivity extends AppCompatActivity {

    private EditText editTextManagerName;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private CustomProgressDialog progressDialog;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_register);

        activity = this;
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = DatabaseFunctions.getDatabases(this).get(0);

        progressDialog = new CustomProgressDialog(this, getString(R.string.registering));

        loadSeed();
    }

    private void loadSeed() {
        Button buttonRegisterManager = findViewById(R.id.buttonRegisterManager);
        buttonRegisterManager.setOnClickListener(v -> {
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

        editTextManagerName = findViewById(R.id.editTextManagerName);
    }

    private void showInternetError() {
        progressDialog.dismiss();
        SharedClass.showSnackBar(activity, activity.getString(R.string.error_check_internet));
    }

    private void registerUser(String schoolEmail, String schoolPassword) {
        final String email = getIntent().getStringExtra("email");
        final String password = getIntent().getStringExtra("password");
        final String name = editTextManagerName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.enter_student_name, Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                ManagerInformation obj = new ManagerInformation(
                        name,
                        password
                );

                final String childName = firebaseAuth.getCurrentUser().getEmail().split("@")[0];

                databaseReference.child("MANAGERS/" + childName).setValue(obj);

                finishAffinity();

                Intent intent = new Intent(getBaseContext(), ManagerProfileActivity.class);
                intent.putExtra("username", schoolEmail);
                intent.putExtra("password", schoolPassword);
                startActivity(intent);
            } else {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), R.string.maybe_profile_data_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
