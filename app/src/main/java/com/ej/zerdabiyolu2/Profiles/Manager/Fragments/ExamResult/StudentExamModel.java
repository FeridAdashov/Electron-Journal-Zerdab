package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.ExamResult;

public class StudentExamModel {
    private String teacher;
    private String lesson;
    private String examSubject;
    private String commonNumber;
    private String numberOfCorrects;
    private String numberOfWrongs;
    private String extraInformation;

    public StudentExamModel() {
    }

    public StudentExamModel(
            String teacher,
            String lesson,
            String examSubject,
            String commonNumber,
            String numberOfCorrects,
            String numberOfWrongs,
            String extraInformation) {
        this.teacher = teacher;
        this.lesson = lesson;
        this.extraInformation = extraInformation;
        this.commonNumber = commonNumber;
        this.numberOfCorrects = numberOfCorrects;
        this.numberOfWrongs = numberOfWrongs;
        this.examSubject = examSubject;
    }

    public String getCommonNumber() {
        return commonNumber;
    }

    public void setCommonNumber(String commonNumber) {
        this.commonNumber = commonNumber;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getExamSubject() {
        return examSubject;
    }

    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(String extraInformation) {
        this.extraInformation = extraInformation;
    }

    public String getNumberOfCorrects() {
        return numberOfCorrects;
    }

    public void setNumberOfCorrects(String numberOfCorrects) {
        this.numberOfCorrects = numberOfCorrects;
    }

    public String getNumberOfWrongs() {
        return numberOfWrongs;
    }

    public void setNumberOfWrongs(String numberOfWrongs) {
        this.numberOfWrongs = numberOfWrongs;
    }
}
