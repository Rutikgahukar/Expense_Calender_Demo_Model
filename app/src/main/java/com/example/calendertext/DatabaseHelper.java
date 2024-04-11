package com.example.calendertext;

import static android.app.DownloadManager.COLUMN_ID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense.db";
    private static final int DATABASE_VERSION = 5;
    private static final String TABLE_NAME = "detail";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_TYPE = "expenseType";
    private static final String COLUMN_BANK = "bankType";
    private static final String COLUMN_EXPENSE_NAME = "expenseName";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your table with appropriate column definitions
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +  // Define id column as primary key with auto-increment
                COLUMN_DATE + " TEXT," +
                COLUMN_EXPENSE_NAME + " TEXT," +
                COLUMN_AMOUNT + " REAL," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_BANK + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    // Inside your DatabaseHelper class
    // Inside your DatabaseHelper class
    public void insertData(String selectedDate, String expenseName, double amount, String expenseType, String bankType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, selectedDate);
        values.put(COLUMN_EXPENSE_NAME, expenseName);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_TYPE, expenseType);
        values.put(COLUMN_BANK, bankType);
        // Insert the data into the table

        Log.d("DatabaseHelper", "Inserted type: " + expenseType);
        db.insert(TABLE_NAME, null, values);
        // Close the database connection
        db.close();
    }
    public List<EventData> getAllEventData() {
        List<EventData> eventDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME; // Select all columns
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            int amount = (int) cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)); // Retrieve Type field
            EventData eventData = new EventData(date, amount, null, type);
            eventDataList.add(eventData);
        }

        cursor.close();
        return eventDataList;
    }
    public List<EventData> getDataForDate(String date) {
        List<EventData> eventDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        while (cursor.moveToNext()) {
            String expenseName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_NAME));
            int amount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
            EventData eventData = new EventData(date, amount, expenseName, type);
            eventDataList.add(eventData);
        }
        cursor.close();
        return eventDataList;
    }
    public List<DatabaseHelper.EventData> getDataForEventType(String eventType) {
        List<DatabaseHelper.EventData> eventDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{eventType});

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            int amount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
            String expenseName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_NAME));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
            DatabaseHelper.EventData eventData = new DatabaseHelper.EventData(date, amount, expenseName, type);
            eventDataList.add(eventData);
        }
        cursor.close();

        return eventDataList;
    }
    public List<String> getAllTypes() {
        List<String> types = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT " + COLUMN_TYPE + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
            types.add(type);
        }

        cursor.close();
        return types;
    }
    public List<String> getAllBanks() {
        List<String> banks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT " + COLUMN_BANK + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String bank = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BANK));
            banks.add(bank);
        }

        cursor.close();
        return banks;
    }
    public double getTotalExpenseForTypeAndMonth(String expenseType, int year, int month) {
        double totalExpense = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        // Construct the query to fetch the total expense for the specified type and month
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_NAME +
                " WHERE " + COLUMN_TYPE + " = ?" +
                " AND strftime('%Y', " + COLUMN_DATE + ") = ?" +
                " AND strftime('%m', " + COLUMN_DATE + ") = ?";
        String[] selectionArgs = { expenseType,
                String.format(Locale.getDefault(), "%04d", year),
                String.format(Locale.getDefault(), "%02d", month) };

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);
        }

        cursor.close();
        return totalExpense;
    }
    public double getTotalExpenseForMonth(int year, int month) {
        double totalExpense = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        // Construct the query to fetch the total expense for the specified month
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_NAME +
                " WHERE strftime('%Y', " + COLUMN_DATE + ") = ?" +
                " AND strftime('%m', " + COLUMN_DATE + ") = ?";
        String[] selectionArgs = {
                String.format(Locale.getDefault(), "%04d", year),
                String.format(Locale.getDefault(), "%02d", month)
        };

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);
        }

        cursor.close();
        return totalExpense;
    }


    public List<EventData> getDataForDateAndType(String selectedDate, String selectedType) {
        List<EventData> eventDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Construct the query to fetch entries for the specified date and type
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " = ? AND " + COLUMN_TYPE + " = ?";
        String[] selectionArgs = {selectedDate, selectedType};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            int amount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
            String expenseName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_NAME));
            EventData eventData = new EventData(date, amount, expenseName, selectedType);
            eventDataList.add(eventData);
        }

        cursor.close();
        return eventDataList;
    }


    public class EventData {
        private String date;
        private int amount;
        private String expenseName;
        private String type;

        public EventData(String date, int amount, String expenseName, String type) {
            this.date = date;
            this.amount = amount;
            this.expenseName = expenseName;
            this.type = type;

        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getExpenseName() {
            return expenseName;
        }

        public void setExpenseName(String expenseName) {
            this.expenseName = expenseName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

}

