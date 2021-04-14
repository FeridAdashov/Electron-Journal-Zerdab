package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.ExamResult;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.CustomDateTime;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NoteExamDataActivity extends AppCompatActivity implements ExamDataDialog.ExamDataDialogListener {

    private final ArrayList<String> listStudents = new ArrayList<>();
    private final ArrayList<String> listTexts = new ArrayList<>();
    private final HashMap<String, StudentExamModel> allData = new HashMap<>();
    private ArrayAdapter<String> adapterStudents;
    private DatabaseReference databaseReference;

    private TextView selectedCardView;
    private int selectedItemPosition, givenResultCount;
    private String teacher,teacherUsername, selectedClass, lessonName, examSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_note_lesson_data);

        databaseReference = DatabaseFunctions.getDatabases(this).get(0);

        teacher = getIntent().getStringExtra("teacher");
        teacherUsername = getIntent().getStringExtra("teacherUsername");
        givenResultCount = getIntent().getIntExtra("givenResultCount", 0);
        selectedClass = getIntent().getStringExtra("selectedClass");
        lessonName = getIntent().getStringExtra("lessonName");
        examSubject = getIntent().getStringExtra("examSubject");

        loadSeed();
        loadStudents();
    }

    private void loadSeed() {
        ImageButton imageButtonNote = findViewById(R.id.buttonSaveLesson);
        imageButtonNote.setOnClickListener(v -> {
            if (listStudents.size() == 0) return;

            if (allData.size() < listStudents.size()) {
                SharedClass.showSnackBar(this, "Bütün şagirdləri qiymətləndirin");
                return;
            }

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        saveData();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.sure_to_save).setPositiveButton(R.string.yes, dialogClickListener).
                    setNegativeButton(R.string.no, dialogClickListener).show();
        });

        ImageButton imageButtonArrowBack = findViewById(R.id.imageButtonArrowBack);
        imageButtonArrowBack.setOnClickListener(v -> finish());

        listStudents.clear();
        listTexts.clear();
        allData.clear();

        ListView listViewStudents = findViewById(R.id.listViewStudents);
        adapterStudents = new ArrayAdapter<>(this, R.layout.list_card_item, R.id.listCardTitle, listTexts);

        listViewStudents.setAdapter(adapterStudents);
        listViewStudents.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedItemPosition = i;
            selectedCardView = view.findViewById(R.id.listCardTitle);
            openExamDataDialog();
        });
    }

    private void loadStudents() {
        final CustomProgressDialog progressDialog = new CustomProgressDialog(this, getString(R.string.data_loading));
        progressDialog.show();

        databaseReference.child("STUDENTS/" + selectedClass).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();

                String name, rootName;

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    rootName = postSnapshot.getKey();
                    listStudents.add(rootName);

                    name = postSnapshot.child("name").getValue(String.class);
                    listTexts.add(name);
                }
                adapterStudents.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), R.string.error_check_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openExamDataDialog() {
        ExamDataDialog dialog = new ExamDataDialog();
        StudentExamModel value = allData.get(listStudents.get(selectedItemPosition));
        if (value != null) {
            dialog.setDefaultValues(value.getExtraInformation(), value.getCommonNumber(),
                    value.getNumberOfCorrects(), value.getNumberOfWrongs());
        }
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void getData(String extraInformation, String commonNumber, String numberOfCorrects, String numberOfWrongs) {
        StudentExamModel StudentExamModel = new StudentExamModel(
                teacher,
                lessonName,
                examSubject,
                commonNumber,
                numberOfCorrects,
                numberOfWrongs,
                extraInformation);
        allData.put(listStudents.get(selectedItemPosition), StudentExamModel);

        selectedCardView.setTextColor(Color.parseColor("#3DDC84"));
        adapterStudents.notifyDataSetChanged();
    }

    private void saveData() {
        for (Map.Entry<String, StudentExamModel> pair : allData.entrySet()) {
            databaseReference
                    .child("EXAM_HISTORY/" + pair.getKey() + "/"
                            + CustomDateTime.getDate(new Date()) + "/"
                            + CustomDateTime.getTime(new Date()))
                    .setValue(pair.getValue());
        }
        databaseReference.child("TEACHER_GIVE_RESULT/" + teacherUsername + "/givenResultCount").setValue(++givenResultCount);
        databaseReference.child("TEACHER_GIVE_RESULT/" + teacherUsername + "/lastGiveTime").setValue(CustomDateTime.getDate(new Date()));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (allData.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.sure_to_exit)
                    .setPositiveButton(R.string.yes, (dialog, which) -> finish()).
                    setNegativeButton(R.string.no, null).show();
        } else finish();
    }
}
