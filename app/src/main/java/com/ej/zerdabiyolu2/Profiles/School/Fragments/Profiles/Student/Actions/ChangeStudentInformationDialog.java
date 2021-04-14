package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Student.Actions;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.ej.zerdabiyolu2.Helper.StorageFunctions;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChangeStudentInformationDialog extends DialogFragment {

    private EditText editTextStudentBiography, editTextStudentMoneyPerMonth;
    private Spinner spinnerClasses;

    private String username, biography, password, registrationDate;
    private Double moneyPerMonth;
    private int currentClassId;
    private ArrayList<String> classes;
    private boolean b = false;

    private DatabaseReference databaseReference;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.change_student_information_dialog, null);

        editTextStudentBiography = view.findViewById(R.id.editTextStudentBiography);
        editTextStudentMoneyPerMonth = view.findViewById(R.id.editTextStudentMoneyPerMonth);
        TextView textViewUserName = view.findViewById(R.id.textViewUserName);
        TextView textViewPassword = view.findViewById(R.id.textViewPassword);
        TextView textViewRegistrationDate = view.findViewById(R.id.textViewRegistrationDate);

        spinnerClasses = view.findViewById(R.id.spinnerClasses);

        builder.setPositiveButton(getString(R.string.save), (dialogInterface, i) -> {
            try {
                databaseReference.child("biography").setValue(editTextStudentBiography.getText().toString());
                databaseReference.child("currentClass").setValue(spinnerClasses.getSelectedItem().toString());
                databaseReference.child("PaymentInfo/moneyPerMonth").setValue(Double.parseDouble(editTextStudentMoneyPerMonth.getText().toString()));
            } catch (Exception e) {
                Toast.makeText(getContext(), getString(R.string.device_storage_error), Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton(getString(R.string.delete), (dialogInterface, i) -> StorageFunctions.deleteUser(getActivity(), databaseReference, true))
                .setNeutralButton(R.string.store_data, (dialogInterface, i) -> StorageFunctions.storeUserInformation(getActivity(), databaseReference))
                .setView(view);

        if (b) {
            editTextStudentBiography.setText(biography);
            editTextStudentMoneyPerMonth.setText(String.valueOf(moneyPerMonth));
            textViewUserName.setText(username);
            textViewPassword.setText(password);
            textViewRegistrationDate.setText(registrationDate);
            ArrayAdapter<String> adapterClasses = new ArrayAdapter<>(getContext(), R.layout.layout_spinner_item, classes);
            spinnerClasses.setAdapter(adapterClasses);
            spinnerClasses.setSelection(currentClassId);
            adapterClasses.notifyDataSetChanged();
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

    public void setDefaultValues(String username,
                                 String biography,
                                 Double moneyPerMonth,
                                 int currentClassId,
                                 ArrayList<String> classes,
                                 String password,
                                 String registrationDate,
                                 DatabaseReference ref) {
        this.username = username;
        this.biography = biography;
        this.moneyPerMonth = moneyPerMonth;
        this.currentClassId = currentClassId;
        this.classes = classes;
        this.password = password;
        this.registrationDate = registrationDate;
        this.databaseReference = ref;
        b = true;
    }
}
