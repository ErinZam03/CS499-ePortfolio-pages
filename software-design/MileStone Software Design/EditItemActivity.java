package com.example.inventoryapp;

import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import androidx.appcompat.app.AppCompatActivity;

public class EditItemActivity extends AppCompatActivity {

    // form fields
    private EditText editItemName;
    private Spinner  categorySpinner;
    private EditText editItemQuantity;
    private Button buttonSaveItem;
    private Button buttonDelete;

    private DatabaseHelper databaseHelper;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Wire up the toolbar with an Up-arrow
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // show the left‐pointing Up arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // make the arrow respond to taps:
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bind your form fields
        editItemName     = findViewById(R.id.itemNameEditText);
        categorySpinner  = findViewById(R.id.categorySpinner);
        editItemQuantity = findViewById(R.id.amountEditText);
        buttonSaveItem   = findViewById(R.id.saveItemButton);
        buttonDelete     = findViewById(R.id.buttonDelete);

        //Setup category spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        databaseHelper = new DatabaseHelper(this);

        // Read the ITEM_ID passed in and load the existing item
        itemId = getIntent().getIntExtra("ITEM_ID", -1);
        if (itemId < 0) {
            Toast.makeText(this, "Invalid item", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        InventoryItem existing = databaseHelper.getItemById(itemId);
        if (existing != null) {
            editItemName.setText(existing.getName());
            editItemQuantity.setText(String.valueOf(existing.getStockLevel()));
            int pos = adapter.getPosition(existing.getCategory());
            if (pos >= 0) categorySpinner.setSelection(pos);
        }

        // Handle Save button
        buttonSaveItem.setOnClickListener(v -> {
            String newName = editItemName.getText().toString().trim();
            String qtyText = editItemQuantity.getText().toString().trim();
            String selectedCategory = categorySpinner.getSelectedItem().toString();

            if (newName.isEmpty() || qtyText.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }
            int newQty = Integer.parseInt(qtyText);
            boolean success = databaseHelper.updateItem(itemId, newName, selectedCategory, newQty);
            if (success) {
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Delete Button
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteDialog();
            }
        });
    }

    // Handle the Delete Item
    private void confirmDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteItem() {
        if (itemId != -1) {
            boolean success = databaseHelper.deleteItem(itemId);
            if (success) {
                Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                finish(); // Return to previous screen
            } else {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Item ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the Up-arrow tap
    @Override
    public boolean onSupportNavigateUp() {
        finish();      // simply close this screen
        return true;   // consume the event
    }
}


