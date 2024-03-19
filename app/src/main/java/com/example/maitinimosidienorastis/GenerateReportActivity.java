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

import com.google.android.material.textfield.TextInputEditText;
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
                Map<String, Double> fields = new HashMap<>();
                String[] fieldNames = {
                        "edibleCoefficient", "water", "dryMatter", "proteinTotal", "proteinAnimal",
                        "proteinPlant", "fatTotal", "fattyAcidsTotalSaturated", "fattyAcidsTotalMonounsaturated",
                        "fattyAcidsTotalPolyunsaturated", "carbohydrates", "starch", "sugars", "fibre", "ashMinerals",
                        "calcium", "iron", "zinc", "thiamin", "riboflavin", "niacin", "folicAcid", "vitaminB6",
                        "alcohol", "cholesterol", "sodium", "magnesium", "phosphorus", "potassium",  "selenium",
                        "iodide", "vitaminA_RE", "vitaminD", "vitaminE_TE", "vitaminB12", "vitaminC",
                        "niacinEquivalent", "energyValueKJ", "energyValueKCAL"
                };
                for (String fieldName : fieldNames) fields.put(fieldName, (double) 0);
                for (int i = 0; i < localProducts.size(); i++) extractProductValues(localProducts.get(i), fields);
                for (int i = 0; i < personalProducts.size(); i++) extractProductValues(personalProducts.get(i), fields);
                downloadExcel(generateExcel(fields));
            }
        });
    }
    private void extractProductValues(Product product, Map<String, Double> fields) {
        if (!product.getWater().equals("No Data")) fields.put("water", fields.get("water") + Double.parseDouble(product.getWater()));
        if (!product.getDryMatter().equals("No Data")) fields.put("water", fields.get("dryMatter") + Double.parseDouble(product.getDryMatter()));
        if (!product.getProteinTotal().equals("No Data")) fields.put("proteinTotal", fields.get("proteinTotal") + Double.parseDouble(product.getProteinTotal()));
        if (!product.getFatTotal().equals("No Data")) fields.put("fatTotal", fields.get("fatTotal") + Double.parseDouble(product.getFatTotal()));
        if (!product.getCarbohydrates().equals("No Data")) fields.put("carbohydrates", fields.get("carbohydrates") + Double.parseDouble(product.getCarbohydrates()));
        if (!product.getEdibleCoefficient().equals("No Data")) fields.put("edibleCoefficient", fields.get("edibleCoefficient") + Double.parseDouble(product.getEdibleCoefficient()));
        if (!product.getProteinAnimal().equals("No Data")) fields.put("proteinAnimal", fields.get("proteinAnimal") + Double.parseDouble(product.getProteinAnimal()));
        if (!product.getProteinPlant().equals("No Data")) fields.put("proteinPlant", fields.get("proteinPlant") + Double.parseDouble(product.getProteinPlant()));
        if (!product.getFattyAcidsTotalSaturated().equals("No Data")) fields.put("fattyAcidsTotalSaturated", fields.get("fattyAcidsTotalSaturated") + Double.parseDouble(product.getFattyAcidsTotalSaturated()));
        if (!product.getFattyAcidsTotalMonounsaturated().equals("No Data")) fields.put("fattyAcidsTotalMonounsaturated", fields.get("fattyAcidsTotalMonounsaturated") + Double.parseDouble(product.getFattyAcidsTotalMonounsaturated()));
        if (!product.getFattyAcidsTotalPolyunsaturated().equals("No Data")) fields.put("fattyAcidsTotalPolyunsaturated", fields.get("fattyAcidsTotalPolyunsaturated") + Double.parseDouble(product.getFattyAcidsTotalPolyunsaturated()));
        if (!product.getStarch().equals("No Data")) fields.put("starch", fields.get("starch") + Double.parseDouble(product.getStarch()));
        if (!product.getSugars().equals("No Data")) fields.put("sugars", fields.get("sugars") + Double.parseDouble(product.getSugars()));
        if (!product.getFibre().equals("No Data")) fields.put("fibre", fields.get("fibre") + Double.parseDouble(product.getFibre()));
        if (!product.getAshMinerals().equals("No Data")) fields.put("ashMinerals", fields.get("ashMinerals") + Double.parseDouble(product.getAshMinerals()));
        if (!product.getCalcium().equals("No Data")) fields.put("calcium", fields.get("calcium") + Double.parseDouble(product.getCalcium()));
        if (!product.getIron().equals("No Data")) fields.put("iron", fields.get("iron") + Double.parseDouble(product.getIron()));
        if (!product.getZinc().equals("No Data")) fields.put("zinc", fields.get("zinc") + Double.parseDouble(product.getZinc()));
        if (!product.getThiamin().equals("No Data")) fields.put("thiamin", fields.get("thiamin") + Double.parseDouble(product.getThiamin()));
        if (!product.getRiboflavin().equals("No Data")) fields.put("riboflavin", fields.get("riboflavin") + Double.parseDouble(product.getRiboflavin()));
        if (!product.getNiacin().equals("No Data")) fields.put("niacin", fields.get("niacin") + Double.parseDouble(product.getNiacin()));
        if (!product.getFolicAcid().equals("No Data")) fields.put("folicAcid", fields.get("folicAcid") + Double.parseDouble(product.getFolicAcid()));
        if (!product.getVitaminB6().equals("No Data")) fields.put("vitaminB6", fields.get("vitaminB6") + Double.parseDouble(product.getVitaminB6()));
        if (!product.getAlcohol().equals("No Data")) fields.put("alcohol", fields.get("alcohol") + Double.parseDouble(product.getAlcohol()));
        if (!product.getCholesterol().equals("No Data")) fields.put("cholesterol", fields.get("cholesterol") + Double.parseDouble(product.getCholesterol()));
        if (!product.getSodium().equals("No Data")) fields.put("sodium", fields.get("sodium") + Double.parseDouble(product.getSodium()));
        if (!product.getMagnesium().equals("No Data")) fields.put("magnesium", fields.get("magnesium") + Double.parseDouble(product.getMagnesium()));
        if (!product.getPhosphorus().equals("No Data")) fields.put("phosphorus", fields.get("phosphorus") + Double.parseDouble(product.getPhosphorus()));
        if (!product.getPotassium().equals("No Data")) fields.put("potassium", fields.get("potassium") + Double.parseDouble(product.getPotassium()));
        if (!product.getSelenium().equals("No Data")) fields.put("selenium", fields.get("selenium") + Double.parseDouble(product.getSelenium()));
        if (!product.getIodide().equals("No Data")) fields.put("iodide", fields.get("iodide") + Double.parseDouble(product.getIodide()));
        if (!product.getVitaminA_RE().equals("No Data")) fields.put("vitaminA_RE", fields.get("vitaminA_RE") + Double.parseDouble(product.getVitaminA_RE()));
        if (!product.getVitaminD().equals("No Data")) fields.put("vitaminD", fields.get("vitaminD") + Double.parseDouble(product.getVitaminD()));
        if (!product.getVitaminE_TE().equals("No Data")) fields.put("vitaminE_TE", fields.get("vitaminE_TE") + Double.parseDouble(product.getVitaminE_TE()));
        if (!product.getVitaminB12().equals("No Data")) fields.put("vitaminB12", fields.get("vitaminB12") + Double.parseDouble(product.getVitaminB12()));
        if (!product.getVitaminC().equals("No Data")) fields.put("vitaminC", fields.get("vitaminC") + Double.parseDouble(product.getVitaminC()));
        if (!product.getNiacinEquivalent().equals("No Data")) fields.put("niacinEquivalent", fields.get("niacinEquivalent") + Double.parseDouble(product.getNiacinEquivalent()));
        if (!product.getEnergyValueKJ().equals("No Data")) fields.put("energyValueKJ", fields.get("energyValueKJ") + Double.parseDouble(product.getEnergyValueKJ()));
        if (!product.getEnergyValueKCAL().equals("No Data")) fields.put("energyValueKCAL", fields.get("energyValueKCAL") + Double.parseDouble(product.getEnergyValueKCAL()));

    }
    private HSSFWorkbook generateExcel(Map<String, Double> fields) {
        HSSFWorkbook excel = new HSSFWorkbook();
        HSSFSheet sheet = excel.createSheet("Ataskaitos duomenys");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("100 gramų kiekyje vieno produkto");
        cell = row.createCell(1);
        cell.setCellValue("Nuo " + fromDateText.getText().toString() + " d. " + fromTimeText.getText().toString() + " iki " + toDateText.getText().toString() + " d. " + toTimeText.getText().toString());

        double totalProducts = localProducts.size() + personalProducts.size();
        ArrayList<Pair<String, Double>> values = new ArrayList<>();
        values.add(new Pair<>("Valgio dalies koeficientas", fields.get("edibleCoefficient") / totalProducts));
        values.add(new Pair<>("Suvartotas vanduo (g)", fields.get("water")));
        values.add(new Pair<>("Suvartotos sausosios medžiagos (g)", fields.get("dryMatter")));
        values.add(new Pair<>("Suvartoti baltymai (g)", fields.get("proteinTotal")));
        values.add(new Pair<>("Suvartoti gyvūniniai baltymai (g)", fields.get("proteinAnimal")));
        values.add(new Pair<>("Suvartoti augaliniai baltymai (g)", fields.get("proteinPlant")));
        values.add(new Pair<>("Baltymų vidurkis per produktą (g)", fields.get("proteinTotal") / totalProducts));
        values.add(new Pair<>("Suvartoti riebalai (g)", fields.get("fatTotal")));
        values.add(new Pair<>("Suvartotos sočiosios riebalų rūgštys (g)", fields.get("fattyAcidsTotalSaturated")));
        values.add(new Pair<>("Suvartotos mononesočiosios riebalų rūgštys (g)", fields.get("fattyAcidsTotalMonounsaturated")));
        values.add(new Pair<>("Suvartotos polinesočiosios riebalų rūgštys (g)", fields.get("fattyAcidsTotalPolyunsaturated")));
        values.add(new Pair<>("Riebalų vidurkis per produktą (g)", fields.get("fatTotal") / totalProducts));
        values.add(new Pair<>("Suvartoti angliavandeniai (g)", fields.get("carbohydrates")));
        values.add(new Pair<>("Angliavandenių vidurkis per produktą (g)", fields.get("carbohydrates") / totalProducts));
        values.add(new Pair<>("Suvartotas cholesterolis (mg)", fields.get("cholesterol")));
        values.add(new Pair<>("Suvartotas krakmolas (g)", fields.get("starch")));
        values.add(new Pair<>("Suvartotas cukrus (g)", fields.get("sugars")));
        values.add(new Pair<>("Suvartotos skaidulinės medžiagos (g)", fields.get("fibre")));
        values.add(new Pair<>("Suvartotos mineralinės medžiagos (g)", fields.get("ashMinerals")));
        values.add(new Pair<>("Suvartotas natris (mg)", fields.get("sodium")));
        values.add(new Pair<>("Suvartotas magnis (mg)", fields.get("magnesium")));
        values.add(new Pair<>("Suvartotas fosforas (mg)", fields.get("phosphorus")));
        values.add(new Pair<>("Suvartotas kalis (mg)", fields.get("potassium")));
        values.add(new Pair<>("Suvartotas kalcis (g)", fields.get("calcium")));
        values.add(new Pair<>("Suvartota geležies (g)", fields.get("iron")));
        values.add(new Pair<>("Suvartotas cinkas (g)", fields.get("zinc")));
        values.add(new Pair<>("Suvartotas selenas (mg)", fields.get("selenium")));
        values.add(new Pair<>("Suvartotas jodas (mg)", fields.get("iodide")));
        values.add(new Pair<>("Suvartota folio rūgšties (g)", fields.get("folicAcid")));
        values.add(new Pair<>("Suvartotas alkoholis (g)", fields.get("alcohol")));
        values.add(new Pair<>("Suvartotas vitaminas A (mg)", fields.get("vitaminA_RE")));
        values.add(new Pair<>("Suvartotas vitaminas D (mg)", fields.get("vitaminD")));
        values.add(new Pair<>("Suvartotas vitaminas E (mg)", fields.get("vitaminE_TE")));
        values.add(new Pair<>("Suvartotas vitaminas B1 (g)", fields.get("thiamin")));
        values.add(new Pair<>("Suvartotas vitaminas B2 (g)", fields.get("riboflavin")));
        values.add(new Pair<>("Suvartotas vitaminas B6 (g)", fields.get("vitaminB6")));
        values.add(new Pair<>("Suvartotas vitaminas B12 (mg)", fields.get("vitaminB12")));
        values.add(new Pair<>("Suvartotas vitaminas C (mg)", fields.get("vitaminC")));
        values.add(new Pair<>("Suvartotas niacinas (g)", fields.get("niacin")));
        values.add(new Pair<>("Suvartotas niacino ekvivalentas (mg)", fields.get("niacinEquivalent")));
        values.add(new Pair<>("Suvartota energinė vertė (kj)", fields.get("energyValueKJ")));
        values.add(new Pair<>("Suvartota energinė vertė (kcal)", fields.get("energyValueKCAL")));
        values.add(new Pair<>("Produktų kiekis", totalProducts));
        for (int i = 1; i <= values.size(); i++) {
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(values.get(i - 1).first);
            cell = row.createCell(1);
            cell.setCellValue(values.get(i - 1).second);
        }
        sheet.setColumnWidth(0, (43 * 256));
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
                        productValues.put("alcohol", dataSnapshot.child("alcohol").getValue(String.class));
                        productValues.put("ashMinerals", dataSnapshot.child("ashMinerals").getValue(String.class));
                        productValues.put("calcium", dataSnapshot.child("calcium").getValue(String.class));
                        productValues.put("cholesterol", dataSnapshot.child("cholesterol").getValue(String.class));
                        productValues.put("edibleCoefficient", dataSnapshot.child("edibleCoefficient").getValue(String.class));
                        productValues.put("energyValueKCAL", dataSnapshot.child("energyValueKCAL").getValue(String.class));
                        productValues.put("energyValueKJ", dataSnapshot.child("energyValueKJ").getValue(String.class));
                        productValues.put("fattyAcidsTotalMonounsaturated", dataSnapshot.child("fattyAcidsTotalMonounsaturated").getValue(String.class));
                        productValues.put("fattyAcidsTotalPolyunsaturated", dataSnapshot.child("fattyAcidsTotalPolyunsaturated").getValue(String.class));
                        productValues.put("fattyAcidsTotalSaturated", dataSnapshot.child("fattyAcidsTotalSaturated").getValue(String.class));
                        productValues.put("fibre", dataSnapshot.child("fibre").getValue(String.class));
                        productValues.put("folicAcid", dataSnapshot.child("folicAcid").getValue(String.class));
                        productValues.put("iodide", dataSnapshot.child("iodide").getValue(String.class));
                        productValues.put("iron", dataSnapshot.child("iron").getValue(String.class));
                        productValues.put("magnesium", dataSnapshot.child("magnesium").getValue(String.class));
                        productValues.put("niacin", dataSnapshot.child("niacin").getValue(String.class));
                        productValues.put("niacinEquivalent", dataSnapshot.child("niacinEquivalent").getValue(String.class));
                        productValues.put("phosphorus", dataSnapshot.child("phosphorus").getValue(String.class));
                        productValues.put("potassium", dataSnapshot.child("potassium").getValue(String.class));
                        productValues.put("proteinAnimal", dataSnapshot.child("proteinAnimal").getValue(String.class));
                        productValues.put("proteinPlant", dataSnapshot.child("proteinPlant").getValue(String.class));
                        productValues.put("riboflavin", dataSnapshot.child("riboflavin").getValue(String.class));
                        productValues.put("selenium", dataSnapshot.child("selenium").getValue(String.class));
                        productValues.put("sodium", dataSnapshot.child("sodium").getValue(String.class));
                        productValues.put("starch", dataSnapshot.child("starch").getValue(String.class));
                        productValues.put("sugars", dataSnapshot.child("sugars").getValue(String.class));
                        productValues.put("thiamin", dataSnapshot.child("thiamin").getValue(String.class));
                        productValues.put("vitaminA_RE", dataSnapshot.child("vitaminA_RE").getValue(String.class));
                        productValues.put("vitaminB12", dataSnapshot.child("vitaminB12").getValue(String.class));
                        productValues.put("vitaminB6", dataSnapshot.child("vitaminB6").getValue(String.class));
                        productValues.put("vitaminC", dataSnapshot.child("vitaminC").getValue(String.class));
                        productValues.put("vitaminD", dataSnapshot.child("vitaminD").getValue(String.class));
                        productValues.put("vitaminE_TE", dataSnapshot.child("vitaminE_TE").getValue(String.class));
                        productValues.put("zinc", dataSnapshot.child("zinc").getValue(String.class));
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
                        productValues.put("alcohol", dataSnapshot.child("Alcohol").getValue(String.class));
                        productValues.put("ashMinerals", dataSnapshot.child("Ash (minerals)").getValue(String.class));
                        productValues.put("calcium", dataSnapshot.child("Calcium").getValue(String.class));
                        productValues.put("cholesterol", dataSnapshot.child("Cholesterol").getValue(String.class));
                        productValues.put("edibleCoefficient", dataSnapshot.child("Edible part").getValue(String.class));
                        productValues.put("energyValueKCAL", dataSnapshot.child("Energy value, kcal").getValue(String.class));
                        productValues.put("energyValueKJ", dataSnapshot.child("Energy value, kJ").getValue(String.class));
                        productValues.put("fattyAcidsTotalMonounsaturated", dataSnapshot.child("Fatty acids, total monounsaturated").getValue(String.class));
                        productValues.put("fattyAcidsTotalPolyunsaturated", dataSnapshot.child("Fatty acids, total polyunsaturated").getValue(String.class));
                        productValues.put("fattyAcidsTotalSaturated", dataSnapshot.child("Fatty acids, total saturated").getValue(String.class));
                        productValues.put("fibre", dataSnapshot.child("Fibre").getValue(String.class));
                        productValues.put("folicAcid", dataSnapshot.child("Folic acid").getValue(String.class));
                        productValues.put("iodide", dataSnapshot.child("Iodide").getValue(String.class));
                        productValues.put("iron", dataSnapshot.child("Iron").getValue(String.class));
                        productValues.put("magnesium", dataSnapshot.child("Magnesium").getValue(String.class));
                        productValues.put("niacin", dataSnapshot.child("Niacin").getValue(String.class));
                        productValues.put("niacinEquivalent", dataSnapshot.child("Niacin equivalent").getValue(String.class));
                        productValues.put("phosphorus", dataSnapshot.child("Phosphorus").getValue(String.class));
                        productValues.put("potassium", dataSnapshot.child("Potassium").getValue(String.class));
                        productValues.put("proteinAnimal", dataSnapshot.child("Protein, animal").getValue(String.class));
                        productValues.put("proteinPlant", dataSnapshot.child("Protein, plant").getValue(String.class));
                        productValues.put("riboflavin", dataSnapshot.child("Vitamin B2").getValue(String.class));
                        productValues.put("selenium", dataSnapshot.child("Selenium").getValue(String.class));
                        productValues.put("sodium", dataSnapshot.child("Sodium").getValue(String.class));
                        productValues.put("starch", dataSnapshot.child("Starch").getValue(String.class));
                        productValues.put("sugars", dataSnapshot.child("Sugars").getValue(String.class));
                        productValues.put("thiamin", dataSnapshot.child("Vitamin B1").getValue(String.class));
                        productValues.put("vitaminA_RE", dataSnapshot.child("Vitamin A (RE)").getValue(String.class));
                        productValues.put("vitaminB12", dataSnapshot.child("Vitamin B12").getValue(String.class));
                        productValues.put("vitaminB6", dataSnapshot.child("Vitamin B6").getValue(String.class));
                        productValues.put("vitaminC", dataSnapshot.child("Vitamin C").getValue(String.class));
                        productValues.put("vitaminD", dataSnapshot.child("Vitamin D").getValue(String.class));
                        productValues.put("vitaminE_TE", dataSnapshot.child("Vitamin E (TE)").getValue(String.class));
                        productValues.put("zinc", dataSnapshot.child("Zinc").getValue(String.class));
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