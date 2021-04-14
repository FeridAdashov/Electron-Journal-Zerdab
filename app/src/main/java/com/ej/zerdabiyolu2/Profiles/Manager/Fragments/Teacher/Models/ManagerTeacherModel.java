package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Teacher.Models;

public class ManagerTeacherModel {
    public String username;
    public String lastCheckedTime;
    public Boolean activeness;

    public ManagerTeacherModel(String username, String lastCheckedTime, Boolean activeness) {
        this.username = username;
        this.lastCheckedTime = lastCheckedTime;
        this.activeness = activeness;
    }
}
