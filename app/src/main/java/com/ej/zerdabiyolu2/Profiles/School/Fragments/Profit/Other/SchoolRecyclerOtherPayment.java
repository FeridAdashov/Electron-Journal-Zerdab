package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.R;

import java.util.ArrayList;

public class SchoolRecyclerOtherPayment extends RecyclerView.Adapter<SchoolRecyclerOtherPayment.MyViewHolder> {

    private final ArrayList<String> searchNameList;
    private final ArrayList<String> nameList;
    private final ArrayList<String> dateList;
    private final ArrayList<Double> amountList;

    public SchoolRecyclerOtherPayment(ArrayList<String> nameList,
                                      ArrayList<String> searchNameList,
                                      ArrayList<String> dateList,
                                      ArrayList<Double> amountList) {
        this.nameList = nameList;
        this.searchNameList = searchNameList;
        this.dateList = dateList;
        this.amountList = amountList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_other_profit_school, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        int position = nameList.indexOf(searchNameList.get(pos));

        String date = dateList.get(position);
        String name = nameList.get(position);
        Double amount = amountList.get(position);

        holder.date.setText(date);
        holder.name.setText(name);
        holder.amount.setText(String.valueOf(amount));
    }

    @Override
    public int getItemCount() {
        return searchNameList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView date, name, amount;

        MyViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.cardTextViewOtherPaymentDate);
            name = itemView.findViewById(R.id.cardTextViewOtherPaymentName);
            amount = itemView.findViewById(R.id.cardTextViewOtherPaymentAmount);
        }
    }
}