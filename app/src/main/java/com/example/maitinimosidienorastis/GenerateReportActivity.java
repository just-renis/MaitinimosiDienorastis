package com.example.maitinimosidienorastis;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.util.Calendar;

import android.Manifest;

public class GenerateReportActivity extends AppCompatActivity {

    private TextView fromDateText, fromTimeText, toDateText, toTimeText;
    private Calendar calendar;
    private Button generateReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        setContentView(R.layout.activity_generate_report);
        fromDateText = findViewById(R.id.fromDateText);
        fromTimeText = findViewById(R.id.fromTimeText);
        toDateText = findViewById(R.id.toDateText);
        toTimeText = findViewById(R.id.toTimeText);
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
                return 0;
            case "Vasario":
                return 1;
            case "Kovo":
                return 2;
            case "Balandžio":
                return 3;
            case "Gegužės":
                return 4;
            case "Birželio":
                return 5;
            case "Liepos":
                return 6;
            case "Rugpjūčio":
                return 7;
            case "Rugsėjo":
                return 8;
            case "Spalio":
                return 9;
            case "Lapkričio":
                return 10;
            case "Gruodžio":
                return 11;
            default:
                return 0;
        }
    }

    private boolean canGenerateReport() {
        String[] fromDate = fromDateText.getText().toString().split(" ");
        int fromYear = Integer.parseInt(fromDate[0]);
        int fromMonth = getMonthIndex(fromDate[1]);
        int fromDay = Integer.parseInt(fromDate[2]);
        String[] toDate = toDateText.getText().toString().split(" ");
        int toYear = Integer.parseInt(toDate[0]);
        int toMonth = getMonthIndex(toDate[1]);
        int toDay = Integer.parseInt(toDate[2]);
        String[] fromTime = fromTimeText.getText().toString().split(" ");
        int fromHour = Integer.parseInt(fromTime[0]);
        int fromMinutes = Integer.parseInt(fromTime[2]);
        String[] toTime = toTimeText.getText().toString().split(" ");
        int toHour = Integer.parseInt(toTime[0]);
        int toMinutes = Integer.parseInt(toTime[2]);
        if (fromYear > toYear) return false;
        if (fromYear == toYear) {
            if (fromMonth > toMonth) return false;
            if (fromMonth == toMonth) {
                if (fromDay > toDay) return false;
                if (fromDay == toDay) {
                    if (fromHour > toHour) return false;
                    if (fromHour == toHour) return fromMinutes < toMinutes;
                }
            }
        }
        return true;
    }

    private void generateReportAndDownload() {
        HSSFWorkbook excel = new HSSFWorkbook();
        HSSFSheet sheet = excel.createSheet("Ataskaitos duomenys");
        HSSFRow row1 = sheet.createRow(0);
        HSSFCell cell1 = row1.createCell(0);
        cell1.setCellValue(fromDateText.getText().toString());
        HSSFCell cell2 = row1.createCell(1);
        cell2.setCellValue(toDateText.getText().toString());
        HSSFRow row2 = sheet.createRow(1);
        HSSFCell cell3 = row2.createCell(0);
        cell3.setCellValue(fromTimeText.getText().toString());
        HSSFCell cell4 = row2.createCell(1);
        cell4.setCellValue(toTimeText.getText().toString());
        downloadExcel(excel);
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
                .setSmallIcon(R.drawable.ic_edit)
                .setContentTitle("Maitinimosi ataskaita")
                .setContentText("Ataskaita išsaugota")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}