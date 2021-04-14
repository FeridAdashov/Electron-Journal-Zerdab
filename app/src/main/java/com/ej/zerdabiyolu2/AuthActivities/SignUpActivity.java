package com.ej.zerdabiyolu2.AuthActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ej.zerdabiyolu2.AuthActivities.RegisterActivities.DirectorRegisterActivity;
import com.ej.zerdabiyolu2.AuthActivities.RegisterActivities.ManagerRegisterActivity;
import com.ej.zerdabiyolu2.AuthActivities.RegisterActivities.SchoolRegisterActivity;
import com.ej.zerdabiyolu2.AuthActivities.RegisterActivities.StudentRegisterActivity;
import com.ej.zerdabiyolu2.AuthActivities.RegisterActivities.TeacherRegisterActivity;
import com.ej.zerdabiyolu2.R;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonNextToInformationPage;
    private EditText editTextEmail, editTextPasswordConfirm;
    private EditText editTextPassword;
    private int statusId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        RadioGroup radioGroupUsers = findViewById(R.id.radioGroupUsers);
        radioGroupUsers.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.radioStudent:
                    statusId = 1;
                    break;
                case R.id.radioTeacher:
                    statusId = 2;
                    break;
                case R.id.radioSchool:
                    statusId = 3;
                    break;
                case R.id.radioDirector:
                    statusId = 4;
                    break;
                case R.id.radioManager:
                    statusId = 5;
                    break;
            }
        });

        switch (statusId) {
            case 1:
                radioGroupUsers.check(R.id.radioStudent);
                break;
            case 2:
                radioGroupUsers.check(R.id.radioTeacher);
                break;
            case 3:
                radioGroupUsers.check(R.id.radioSchool);
                break;
            case 4:
                radioGroupUsers.check(R.id.radioDirector);
                break;
            case 5:
                radioGroupUsers.check(R.id.radioManager);
                break;
        }

        buttonNextToInformationPage = findViewById(R.id.buttonNextToInformationPage);
        buttonNextToInformationPage.setOnClickListener(this);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        editTextPasswordConfirm.setOnClickListener(this);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
    }


    @Override
    public void onClick(View view) {
        if (view == buttonNextToInformationPage) {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String passwordConfirm = editTextPasswordConfirm.getText().toString();

            if (email.length() < 5) {
                Toast.makeText(getBaseContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
                return;
            } else email += "@mail.ru";

            if (password.length() < 8) {
                Toast.makeText(getBaseContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(passwordConfirm)) {
                Toast.makeText(getBaseContext(), R.string.password_not_matched, Toast.LENGTH_SHORT).show();
                return;
            }

            switch (statusId) {
                case 1:
                    Intent intent = new Intent(getBaseContext(), StudentRegisterActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    startActivity(intent);
                    break;

                case 2:
                    Intent intent2 = new Intent(getBaseContext(), TeacherRegisterActivity.class);
                    intent2.putExtra("email", email);
                    intent2.putExtra("password", password);
                    startActivity(intent2);
                    break;

                case 3:
                    Intent intent3 = new Intent(getBaseContext(), SchoolRegisterActivity.class);
                    intent3.putExtra("email", email);
                    intent3.putExtra("password", password);
                    startActivity(intent3);
                    break;

                case 4:
                    Intent intent4 = new Intent(getBaseContext(), DirectorRegisterActivity.class);
                    intent4.putExtra("email", email);
                    intent4.putExtra("password", password);
                    startActivity(intent4);
                    break;

                case 5:
                    Intent intent5 = new Intent(getBaseContext(), ManagerRegisterActivity.class);
                    intent5.putExtra("email", email);
                    intent5.putExtra("password", password);
                    startActivity(intent5);
                    break;
            }
        }
    }
}
