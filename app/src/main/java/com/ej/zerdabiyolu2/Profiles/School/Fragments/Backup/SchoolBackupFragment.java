package com.ej.zerdabiyolu2.Profiles.School.Fragments.Backup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ej.zerdabiyolu2.Helper.CustomDateTime;
import com.ej.zerdabiyolu2.Helper.DatabaseFunctions;
import com.ej.zerdabiyolu2.Helper.SharedClass;
import com.ej.zerdabiyolu2.Helper.StorageFunctions;
import com.ej.zerdabiyolu2.Helper.TableGenerator;
import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;


public class SchoolBackupFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private View view;
    private TextView textViewBeginDate;
    private TextView textViewEndDate;
    private final ArrayList<TextView> radioTextViewList = new ArrayList<>();
    private final boolean[] radioButtonStatus = {false, false, false, false, false, false};

    private DatabaseReference dr;

    private Activity activity;
    private boolean dateStatus;
    private String beginDate;
    private String endDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_depo_backup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        this.activity = getActivity();

        dr = DatabaseFunctions.getDatabases(activity).get(0);

        configureRadioButtons();
        configureDateSection();
        configureButtons();
    }

    private void configureRadioButtons() {
        radioTextViewList.add(view.findViewById(R.id.textViewProfit));
        radioTextViewList.add(view.findViewById(R.id.textViewExpenditure));
        radioTextViewList.add(view.findViewById(R.id.textViewExam));
        radioTextViewList.add(view.findViewById(R.id.textViewLessons));
        radioTextViewList.add(view.findViewById(R.id.textViewReport));
        radioTextViewList.add(view.findViewById(R.id.textViewTeacherValuation));

        for (int i = 0; i < radioTextViewList.size(); i++) {
            int finalI = i;
            radioTextViewList.get(i).setOnClickListener(v -> checkRadioButton(finalI));
        }
    }

    private void checkRadioButton(int i) {
        radioButtonStatus[i] = !radioButtonStatus[i];

        radioTextViewList.get(i).setBackgroundColor(
                getResources().getColor(radioButtonStatus[i] ? R.color.white : R.color.edit_text_back));

        radioTextViewList.get(i).setTextColor(
                getResources().getColor(radioButtonStatus[i] ? R.color.colorNavText : R.color.white));

        radioTextViewList.get(i).setCompoundDrawablesWithIntrinsicBounds(
                radioButtonStatus[i] ? R.drawable.ic_checked_box : R.drawable.ic_unchecked_box_white,
                0, 0, 0);
    }

    private void configureDateSection() {
        textViewBeginDate = view.findViewById(R.id.textViewBeginDate);
        textViewEndDate = view.findViewById(R.id.textViewEndDate);

        beginDate = CustomDateTime.getDate(new Date());
        endDate = CustomDateTime.getDate(new Date());
        textViewBeginDate.setText(beginDate);
        textViewEndDate.setText(endDate);

        textViewBeginDate.setOnClickListener(v -> {
            dateStatus = true;
            SharedClass.showDatePickerDialog(activity, this);
        });

        textViewEndDate.setOnClickListener(v -> {
            dateStatus = false;
            SharedClass.showDatePickerDialog(activity, this);
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        ++month;
        String m = month < 10 ? "0" + month : "" + month;
        String d = day < 10 ? "0" + day : "" + day;

        if (dateStatus) {
            beginDate = year + "_" + m + "_" + d;
            textViewBeginDate.setText(beginDate);
        } else {
            endDate = year + "_" + m + "_" + d;
            textViewEndDate.setText(endDate);
        }
    }

    private void configureButtons() {
        TextView textViewDeleteInfo = view.findViewById(R.id.textViewDeleteInfo);
        TextView textViewSaveInfo = view.findViewById(R.id.textViewSaveInfo);

        textViewDeleteInfo.setOnClickListener(v -> delete());
        textViewSaveInfo.setOnClickListener(v -> save());
    }

    private void save() {
        AlertDialog.Builder b = new AlertDialog.Builder(activity);
        b.setPositiveButton(R.string.save, (dialog, which) -> {
            if (radioButtonStatus[0]) {
                saveProfitsStudentPayment();
                saveProfitsOtherPayment();
            }
            if (radioButtonStatus[1]) saveExpenditure();
            if (radioButtonStatus[2]) saveExamHistory();
            if (radioButtonStatus[3]) saveLessonHistory();
            if (radioButtonStatus[4]) saveExport();
            if (radioButtonStatus[5]) saveTeacherValuation();
        });
        b.setNegativeButton(R.string.m_cancel, (dialog, which) -> dialog.dismiss());
        b.show();
    }

    private void saveProfitsStudentPayment() {
        dr.child("PROFIT/STUDENT_PAYMENT")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TableGenerator tableGenerator = new TableGenerator();
                        ArrayList<String> headersList = new ArrayList<>();
                        headersList.add("Gün");
                        headersList.add("Saat");
                        headersList.add("Miqdar");
                        headersList.add("Ödənilib");
                        headersList.add("Qalıq Borc");

                        for (DataSnapshot snapshotUsers : snapshot.getChildren()) {
                            String userName = snapshotUsers.getKey();

                            dr.child("STUDENT_CLASS/" + userName).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotClass) {
                                    dr.child("STUDENTS/" + snapshotClass.getValue(String.class) + "/" + userName + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshotStudentsName) {
                                            String studentName = snapshotStudentsName.getValue(String.class);
                                            StringBuilder text = new StringBuilder();

                                            ArrayList<ArrayList<String>> rowsList = new ArrayList<>();
                                            for (DataSnapshot snapshotDateTime : snapshotUsers.getChildren()) {
                                                String date = snapshotDateTime.getKey().split(" ")[0];
                                                String time = snapshotDateTime.getKey().split(" ")[1];

                                                if (!SharedClass.checkDate(beginDate, date, endDate))
                                                    continue;

                                                ArrayList<String> row = new ArrayList<>();
                                                row.add(date);
                                                row.add(time);
                                                row.add(String.valueOf(snapshotDateTime.child("amount").getValue(Double.class)));
                                                row.add(String.valueOf(snapshotDateTime.child("payed").getValue(Double.class)));
                                                row.add(String.valueOf(snapshotDateTime.child("debt").getValue(Double.class)));
                                                rowsList.add(row);

                                                text.append(tableGenerator.generateTable(headersList, rowsList));
                                            }
                                            StorageFunctions.store(activity, "Gəlirlər/Şagird Ödəmələri", studentName, text.toString());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        SharedClass.showSnackBar(activity, "Yadda saxlanıldı");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void saveProfitsOtherPayment() {
        dr.child("PROFIT/OTHER")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TableGenerator tableGenerator = new TableGenerator();
                        ArrayList<String> headersList = new ArrayList<>();
                        headersList.add("Gün");
                        headersList.add("Ad");
                        headersList.add("Miqdar");

                        for (DataSnapshot snapshotDate : snapshot.getChildren()) {
                            String date = snapshotDate.getKey();

                            if (!SharedClass.checkDate(beginDate, date, endDate)) continue;

                            StringBuilder text = new StringBuilder();

                            ArrayList<ArrayList<String>> rowsList = new ArrayList<>();
                            for (DataSnapshot snapshotPaymentName : snapshotDate.getChildren()) {
                                String name = snapshotPaymentName.getKey();

                                ArrayList<String> row = new ArrayList<>();
                                row.add(date);
                                row.add(name);
                                row.add(String.valueOf(snapshotPaymentName.getValue(Double.class)));
                                rowsList.add(row);

                                text.append(tableGenerator.generateTable(headersList, rowsList));
                            }
                            StorageFunctions.store(activity, "Gəlirlər", "Digər Ödəmələr", text.toString());
                        }
                        SharedClass.showSnackBar(activity, "Yadda saxlanıldı");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void saveExpenditure() {
        dr.child("EXPENDITURE")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TableGenerator tableGenerator = new TableGenerator();
                        ArrayList<String> headersList = new ArrayList<>();
                        headersList.add("Gün");
                        headersList.add("Ad");
                        headersList.add("Miqdar");

                        for (DataSnapshot snapshotDate : snapshot.getChildren()) {
                            String date = snapshotDate.getKey();

                            if (!SharedClass.checkDate(beginDate, date, endDate)) continue;

                            StringBuilder text = new StringBuilder();

                            ArrayList<ArrayList<String>> rowsList = new ArrayList<>();
                            for (DataSnapshot snapshotPaymentName : snapshotDate.getChildren()) {
                                String name = snapshotPaymentName.getKey();

                                ArrayList<String> row = new ArrayList<>();
                                row.add(date);
                                row.add(name);
                                row.add(String.valueOf(snapshotPaymentName.getValue(Double.class)));
                                rowsList.add(row);

                                text.append(tableGenerator.generateTable(headersList, rowsList));
                            }
                            StorageFunctions.store(activity, "", "Xərclər", text.toString());
                        }
                        SharedClass.showSnackBar(activity, "Yadda saxlanıldı");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void saveExamHistory() {
        dr.child("EXAM_HISTORY")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TableGenerator tableGenerator = new TableGenerator();
                        ArrayList<String> headersList = new ArrayList<>();
                        headersList.add("Fənn");
                        headersList.add("Ad");
                        headersList.add("Doğru Sayı");
                        headersList.add("Yanlış Sayı");
                        headersList.add("Müəllim");
                        headersList.add("Əlavə Məlumat");

                        for (DataSnapshot snapshotUser : snapshot.getChildren()) {
                            String username = snapshotUser.getKey();

                            dr.child("STUDENT_CLASS/" + username).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotClass) {
                                    dr.child("STUDENTS/" + snapshotClass.getValue(String.class) + "/" + username + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshotStudentName) {
                                            String studentName = snapshotStudentName.getValue(String.class);
                                            StringBuilder text = new StringBuilder();

                                            for (DataSnapshot snapshotDates : snapshotUser.getChildren()) {
                                                String date = snapshotDates.getKey();

                                                if (!SharedClass.checkDate(beginDate, date, endDate))
                                                    continue;

                                                ArrayList<ArrayList<String>> rowsList = new ArrayList<>();
                                                for (DataSnapshot snapshotTime : snapshotDates.getChildren()) {
                                                    ArrayList<String> row = new ArrayList<>();
                                                    row.add(snapshotTime.child("lesson").getValue(String.class));
                                                    row.add(snapshotTime.child("examSubject").getValue(String.class));
                                                    row.add(snapshotTime.child("numberOfCorrects").getValue(String.class));
                                                    row.add(snapshotTime.child("numberOfWrongs").getValue(String.class));
                                                    row.add(snapshotTime.child("teacher").getValue(String.class));
                                                    row.add(snapshotTime.child("extraInformation").getValue(String.class).replace(".", "\n"));
                                                    rowsList.add(row);
                                                }
                                                text.append(tableGenerator.generateTable(headersList, rowsList)).append("\n\n");
                                            }
                                            StorageFunctions.store(activity, "Quizlər", studentName, text.toString());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        SharedClass.showSnackBar(activity, "Yadda saxlanıldı");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void saveLessonHistory() {
        dr.child("LESSON_HISTORY")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshotUser : snapshot.getChildren())
                            StorageFunctions.storeStudentAllLessonTimeData(activity, snapshotUser.getRef());
                        SharedClass.showSnackBar(activity, "Yadda saxlanıldı");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void saveExport() {
        _saveReport(1);
        _saveReport(2);
        _saveReport(3);
    }

    private void _saveReport(int status) {
        String name;
        if (status == 1) name = "StudentPayment";
        else if (status == 2) name = "OtherPayment";
        else name = "Expenditure";

        dr.child("REPORT/" + name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TableGenerator tableGenerator = new TableGenerator();
                        ArrayList<String> headersList = new ArrayList<>();
                        headersList.add("Gün");
                        headersList.add("Saat");
                        if (status == 1) headersList.add("Şagird");
                        headersList.add("Miqdar");

                        StringBuilder text = new StringBuilder();
                        ArrayList<ArrayList<String>> rowsList = new ArrayList<>();

                        for (DataSnapshot snapshotDateTime : snapshot.getChildren()) {
                            ArrayList<String> row = new ArrayList<>();
                            String date = snapshotDateTime.getKey().split(" ")[0].trim();
                            String time = snapshotDateTime.getKey().split(" ")[1].trim();
                            Double value = snapshotDateTime.getValue(Double.class);

                            row.add(date);
                            row.add(time);
                            if (status == 1)
                                row.add(snapshotDateTime.getKey().split(" ")[1].trim());
                            row.add(value + "");

                            rowsList.add(row);
                        }
                        text.append(tableGenerator.generateTable(headersList, rowsList));
                        StorageFunctions.store(activity, "Çıxarış", name, text.toString());
                        SharedClass.showSnackBar(activity, "Yadda saxlanıldı");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void saveTeacherValuation() {
        dr.child("TEACHER_VALUATION")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TableGenerator tableGenerator = new TableGenerator();
                        ArrayList<String> headersList = new ArrayList<>();
                        headersList.add("Sinif");
                        headersList.add("Dərs");
                        headersList.add("Vaxtında Başlama");
                        headersList.add("Tapşırıqları Yoxlama");
                        headersList.add("Geyim Balı");
                        headersList.add("Səhvlər Üzərində İş");
                        headersList.add("Sorğu/Sual");
                        headersList.add("Vaxtın İdarə Olunması");
                        headersList.add("Yeni Dərsi Öyrədildimi");
                        headersList.add("Ev Tapşırığı Verildimi");
                        headersList.add("Müasir Texnologiya");
                        headersList.add("Əyani Vəsait İstifadəsi");
                        headersList.add("Əlavə Məlumat");

                        for (DataSnapshot snapshotUser : snapshot.getChildren()) {
                            String username = snapshotUser.getKey();

                            dr.child("TEACHERS/" + username + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotTeacherName) {
                                    String teacherName = snapshotTeacherName.getValue(String.class);
                                    StringBuilder text = new StringBuilder();

                                    ArrayList<ArrayList<String>> rowsList = new ArrayList<>();
                                    for (DataSnapshot snapshotDates : snapshotUser.getChildren()) {
                                        String date = snapshotDates.getKey().split(" ")[0];

                                        if (!SharedClass.checkDate(beginDate, date, endDate))
                                            continue;

                                        ArrayList<String> row = new ArrayList<>();
                                        row.add(snapshotDates.child("className").getValue(String.class));
                                        row.add(snapshotDates.child("lessonName").getValue(String.class));
                                        row.add(snapshotDates.child("startInTime").getValue(String.class));
                                        row.add(snapshotDates.child("checkHomework").getValue(String.class));
                                        row.add(snapshotDates.child("suitableDressed").getValue(String.class));
                                        row.add(snapshotDates.child("workOnMistakes").getValue(String.class));
                                        row.add(snapshotDates.child("questionAnswer").getValue(String.class));
                                        row.add(snapshotDates.child("manageTime").getValue(String.class));
                                        row.add(snapshotDates.child("teachNewSubject").getValue(String.class));
                                        row.add(snapshotDates.child("givingHomework").getValue(String.class));
                                        row.add(snapshotDates.child("usingTechnology").getValue(String.class));
                                        row.add(snapshotDates.child("usingVisualAids").getValue(String.class));
                                        row.add(snapshotDates.child("extraInformation").getValue(String.class).replace(".", "\n"));
                                        rowsList.add(row);
                                    }
                                    text.append(tableGenerator.generateTable(headersList, rowsList)).append("\n\n");
                                    StorageFunctions.store(activity, "Müəllim Qiymətləndirmə", teacherName, text.toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        SharedClass.showSnackBar(activity, "Yadda saxlanıldı");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void delete() {
        AlertDialog.Builder b = new AlertDialog.Builder(activity);
        b.setPositiveButton(R.string.delete, (dialog, which) -> {
            if (radioButtonStatus[0]) {
                deleteProfitsStudentPayment();
                deleteProfitsOtherPayment();
            }
            if (radioButtonStatus[1]) deleteExpenditure();
            if (radioButtonStatus[2]) deleteExamHistory();
            if (radioButtonStatus[3]) deleteLessonHistory();
            if (radioButtonStatus[4]) deleteReport();
            if (radioButtonStatus[5]) deleteTeacherValuation();
        });
        b.setNegativeButton(R.string.m_cancel, (dialog, which) -> dialog.dismiss());
        b.show();
    }

    private void deleteProfitsStudentPayment() {
        dr.child("PROFIT/STUDENT_PAYMENT")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshotUsers : snapshot.getChildren())
                            for (DataSnapshot snapshotDateTime : snapshotUsers.getChildren())
                                if (SharedClass.checkDate(beginDate, snapshotDateTime.getKey().split(" ")[0], endDate))
                                    snapshotDateTime.getRef().removeValue();

                        SharedClass.showSnackBar(activity, "Silindi");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void deleteProfitsOtherPayment() {
        dr.child("PROFIT/OTHER")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshotDate : snapshot.getChildren())
                            if (SharedClass.checkDate(beginDate, snapshotDate.getKey(), endDate))
                                snapshotDate.getRef().removeValue();

                        SharedClass.showSnackBar(activity, "Silindi");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void deleteExpenditure() {
        dr.child("EXPENDITURE")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshotDate : snapshot.getChildren())
                            if (SharedClass.checkDate(beginDate, snapshotDate.getKey(), endDate))
                                snapshotDate.getRef().removeValue();

                        SharedClass.showSnackBar(activity, "Silindi");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void deleteExamHistory() {
        dr.child("EXAM_HISTORY")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshotUser : snapshot.getChildren())
                            for (DataSnapshot snapshotDates : snapshotUser.getChildren())
                                if (SharedClass.checkDate(beginDate, snapshotDates.getKey(), endDate))
                                    snapshotDates.getRef().removeValue();

                        SharedClass.showSnackBar(activity, "Silindi");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void deleteLessonHistory() {
        dr.child("LESSON_HISTORY")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshotUser : snapshot.getChildren())
                            for (DataSnapshot snapshotDate : snapshotUser.getChildren())
                                if (SharedClass.checkDate(beginDate, snapshotDate.getKey(), endDate))
                                    snapshotDate.getRef().removeValue();

                        SharedClass.showSnackBar(activity, "Silindi");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void deleteReport() {
        _deleteReport("StudentPayment");
        _deleteReport("Other");
        _deleteReport("Expenditure");
    }

    private void _deleteReport(String name) {
        dr.child("REPORT/" + name)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshotDateTime : snapshot.getChildren())
                            if (SharedClass.checkDate(beginDate, snapshotDateTime.getKey().split(" ")[0], endDate))
                                snapshotDateTime.getRef().removeValue();

                        SharedClass.showSnackBar(activity, "Silindi");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }

    private void deleteTeacherValuation() {
        dr.child("TEACHER_VALUATION")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshotUser : snapshot.getChildren())
                            for (DataSnapshot snapshotDates : snapshotUser.getChildren())
                                if (SharedClass.checkDate(beginDate, snapshotDates.getKey().split(" ")[0], endDate))
                                    snapshotDates.getRef().removeValue();

                        SharedClass.showSnackBar(activity, "Silindi");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SharedClass.showSnackBar(activity, "Uğusruz əməliyyat");
                    }
                });
    }
}