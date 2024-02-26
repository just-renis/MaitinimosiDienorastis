package com.example.maitinimosidienorastis;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button logOutButton, goToMyProductsButton, goToMealLogButton, goToMealLogDataButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        logOutButton = findViewById(R.id.logOutButton);
        goToMyProductsButton = findViewById(R.id.goToMyProducts);
        goToMealLogButton = findViewById(R.id.goToMealLogButton);
        goToMealLogDataButton = findViewById(R.id.goToMealLogDataButton);
        if (auth.getCurrentUser() == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        goToMyProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MyProductsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        goToMealLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MealLogActivity.class);
                startActivity(intent);
                finish();
            }
        });
        goToMealLogDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MealLogDataActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) recreate();
    }

    public void readExcelAndWriteToDB(Context context, String fileName) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            Row firstRow = rowIterator.next();
            List<String> keys = new ArrayList<>();
            for (Cell cell : firstRow) keys.add(cell.getStringCellValue());
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("products");
            int index = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, Object> product = new HashMap<>();
                int cellIndex = 0;
                for (Cell cell : row) {
                    String value;
                    if (cell.getCellType() == CellType.NUMERIC) {
                        double numericValue = cell.getNumericCellValue();
                        if (numericValue == Math.floor(numericValue)) value = String.valueOf((int) numericValue);
                        else value = String.valueOf(numericValue);
                    }
                    else value = cell.getStringCellValue();
                    product.put(keys.get(cellIndex++), value);
                }
                product.put("Id", ++index);
                db.push().setValue(product);
            }
            inputStream.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}