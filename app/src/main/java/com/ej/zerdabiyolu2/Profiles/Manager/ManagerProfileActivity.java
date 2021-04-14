package com.ej.zerdabiyolu2.Profiles.Manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ej.zerdabiyolu2.AuthActivities.LoginActivity;
import com.ej.zerdabiyolu2.Profiles.Manager.Fragments.ExamResult.TeachersForExamResultFragment;
import com.ej.zerdabiyolu2.Profiles.Manager.Fragments.NoteLesson.TeachersNoteLessonFragment;
import com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Student.ManagerStudentProfilesFragment;
import com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Teacher.ManagerTeacherProfilesFragment;
import com.ej.zerdabiyolu2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ManagerProfileActivity extends AppCompatActivity {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Activity activity;
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_student:
                        openFragment(new ManagerStudentProfilesFragment());
                        return true;
                    case R.id.navigation_teacher:
                        openTeacherMenuDialog();
                        return true;
                    case R.id.navigation_log_out:
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(this, LoginActivity.class));
                        return true;
                }
                return false;
            };

    private void openTeacherMenuDialog() {
        new AlertDialog.Builder(activity)
                .setNeutralButton("Yoxlama", (dialog, which) -> openFragment(new ManagerTeacherProfilesFragment()))
                .setPositiveButton("Dərs", (dialog, which) -> openFragment(new TeachersNoteLessonFragment()))
                .setNegativeButton("Quiz", (dialog, which) -> openFragment(new TeachersForExamResultFragment()))
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_profile);

        activity = this;

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_manager);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        openFragment(new ManagerStudentProfilesFragment());
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}