package com.ej.zerdabiyolu2.Profiles.School.Fragments.DeletedUsers;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.CustomDialogs.ShowInformationDialog;
import com.ej.zerdabiyolu2.Helper.CustomDateTime;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class DeletedUsersSchool extends Fragment {

    private CustomProgressDialog progressDialog;
    private ArrayAdapter<String> listAdapter;
    private final ArrayList<String> listUsers = new ArrayList<>();
    private final ArrayList<String> listSearchUsers = new ArrayList<>();

    private DatabaseReference databaseReference;

    private View view;
    private Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_deleted_users_director, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        this.activity = getActivity();

        progressDialog = new CustomProgressDialog(getActivity(), getString(R.string.data_loading));

        databaseReference = DatabaseFunctions.getDatabases(getActivity()).get(0).child("DELETED_USERS");

        loadSeed();
    }

    private void loadSeed() {
        ListView listViewUsers = view.findViewById(R.id.listViewUsers);
        listViewUsers.setOnItemClickListener((parent, view, position, l) -> {
            final String urlChild = listUsers.get(position).split("-")[0].trim();

            new AlertDialog.Builder(getContext())
                    .setMessage(urlChild)
                    .setPositiveButton("Ödə", (dialog, which) -> {
                        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(activity);

                        final EditText editText = new EditText(activity);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                        alert.setMessage("Borcun ödənilməsi");
                        alert.setView(editText);
                        alert.setPositiveButton(R.string.save, (dialogInterface1, i12) -> {
                            try {
                                double value = Double.parseDouble(editText.getText().toString());

                                databaseReference.child(urlChild + "/debt").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Double debt = snapshot.getValue(Double.class);
                                        if (debt != null && debt > 0 && value <= debt) {
                                            debt -= value;
                                            snapshot.getRef().setValue(debt);

                                            DatabaseFunctions.changeBudget(activity, value, true);
                                            DatabaseFunctions.getDatabases(activity).get(0)
                                                    .child("REPORT/StudentPayment/"
                                                            + CustomDateTime.getDate(new Date()) + " "
                                                            + CustomDateTime.getTime(new Date()) + " "
                                                            + urlChild).setValue(value);
                                            loadUsers();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } catch (Exception e) {
                                Toast.makeText(activity, "Yanlış dəyər", Toast.LENGTH_LONG).show();
                                Log.d("AAAAAA", e.toString());
                            }
                        });
                        alert.setNegativeButton(R.string.m_cancel, null);
                        alert.show();
                    })
                    .setNegativeButton(R.string.m_cancel, null)
                    .setNeutralButton(R.string.delete, (dialogInterface, i) -> {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setPositiveButton(R.string.only_this, (dialogInterface14, i14) -> {
                            databaseReference.child(urlChild).removeValue();
                            loadUsers();
                        });
                        alert.setNegativeButton(R.string.m_cancel, null);
                        alert.setNeutralButton(R.string.all, (dialogInterface1, i1) -> {
                            databaseReference.removeValue();
                            loadUsers();
                        });
                        alert.show();
                    }).show();
        });

        listViewUsers.setOnItemLongClickListener((parent, view, position, id) -> {
            progressDialog.show();

            databaseReference.child(listSearchUsers.get(position).split("-")[0].trim() + "/info").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();

                    String data = dataSnapshot.getValue(String.class);

                    ShowInformationDialog dialog = new ShowInformationDialog();
                    dialog.setMessage(data);
                    dialog.show(getChildFragmentManager(), "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });
            return true;
        });

        listAdapter = new ArrayAdapter<>(getContext(), R.layout.list_group, R.id.lblListHeader, listSearchUsers);
        listViewUsers.setAdapter(listAdapter);

        configureEditTextSearch();

        loadUsers();
    }

    private void configureEditTextSearch() {
        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.clearFocus();
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listSearchUsers.clear();

                String search = s.toString().toLowerCase();

                for (String name : listUsers)
                    if (name.toLowerCase().contains(search))
                        listSearchUsers.add(name);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadUsers() {
        progressDialog.show();

        listUsers.clear();
        listSearchUsers.clear();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Double debt = dataSnapshot.child("debt").getValue(Double.class);
                    String text = dataSnapshot.getKey();

                    if (debt != null) text += "  -  Borc: " + debt;
                    listUsers.add(text);
                }

                listSearchUsers.addAll(listUsers);

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.error_check_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
