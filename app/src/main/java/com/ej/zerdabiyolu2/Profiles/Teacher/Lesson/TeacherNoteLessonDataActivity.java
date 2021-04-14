package com.ej.zerdabiyolu2.Profiles.Teacher.Lesson;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TeacherNoteLessonDataActivity extends AppCompatActivity implements LessonDataDialog.StudentDataDialogListener {

    private final ArrayList<String> listStudents = new ArrayList<>();
    private final ArrayList<String> listTexts = new ArrayList<>();
    private final HashMap<String, StudentLessonModel> allData = new HashMap<>();
    private ArrayAdapter<String> adapterStudents;
    private DatabaseReference databaseReference;

    private TextView selectedCardView;
    private int selectedItemPosition, noteCount;
    private String selectedClass, lessonName, lessonSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_note_lesson_data);

        databaseReference = DatabaseFunctions.getDatabases(this).get(0);

        selectedClass = getIntent().getStringExtra("selectedClass");
        lessonName = getIntent().getStringExtra("lessonName");
        lessonSubject = getIntent().getStringExtra("lessonSubject");
        noteCount = getIntent().getIntExtra("noteCount", 0);

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
            openStudentDataDialog();
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
                    name = postSnapshot.child("name").getValue(String.class);

                    listStudents.add(rootName + " " + name);
                    listTexts.add(rootName + " " + name);
                }
                adapterStudents.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), R.string.error_check_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openStudentDataDialog() {
        LessonDataDialog dialog = new LessonDataDialog();
        StudentLessonModel value = allData.get(listStudents.get(selectedItemPosition));
        if (value != null) {
            dialog.setDefaultValues(value.getExtraInformation(), value.getLessonRate(), value.getBehaviourRate());
        }
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void getData(String extraInformation, String lessonRate, String behaviourRate) {
        StudentLessonModel studentLessonModel = new StudentLessonModel(
                lessonName,
                lessonSubject,
                lessonRate,
                behaviourRate,
                extraInformation);
        allData.put(listStudents.get(selectedItemPosition), studentLessonModel);

        selectedCardView.setTextColor(Color.parseColor("#3DDC84"));
        adapterStudents.notifyDataSetChanged();
    }

    private void saveData() {
        for (Map.Entry<String, StudentLessonModel> pair : allData.entrySet()) {
            databaseReference
                    .child("LESSON_HISTORY/" + pair.getKey() + "/"
                            + CustomDateTime.getDate(new Date())
                            + "/" + CustomDateTime.getTime(new Date()))
                    .setValue(pair.getValue());
        }
        databaseReference.child("TEACHER_NOTE_LESSON/"
                + FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0]
                + "/noteCount")
                .setValue(++noteCount);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (allData.size() > 0) {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.sure_to_exit).setPositiveButton(R.string.yes, dialogClickListener).
                    setNegativeButton(R.string.no, dialogClickListener).show();
        } else finish();
    }
}
