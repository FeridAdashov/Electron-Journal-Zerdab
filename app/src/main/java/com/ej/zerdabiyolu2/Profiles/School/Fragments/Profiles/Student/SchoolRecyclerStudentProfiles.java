package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Student;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Student.Actions.ChangeStudentInformationDialog;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Student.Actions.LessonHistoryActivity;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolRecyclerStudentProfiles extends RecyclerView.Adapter<SchoolRecyclerStudentProfiles.MyViewHolder> {

    private final Activity activity;
    private final FragmentManager childFragmentManager;
    private final ArrayList<String> nameList;
    private final ArrayList<String> searchNameList;
    private final ArrayList<String> userNameList;
    private final ArrayList<String> classList;
    private final String selectedClass;

    public SchoolRecyclerStudentProfiles(Activity activity,
                                         FragmentManager childFragmentManager,
                                         ArrayList<String> nameList,
                                         ArrayList<String> searchNameList,
                                         ArrayList<String> userNameList,
                                         ArrayList<String> classList,
                                         String selectedClass) {
        this.activity = activity;
        this.childFragmentManager = childFragmentManager;
        this.nameList = nameList;
        this.searchNameList = searchNameList;
        this.userNameList = userNameList;
        this.classList = classList;
        this.selectedClass = selectedClass;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_student_profile, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        int position = nameList.indexOf(searchNameList.get(pos));

        String name = nameList.get(position);
        String username = userNameList.get(position);

        holder.name.setText(name);
        holder.cardView.setOnClickListener(v -> new AlertDialog.Builder(activity)
                .setPositiveButton("Hesab məlumatı", (dialog, which) -> openChangeStudentProfileDataDialog(username))
                .setNeutralButton("Dərs tarixçəsi", (dialogInterface1, i1) -> openLessonHistory(username + " " + name))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show());
    }

    private void openChangeStudentProfileDataDialog(String username) {
        CustomProgressDialog progressDialog = new CustomProgressDialog(activity, activity.getString(R.string.data_loading));
        progressDialog.show();
        DatabaseFunctions.getDatabases(activity).get(0).child("STUDENTS/" + selectedClass + "/" + username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ChangeStudentInformationDialog dialog = new ChangeStudentInformationDialog();
                        dialog.setDefaultValues(
                                username,
                                dataSnapshot.child("biography").getValue(String.class),
                                dataSnapshot.child("PaymentInfo/moneyPerMonth").getValue(Double.class),
                                classList.indexOf(selectedClass),
                                classList,
                                dataSnapshot.child("password").getValue(String.class),
                                dataSnapshot.child("registrationDate").getValue(String.class),
                                dataSnapshot.getRef());

                        progressDialog.dismiss();
                        dialog.show(childFragmentManager, "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void openLessonHistory(String user) {
        Intent intent = new Intent(activity, LessonHistoryActivity.class);
        intent.putExtra("user", user);
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return searchNameList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView name;

        MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewStudentProfile);
            name = itemView.findViewById(R.id.cardTextViewStudentName);
        }
    }
}