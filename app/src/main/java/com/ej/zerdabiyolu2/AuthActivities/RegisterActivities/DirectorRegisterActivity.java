package com.ej.zerdabiyolu2.AuthActivities.RegisterActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.InformationClasses.DirectorInformation;
import com.ej.zerdabiyolu2.InformationClasses.ManagerInformation;
import com.ej.zerdabiyolu2.Profiles.Director.DirectorProfileActivity;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class DirectorRegisterActivity extends AppCompatActivity {

    private EditText editTextDirectorName;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private CustomProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = DatabaseFunctions.getDatabases(this).get(0);

        progressDialog = new CustomProgressDialog(this, getString(R.string.registering));

        loadSeed();
    }

    private void loadSeed() {
        Button buttonRegisterDirector = findViewById(R.id.buttonRegisterDirector);
        buttonRegisterDirector.setOnClickListener(v -> registerUser());

        editTextDirectorName = findViewById(R.id.editTextDirectorName);
    }

    private void registerUser() {
        final String email = getIntent().getStringExtra("email");
        final String password = getIntent().getStringExtra("password");
        final String name = editTextDirectorName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.enter_student_name, Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                DirectorInformation obj = new DirectorInformation(name, password);

                final String childName = firebaseAuth.getCurrentUser().getEmail().split("@")[0];
                databaseReference.child("DIRECTOR/" + childName).setValue(obj);

                finishAffinity();

                startActivity(new Intent(getBaseContext(), DirectorProfileActivity.class));
            } else {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), R.string.maybe_profile_data_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
