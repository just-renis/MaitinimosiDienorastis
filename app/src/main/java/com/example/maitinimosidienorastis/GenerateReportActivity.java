package com.example.maitinimosidienorastis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class GenerateReportActivity extends AppCompatActivity {

    private TextView fromDateText, fromTimeText, toDateText, toTimeText;
    private Calendar calendar;
    private Button generateReportButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        switch(month) {
            case 0: dateText.setText(year + " Sausio " + day); return;
            case 1: dateText.setText(year + " Vasario " + day); return;
            case 2: dateText.setText(year + " Kovo " + day); return;
            case 3: dateText.setText(year + " Balandžio " + day); return;
            case 4: dateText.setText(year + " Gegužės " + day); return;
            case 5: dateText.setText(year + " Birželio " + day); return;
            case 6: dateText.setText(year + " Liepos " + day); return;
            case 7: dateText.setText(year + " Rugpjūčio " + day); return;
            case 8: dateText.setText(year + " Rugsėjo " + day); return;
            case 9: dateText.setText(year + " Spalio " + day); return;
            case 10: dateText.setText(year + " Lapkričio " + day); return;
            default: dateText.setText(year + " Gruodžio " + day);
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

            }
        });
    }
}