package com.example.maitinimosidienorastis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.Manifest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GenerateReportActivity extends AppCompatActivity {

    private TextView fromDateText, fromTimeText, toDateText, toTimeText;
    private Calendar calendar;
    private Button generateReportButton;
    private ArrayList<Product> localProducts = new ArrayList<>();
    private ArrayList<Product> personalProducts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        setContentView(R.layout.activity_generate_report);
        fromDateText = findViewById(R.id.fromDateText);
        fromDateText.setPaintFlags(fromDateText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        fromTimeText = findViewById(R.id.fromTimeText);
        fromTimeText.setPaintFlags(fromTimeText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        toDateText = findViewById(R.id.toDateText);
        toDateText.setPaintFlags(toDateText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        toTimeText = findViewById(R.id.toTimeText);
        toTimeText.setPaintFlags(toTimeText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        generateReportButton = findViewById(R.id.generateReportButton);
        calendar = Calendar.getInstance();
        setupDate(fromDateText);
        setupDate(toDateText);
        setupTimeButton(fromTimeText);
        setupTimeButton(toTimeText);
        setupGenerateReportButton();
    }

    private void setupDate(TextView dateText) {
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(GenerateReportActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                setDateText(dateText, year, month, dayOfMonth);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void setDateText(TextView dateText, int year, int month, int day) {
        switch (month) {
            case 0:
                dateText.setText(year + " Sausio " + day);
                return;
            case 1:
                dateText.setText(year + " Vasario " + day);
                return;
            case 2:
                dateText.setText(year + " Kovo " + day);
                return;
            case 3:
                dateText.setText(year + " Balandžio " + day);
                return;
            case 4:
                dateText.setText(year + " Gegužės " + day);
                return;
            case 5:
                dateText.setText(year + " Birželio " + day);
                return;
            case 6:
                dateText.setText(year + " Liepos " + day);
                return;
            case 7:
                dateText.setText(year + " Rugpjūčio " + day);
                return;
            case 8:
                dateText.setText(year + " Rugsėjo " + day);
                return;
            case 9:
                dateText.setText(year + " Spalio " + day);
                return;
            case 10:
                dateText.setText(year + " Lapkričio " + day);
                return;
            default:
                dateText.setText(year + " Gruodžio " + day);
        }
    }

    private void setupTimeButton(TextView timeText) {
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(GenerateReportActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timeText.setText(hourOfDay + " val. " + minute + " min.");
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });
    }

    private void setupGenerateReportButton() {
        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromDateText.getText().toString().equals("Nėra pasirinktos datos")) {
                    Toast.makeText(GenerateReportActivity.this, "Pirmoji data nėra pasirinkta", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fromTimeText.getText().toString().equals("Nėra pasirinkto laiko")) {
                    Toast.makeText(GenerateReportActivity.this, "Pirmas laikas nėra pasirinktas", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (toDateText.getText().toString().equals("Nėra pasirinktos datos")) {
                    Toast.makeText(GenerateReportActivity.this, "Antroji data nėra pasirinkta", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (toTimeText.getText().toString().equals("Nėra pasirinkto laiko")) {
                    Toast.makeText(GenerateReportActivity.this, "Antras laikas nėra pasirinktas", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!canGenerateReport()) {
                    Toast.makeText(GenerateReportActivity.this, "Pirmoji data negali būti vėliau nei antroji data", Toast.LENGTH_SHORT).show();
                    return;
                }
                generateReportAndDownload();
            }
        });
    }

    private int getMonthIndex(String monthName) {
        switch (monthName) {
            case "Sausio":
                return 1;
            case "Vasario":
                return 2;
            case "Kovo":
                return 3;
            case "Balandžio":
                return 4;
            case "Gegužės":
                return 5;
            case "Birželio":
                return 6;
            case "Liepos":
                return 7;
            case "Rugpjūčio":
                return 8;
            case "Rugsėjo":
                return 9;
            case "Spalio":
                return 10;
            case "Lapkričio":
                return 11;
            case "Gruodžio":
                return 12;
            default:
                return 1;
        }
    }

    private boolean canGenerateReport() {
        long fromDate = getMillisFromDateTime(fromDateText.getText().toString(), fromTimeText.getText().toString(), false);
        long toDate = getMillisFromDateTime(toDateText.getText().toString(), toTimeText.getText().toString(), false);
        return fromDate < toDate;
    }

    private void generateReportAndDownload() {
        setAndProcessValidMealTimeStamps(new CallbackInterface() {
            @Override
            public void onCallBack() {
                double water = 0, dryMatter = 0, protein = 0, fat = 0, carbohydrates = 0;
                for (int i = 0; i < localProducts.size(); i++) {
                    Product product = localProducts.get(i);
                    if (!product.getWater().equals("No Data")) water += Double.parseDouble(product.getWater());
                    if (!product.getDryMatter().equals("No Data")) dryMatter += Double.parseDouble(product.getDryMatter());
                    if (!product.getProteinTotal().equals("No Data")) protein += Double.parseDouble(product.getProteinTotal());
                    if (!product.getFatTotal().equals("No Data")) fat += Double.parseDouble(product.getFatTotal());
                    if (!product.getCarbohydrates().equals("No Data")) carbohydrates += Double.parseDouble(product.getCarbohydrates());
                }
                for (int i = 0; i < personalProducts.size(); i++) {
                    Product product = personalProducts.get(i);
                    if (!product.getWater().equals("No Data")) water += Double.parseDouble(product.getWater());
                    if (!product.getDryMatter().equals("No Data")) dryMatter += Double.parseDouble(product.getDryMatter());
                    if (!product.getProteinTotal().equals("No Data")) protein += Double.parseDouble(product.getProteinTotal());
                    if (!product.getFatTotal().equals("No Data")) fat += Double.parseDouble(product.getFatTotal());
                    if (!product.getCarbohydrates().equals("No Data")) carbohydrates += Double.parseDouble(product.getCarbohydrates());
                }
                downloadExcel(generateExcel(water, dryMatter, protein, fat, carbohydrates));
            }
        });
    }
    private HSSFWorkbook generateExcel(double water, double dryMatter, double protein, double fat, double carbohydrates) {
        HSSFWorkbook excel = new HSSFWorkbook();
        HSSFSheet sheet = excel.createSheet("Ataskaitos duomenys");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("100 gramų kiekyje vieno produkto");
        cell = row.createCell(1);
        cell.setCellValue("Nuo " + fromDateText.getText().toString() + " d. " + fromTimeText.getText().toString() + " iki " + toDateText.getText().toString() + " d. " + toTimeText.getText().toString());

        double totalProducts = localProducts.size() + personalProducts.size();
        ArrayList<Pair<String, Double>> values = new ArrayList<>();
        values.add(new Pair<>("Suvartotas vanduo (g)", water));
        values.add(new Pair<>("Suvartotos sausosios medžiagos (g)", dryMatter));
        values.add(new Pair<>("Suvartoti baltymai (g)", protein));
        values.add(new Pair<>("Suvartoti riebalai (g)", fat));
        values.add(new Pair<>("Suvartoti angliavandeniai (g)", carbohydrates));
        values.add(new Pair<>("Baltymų vidurkis per produktą (g)", protein / totalProducts));
        values.add(new Pair<>("Riebalų vidurkis per produktą (g)", fat / totalProducts));
        values.add(new Pair<>("Angliavandenių vidurkis per produktą (g)", carbohydrates / totalProducts));
        values.add(new Pair<>("Produktų kiekis", totalProducts));
        for (int i = 1; i <= values.size(); i++) {
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(values.get(i - 1).first);
            cell = row.createCell(1);
            cell.setCellValue(values.get(i - 1).second);
        }
        sheet.setColumnWidth(0, (40 * 256));
        sheet.setColumnWidth(1, (60 * 256));
        return excel;
    }
    private void setAndProcessValidMealTimeStamps(CallbackInterface callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("mealTimeStamps");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<MealTimeStamp> mealTimeStamps = new ArrayList<>();
                for (DataSnapshot mealTimeStampSnapshot : dataSnapshot.getChildren()) {
                    if (isWithinRange(mealTimeStampSnapshot.child("date").getValue(String.class), mealTimeStampSnapshot.child("time").getValue(String.class))) {
                        ArrayList<String> personalProductsKeys = new ArrayList<>();
                        ArrayList<String> localProductsKeys = new ArrayList<>();
                        for (DataSnapshot productKeySnapshot : mealTimeStampSnapshot.child("personalProducts").getChildren()) {
                            personalProductsKeys.add(productKeySnapshot.getValue(String.class));
                        }
                        for (DataSnapshot productKeySnapshot : mealTimeStampSnapshot.child("localProducts").getChildren()) {
                            localProductsKeys.add(productKeySnapshot.getValue(String.class));
                        }
                        mealTimeStamps.add(new MealTimeStamp(
                                mealTimeStampSnapshot.getKey(),
                                mealTimeStampSnapshot.child("date").getValue(String.class),
                                mealTimeStampSnapshot.child("time").getValue(String.class),
                                personalProductsKeys,
                                localProductsKeys
                        ));
                    }
                }
                if (mealTimeStamps.isEmpty()) {
                    Toast.makeText(GenerateReportActivity.this, "Nėra jokių mitybos veiklų šiame laiko tarpe", Toast.LENGTH_SHORT).show();
                    return;
                }
                processMealTimeStamps(mealTimeStamps, callback);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void processMealTimeStamps(ArrayList<MealTimeStamp> mealTimeStamps, CallbackInterface callback) {
        AtomicInteger operationsCompleted = new AtomicInteger(0);
        for (MealTimeStamp mealTimeStamp : mealTimeStamps) {
            processPersonalProducts(mealTimeStamp, new CallbackInterface() {
                @Override
                public void onCallBack() {
                    if (operationsCompleted.incrementAndGet() == 2 * mealTimeStamps.size()) callback.onCallBack();
                }
            });
            processLocalProducts(mealTimeStamp, new CallbackInterface() {
                @Override
                public void onCallBack() {
                    if (operationsCompleted.incrementAndGet() == 2 * mealTimeStamps.size()) callback.onCallBack();
                }
            });
        }
    }
    private void processPersonalProducts(MealTimeStamp mealTimeStamp, CallbackInterface callback) {
        ArrayList<String> personalProductKeys = mealTimeStamp.getPersonalProducts();
        int totalProductsToLoad = personalProductKeys.size();
        if (totalProductsToLoad == 0) {
            callback.onCallBack();
            return;
        }
        AtomicInteger productsLoaded = new AtomicInteger(0);
        DatabaseReference personalProductsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("products");
        for (String key : personalProductKeys) {
            personalProductsRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> productValues = new HashMap<String, String>();
                        productValues.put("water", dataSnapshot.child("water").getValue(String.class));
                        productValues.put("dryMatter", dataSnapshot.child("dryMatter").getValue(String.class));
                        productValues.put("proteinTotal", dataSnapshot.child("proteinTotal").getValue(String.class));
                        productValues.put("fatTotal", dataSnapshot.child("fatTotal").getValue(String.class));
                        productValues.put("carbohydrates", dataSnapshot.child("carbohydrates").getValue(String.class));
                        personalProducts.add(new Product(productValues));
                        if (productsLoaded.incrementAndGet() == totalProductsToLoad) callback.onCallBack();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }
    private void processLocalProducts(MealTimeStamp mealTimeStamp, CallbackInterface callback) {
        ArrayList<String> localProductKeys = mealTimeStamp.getLocalProducts();
        int totalProductsToLoad = localProductKeys.size();
        if (totalProductsToLoad == 0) {
            callback.onCallBack();
            return;
        }
        AtomicInteger productsLoaded = new AtomicInteger(0);
        DatabaseReference localProductsRef = FirebaseDatabase.getInstance().getReference("products");
        for (String key : localProductKeys) {
            localProductsRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> productValues = new HashMap<>();
                        productValues.put("water", dataSnapshot.child("Water").getValue(String.class));
                        productValues.put("dryMatter", dataSnapshot.child("Dry matter").getValue(String.class));
                        productValues.put("proteinTotal", dataSnapshot.child("Protein, total").getValue(String.class));
                        productValues.put("fatTotal", dataSnapshot.child("Fat, total").getValue(String.class));
                        productValues.put("carbohydrates", dataSnapshot.child("Carbohydrates").getValue(String.class));
                        localProducts.add(new Product(productValues));
                        if (productsLoaded.incrementAndGet() == totalProductsToLoad) callback.onCallBack();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }
    private boolean isWithinRange(String date, String time) {
        long mealTimeStampMillis = getMillisFromDateTime(date, time, true);
        long fromMillis = getMillisFromDateTime(fromDateText.getText().toString(), fromTimeText.getText().toString(), false);
        long toMillis = getMillisFromDateTime(toDateText.getText().toString(), toTimeText.getText().toString(), false);
        return mealTimeStampMillis >= fromMillis && mealTimeStampMillis <= toMillis;
    }
    private long getMillisFromDateTime(String date, String time, boolean isMealTimeStamp) {
        String[] fromDate = date.split(" ");
        int year = Integer.parseInt(fromDate[0]);
        int month = isMealTimeStamp ? Integer.parseInt(fromDate[1]) : getMonthIndex(fromDate[1]);
        int day = Integer.parseInt(fromDate[2]);
        String[] fromTime = time.split(" ");
        int hour = Integer.parseInt(fromTime[0]);
        int minutes = Integer.parseInt(isMealTimeStamp ? fromTime[1] : fromTime[2]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minutes, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    private void downloadExcel(HSSFWorkbook excel) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Maitinimosi Dienoraščio Ataskaita_" + System.currentTimeMillis() + ".xls");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            excel.write(fileOutputStream);
            fileOutputStream.close();
            excel.close();
            showDownloadNotification(file);
            Toast.makeText(this, "Ataskaita sukurta sėkmingai", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Nepavyko sukurti ataskaitos", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }

    private void createNotificationChannel() {
        String channelId = "ExcelDownloadChannel";
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager.getNotificationChannel(channelId) == null) {
            NotificationChannel channel = new NotificationChannel(channelId, "Report", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Report generation");
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showDownloadNotification(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(this, "com.example.maitinimosidienorastis.fileprovider", file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ExcelDownloadChannel")
            .setSmallIcon(R.drawable.ic_icon)
            .setContentTitle("Maitinimosi ataskaita")
            .setContentText("Ataskaita išsaugota")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}