package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.ExamResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GiveExamResultActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextExamSubject;
    private Button buttonNext;

    private DatabaseReference databaseReference;

    private ArrayList<String> listLessons = new ArrayList<>();
    private ArrayList<String> listClasses = new ArrayList<>();
    private Spinner spinnerClass, spinnerLesson;
    private CustomProgressDialog progressDialog;

    private Activity activity;
    private String teacher, teacherUsername;
    private int givenResultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_exam_result);

        activity = this;
        teacher = getIntent().getStringExtra("teacher");
        teacherUsername = getIntent().getStringExtra("teacherUsername");
        givenResultCount = getIntent().getIntExtra("givenResultCount", 0);

        progressDialog = new CustomProgressDialog(this, getString(R.string.data_loading));
        progressDialog.show();

        databaseReference = DatabaseFunctions.getDatabases(this).get(0);

        loadSeed();
    }

    private void loadSeed() {
        ImageView imageViewArrowBack = findViewById(R.id.imageViewArrowBack);
        imageViewArrowBack.setOnClickListener(v -> finish());

        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(this);

        editTextExamSubject = findViewById(R.id.editTextExamSubject);

        loadClassLesson();
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
        if (view == buttonNext) {
            if (TextUtils.isEmpty(editTextExamSubject.getText().toString())) {
                SharedClass.showSnackBar(activity, "Ad verin");
                return;
            }

            if (spinnerLesson.getSelectedItemId() == 0) {
                SharedClass.showSnackBar(activity, activity.getString(R.string.select_lesson_name));
                return;
            }

            if (spinnerClass.getSelectedItemId() == 0) {
                SharedClass.showSnackBar(activity, activity.getString(R.string.select_class_name));
                return;
            }

            Intent intent = new Intent(getBaseContext(), NoteExamDataActivity.class);
            intent.putExtra("teacher", teacher);
            intent.putExtra("teacherUsername", teacherUsername);
            intent.putExtra("givenResultCount", givenResultCount);
            intent.putExtra("selectedClass", spinnerClass.getSelectedItem().toString());
            intent.putExtra("lessonName", spinnerLesson.getSelectedItem().toString());
            intent.putExtra("examSubject", editTextExamSubject.getText().toString());
            startActivity(intent);
        }
    }
}
