package com.ej.zerdabiyolu2.InformationClasses;

public class StudentInformation extends CommonInformation {

    private String biography;
    private String registrationDate;


    public StudentInformation(String name, String biography, String password, String registrationDate) {
        super(name, password);
        this.biography = biography;
        this.registrationDate = registrationDate;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
}
