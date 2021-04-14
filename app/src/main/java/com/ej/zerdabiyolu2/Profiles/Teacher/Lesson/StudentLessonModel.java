package com.ej.zerdabiyolu2.Profiles.Teacher.Lesson;

public class StudentLessonModel {
    private String lesson;
    private String lessonSubject;
    private String lessonRate;
    private String behaviourRate;
    private String extraInformation;


    public StudentLessonModel(
            String lesson,
            String lessonSubject,
            String lessonRate,
            String behaviourRate,
            String extraInformation) {
        this.lesson = lesson;
        this.extraInformation = extraInformation;
        this.lessonRate = lessonRate;
        this.behaviourRate = behaviourRate;
        this.lessonSubject = lessonSubject;
    }

    public String getLessonSubject() {
        return lessonSubject;
    }

    public void setLessonSubject(String lessonSubject) {
        this.lessonSubject = lessonSubject;
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

    public String getLessonRate() {
        return lessonRate;
    }

    public void setLessonRate(String lessonRate) {
        this.lessonRate = lessonRate;
    }

    public String getBehaviourRate() {
        return behaviourRate;
    }

    public void setBehaviourRate(String behaviourRate) {
        this.behaviourRate = behaviourRate;
    }
}
