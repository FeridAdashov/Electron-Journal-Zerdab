package com.ej.zerdabiyolu2.Profiles.Teacher.Lesson;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ej.zerdabiyolu2.R;

import java.util.ArrayList;
import java.util.Arrays;

public class LessonDataDialog extends AppCompatDialogFragment {

    private final ArrayList<String> listBehaviourRate = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
    private final ArrayList<String> listLessonRate = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
    boolean b = false;
    private EditText editTextExtraInformation;
    private Spinner spinnerLessonRate, spinnerBehaviourRate;
    private StudentDataDialogListener listener;
    private String extra, posLesson, posBehaviour;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.layout_lesson_data, null);

        spinnerLessonRate = view.findViewById(R.id.spinnerLessonRate);
        ArrayAdapter<String> adapterLessonRate = new ArrayAdapter<>(getContext(), R.layout.layout_spinner_item, listLessonRate);
        spinnerLessonRate.setAdapter(adapterLessonRate);

        spinnerBehaviourRate = view.findViewById(R.id.spinnerBehaviourRate);
        ArrayAdapter<String> adapterBehaviourRate = new ArrayAdapter<>(getContext(), R.layout.layout_spinner_item, listBehaviourRate);
        spinnerBehaviourRate.setAdapter(adapterBehaviourRate);

        editTextExtraInformation = view.findViewById(R.id.editTextExtraInformation);

        String s = getString(R.string.not_present);
        listLessonRate.add(0, s);
        listBehaviourRate.add(0, s);

        builder.setView(view)
                .setPositiveButton(getString(R.string.save), (dialogInterface, i) -> listener.getData(
                        editTextExtraInformation.getText().toString(),
                        spinnerLessonRate.getSelectedItem().toString(),
                        spinnerBehaviourRate.getSelectedItem().toString()));

        if (b) {
            editTextExtraInformation.setText(extra);
            spinnerLessonRate.setSelection(listLessonRate.indexOf(posLesson));
            spinnerBehaviourRate.setSelection(listBehaviourRate.indexOf(posBehaviour));
        }

        final int h = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
        final AlertDialog d = builder.create();
        d.getWindow().setBackgroundDrawableResource(R.drawable.shapesignup);
        d.setOnShowListener(arg0 -> {
            int width = view.getWidth();
            int height = view.getHeight();

            if (height > h)
                d.getWindow().setLayout(width, h);

            d.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_green_light));
        });
        return d;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (StudentDataDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement StudentDataDialogListener");
        }
    }

    public void setDefaultValues(String extra, String posLesson, String posBehaviour) {
        this.extra = extra;
        this.posLesson = posLesson;
        this.posBehaviour = posBehaviour;
        b = true;
    }

    public interface StudentDataDialogListener {
        void getData(String extraInformation, String lessonRate, String behaviourRate);
    }
}
