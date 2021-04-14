package com.ej.zerdabiyolu2.Profiles.Teacher;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ej.zerdabiyolu2.AuthActivities.LoginActivity;
import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.CustomDateTime;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.Profiles.School.SchoolProfileActivity;
import com.ej.zerdabiyolu2.Profiles.Teacher.Lesson.TeacherNoteLessonDataActivity;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TeacherProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewQuizStatus, textViewNoteLessonStatus;
    private EditText editTextLessonSubject;
    private Button buttonTeacherNext;
    private ImageButton buttonLogOut;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ArrayList<String> listLessons = new ArrayList<>();
    private ArrayList<String> listClasses = new ArrayList<>();
    private Spinner spinnerClass, spinnerLesson;
    private CustomProgressDialog progressDialog;

    private Activity activity;
    private String username, password, rootName;
    private boolean goback = false;
    private Integer noteCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        activity = this;

        if (getIntent().hasExtra("username")) {
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
            goback = true;
        }

        progressDialog = new CustomProgressDialog(this, getString(R.string.data_loading));
        progressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();
        rootName = firebaseAuth.getCurrentUser().getEmail().split("@")[0];
        databaseReference = DatabaseFunctions.getDatabases(this).get(0);

        loadSeed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        configureStatusTextViews();
    }

    private void configureStatusTextViews() {
        textViewQuizStatus = findViewById(R.id.textViewQuizStatus);
        textViewNoteLessonStatus = findViewById(R.id.textViewNoteLesson);

        databaseReference.child("TEACHERS/" + rootName + "/countOfClass").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Integer count = snapshot.getValue(Integer.class);

                databaseReference.child("TEACHER_GIVE_RESULT/" + rootName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer givenResultCount = snapshot.child("givenResultCount").getValue(Integer.class);
                        String lastGiveTime = snapshot.child("lastGiveTime").getValue(String.class);

                        if (givenResultCount == null) {
                            givenResultCount = 0;
                            snapshot.getRef().child("givenResultCount").setValue(givenResultCount);
                        }
                        if (lastGiveTime == null) {
                            lastGiveTime = CustomDateTime.getDate(new Date());
                            snapshot.getRef().child("lastGiveTime").setValue(lastGiveTime);
                        }

                        textViewQuizStatus.setText("Quiz məlumatı:   " + givenResultCount + " / " + count);

                        long diffInMillies = Math.abs(new Date().getTime() - CustomDateTime.getDate(lastGiveTime).getTime());
                        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                        boolean b = givenResultCount >= count || diff < 14;
                        textViewQuizStatus.setTextColor(Color.parseColor(b ? "#01294b" : "#FFFFFF"));
                        textViewQuizStatus.setBackgroundColor(Color.parseColor(b ? "#FFFFFF" : "#FF5722"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                databaseReference.child("TEACHER_NOTE_LESSON/" + rootName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        noteCount = snapshot.child("noteCount").getValue(Integer.class);
                        String lastNoteTime = snapshot.child("lastNoteTime").getValue(String.class);

                        if (noteCount == null) {
                            noteCount = 0;
                            snapshot.getRef().child("noteCount").setValue(noteCount);
                        }

                        boolean b = false;
                        if (lastNoteTime != null) {
                            long diffInMillies = Math.abs(new Date().getTime() - CustomDateTime.getDate(lastNoteTime).getTime());
                            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                            b = diff > 6;
                        }

                        if (lastNoteTime == null || (noteCount >= count && b)) {
                            noteCount = 0;
                            snapshot.getRef().child("lastNoteTime").setValue(CustomDateTime.getDate(new Date()));
                            snapshot.getRef().child("noteCount").setValue(noteCount);
                            textViewNoteLessonStatus.setText("Qiymətləndirmə:   " + noteCount + " / " + count);

                            textViewNoteLessonStatus.setTextColor(Color.parseColor("#01294b"));
                            textViewNoteLessonStatus.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            return;
                        }

                        textViewNoteLessonStatus.setText("Qiymətləndirmə:   " + noteCount + " / " + count);
                        textViewNoteLessonStatus.setTextColor(Color.parseColor(b ? "#FFFFFF" : "#01294b"));
                        textViewNoteLessonStatus.setBackgroundColor(Color.parseColor(b ? "#FF5722" : "#FFFFFF"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadSeed() {
        buttonLogOut = findViewById(R.id.buttonLogOutFromTeacherAccount);
        buttonLogOut.setOnClickListener(this);

        buttonTeacherNext = findViewById(R.id.buttonNext);
        buttonTeacherNext.setOnClickListener(this);

        editTextLessonSubject = findViewById(R.id.editTextExamSubject);

        databaseReference.child("TEACHERS/" + rootName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                Boolean active = dataSnapshot.child("active").getValue(Boolean.class);

                if (!goback && (active == null || !active)) {
                    firebaseAuth.signOut();
                    finish();
                }

                loadClassLesson();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadClassLesson() {
        listLessons.clear();
        listClasses.clear();
        listLessons.add(getString(R.string.select_lesson_name));
        listClasses.add(getString(R.string.select_class_name));

        configureClassesSpinner();
        configureLessonsSpinner();

        GenericTypeIndicator<ArrayList<String>> s = new GenericTypeIndicator<ArrayList<String>>() {
        };

        databaseReference.child("SCHOOL").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotSchool) {
                progressDialog.dismiss();

                for (DataSnapshot snapshot : dataSnapshotSchool.getChildren()) {
                    listClasses = snapshot.child("classes").getValue(s);
                    listLessons = snapshot.child("lessons").getValue(s);
                }

                if (listLessons == null || listClasses == null) return;

                listLessons.add(0, getString(R.string.select_lesson_name));
                listClasses.add(0, getString(R.string.select_class_name));

                configureClassesSpinner();
                configureLessonsSpinner();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                SharedClass.showSnackBar(activity, activity.getString(R.string.error_check_internet));
            }
        });
    }

    private void configureClassesSpinner() {
        spinnerClass = findViewById(R.id.spinnerClasses);
        ArrayAdapter<String> adapterClasses = new ArrayAdapter<>(this, R.layout.layout_spinner_item, listClasses);
        spinnerClass.setAdapter(adapterClasses);
    }

    private void configureLessonsSpinner() {
        spinnerLesson = findViewById(R.id.spinnerLessons);
        ArrayAdapter<String> adapterLessons = new ArrayAdapter<>(this, R.layout.layout_spinner_item, listLessons);
        spinnerLesson.setAdapter(adapterLessons);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogOut) {
            firebaseAuth.signOut();
            if (goback) {
                final CustomProgressDialog progressDialog = new CustomProgressDialog(this, getString(R.string.signing));
                progressDialog.show();

                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        finish();
                        startActivity(new Intent(getBaseContext(), SchoolProfileActivity.class));
                    } else {
                        progressDialog.show();
                        SharedClass.showSnackBar(activity, activity.getString(R.string.incorrect_email));
                    }
                });
            } else {
                finish();
                startActivity(new Intent(this, LoginActivity.class));
            }
        }

        if (view == buttonTeacherNext) {
            if (spinnerLesson.getSelectedItemId() == 0) {
                SharedClass.showSnackBar(activity, activity.getString(R.string.select_lesson_name));
                return;
            }

            if (spinnerClass.getSelectedItemId() == 0) {
                SharedClass.showSnackBar(activity, activity.getString(R.string.select_class_name));
                return;
            }

            String lessonSubject = editTextLessonSubject.getText().toString();
            if (lessonSubject.equals("")) lessonSubject = "Həftəlik qiymətləndirmə";

            Intent intent = new Intent(getBaseContext(), TeacherNoteLessonDataActivity.class);
            intent.putExtra("selectedClass", spinnerClass.getSelectedItem().toString());
            intent.putExtra("lessonName", spinnerLesson.getSelectedItem().toString());
            intent.putExtra("lessonSubject", lessonSubject);
            intent.putExtra("noteCount", noteCount);
            startActivity(intent);
        }
    }
}
