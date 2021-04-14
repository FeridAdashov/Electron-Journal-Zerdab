package com.ej.zerdabiyolu2.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ej.zerdabiyolu2.R;

public class ChangeValueDialog extends AppCompatDialogFragment {

    private String value = "";
    private int index, id;
    private ChangeValueDialogListener listener;

    private EditText editTextValue;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.change_value_dialog, null);

        editTextValue = view.findViewById(R.id.editTextValue);
        editTextValue.setText(value);

        builder.setView(view)
                .setPositiveButton("Delete", (dialogInterface, i) -> listener.getData("", index, id, 0))
                .setNegativeButton("Save", (dialogInterface, i) -> listener.getData(editTextValue.getText().toString(), index, id, 1));

        final int h = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
        final AlertDialog d = builder.create();
        d.getWindow().setBackgroundDrawableResource(R.drawable.shapesignup);
        d.setOnShowListener(arg0 -> {
            int width = view.getWidth();
            int height = view.getHeight();

            if (height > h)
                d.getWindow().setLayout(width, h);

            d.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_green_light));
            d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
        });
        return d;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ChangeValueDialog.ChangeValueDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ChangeValueDialogListener");
        }
    }

    public void setValue(String value, int i, int id) {
        this.value = value;
        this.index = i;
        this.id = id;
    }

    public interface ChangeValueDialogListener {
        void getData(String value, int i, int id, int status);
    }
}
