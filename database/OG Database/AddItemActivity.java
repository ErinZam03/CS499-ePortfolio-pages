package com.example.inventoryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

        // Initialize UI components
        itemNameEditText = findViewById(R.id.itemNameEditText);
        amountEditText = findViewById(R.id.amountEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveItemButton = findViewById(R.id.saveItemButton);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Set onClickListener for the Save button
        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItemToDatabase();
            }
        });
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

        // Save item to the database
        boolean success = databaseHelper.addItem(itemName, amount);
        if (success) {
            Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the AddItemActivity and go back to the previous screen
        } else {
            Toast.makeText(this, "Error adding item", Toast.LENGTH_SHORT).show();
        }
    }
}