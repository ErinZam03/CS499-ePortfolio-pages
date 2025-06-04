package com.example.inventoryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemNameEditText;
    private EditText amountEditText;
    private Spinner categorySpinner;
    private Button saveItemButton;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //added Enable the Up arrow in the built-in ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Item"); // optional
        }

        // Initialize UI components
        itemNameEditText = findViewById(R.id.itemNameEditText);
        amountEditText = findViewById(R.id.amountEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveItemButton = findViewById(R.id.saveItemButton);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Setup spinner options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);


        // Set onClickListener for the Save button
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItemToDatabase();
            }
        });
    }
    // added this override at the same level as onCreate
    @Override
    public boolean onSupportNavigateUp() {
        finish();    // close this Activity and go back
        return true; // we handled it
    }

    // Method to save item to the database
    private void saveItemToDatabase() {
        String itemName = itemNameEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String amountText = amountEditText.getText().toString().trim();

        if (itemName.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save item to the database including category
        boolean success = databaseHelper.addItem(itemName, category, amount);
        if (success) {
            Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the AddItemActivity and go back to the previous screen
        } else {
            Toast.makeText(this, "Error adding item", Toast.LENGTH_SHORT).show();
        }
    }

}