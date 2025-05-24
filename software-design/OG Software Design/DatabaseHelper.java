package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "InventoryApp.db";
    private static final int DATABASE_VERSION = 1;

    // User Table
    private static final String TABLE_USER = "User";
    private static final String COLUMN_USERNAME = "Username";
    private static final String COLUMN_PASSWORD = "Password";

    // Inventory Table
    private static final String TABLE_INVENTORY = "Inventory";
    private static final String COLUMN_ITEM_ID = "ItemID";
    private static final String COLUMN_ITEM_NAME = "ItemName";
    private static final String COLUMN_QUANTITY = "Quantity";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User Table
        String createUserTable = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUserTable);

        // Create Inventory Table
        String createInventoryTable = "CREATE TABLE " + TABLE_INVENTORY + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_QUANTITY + " INTEGER)";
        db.execSQL(createInventoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    // CRUD Methods for User Table

    // Add a new user
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USER, null, values);
        return result != -1;
    }

    // Validate user credentials
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                null,
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close(); // Close the database here
        return isValid;
    }

    // Update a user's password
    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        int rowsAffected = db.update(TABLE_USER, values, COLUMN_USERNAME + "=?",
                new String[]{username});
        return rowsAffected > 0;
    }

    // CRUD Methods for Inventory Table

    // Add a new inventory item
    public boolean addItem(String itemName, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ItemName", itemName);
        values.put("Quantity", quantity);
        long result = db.insert("Inventory", null, values);
        return result != -1;
    }

    // **Update an inventory item**
    public boolean updateItem(int itemId, String itemName, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_QUANTITY, quantity);

        int rowsAffected = db.update(TABLE_INVENTORY, values, COLUMN_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)});
        return rowsAffected > 0;
    }

    // **Delete an inventory item**
    public boolean deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_INVENTORY, COLUMN_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)});
        return rowsDeleted > 0;
    }

    // Get all inventory items
    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null);
    }
}
