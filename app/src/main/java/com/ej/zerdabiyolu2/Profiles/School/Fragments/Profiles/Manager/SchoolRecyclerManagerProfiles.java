package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profiles.Manager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.R;

import java.util.ArrayList;

public class SchoolRecyclerManagerProfiles extends RecyclerView.Adapter<SchoolRecyclerManagerProfiles.MyViewHolder> {

    private final Activity activity;
    private final ArrayList<String> nameList;
    private final ArrayList<String> searchNameList;
    private final ArrayList<String> userNameList;


    public SchoolRecyclerManagerProfiles(Activity activity,
                                         ArrayList<String> nameList,
                                         ArrayList<String> searchNameList,
                                         ArrayList<String> userNameList) {
        this.activity = activity;
        this.nameList = nameList;
        this.searchNameList = searchNameList;
        this.userNameList = userNameList;
    }

    @Override
    public SchoolRecyclerManagerProfiles.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_manager_profile, parent, false);
        return new SchoolRecyclerManagerProfiles.MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolRecyclerManagerProfiles.MyViewHolder holder, final int pos) {
        int position = nameList.indexOf(searchNameList.get(pos));

        String name = nameList.get(position);
        String username = userNameList.get(position);

        holder.name.setText(name);
        holder.name.setOnClickListener(v -> {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.sure_to_delete_profile)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        DatabaseFunctions.getDatabases(activity).get(0).child("MANAGERS/" + username).removeValue();
                        SharedClass.showSnackBar(activity, "Hesab silindi. Səhifəni yeniləyin!!!");
                    })
                    .setNegativeButton(R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return searchNameList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;

        MyViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cardTextViewManagerName);
        }
    }
}