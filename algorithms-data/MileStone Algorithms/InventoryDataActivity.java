package com.example.inventoryapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//added Menu for Logout button
import android.view.Menu;
import android.view.MenuItem;

//added for new Inventory fields
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InventoryDataActivity extends AppCompatActivity {

    private RecyclerView dataRecyclerView;
    private InventoryAdapter inventoryAdapter;
    private DatabaseHelper databaseHelper;

    //added Inventory fields
    private EditText searchBar;
    private Spinner categoryDropdown;
    private Button buttonSearch;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        searchBar = findViewById(R.id.searchBar);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        buttonSearch = findViewById(R.id.buttonSearch);

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
        inventoryAdapter = new InventoryAdapter();
        inventoryAdapter.updateList(inventoryList); // Save the full list
        inventoryAdapter.sortAndFilter("", "All", "Name A→Z"); // Default filter/sort
        dataRecyclerView.setAdapter(inventoryAdapter);

        // added Enter/Search button
        buttonSearch.setOnClickListener(v -> {
            String query    = searchBar.getText().toString().trim();
            String category = categoryDropdown.getSelectedItem().toString();

            // Use default sorting: Name A→Z
            inventoryAdapter.sortAndFilter(query, category, "Name A→Z");

            noDataText.setVisibility(
                    inventoryAdapter.getItemCount() == 0
                            ? View.VISIBLE
                            : View.GONE
            );

            // Scroll back to the top of the list after filtering
            dataRecyclerView.scrollToPosition(0);
        });

        //FAB to add new items
        FloatingActionButton addItemFab = findViewById(R.id.addItemFab);
        addItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryDataActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the res/menu/menu_inventory.xml into the app bar
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    //added Handle taps on the app-bar items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // clear back-stack and return to login
            Intent logout = new Intent(this, LoginActivity.class);

            // Clear the app’s back-stack on logout so user can’t return with Back
            logout.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(logout);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                int categoryIndex = cursor.getColumnIndex("Category");
                String category = categoryIndex != -1 ? cursor.getString(categoryIndex) : "General";

                inventoryList.add(new InventoryItem(id, name, category, stockLevel));
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Error fetching inventory data", Toast.LENGTH_SHORT).show();
        }

        return inventoryList;
    }

    // Refresh inventory list when returning from Add/Edit
    @Override
    protected void onResume() {
        super.onResume();

        List<InventoryItem> updatedList = fetchInventoryData();

        String query = searchBar.getText().toString().trim();
        String category = categoryDropdown.getSelectedItem().toString();
        inventoryAdapter.updateList(updatedList);
        inventoryAdapter.sortAndFilter(query, category, "Name A→Z");

        TextView noDataText = findViewById(R.id.noDataText);
        noDataText.setVisibility(
                inventoryAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}

