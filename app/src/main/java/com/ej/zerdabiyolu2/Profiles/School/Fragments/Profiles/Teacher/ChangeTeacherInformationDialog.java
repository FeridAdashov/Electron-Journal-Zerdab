package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Teacher;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.ej.zerdabiyolu2.Helper.StorageFunctions;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DatabaseReference;

public class ChangeTeacherInformationDialog extends DialogFragment {

    private EditText editTextCountOfClass, editTextBiography;

    private int countOfClass;
    private String biography, userName, password;
    private boolean b = false;

    private DatabaseReference databaseReference;

    @Override
    public void onDismiss(@NonNull final DialogInterface dialog) {
        super.onDismiss(dialog);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) parentFragment).onDismiss(dialog);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.change_teacher_information_dialog, null);

        editTextCountOfClass = view.findViewById(R.id.editTextTeacherCountOfClass);
        editTextBiography = view.findViewById(R.id.editTextTeacherBiography);
        TextView textViewUserName = view.findViewById(R.id.textViewUserName);
        TextView textViewPassword = view.findViewById(R.id.textViewPassword);

        builder.setPositiveButton(getString(R.string.save), (dialogInterface, i) -> {
            try {
                databaseReference.child("countOfClass").setValue(Integer.parseInt(editTextCountOfClass.getText().toString()));
                databaseReference.child("biography").setValue(editTextBiography.getText().toString());
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.device_storage_error, Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton(getString(R.string.delete), (dialogInterface, i) -> StorageFunctions.deleteUser(getActivity(), databaseReference, false))
                .setNeutralButton(R.string.store_data, (dialogInterface, i) -> StorageFunctions.storeUserInformation(getActivity(), databaseReference)).setView(view);

        if (b) {
            editTextCountOfClass.setText(String.valueOf(countOfClass));
            editTextBiography.setText(biography);
            textViewUserName.setText(userName);
            textViewPassword.setText(password);
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
            d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
            d.getButton(android.app.AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(android.R.color.holo_green_light));
        });
        return d;
    }

    public void setDefaultValues(String userName, String password,
                                 Integer countOfClass, String biography,
                                 DatabaseReference databaseReference) {
        this.userName = userName;
        this.password = password;
        this.countOfClass = countOfClass;
        this.biography = biography;
        this.databaseReference = databaseReference;
        b = true;
    }
}
