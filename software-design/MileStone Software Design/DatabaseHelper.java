package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "InventoryApp.db";
    private static final int DATABASE_VERSION = 2;

    // User Table
    private static final String TABLE_USER = "User";
    private static final String COLUMN_USERNAME = "Username";
    private static final String COLUMN_PASSWORD = "Password";

    // Inventory Table
    private static final String TABLE_INVENTORY = "Inventory";
    private static final String COLUMN_ITEM_ID = "ItemID";
    private static final String COLUMN_ITEM_NAME = "ItemName";
    private static final String COLUMN_QUANTITY = "Quantity";
    private static final String COLUMN_CATEGORY = "Category";

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
                COLUMN_QUANTITY + " INTEGER, " +
                COLUMN_CATEGORY + " TEXT)";
        db.execSQL(createInventoryTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_INVENTORY + " ADD COLUMN " + COLUMN_CATEGORY + " TEXT");
        }
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


   // Check if Username exist already
    public boolean userExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USER,
                new String[]{ COLUMN_USERNAME },
                COLUMN_USERNAME + " = ?",
                new String[]{ username },
                null, null, null
        );
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    // CRUD Methods for Inventory Table

    // Add a new inventory item
    public boolean addItem(String itemName, String category, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_CATEGORY, category);
        long result = db.insert(TABLE_INVENTORY, null, values);
        return result != -1;
    }


    // **Update an inventory item**
    public boolean updateItem(int itemId, String itemName, String selectedCategory, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_CATEGORY, selectedCategory);

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

    /**
     * Retrieve a single inventory item by its database ID.
     *
     * @param itemId the primary key ID of the item in the Inventory table
     * @return an InventoryItem object if found, or null otherwise
     */
    public InventoryItem getItemById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_INVENTORY,
                new String[]{COLUMN_ITEM_ID, COLUMN_ITEM_NAME, COLUMN_QUANTITY, COLUMN_CATEGORY},
                COLUMN_ITEM_ID + " = ?",
                new String[]{String.valueOf(itemId)},
                null, null, null
        );

        InventoryItem item = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME));
                int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                item = new InventoryItem(itemId, name, category, quantity);
            }
            cursor.close();
        }
        db.close();
        return item;
    }
}


