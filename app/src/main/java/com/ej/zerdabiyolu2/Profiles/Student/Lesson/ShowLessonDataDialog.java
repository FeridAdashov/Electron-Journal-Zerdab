package com.ej.zerdabiyolu2.Profiles.Student.Lesson;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ej.zerdabiyolu2.R;

public class ShowLessonDataDialog extends AppCompatDialogFragment {

    private String lesson, lessonSubject, extraInformation, lessonRate, behaviourRate;
    private boolean b = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.show_lesson_data_dialog, null);

        TextView textViewDataLesson = view.findViewById(R.id.textViewDataLesson);
        TextView textViewDataLessonSubject = view.findViewById(R.id.textViewDataSubject);
        TextView textViewDataLessonRate = view.findViewById(R.id.textViewDataLessonRate);
        TextView textViewDataBehaviourRate = view.findViewById(R.id.textViewDataBehaviourRate);
        TextView textViewExtraInformation = view.findViewById(R.id.textViewExtraInformation);

        builder.setView(view).setPositiveButton(R.string.close, (dialogInterface, i) -> {
        });

        if (b) {
            textViewDataLesson.setText(lesson);
            textViewDataLessonSubject.setText(lessonSubject);
            textViewDataLessonRate.setText(lessonRate);
            textViewDataBehaviourRate.setText(behaviourRate);
            textViewExtraInformation.setText(extraInformation);
        }

        final int h = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
        final AlertDialog d = builder.create();
        d.getWindow().setBackgroundDrawableResource(R.drawable.shapesignup);
        d.setOnShowListener(d2 -> {
            int width = view.getWidth();
            int height = view.getHeight();

            if (height > h) d.getWindow().setLayout(width, h);

            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accent));
        });

        return d;
    }

    public void setDefaultValues(String lesson, String lessonSubject, String lessonRate, String behaviourRate, String extra) {
        this.lesson = lesson;
        this.lessonSubject = lessonSubject;
        this.lessonRate = lessonRate;
        this.behaviourRate = behaviourRate;
        this.extraInformation = extra;
        b = true;
    }
}
