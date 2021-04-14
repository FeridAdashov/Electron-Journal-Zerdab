package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Student;

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
import com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Student.ShowProfit.ShowProfitHistoryActivity;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SchoolRecyclerStudentPayment extends RecyclerView.Adapter<SchoolRecyclerStudentPayment.MyViewHolder> {

    private final TextView textViewCommonPayed, textViewCommonDebt;
    private final ArrayList<String> searchNameList;
    private final HashMap<String, ProfitModel> map;
    private final String selectedClass;
    private final Activity activity;

    public SchoolRecyclerStudentPayment(Activity activity,
                                        TextView textViewCommonPayed,
                                        TextView textViewCommonDebt,
                                        ArrayList<String> searchNameList,
                                        HashMap<String, ProfitModel> map,
                                        String selectedClass) {
        this.activity = activity;
        this.textViewCommonPayed = textViewCommonPayed;
        this.textViewCommonDebt = textViewCommonDebt;
        this.searchNameList = searchNameList;
        this.map = map;
        this.selectedClass = selectedClass;
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
        holder.buttonAddProfit.setOnClickListener(v -> addProfit(model, name));
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

    private void addProfit(ProfitModel model, String name) {
        DatabaseReference dr = DatabaseFunctions.getDatabases(activity).get(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final EditText text = new EditText(activity);
        text.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        text.setHint(activity.getString(R.string.enter_amount));

        builder.setTitle(activity.getString(R.string.student_payment)).setView(text);
        builder.setPositiveButton(R.string.ok, (di, i1) ->
        {
            if (selectedClass.equals("")) return;

            double amount;
            try {
                amount = Double.parseDouble(text.getText().toString());
            } catch (Exception e) {
                return;
            }

            String paymentChild = "STUDENTS/" + selectedClass + "/" + model.username + "/PaymentInfo";

            model.payed += amount;
            model.commonPayed += amount;
            if (model.payed >= model.debt || (model.payed >= model.moneyPerMonth && model.debt > 0.)) {
                int payedMonths = (int) (model.payed / model.moneyPerMonth);
                if (payedMonths > 0) {
                    model.payed -= model.moneyPerMonth * payedMonths;
                    model.debt -= model.moneyPerMonth * payedMonths;

                    if (model.debt < 0) model.debt = 0;

                    DateFormat df = new SimpleDateFormat("yyyy_MM_dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(model.paymentTime);
                    calendar.add(Calendar.MONTH, payedMonths);

                    model.paymentTime = calendar.getTime();
                    dr.child(paymentChild + "/paymentTime").setValue(df.format(model.paymentTime));
                }
            }
            notifyDataSetChanged();
            setCommonTextviews();

            DateFormat df = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
            String time = df.format(Calendar.getInstance().getTime());

            dr.child("REPORT/StudentPayment/" + time + " " + name).setValue(amount);
            dr.child(paymentChild + "/payed").setValue(model.payed);
            dr.child(paymentChild + "/commonPayed").setValue(model.commonPayed);

            DatabaseReference reference = dr.child("PROFIT/STUDENT_PAYMENT/" + model.username + "/" + time);

            reference.child("amount").setValue(SharedClass.twoDigitDecimal(amount));
            reference.child("payed").setValue(SharedClass.twoDigitDecimal(model.payed));
            reference.child("debt").setValue(SharedClass.twoDigitDecimal(model.debt > 0 ? model.debt - model.payed : 0.0));

            DatabaseFunctions.changeBudget(activity, amount, true);
        });
        builder.setNegativeButton(R.string.m_cancel, (di, i12) -> di.dismiss());
        builder.create().show();
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