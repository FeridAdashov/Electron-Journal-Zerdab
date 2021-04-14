package com.ej.zerdabiyolu2.Profiles.Director;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ej.zerdabiyolu2.AuthActivities.LoginActivity;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.Profiles.Director.Fragments.DeletedUsers.DeletedUsersDirector;
import com.ej.zerdabiyolu2.Profiles.Director.Fragments.ExamResult.TeachersForExamResultDirectorFragment;
import com.ej.zerdabiyolu2.Profiles.Director.Fragments.Profit.StudentProfitDirectorFragment;
import com.ej.zerdabiyolu2.Profiles.Director.Fragments.Statement.StatementFragment;
import com.ej.zerdabiyolu2.Profiles.Director.Fragments.TeacherValuation.DirectorTeacherValuationFragment;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Expenditure.ExpenditureSchoolFragment;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Other.OtherProfitSchoolFragment;
import com.ej.zerdabiyolu2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DirectorProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textViewToolbarTitle;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private int fragmentId;

    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                if (fragmentId != item.getItemId()) {
                    fragmentId = item.getItemId();
                    switch (item.getItemId()) {
                        case R.id.navigation_payments:
                            openFragment(new StudentProfitDirectorFragment(), "Şagird Ödəmələri");
                            return true;
                        case R.id.navigation_expenditure:
                            openFragment(new ExpenditureSchoolFragment(), "Xərclər");
                            return true;
                        case R.id.navigation_statement:
                            openFragment(new StatementFragment(), "Çıxarış");
                            return true;
                    }
                }
                return false;
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_profile);

        loadSeed();
    }

    private void loadSeed() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = DatabaseFunctions.getDatabases(this).get(0);

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_director);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        NavigationView navigationView = findViewById(R.id.nav_view_director);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbarDirector);
        textViewToolbarTitle = toolbar.findViewById(R.id.directorToolbarTitle);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_director);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        openFragment(new StudentProfitDirectorFragment(), "Şagird Ödəmələri");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_director);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        closeDrawer();

        int fragmentId = item.getItemId();
        if (this.fragmentId != fragmentId) {
            this.fragmentId = fragmentId;

            if (fragmentId == R.id.nav_other_payments)
                openFragment(new OtherProfitSchoolFragment(), "Digər Ödəmələr");
            else if (fragmentId == R.id.nav_teacher_valuation)
                openFragment(new DirectorTeacherValuationFragment(), "Müəllim Qiymətləndirmə");
            else if (fragmentId == R.id.nav_exam)
                openFragment(new TeachersForExamResultDirectorFragment(), "Quiz Nəzarət");
            else if (fragmentId == R.id.nav_deleted_users)
                openFragment(new DeletedUsersDirector(), "Silinmiş Şəxslər");
            else if (fragmentId == R.id.nav_budget) {
                this.fragmentId = 0;
                showBudget();
            } else if (fragmentId == R.id.nav_logout) {
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }
        return true;
    }

    private void showBudget() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.data_loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        final Context context = this;

        databaseReference.child("BUDGET").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull final DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                final AlertDialog.Builder alert = new AlertDialog.Builder(context);

                final EditText editText = new EditText(context);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setText(String.valueOf(dataSnapshot.getValue(Double.class)));

                alert.setMessage(getString(R.string.budget));
                alert.setView(editText);
                alert.setPositiveButton(R.string.save, (dialogInterface1, i12) -> {
                    String text = editText.getText().toString();
                    if (TextUtils.isEmpty(text)) return;
                    dataSnapshot.getRef().setValue(SharedClass.twoDigitDecimal(Double.parseDouble(text)));
                });
                alert.setNegativeButton(R.string.m_cancel, null);
                alert.show();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    public void openFragment(Fragment fragment, String pageName) {
        closeDrawer();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentDirector, fragment);
        transaction.commit();

        textViewToolbarTitle.setText(pageName);
    }

    private void closeDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_director);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
    }
}
