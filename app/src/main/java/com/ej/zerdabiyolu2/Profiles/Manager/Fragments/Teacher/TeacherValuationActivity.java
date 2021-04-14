package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Teacher;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.ej.zerdabiyolu2.Helper.CustomDateTime;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Teacher.Models.TeacherValuationModel;
import com.ej.zerdabiyolu2.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class TeacherValuationActivity extends AppCompatActivity {

    private final ArrayList<String> rates = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
    private EditText editTextClassName, editTextLessonName, editTextExtraInformation;
    private CheckBox checkboxStartInTime, checkboxCheckHomework, checkboxTeachNewSubject, checkboxGivingHomework, checkboxUsingTechnology, checkboxUsingVisualAids;
    private Spinner spinnerSuitableDressed, spinnerManageTime, spinnerWorkOnMistakes, spinnerQuestionAnswer;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_valuation);

        username = getIntent().getStringExtra("user");

        ImageButton imageButtonNote = findViewById(R.id.imageButtonNoteValuation);
        imageButtonNote.setOnClickListener(v -> note());

        ImageButton imageButtonArrowBack = findViewById(R.id.imageButtonArrowBack);
        imageButtonArrowBack.setOnClickListener(v -> finish());

        editTextClassName = findViewById(R.id.editTextClassName);
        editTextLessonName = findViewById(R.id.editTextLessonName);
        editTextExtraInformation = findViewById(R.id.editTextExtraInformation);
        checkboxStartInTime = findViewById(R.id.checkboxStartInTime);
        checkboxCheckHomework = findViewById(R.id.checkboxCheckHomework);
        checkboxTeachNewSubject = findViewById(R.id.checkboxTeachNewSubject);
        checkboxGivingHomework = findViewById(R.id.checkboxGivingHomework);
        checkboxUsingTechnology = findViewById(R.id.checkboxUsingTechnology);
        checkboxUsingVisualAids = findViewById(R.id.checkboxUsingVisualAids);

        spinnerSuitableDressed = findViewById(R.id.spinnerSuitableDressed);
        ArrayAdapter<String> adapterSuitableDressed = new ArrayAdapter<>(this, R.layout.layout_spinner_item, rates);
        spinnerSuitableDressed.setAdapter(adapterSuitableDressed);

        spinnerManageTime = findViewById(R.id.spinnerManageTime);
        ArrayAdapter<String> adapterManageTime = new ArrayAdapter<>(this, R.layout.layout_spinner_item, rates);
        spinnerManageTime.setAdapter(adapterManageTime);

        spinnerWorkOnMistakes = findViewById(R.id.spinnerWorkOnMistakes);
        ArrayAdapter<String> adapterWorkOnMistakes = new ArrayAdapter<>(this, R.layout.layout_spinner_item, rates);
        spinnerWorkOnMistakes.setAdapter(adapterWorkOnMistakes);

        spinnerQuestionAnswer = findViewById(R.id.spinnerQuestionAnswer);
        ArrayAdapter<String> adapterQuestionAnswer = new ArrayAdapter<>(this, R.layout.layout_spinner_item, rates);
        spinnerQuestionAnswer.setAdapter(adapterQuestionAnswer);
    }

    private void note() {
        String date = CustomDateTime.getDate(new Date());
        String time = CustomDateTime.getTime(new Date());

        String className = editTextClassName.getText().toString();
        String lessonName = editTextLessonName.getText().toString();
        String extra = editTextExtraInformation.getText().toString();

        if (className.equals("") || lessonName.equals("")) return;

        String suitableDressed = spinnerSuitableDressed.getSelectedItem().toString();
        String manageTime = spinnerManageTime.getSelectedItem().toString();
        String workOnMistakes = spinnerWorkOnMistakes.getSelectedItem().toString();
        String questionAnswer = spinnerQuestionAnswer.getSelectedItem().toString();

        String startInTime;
        if (checkboxStartInTime.isChecked()) startInTime = "Hə";
        else startInTime = "Yox";

        String checkHomework;
        if (checkboxCheckHomework.isChecked()) checkHomework = "Hə";
        else checkHomework = "Yox";

        String teachNewSubject;
        if (checkboxTeachNewSubject.isChecked()) teachNewSubject = "Hə";
        else teachNewSubject = "Yox";

        String givingHomework;
        if (checkboxGivingHomework.isChecked()) givingHomework = "Hə";
        else givingHomework = "Yox";

        String usingTechnology;
        if (checkboxUsingTechnology.isChecked()) usingTechnology = "Hə";
        else usingTechnology = "Yox";

        String usingVisualAids;
        if (checkboxUsingVisualAids.isChecked()) usingVisualAids = "Hə";
        else usingVisualAids = "Yox";

        TeacherValuationModel data = new TeacherValuationModel(
                className,
                lessonName,
                startInTime,
                suitableDressed,
                checkHomework,
                workOnMistakes,
                manageTime,
                teachNewSubject,
                givingHomework,
                usingTechnology,
                usingVisualAids,
                questionAnswer,
                extra);

        DatabaseFunctions.getDatabases(this).get(0)
                .child("TEACHER_VALUATION/" + username + "/" + date + " " + time)
                .setValue(data);

        DatabaseFunctions.getDatabases(this).get(0)
                .child("TEACHERS/" + username + "/lastCheckedTime").setValue(date);

        finish();
    }
}