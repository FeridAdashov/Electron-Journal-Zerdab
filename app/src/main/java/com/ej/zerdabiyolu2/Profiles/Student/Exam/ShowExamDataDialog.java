package com.ej.zerdabiyolu2.Profiles.Student.Exam;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ej.zerdabiyolu2.Profiles.Manager.Fragments.ExamResult.StudentExamModel;
import com.ej.zerdabiyolu2.R;

public class ShowExamDataDialog extends AppCompatDialogFragment {

    private StudentExamModel examModel;
    private boolean b = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.show_exam_data_dialog, null);

        TextView textViewLesson = view.findViewById(R.id.textViewLesson);
        TextView textViewExamSubject = view.findViewById(R.id.textViewExamSubject);
        TextView textViewNumberOfCorrects = view.findViewById(R.id.textViewNumberOfCorrects);
        TextView textViewNumberOfWrongs = view.findViewById(R.id.textViewNumberOfWrongs);
        TextView textViewCommonNumber = view.findViewById(R.id.textViewCommonNumber);
        TextView textViewTeacher = view.findViewById(R.id.textViewTeacher);
        TextView textViewExtraInformation = view.findViewById(R.id.textViewExtraInformation);

        builder.setView(view).setPositiveButton(R.string.close, (dialogInterface, i) -> {
        });

        if (b) {
            textViewLesson.setText(examModel.getLesson());
            textViewExamSubject.setText(examModel.getExamSubject());
            textViewNumberOfCorrects.setText(examModel.getNumberOfCorrects());
            textViewNumberOfWrongs.setText(examModel.getNumberOfWrongs());
            textViewCommonNumber.setText(examModel.getCommonNumber());
            textViewTeacher.setText(examModel.getTeacher());
            textViewExtraInformation.setText(examModel.getExtraInformation());
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

    public void setDefaultValues(StudentExamModel examModel) {
        this.examModel = examModel;
        b = true;
    }
}
