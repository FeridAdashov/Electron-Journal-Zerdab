package com.ej.zerdabiyolu2.Profiles.School;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ej.zerdabiyolu2.AuthActivities.LoginActivity;
import com.ej.zerdabiyolu2.AuthActivities.SignUpActivity;
import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Backup.SchoolBackupFragment;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.DeletedUsers.DeletedUsersSchool;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Expenditure.ExpenditureSchoolFragment;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Grid.GridSchool;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Manager.ManagerProfilesFragment;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.School.ChangeSchoolInformationDialog;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Student.SchoolStudentProfilesFragment;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Teacher.TeacherProfilesFragment;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Other.OtherProfitSchoolFragment;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Student.StudentProfitSchoolFragment;
import com.ej.zerdabiyolu2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textViewToolbarTitle;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Activity activity;

    private int selected_menu = R.id.navigation_payments;
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                if (selected_menu != item.getItemId() || selected_menu == R.id.navigation_profiles) {
                    selected_menu = item.getItemId();
                    switch (item.getItemId()) {
                        case R.id.navigation_payments:
                            openFragment(new StudentProfitSchoolFragment(), "Şagird Ödəmələri");
                            return true;
                        case R.id.navigation_expenditure:
                            openFragment(new ExpenditureSchoolFragment(), "Xərclər");
                            return true;
                        case R.id.navigation_profiles:
                            chooseProfileType();
                            return true;
                    }
                }
                return false;
            };

    private void chooseProfileType() {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.open_profiles_dialog, null);

        TextView textViewStudents = dialogView.findViewById(R.id.textViewStudents);
        TextView textViewTeachers = dialogView.findViewById(R.id.textViewTeachers);
        TextView textViewManagers = dialogView.findViewById(R.id.textViewManagers);
        TextView textViewSchool = dialogView.findViewById(R.id.textViewSchool);

        textViewStudents.setOnClickListener(v -> {
            openFragment(new SchoolStudentProfilesFragment(), "Şagird Hesabları");
            dialog.dismiss();
        });
        textViewTeachers.setOnClickListener(v -> {
            openFragment(new TeacherProfilesFragment(), "Müəllim Hesabları");
            dialog.dismiss();
        });
        textViewManagers.setOnClickListener(v -> {
            openFragment(new ManagerProfilesFragment(), "Menecer Hesabları");
            dialog.dismiss();
        });
        textViewSchool.setOnClickListener(v -> {
            GenericTypeIndicator<ArrayList<String>> s = new GenericTypeIndicator<ArrayList<String>>() {
            };

            CustomProgressDialog progressDialog = new CustomProgressDialog(activity, getString(R.string.data_loading));
            progressDialog.show();

            String school = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
            DatabaseFunctions.getDatabases(activity).get(0).child("SCHOOL/" + school)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressDialog.dismiss();

                            Double moneyPerMonth = snapshot.child("moneyPerMonth").getValue(Double.class);
                            if (moneyPerMonth == null) moneyPerMonth = 0.;

                            ChangeSchoolInformationDialog d = new ChangeSchoolInformationDialog();
                            d.setDefaultValues(moneyPerMonth, snapshot.child("classes").getValue(s),
                                    snapshot.child("lessons").getValue(s));
                            d.show(getSupportFragmentManager(), "");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                        }
                    });

            dialog.dismiss();
        });

        dialog.setView(dialogView);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        activity = this;

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_school);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        NavigationView navigationView = findViewById(R.id.nav_view_school);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbarSchool);
        textViewToolbarTitle = toolbar.findViewById(R.id.schoolToolbarTitle);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_school);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        openFragment(new StudentProfitSchoolFragment(), "Şagird Ödəmələri");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_school);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (selected_menu != item.getItemId()) {
            selected_menu = item.getItemId();

            if (selected_menu == R.id.nav_other_payments)
                openFragment(new OtherProfitSchoolFragment(), "Digər Ödəmələri");
            else if (selected_menu == R.id.nav_grid) openFragment(new GridSchool(), "Cədvəl");
            else if (selected_menu == R.id.nav_create_profile)
                startActivity(new Intent(this, SignUpActivity.class));
            else if (selected_menu == R.id.nav_backup)
                openFragment(new SchoolBackupFragment(), "Yedəkləmə");
            else if (selected_menu == R.id.nav_deleted_users)
                openFragment(new DeletedUsersSchool(), getString(R.string.deleted_persons));
            else if (selected_menu == R.id.nav_logout) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
            }

            DrawerLayout drawer = findViewById(R.id.drawer_layout_school);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    public void openFragment(Fragment fragment, String pageName) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();

        textViewToolbarTitle.setText(pageName);
    }
}
