package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Teacher;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SchoolRecyclerTeacherProfiles extends RecyclerView.Adapter<SchoolRecyclerTeacherProfiles.MyViewHolder> {

    private final Activity activity;
    private final FragmentManager childFragmentManager;
    private final ArrayList<String> nameList;
    private final ArrayList<String> searchNameList;
    private final ArrayList<String> userNameList;


    public SchoolRecyclerTeacherProfiles(Activity activity,
                                         FragmentManager childFragmentManager,
                                         ArrayList<String> nameList,
                                         ArrayList<String> searchNameList,
                                         ArrayList<String> userNameList) {
        this.activity = activity;
        this.childFragmentManager = childFragmentManager;
        this.nameList = nameList;
        this.searchNameList = searchNameList;
        this.userNameList = userNameList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_school_teacher_profile, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        int position = nameList.indexOf(searchNameList.get(pos));

        String name = nameList.get(position);
        String username = userNameList.get(position);

        holder.name.setText(name);
        holder.cardView.setOnClickListener(v -> openChangeTeacherProfileDataDialog(username));
    }

    private void openChangeTeacherProfileDataDialog(String username) {
        CustomProgressDialog progressDialog = new CustomProgressDialog(activity, activity.getString(R.string.data_loading));
        progressDialog.show();
        DatabaseFunctions.getDatabases(activity).get(0).child("TEACHERS/" + username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ChangeTeacherInformationDialog dialog = new ChangeTeacherInformationDialog();
                        dialog.setDefaultValues(
                                username,
                                dataSnapshot.child("password").getValue(String.class),
                                dataSnapshot.child("countOfClass").getValue(Integer.class),
                                dataSnapshot.child("biography").getValue(String.class),
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

    @Override
    public int getItemCount() {
        return searchNameList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView name;

        MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewTeacherProfile);
            name = itemView.findViewById(R.id.cardTextViewTeacherName);
        }
    }
}