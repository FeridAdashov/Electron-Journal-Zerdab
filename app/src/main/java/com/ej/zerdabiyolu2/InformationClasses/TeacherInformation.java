package com.ej.zerdabiyolu2.InformationClasses;

public class TeacherInformation extends CommonInformation {
    private int countOfClass;
    private String biography;
    private Boolean active = true;

    public TeacherInformation(String name, Integer countOfClass, String biography, String password) {
        super(name, password);
        this.countOfClass = countOfClass;
        this.biography = biography;
    }

    public Integer getCountOfClass() {
        return countOfClass;
    }

    public void setCountOfClass(Integer countOfClass) {
        this.countOfClass = countOfClass;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
