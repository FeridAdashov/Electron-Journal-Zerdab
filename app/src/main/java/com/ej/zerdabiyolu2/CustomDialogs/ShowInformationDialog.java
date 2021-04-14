package com.ej.zerdabiyolu2.CustomDialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ej.zerdabiyolu2.R;

public class ShowInformationDialog extends AppCompatDialogFragment {

    TextView textViewInfo;

    private String info = "";
    private boolean b = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.show_info_dialog, null);

        textViewInfo = view.findViewById(R.id.textViewİnfo);

        builder.setView(view)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {

                });

        if (b) {
            textViewInfo.setText(info);
        }

        final int h = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
        final AlertDialog d = builder.create();
        d.getWindow().setBackgroundDrawableResource(R.drawable.shapesignup);
        d.setOnShowListener(d2 -> {
            int width = view.getWidth();
            int height = view.getHeight();

            if (height > h)
                d.getWindow().setLayout(width, h);

            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        });
        return d;
    }

    public void setMessage(String info) {
        this.info = info;
        b = true;
    }
}
