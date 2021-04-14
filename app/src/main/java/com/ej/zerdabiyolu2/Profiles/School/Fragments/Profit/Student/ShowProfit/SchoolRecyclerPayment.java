package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Student.ShowProfit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ej.zerdabiyolu2.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SchoolRecyclerPayment extends RecyclerView.Adapter<SchoolRecyclerPayment.MyViewHolder> {

    private final ArrayList<String> searchTimeList;
    private final HashMap<String, PaymentModel> map;

    public SchoolRecyclerPayment(ArrayList<String> searchTimeList,
                                 HashMap<String, PaymentModel> map) {
        this.searchTimeList = searchTimeList;
        this.map = map;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_payment_school, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        String name = searchTimeList.get(pos);

        PaymentModel model = map.get(name);
        if (model == null) return;

        holder.time.setText(name);
        holder.amount.setText(String.valueOf(model.amount));
        holder.payed.setText(String.valueOf(model.payed));
        holder.debt.setText(String.valueOf(model.debt));
    }

    @Override
    public int getItemCount() {
        return searchTimeList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView time, amount, debt, payed;

        MyViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.cardTextViewPaymentTime);
            amount = itemView.findViewById(R.id.cardTextViewPaymentAmount);
            debt = itemView.findViewById(R.id.cardTextViewPaymentDebt);
            payed = itemView.findViewById(R.id.cardTextViewPaymentPayed);
        }
    }
}