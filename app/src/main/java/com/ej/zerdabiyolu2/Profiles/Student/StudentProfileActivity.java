package com.ej.zerdabiyolu2.Profiles.Student;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ej.zerdabiyolu2.AuthActivities.LoginActivity;
import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.CustomDialogs.ShowInformationDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.Profiles.School.SchoolProfileActivity;
import com.ej.zerdabiyolu2.Profiles.Student.Exam.StudentExamsFragment;
import com.ej.zerdabiyolu2.Profiles.Student.Lesson.StudentLessonsFragment;
import com.ej.zerdabiyolu2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StudentProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;
    private CustomProgressDialog progressDialog;
    private TextView toolBarTitle;

    private Activity activity;
    private String schoolUsername, schoolPassword, studentClass, studentUserName;
    private boolean goback = false;

    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_lessons:
                        openFragment(new StudentLessonsFragment(), "Dərs Məlumatı");
                        return true;
                    case R.id.navigation_exam_result:
                        openFragment(new StudentExamsFragment(), "Quiz Məlumatı");
                        return true;
                    case R.id.navigation_messages:
                        showMessages();
                        return true;
                    case R.id.navigation_payment_history:
                        showPayment();
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        activity = this;

        if (getIntent().hasExtra("username")) {
            schoolUsername = getIntent().getStringExtra("username");
            schoolPassword = getIntent().getStringExtra("password");
            goback = true;
        }

        databaseReference = DatabaseFunctions.getDatabases(this).get(0);
        progressDialog = new CustomProgressDialog(this, getString(R.string.data_loading));

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_student);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        NavigationView navigationView = findViewById(R.id.nav_view_student);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbarStudent);
        toolBarTitle = toolbar.findViewById(R.id.studentToolbarTitle);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_student);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        getStudentClass();
        openFragment(new StudentLessonsFragment(), "Dərs Məlumatı");

        checkNewMessages();
    }

    private void getStudentClass() {
        studentUserName = firebaseAuth.getCurrentUser().getEmail().split("@")[0];
        databaseReference.child("STUDENT_CLASS/" + studentUserName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        studentClass = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showGrid() {
        if (studentClass == null) return;

        progressDialog.show();
        databaseReference.child("GRID/" + studentClass).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                StringBuilder allGrid = new StringBuilder("\n");

                for (DataSnapshot snapshotGrid : dataSnapshot.getChildren()) //There is only one school
                {
                    String grid = snapshotGrid.getValue(String.class);

                    if (grid != null && !grid.equals(""))
                        allGrid.append("----------- ").append(snapshotGrid.getKey())
                                .append(" -ci gün -----------\n")
                                .append(grid)
                                .append("\n\n\n");

                    if (allGrid.toString().trim().equals(""))
                        allGrid = new StringBuilder(getString(R.string.nothing_info));
                }

                progressDialog.dismiss();

                ShowInformationDialog dialog = new ShowInformationDialog();
                dialog.setMessage(allGrid.toString());
                dialog.show(getSupportFragmentManager(), "");
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void showMessages() {
        progressDialog.show();
        databaseReference.child("MESSAGES/" + studentUserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                dataSnapshot.getRef().child("hasNewMessage").setValue(false);

                StringBuilder allMessages = new StringBuilder("\n");
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    if (snapshot.getKey().contains("_") && !snapshot.child("message").getValue(String.class).equals(""))
                        allMessages.append("----------- ")
                                .append(snapshot.getKey())
                                .append(" -----------\n")
                                .append(snapshot.child("message").getValue(String.class)).append("\n\n\n");

                if (allMessages.toString().trim().equals(""))
                    allMessages = new StringBuilder(getString(R.string.nothing_info));

                ShowInformationDialog dialog = new ShowInformationDialog();
                dialog.setMessage(allMessages.toString());
                dialog.show(getSupportFragmentManager(), "Example");
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showPayment() {
        progressDialog.show();

        databaseReference.child("STUDENTS/" + studentClass + "/" + studentUserName + "/PaymentInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotStudent) {
                progressDialog.dismiss();
                try {
                    Double moneyPerMonth = snapshotStudent.child("moneyPerMonth").getValue(Double.class);

                    Double commonPayed = snapshotStudent.child("commonPayed").getValue(Double.class);
                    if (commonPayed == null) commonPayed = 0d;

                    Double payed = snapshotStudent.child("payed").getValue(Double.class);
                    if (payed == null) payed = 0d;

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_YEAR, 1);

                    DateFormat df = new SimpleDateFormat("yyyy_MM_dd");
                    Date cd = df.parse(df.format(cal.getTime()));

                    String paymentTime = snapshotStudent.child("paymentTime").getValue(String.class);
                    Date payment_date = df.parse(paymentTime);

                    double debt;

                    int unpayed_months = -1;
                    while (payment_date.before(cd)) {
                        unpayed_months++;
                        cd.setMonth(cd.getMonth() - 1);
                    }
                    debt = unpayed_months * moneyPerMonth - payed;

                    if(debt < 0) debt = 0;

                    new AlertDialog.Builder(activity)
                            .setMessage("Ödənilib:  " + SharedClass.twoDigitDecimalAsString(commonPayed)
                                    + "\n\nBorc:  " + SharedClass.twoDigitDecimalAsString(debt))
                            .setNeutralButton(R.string.payment_history, (dialog, which) -> showPaymentHistory())
                            .setPositiveButton(R.string.m_cancel, null)
                            .show();
                } catch (Exception e) {
                    progressDialog.dismiss();
                    Log.d("AAAAA", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    private void showPaymentHistory() {
        progressDialog.show();
        databaseReference.child("PROFIT/STUDENT_PAYMENT/" + studentUserName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();

                        StringBuilder paymentHistory = new StringBuilder();
                        for (DataSnapshot snapshotDate : dataSnapshot.getChildren()) {
                            paymentHistory.append("\n\n~~~~~~~~~~~~~~~~~~~~\n")
                                    .append(snapshotDate.getKey().split(" ")[0])
                                    .append("   :   ").append(snapshotDate.child("amount").getValue(Double.class))
                                    .append(" AZN");
                        }
                        progressDialog.dismiss();

                        if (paymentHistory.toString().trim().equals(""))
                            paymentHistory = new StringBuilder(getString(R.string.nothing_info));

                        ShowInformationDialog dialog = new ShowInformationDialog();
                        dialog.setMessage(paymentHistory.toString());
                        dialog.show(getSupportFragmentManager(), "Example");
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void checkNewMessages() {
        final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        databaseReference.child("MESSAGES/" + user + "/hasNewMessage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean b = dataSnapshot.getValue(Boolean.class);

                if (b != null && b) {
                    progressDialog.dismiss();
                    new android.app.AlertDialog.Builder(activity)
                            .setTitle("Yeni mesajınız var")
                            .setPositiveButton("Göstər", (dialog, which) -> showMessages())
                            .setNegativeButton(R.string.m_cancel, null)
                            .setIcon(android.R.drawable.ic_dialog_email)
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    public void openFragment(Fragment fragment, String pageName) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();

        toolBarTitle.setText(pageName);
    }

    private void logOut() {
        firebaseAuth.signOut();
        if (goback) {
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(schoolUsername, schoolPassword).addOnCompleteListener(this, task -> {
                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    finish();
                    startActivity(new Intent(getBaseContext(), SchoolProfileActivity.class));
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), R.string.incorrect_email, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int selected_menu = item.getItemId();

        if (selected_menu == R.id.nav_grid) showGrid();
        else if (selected_menu == R.id.nav_logout) logOut();

        DrawerLayout drawer = findViewById(R.id.drawer_layout_student);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}