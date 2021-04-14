package com.ej.zerdabiyolu2.Profiles.Manager.Fragments.Teacher.Models;

public class TeacherValuationModel {

    private String className;
    private String lessonName;
    private String startInTime;
    private String suitableDressed;
    private String checkHomework;
    private String workOnMistakes;
    private String manageTime;
    private String teachNewSubject;
    private String givingHomework;
    private String usingTechnology;
    private String usingVisualAids;
    private String questionAnswer;
    private String extraInformation;


    public TeacherValuationModel(String className, String lessonName, String startInTime, String suitableDressed, String checkHomework, String workOnMistakes, String manageTime, String teachNewSubject, String givingHomework, String usingTechnology, String usingVisualAids, String questionAnswer, String extraInformation) {
        this.className = className;
        this.lessonName = lessonName;
        this.startInTime = startInTime;
        this.suitableDressed = suitableDressed;
        this.checkHomework = checkHomework;
        this.workOnMistakes = workOnMistakes;
        this.manageTime = manageTime;
        this.teachNewSubject = teachNewSubject;
        this.givingHomework = givingHomework;
        this.usingTechnology = usingTechnology;
        this.usingVisualAids = usingVisualAids;
        this.questionAnswer = questionAnswer;
        this.extraInformation = extraInformation;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getStartInTime() {
        return startInTime;
    }

    public void setStartInTime(String startInTime) {
        this.startInTime = startInTime;
    }

    public String getSuitableDressed() {
        return suitableDressed;
    }

    public void setSuitableDressed(String suitableDressed) {
        this.suitableDressed = suitableDressed;
    }

    public String getCheckHomework() {
        return checkHomework;
    }

    public void setCheckHomework(String checkHomework) {
        this.checkHomework = checkHomework;
    }

    public String getWorkOnMistakes() {
        return workOnMistakes;
    }

    public void setWorkOnMistakes(String workOnMistakes) {
        this.workOnMistakes = workOnMistakes;
    }

    public String getManageTime() {
        return manageTime;
    }

    public void setManageTime(String manageTime) {
        this.manageTime = manageTime;
    }

    public String getTeachNewSubject() {
        return teachNewSubject;
    }

    public void setTeachNewSubject(String teachNewSubject) {
        this.teachNewSubject = teachNewSubject;
    }

    public String getGivingHomework() {
        return givingHomework;
    }

    public void setGivingHomework(String givingHomework) {
        this.givingHomework = givingHomework;
    }

    public String getUsingTechnology() {
        return usingTechnology;
    }

    public void setUsingTechnology(String usingTechnology) {
        this.usingTechnology = usingTechnology;
    }

    public String getUsingVisualAids() {
        return usingVisualAids;
    }

    public void setUsingVisualAids(String usingVisualAids) {
        this.usingVisualAids = usingVisualAids;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public String getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(String extraInformation) {
        this.extraInformation = extraInformation;
    }
}
