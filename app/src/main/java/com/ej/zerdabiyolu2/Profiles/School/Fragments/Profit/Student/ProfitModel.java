package com.ej.zerdabiyolu2.Profiles.School.Fragments.Profit.Student;

import java.util.Date;

public class ProfitModel {
    public String username;
    public Date paymentTime;
    public double moneyPerMonth;
    public double payed;
    public double commonPayed;
    public double debt;

    public ProfitModel(String username, Date paymentTime, double moneyPerMonth, double payed, double commonPayed, double debt) {
        this.username = username;
        this.paymentTime = paymentTime;
        this.moneyPerMonth = moneyPerMonth;
        this.payed = payed;
        this.commonPayed = commonPayed;
        this.debt = debt;
    }
}
