package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.School;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.StorageFunctions;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChangeSchoolInformationDialog extends AppCompatDialogFragment implements View.OnClickListener {

    boolean b = false;
    private EditText editTextClassName;
    private EditText editTextLessonName;
    private EditText editTextMoneyPerMonth;
    private Spinner spinnerClasses;
    private Spinner spinnerLessons;
    private ImageButton buttonAddLessonName;
    private ImageButton buttonAddClassName;
    private ArrayList<String> classes;
    private ArrayList<String> lessons;
    private Double moneyPerMonth;
    private ArrayAdapter<String> adapterClasses;
    private ArrayAdapter<String> adapterLessons;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.change_school_information_dialog, null);

        editTextClassName = view.findViewById(R.id.editTextClassName);
        editTextLessonName = view.findViewById(R.id.editTextLessonName);
        editTextMoneyPerMonth = view.findViewById(R.id.editTextMoneyPerMonth);
        spinnerClasses = view.findViewById(R.id.spinnerClasses);
        spinnerLessons = view.findViewById(R.id.spinnerLessons);
        buttonAddClassName = view.findViewById(R.id.buttonAddClassName);
        buttonAddLessonName = view.findViewById(R.id.buttonAddLessonName);

        buttonAddClassName.setOnClickListener(this);
        buttonAddLessonName.setOnClickListener(this);

        spinnerClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                if (i != 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.delete_class_name)
                            .setMessage(R.string.sure_to_delete_class)
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                classes.remove(i);
                                adapterClasses.notifyDataSetChanged();
                            })
                            .setNegativeButton(R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    spinnerClasses.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerLessons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                if (i != 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.delete_lesson_name)
                            .setMessage(R.string.sure_to_delete_lesson)
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                lessons.remove(i);
                                adapterLessons.notifyDataSetChanged();
                            })
                            .setNegativeButton(R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    spinnerLessons.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        builder.setNegativeButton(getString(R.string.save), (dialogInterface, i) -> {
            try {
                saveData(
                        Double.parseDouble(editTextMoneyPerMonth.getText().toString()),
                        classes,
                        lessons);
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.incorrect_decimal_value, Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton(getString(R.string.m_cancel), null).setView(view);

        if (b) {
            classes.add(0, getString(R.string.class_list));
            lessons.add(0, getString(R.string.leeson_list));
            editTextMoneyPerMonth.setText(String.valueOf(moneyPerMonth));
            adapterClasses = new ArrayAdapter<>(getContext(), R.layout.layout_spinner_item, classes);
            spinnerClasses.setAdapter(adapterClasses);
            adapterLessons = new ArrayAdapter<>(getContext(), R.layout.layout_spinner_item, lessons);
            spinnerLessons.setAdapter(adapterLessons);
        }

        final int h = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
        final AlertDialog d = builder.create();
        d.getWindow().setBackgroundDrawableResource(R.drawable.shapesignup);
        d.setOnShowListener(arg0 -> {
            int width = view.getWidth();
            int height = view.getHeight();

            if (height > h)
                d.getWindow().setLayout(width, h);

            d.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
            d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.holo_green_light));
        });
        return d;
    }

    private void saveData(Double moneyPerMonth, ArrayList<String> classes, ArrayList<String> lessons) {
        classes.remove(0);
        lessons.remove(0);
        StorageFunctions.sortArray(classes);
        StorageFunctions.sortArray(lessons);
        try {
            DatabaseReference databaseReference = DatabaseFunctions.getDatabases(getActivity()).get(0)
                    .child("SCHOOL/" + FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0]);
            databaseReference.child("moneyPerMonth").setValue(moneyPerMonth);
            databaseReference.child("classes").setValue(classes);
            databaseReference.child("lessons").setValue(lessons);
        } catch (Exception e) {
            Log.d("AAAAAA", e.toString());
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonAddClassName) {
            String name = editTextClassName.getText().toString().trim().toUpperCase();
            if (!name.equals("") && !classes.contains(name)) {
                classes.add(name);
                editTextClassName.setText("");
            }
        }

        if (view == buttonAddLessonName) {
            String name = editTextLessonName.getText().toString().trim().toUpperCase();
            if (!name.equals("") && !lessons.contains(name)) {
                lessons.add(name);
                editTextLessonName.setText("");
            }
        }
    }

    public void setDefaultValues(Double moneyPerMonth, ArrayList<String> classes, ArrayList<String> lessons) {
        this.classes = classes;
        this.lessons = lessons;
        this.moneyPerMonth = moneyPerMonth;
        b = true;
    }
}
