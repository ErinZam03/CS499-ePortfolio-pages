package com.example.inventoryapp;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InventoryDataActivity extends AppCompatActivity {

    private RecyclerView dataRecyclerView;
    private InventoryAdapter inventoryAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        // Initialize RecyclerView
        dataRecyclerView = findViewById(R.id.dataRecyclerView);
        dataRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize TextView for no data message
        TextView noDataText = findViewById(R.id.noDataText);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Fetch inventory data
        List<InventoryItem> inventoryList = fetchInventoryData();

        // Show or hide the "no data" message based on the inventory data
        if (inventoryList.isEmpty()) {
            noDataText.setVisibility(View.VISIBLE);
        } else {
            noDataText.setVisibility(View.GONE);
        }

        // Set up the adapter
        inventoryAdapter = new InventoryAdapter(inventoryList);
        dataRecyclerView.setAdapter(inventoryAdapter);

        FloatingActionButton addItemFab = findViewById(R.id.addItemFab);
        addItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryDataActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });
    }

    // Fetch inventory data from the database
    private List<InventoryItem> fetchInventoryData() {
        List<InventoryItem> inventoryList = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllItems();

        if (cursor != null) {
            int idIndex = cursor.getColumnIndex("ItemID");
            int nameIndex = cursor.getColumnIndex("ItemName");
            int quantityIndex = cursor.getColumnIndex("Quantity");

            while (cursor.moveToNext()) {
                int id = idIndex != -1 ? cursor.getInt(idIndex) : -1;
                String name = nameIndex != -1 ? cursor.getString(nameIndex) : "Unknown";
                int stockLevel = quantityIndex != -1 ? cursor.getInt(quantityIndex) : 0;

                String category = "General"; // Placeholder category
                inventoryList.add(new InventoryItem(id, name, category, stockLevel));
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Error fetching inventory data", Toast.LENGTH_SHORT).show();
        }

        return inventoryList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}

