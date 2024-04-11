package com.example.calendertext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DateDetailsActivity extends AppCompatActivity {
    public static final String SELECTED_DATE = "selected_date";
    private EditText expenseEdittext, amountEdittext;
    private Button saveButton;
    private List<String> expenseTypesList = new ArrayList<>();
    private List<String> bankTypeList = new ArrayList<>();
    private Spinner spinnerType,spinnerBank;
    private ArrayAdapter<String> spinnerAdapterType;
    private ArrayAdapter<String> spinnerAdapterBank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_details);

        amountEdittext = findViewById(R.id.amountEditText);
        expenseEdittext = findViewById(R.id.expenseEditText);
        Spinner frequency = findViewById(R.id.frequencySpinner);
        ImageView addTypeImg = findViewById(R.id.addTypeImg);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerBank = findViewById(R.id.spinnerBank);

        ImageView addBankImg = findViewById(R.id.addBankImg);

        String[] frequencies = {"Select Frequency", "One Time", "Monthly"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, frequencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequency.setAdapter(adapter);

        saveButton = findViewById(R.id.saveButton);

        String selectedDate = getIntent().getStringExtra(SELECTED_DATE);
        Toast.makeText(this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<String> expenseTypes = dbHelper.getAllTypes(); // Fetch expense types
        List<String> bankTypes = dbHelper.getAllBanks();

        ArrayAdapter<String> expenseTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, expenseTypes);
        expenseTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(expenseTypeAdapter);

        // Initialize spinnerAdapterType with expenseTypeAdapter
        spinnerAdapterType = expenseTypeAdapter;

        ArrayAdapter<String> bankAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, bankTypes);
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBank.setAdapter(bankAdapter);

        // Initialize spinnerAdapterBank with bankAdapter
        spinnerAdapterBank = bankAdapter;

        addTypeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the dialog layout
                View dialogView = LayoutInflater.from(DateDetailsActivity.this).inflate(R.layout.dialog_add_expense_type, null);

                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(DateDetailsActivity.this);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                // Initialize views in the dialog layout
                EditText editTextExpenseType = dialogView.findViewById(R.id.editTextExpenseType);
                Button buttonSaveExpenseType = dialogView.findViewById(R.id.buttonSaveExpenseType);

                // Set click listener for the Save button
                buttonSaveExpenseType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the entered expense type from the EditText
                        String expenseType = editTextExpenseType.getText().toString();

                        if (!expenseType.isEmpty()) {
                            // Add the new expense type to the data source of the Spinner's adapter
                            expenseTypesList.add(expenseType);

                            // Notify the adapter that the data set has changed
                            spinnerAdapterType.clear();
                            spinnerAdapterType.addAll(expenseTypesList);
                            spinnerAdapterType.notifyDataSetChanged();

                            // Select the newly added type in the spinner
                            spinnerType.setSelection(spinnerAdapterType.getPosition(expenseType));

                            // Log to verify the data source and adapter update
                            Log.d("DateDetailsActivity", "New expense type added: " + expenseType);
                            Log.d("DateDetailsActivity", "Selected type in spinner: " + expenseType);
                        } else {
                            // Handle the case where expenseType is empty
                            Toast.makeText(DateDetailsActivity.this, "Please enter an expense type", Toast.LENGTH_SHORT).show();
                        }

                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });



        addBankImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the dialog layout
                View dialogView = LayoutInflater.from(DateDetailsActivity.this).inflate(R.layout.dialog_add_bank, null);

                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(DateDetailsActivity.this);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                EditText editTextBankType = dialogView.findViewById(R.id.editTextBankName);
                Button buttonSaveBankType = dialogView.findViewById(R.id.buttonSaveBank);

                // Set click listener for the Save button
                buttonSaveBankType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the entered bank type from the EditText
                        String bankType = editTextBankType.getText().toString();

                        if (!bankType.isEmpty()) {
                            // Add the new bank type to the data source of the Spinner's adapter
                            bankTypeList.add(bankType);

                            // Notify the adapter that the data set has changed
                            spinnerAdapterBank.clear();
                            spinnerAdapterBank.addAll(bankTypeList);
                            spinnerAdapterBank.notifyDataSetChanged();

                            // Log to verify the data source and adapter update
                            Log.d("DateDetailsActivity", "New bank type added: " + bankType);
                            Log.d("DateDetailsActivity", "Selected bank type in spinner: " + bankType);
                        } else {
                            // Handle the case where bankType is empty
                            Toast.makeText(DateDetailsActivity.this, "Please enter a bank type", Toast.LENGTH_SHORT).show();
                        }

                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                dialog.show();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountText = amountEdittext.getText().toString().trim();
                String expenseName = expenseEdittext.getText().toString().trim();
                String selectedFrequency = frequency.getSelectedItem().toString();
                String expenseType = spinnerType.getSelectedItem().toString().trim();
                String bankType = spinnerBank.getSelectedItem().toString();
                if (!amountText.isEmpty() && !selectedFrequency.equals("Select Frequency")) {
                    Double amount = Double.parseDouble(amountText);

                    // Insert data into the database
                    dbHelper.insertData(selectedDate, expenseName, amount, expenseType, bankType);

                    // Display success message
                    Toast.makeText(DateDetailsActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();

                    if (selectedFrequency.equals("Monthly")) {
                        int month = Integer.parseInt(selectedDate.substring(5, 7));
                        int year = Integer.parseInt(selectedDate.substring(0, 4));

                        // Start the loop from the selected month and continue until December
                        for (int i = month + 1; i <= 12; i++) {
                            String nextMonthDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, i, Integer.parseInt(selectedDate.substring(8, 10)));
                            dbHelper.insertData(nextMonthDate, expenseName, amount, expenseType, bankType);
                        }
                    }

                    // Navigate back to MainActivity
                    Intent intent = new Intent(DateDetailsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Display error message if amount or frequency is not selected
                    Toast.makeText(DateDetailsActivity.this, "Please enter an amount and select a frequency", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
