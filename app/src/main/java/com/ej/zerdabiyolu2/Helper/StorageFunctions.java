package com.ej.zerdabiyolu2.Helper;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ej.zerdabiyolu2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageFunctions {
    private static String text = "";
    private static String fileName = "";

    public static void store(Activity activity, String parentName, String childName, String text) {
        String app_name = "Electron Journal Data";
//        String app_name = "Pictures"; //For LD app opener in PC

        if (Environment.getExternalStorageState().equalsIgnoreCase("mounted"))//Check if Device Storage is present
        {
            try {
                File root = new File(Environment.getExternalStorageDirectory(), app_name);
                if (!root.exists()) root.mkdirs();

                File parent = root;

                if (!parentName.equals("")) {
                    parent = new File(root, parentName);
                    if (!parent.exists()) parent.mkdirs();
                }

                if (childName.contains(":")) childName = childName.replaceAll(":", "ː");

                File myTxt = new File(parent, childName + ".txt");

                FileWriter writer = new FileWriter(myTxt);
                writer.append(text);//Writing the text
                writer.flush();
                writer.close();
            } catch (IOException e) {
                Log.d("AAAAA", e.toString());
            }
        } else SharedClass.showSnackBar(activity, "Yaddaş problemi");
    }

    public static void storeUserInformation(final Activity activity, DatabaseReference databaseReference) {
        try {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    text = "Ad :  " + name + "\n";
                    text += "Bioqrafiya. :  " + dataSnapshot.child("biography").getValue(String.class);

                    store(activity, name, "ABOUT", text);
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(activity, R.string.error_check_internet, Toast.LENGTH_SHORT).show();
        }
    }

    public static void storeStudentAllLessonTimeData(final Activity activity, final DatabaseReference databaseReference) {
        String username_name = databaseReference.getKey();
        fileName = username_name.substring(username_name.indexOf(" ")).trim();

        ArrayList<String> headersList = new ArrayList<>();
        headersList.add("Dərs Saatı");
        headersList.add("Fənn Adı");
        headersList.add("Dərs Mövzusu");
        headersList.add("Dərs Qiyməti");
        headersList.add("Davranış Qimyəti");
        headersList.add("Əlavə Məlumat");

        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshotParent) {
                        StringBuilder text = new StringBuilder();

                        for (DataSnapshot dataSnapshotDate : dataSnapshotParent.getChildren()) {
                            text.append("Gün: ").append(dataSnapshotDate.getKey()).append("\n\n");

                            ArrayList<ArrayList<String>> rowsList = new ArrayList<>();

                            for (DataSnapshot snapshotTime : dataSnapshotDate.getChildren()) {
                                ArrayList<String> row = new ArrayList<>();
                                row.add(snapshotTime.getKey());
                                row.add(snapshotTime.child("lesson").getValue(String.class));
                                row.add(snapshotTime.child("lessonSubject").getValue(String.class));
                                row.add(snapshotTime.child("lessonRate").getValue(String.class));
                                row.add(snapshotTime.child("behaviourRate").getValue(String.class));
                                row.add(snapshotTime.child("extraInformation").getValue(String.class));

                                rowsList.add(row);
                            }
                            TableGenerator tableGenerator = new TableGenerator();
                            text.append(tableGenerator.generateTable(headersList, rowsList)).append("\n\n");
                        }
                        store(activity, "Dərs Məlumatları", fileName, text.toString());
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static void storeStudentAllExamData(final Activity activity, final DatabaseReference databaseReference) {
        fileName = databaseReference.getKey();

        ArrayList<String> headersList = new ArrayList<>();
        headersList.add("Imtahan Saatı");
        headersList.add("Fənn Adı");
        headersList.add("Imtahan Mövzusu");
        headersList.add("Doğru sual sayı");
        headersList.add("Yanlış sual sayı");
        headersList.add("Ümumi sual sayı");
        headersList.add("Müəllim");
        headersList.add("Əlavə Məlumat");

        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshotParent) {
                        StringBuilder text = new StringBuilder();

                        for (DataSnapshot dataSnapshotDate : dataSnapshotParent.getChildren()) {
                            text.append("Gün: ").append(dataSnapshotDate.getKey()).append("\n\n");

                            ArrayList<ArrayList<String>> rowsList = new ArrayList<>();

                            for (DataSnapshot snapshotTime : dataSnapshotDate.getChildren()) {
                                ArrayList<String> row = new ArrayList<>();
                                row.add(snapshotTime.getKey());
                                row.add(snapshotTime.child("lesson").getValue(String.class));
                                row.add(snapshotTime.child("examSubject").getValue(String.class));
                                row.add(snapshotTime.child("numberOfCorrects").getValue(String.class));
                                row.add(snapshotTime.child("numberOfWrongs").getValue(String.class));
                                row.add(snapshotTime.child("commonNumber").getValue(String.class));
                                row.add(snapshotTime.child("teacher").getValue(String.class));
                                row.add(snapshotTime.child("extraInformation").getValue(String.class));

                                rowsList.add(row);
                            }
                            TableGenerator tableGenerator = new TableGenerator();
                            text.append(tableGenerator.generateTable(headersList, rowsList)).append("\n\n");
                        }
                        store(activity, "Imtahan Məlumatları", fileName, text.toString());
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static void deleteUser(final Activity activity, final DatabaseReference databaseReference, boolean isStudent) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.deleting_profile)
                .setMessage(R.string.sure_to_delete)
                .setPositiveButton(R.string.yes, (dialog, which) -> {

                    LinearLayout parent = new LinearLayout(activity);
                    parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    parent.setOrientation(LinearLayout.VERTICAL);

                    final EditText editText1 = new EditText(activity);
                    final EditText editText2 = new EditText(activity);

                    editText1.setHint(R.string.name);
                    editText2.setHint(R.string.reason);

                    parent.addView(editText1);
                    parent.addView(editText2);

                    final AlertDialog alert = new AlertDialog.Builder(activity)
                            .setMessage(R.string.name_reason)
                            .setPositiveButton(android.R.string.ok, null)
                            .setNegativeButton(android.R.string.cancel, null)
                            .setView(parent)
                            .create();

                    alert.setOnShowListener(dialog1 -> {

                        Button button = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(view -> {
                            try {
                                if (editText1.getText().toString().trim().length() < 3) {
                                    Toast.makeText(activity, activity.getString(R.string.enter_your_name), Toast.LENGTH_SHORT).show();
                                } else if (editText2.getText().toString().trim().length() < 3) {
                                    Toast.makeText(activity, activity.getString(R.string.enter_reason), Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog1.dismiss();

                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                                            final String name = dataSnapshot.child("name").getValue(String.class);

                                            if (name == null)
                                                Toast.makeText(activity, "Hesab silinmədi", Toast.LENGTH_LONG).show();

                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH_mm_ss");

                                            text = "Ad :  " + name + "\n";
                                            text += "Bioqrafiya :  " + dataSnapshot.child("biography").getValue(String.class) + "\n\n";
                                            if (isStudent)
                                                text += "Qeydiyyat günü :  " + dataSnapshot.child("registrationDate").getValue(String.class) + "\n\n";
                                            text += "Silən Şəxsin Adı :  " + editText1.getText().toString() + "\n\n";
                                            text += "Silinmə Səbəbi :  " + editText2.getText().toString() + "\n\n";
                                            text += "Əməliyyat Tarixi :  " + sdf.format(new Date());

                                            DatabaseFunctions.getDatabases(activity).get(0).child("DELETED_USERS/" + name + "/info").setValue(text);

                                            if (isStudent) {
                                                Double moneyPerMonth = dataSnapshot.child("PaymentInfo/moneyPerMonth").getValue(Double.class);
                                                String lastPaymentTime = dataSnapshot.child("PaymentInfo/paymentTime").getValue(String.class);
                                                double debt = 0.;

                                                if (moneyPerMonth != null && lastPaymentTime != null) {
                                                    DateFormat df = new SimpleDateFormat("yyyy_MM_dd");

                                                    Double payed = dataSnapshot.child("PaymentInfo/payed").getValue(Double.class);
                                                    if (payed == null) payed = 0d;

                                                    try {
                                                        Date last_payment_time = df.parse(lastPaymentTime);
                                                        Date cd = df.parse(df.format(new Date()));

                                                        int unpayed_months = -1;
                                                        while (last_payment_time.before(cd)) {
                                                            unpayed_months++;
                                                            cd.setMonth(cd.getMonth() - 1);
                                                        }
                                                        debt = unpayed_months * moneyPerMonth - payed;
                                                    } catch (Exception e) {
                                                        Log.d("AAAAA", e.toString());
                                                    }
                                                }
                                                DatabaseFunctions.getDatabases(activity).get(0)
                                                        .child("DELETED_USERS/" + name + "/debt").setValue(debt);
                                                DatabaseFunctions.getDatabases(activity).get(0)
                                                        .child("LESSON_HISTORY/" + databaseReference.getKey() + " " + name).removeValue();
                                                DatabaseFunctions.getDatabases(activity).get(0)
                                                        .child("MESSAGES/" + databaseReference.getKey()).removeValue();
                                                DatabaseFunctions.getDatabases(activity).get(0)
                                                        .child("MESSAGES/" + databaseReference.getKey()).removeValue();
                                                DatabaseFunctions.getDatabases(activity).get(0)
                                                        .child("STUDENT_CLASS/" + databaseReference.getKey()).removeValue();
                                            } else {
                                                DatabaseFunctions.getDatabases(activity).get(0)
                                                        .child("TEACHER_GIVE_RESULT/" + databaseReference.getKey()).removeValue();
                                                DatabaseFunctions.getDatabases(activity).get(0)
                                                        .child("TEACHER_VALUATION/" + databaseReference.getKey()).removeValue();
                                            }

                                            databaseReference.removeValue();
                                            Toast.makeText(activity, "Hesab silindi, Səhifəni yeniləyin!!!", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    });
                    alert.show();
                })
                .setNegativeButton(R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void sortArray(ArrayList<String> array) {
        String[] a = array.toArray(new String[0]);
        Arrays.sort(a, (s, t1) -> {
            final Pattern PATTERN = Pattern.compile("(\\D*)(\\d*)");
            Matcher m1 = PATTERN.matcher(s);
            Matcher m2 = PATTERN.matcher(t1);

            // The only way find() could fail is at the end of a string
            while (m1.find() && m2.find()) {
                int nonDigitCompare = m1.group(1).compareTo(m2.group(1));
                if (0 != nonDigitCompare) {
                    return nonDigitCompare;
                }

                if (m1.group(2).isEmpty()) {
                    return m2.group(2).isEmpty() ? 0 : -1;
                } else if (m2.group(2).isEmpty()) {
                    return +1;
                }

                Integer n1 = new Integer(m1.group(2));
                Integer n2 = new Integer(m2.group(2));
                int numberCompare = n1.compareTo(n2);
                if (0 != numberCompare) {
                    return numberCompare;
                }
            }
            return m1.hitEnd() && m2.hitEnd() ? 0 : m1.hitEnd() ? -1 : +1;
        });
        array.clear();
        array.addAll(Arrays.asList(a));
    }
}
