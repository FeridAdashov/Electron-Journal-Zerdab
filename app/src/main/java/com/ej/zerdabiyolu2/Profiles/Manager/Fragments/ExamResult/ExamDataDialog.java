package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.ExamResult;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ej.zerdabiyolu2.R;

public class ExamDataDialog extends AppCompatDialogFragment {

    boolean b = false;
    private ExamDataDialogListener listener;
    private String extra, commonCount, numberOfCorrects, numberOfWrongs;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.layout_exam_data, null);

        EditText editTextExtraInformation = view.findViewById(R.id.editTextExtraInformation);
        EditText editTextCommonCount = view.findViewById(R.id.editTextCommonCount);
        EditText editTextCountOfCorrects = view.findViewById(R.id.editTextCountOfCorrects);
        EditText editTextCountOfWrongs = view.findViewById(R.id.editTextCountOfWrongs);

        builder.setView(view)
                .setPositiveButton(getString(R.string.save), (dialogInterface, i) -> listener.getData(
                        editTextExtraInformation.getText().toString(),
                        editTextCommonCount.getText().toString(),
                        editTextCountOfCorrects.getText().toString(),
                        editTextCountOfWrongs.getText().toString()))
                .setNegativeButton(getString(R.string.m_cancel), null);

        if (b) {
            editTextExtraInformation.setText(extra);
            editTextCommonCount.setText(commonCount);
            editTextCountOfCorrects.setText(numberOfCorrects);
            editTextCountOfWrongs.setText(numberOfWrongs);
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
            listener = (ExamDataDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ExamDataDialogListener");
        }
    }

    public void setDefaultValues(String extra, String commonCount, String numberOfCorrects, String numberOfWrongs) {
        this.extra = extra;
        this.commonCount = commonCount;
        this.numberOfCorrects = numberOfCorrects;
        this.numberOfWrongs = numberOfWrongs;
        b = true;
    }

    public interface ExamDataDialogListener {
        void getData(String extraInformation, String commonCount, String numberOfCorrects, String numberOfWrongs);
    }
}
