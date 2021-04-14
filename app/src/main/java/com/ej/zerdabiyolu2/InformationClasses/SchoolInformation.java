package com.ej.zerdabiyolu2.InformationClasses;

import java.util.List;

public class SchoolInformation extends CommonInformation {
    private List<String> classes, lessons;
    private Double moneyPerMonth;


    public SchoolInformation(String name, Double moneyPerMonth, List<String> lessons, List<String> classes, String password) {
        super(name, password);
        this.moneyPerMonth = moneyPerMonth;
        this.lessons = lessons;
        this.classes = classes;
    }

    public Double getMoneyPerMonth() {
        return moneyPerMonth;
    }

    public void setMoneyPerMonth(Double moneyPerMonth) {
        this.moneyPerMonth = moneyPerMonth;
    }

    public List<String> getLessons() {
        return lessons;
    }

    public void setLessons(List<String> lessons) {
        this.lessons = lessons;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }
}
