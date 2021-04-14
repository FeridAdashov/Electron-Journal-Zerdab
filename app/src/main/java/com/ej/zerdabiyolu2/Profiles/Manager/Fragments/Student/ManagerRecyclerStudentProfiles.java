package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Student;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.CustomDialogs.CustomProgressDialog;
import com.ej.zerdabiyolu2.CustomDialogs.ShowInformationDialog;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ManagerRecyclerStudentProfiles extends RecyclerView.Adapter<ManagerRecyclerStudentProfiles.MyViewHolder> {

    private final Activity activity;
    private final FragmentManager fragmentManager;
    private final ArrayList<String> nameList;
    private final ArrayList<String> searchNameList;
    private final ArrayList<String> userNameList;

    public ManagerRecyclerStudentProfiles(Activity activity,
                                          FragmentManager fragmentManager,
                                          ArrayList<String> nameList,
                                          ArrayList<String> searchNameList,
                                          ArrayList<String> userNameList) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.nameList = nameList;
        this.searchNameList = searchNameList;
        this.userNameList = userNameList;
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
        holder.cardView.setOnClickListener(v -> openSendMessageDialog(username));
        holder.cardView.setOnLongClickListener(v -> {
            showMessages(username);
            return true;
        });
    }

    private void showMessages(String user) {
        CustomProgressDialog progressDialog = new CustomProgressDialog(activity, activity.getString(R.string.data_loading));
        final DatabaseReference dr = DatabaseFunctions.getDatabases(activity).get(0).child("MESSAGES");

        progressDialog.show();

        dr.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                StringBuilder allMessages = new StringBuilder("\n");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (key != null && key.contains("_") && !snapshot.child("message").getValue(String.class).equals(""))
                        allMessages.append("----------- ")
                                .append(key)
                                .append(" -----------\n")
                                .append(snapshot.child("message").getValue(String.class)).append("\n\n\n");
                }

                if (allMessages.toString().trim().equals(""))
                    allMessages = new StringBuilder(activity.getString(R.string.nothing_info));

                ShowInformationDialog dialog = new ShowInformationDialog();
                dialog.setMessage(allMessages.toString());
                dialog.show(fragmentManager, "Example");
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    private void openSendMessageDialog(String username) {
        final DatabaseReference dr = DatabaseFunctions.getDatabases(activity).get(0).child("MESSAGES");

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd");
        final String date = df.format(c.getTime());
        df = new SimpleDateFormat("HH_mm_ss");
        final String time = df.format(c.getTime());

        dr.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        SimpleDateFormat df1 = new SimpleDateFormat("yyyy_MM_dd");
                        Date firstDate = df1.parse(date);
                        Date secondDate = df1.parse(snapshot.child("date").getValue(String.class));

                        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
                        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                        if (diff >= 10) snapshot.getRef().removeValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setMessage(R.string.send_message);
        final EditText editText = new EditText(activity);
        alert.setView(editText);
        alert.setPositiveButton(R.string.send, (dialog, which) -> {
            dr.child(username + "/" + date + "  --  " + time + "/message").setValue(editText.getText().toString());
            dr.child(username + "/" + date + "  --  " + time + "/date").setValue(date);
            dr.child(username + "/hasNewMessage").setValue(true);
        });
        alert.setNeutralButton(R.string.send_all, (dialogInterface, i12) -> {
            for (String user : userNameList) {
                dr.child(user + "/" + date + "  --  " + time + "/message").setValue(editText.getText().toString());
                dr.child(user + "/" + date + "  --  " + time + "/date").setValue(date);
                dr.child(user + "/hasNewMessage").setValue(true);
            }
        });
        alert.setNegativeButton(R.string.m_cancel, null);
        alert.show();
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