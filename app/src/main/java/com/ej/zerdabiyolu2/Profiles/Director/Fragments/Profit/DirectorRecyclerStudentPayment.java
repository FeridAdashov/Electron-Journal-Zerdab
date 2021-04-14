package com.ej.zerdabiyolu2.Profiles.Director.Fragments.Profit;

import android.app.Activity;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Student.ProfitModel;
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Student.ShowProfit.ShowProfitHistoryActivity;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DirectorRecyclerStudentPayment extends RecyclerView.Adapter<DirectorRecyclerStudentPayment.MyViewHolder> {

    private final TextView textViewCommonPayed, textViewCommonDebt;
    private final ArrayList<String> searchNameList;
    private final HashMap<String, ProfitModel> map;
    private final Activity activity;

    public DirectorRecyclerStudentPayment(Activity activity,
                                          TextView textViewCommonPayed,
                                          TextView textViewCommonDebt,
                                          ArrayList<String> searchNameList,
                                          HashMap<String, ProfitModel> map) {
        this.activity = activity;
        this.textViewCommonPayed = textViewCommonPayed;
        this.textViewCommonDebt = textViewCommonDebt;
        this.searchNameList = searchNameList;
        this.map = map;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_student_profit_school, parent, false);
        setCommonTextviews();
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        String name = searchNameList.get(pos);

        ProfitModel model = map.get(name);
        if (model == null) return;

        double commonPayed = model.commonPayed;
        double payed = model.payed;
        double debt = model.debt;

        holder.name.setText(name);
        holder.payed.setText(SharedClass.twoDigitDecimalAsString(commonPayed));
        holder.debt.setText(debt > 0. ? SharedClass.twoDigitDecimalAsString(debt - payed) : "0.0");
        holder.buttonAddProfit.setVisibility(View.GONE);
        holder.cardViewStudentPayment.setOnClickListener(v -> showPaymentHistory(model.username));
    }

    private void showPaymentHistory(String user) {
        new AlertDialog.Builder(activity)
                .setTitle("Ödəmə tarixçəsinə bax")
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    Intent intent = new Intent(activity, ShowProfitHistoryActivity.class);
                    intent.putExtra("user", user);
                    activity.startActivity(intent);
                })
                .setNegativeButton(R.string.m_cancel, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setCommonTextviews() {
        double commonPayed = 0., commonDebt = 0.;
        for (ProfitModel model : map.values()) {
            commonPayed += model.payed;
            commonDebt += model.debt;
        }

        if (commonDebt > 0) commonDebt -= commonPayed;

        textViewCommonPayed.setText(activity.getString(R.string.payed) + ":  " + SharedClass.twoDigitDecimalAsString(commonPayed));
        textViewCommonDebt.setText(activity.getString(R.string.debt) + ":  " + SharedClass.twoDigitDecimalAsString(commonDebt));
    }

    @Override
    public int getItemCount() {
        return searchNameList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardViewStudentPayment;
        private final TextView name, debt, payed;
        private final ImageButton buttonAddProfit;

        MyViewHolder(View itemView) {
            super(itemView);
            cardViewStudentPayment = itemView.findViewById(R.id.cardViewStudentPayment);
            name = itemView.findViewById(R.id.cardTextViewStudentName);
            debt = itemView.findViewById(R.id.cardTextViewStudentDebt);
            payed = itemView.findViewById(R.id.cardTextViewStudentPayed);
            buttonAddProfit = itemView.findViewById(R.id.cardButtonAddProfit);
        }
    }
}