package com.example.calendertext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Context context;
    private TextView monthlyExpenseTextView;
    private MaterialCalendarView calendarView;
    private List<DatabaseHelper.EventData> eventDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        context = this;
        calendarView = findViewById(R.id.calendarView);
        monthlyExpenseTextView = findViewById(R.id.monthlyexpense);

        int minYear = 2024;
        int maxYear = 2024;
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(minYear, 1, 1))
                .setMaximumDate(CalendarDay.from(maxYear, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        TodayDecorator todayDecorator = new TodayDecorator(context);
        calendarView.addDecorator(todayDecorator);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        eventDates = dbHelper.getAllEventData();

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                SecondActivity(date);
            }
        });

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int selectedYear = date.getYear();
                int selectedMonth = date.getMonth();

                // Retrieve the selected type from the toolbar spinner
                Spinner spinner = (Spinner) findViewById(R.id.menu_dropdown);
                String selectedType = (String) spinner.getSelectedItem();

                // Update the monthly expense text view
                updateMonthlyExpenseTextView(selectedType, selectedYear, selectedMonth);
            }
        });
    }
    private void SecondActivity(CalendarDay date) {
        String selectedDateString = formatDate(date);
        if (selectedDateString != null && !selectedDateString.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra(DateDetailsActivity.SELECTED_DATE, selectedDateString);
            startActivity(intent);
        } else {
            Toast.makeText(context, "Error converting date format", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatDate(CalendarDay date) {
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDay();

        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem filterItem = menu.findItem(R.id.menu_dropdown);
        Spinner spinner = (Spinner) filterItem.getActionView().findViewById(R.id.menu_dropdown);

        // Retrieve types from the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<String> types = dbHelper.getAllTypes();

        // Add "All" as the default option
        types.add(0, "All");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set "All" as the default selection
        spinner.setSelection(0);

        // Handle item selection in the dropdown
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = (String) parent.getItemAtPosition(position);

                // Update the calendar to display the filtered data
                updateCalendarDecorators(selectedType);

                // Update the monthly expense text view
                CalendarDay date = calendarView.getCurrentDate();
                int year = date.getYear();
                int month = date.getMonth();
                updateMonthlyExpenseTextView(selectedType, year, month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();

        // Get the currently selected type from the spinner if not null
        Spinner spinner = (Spinner) findViewById(R.id.menu_dropdown);
        if (spinner != null) {
            String selectedType = (String) spinner.getSelectedItem();

            // Update the calendar to display the filtered data
            updateCalendarDecorators(selectedType);

            // Update the monthly expense text view
            CalendarDay date = calendarView.getCurrentDate();
            int year = date.getYear();
            int month = date.getMonth();
            updateMonthlyExpenseTextView(selectedType, year, month);
        } else {
            // Spinner is null, handle the situation accordingly
            Log.e("MainActivity", "Spinner is null in onResume()");
            // For example, show all options as default or display a message to the user
            // Here, we'll show all options by setting a default value in updateCalendarDecorators()
            updateCalendarDecorators("All");
        }
    }


    private void updateMonthlyExpenseTextView(String expenseType, int year, int month) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        double totalExpense;

        // If expenseType is "All", calculate total expense for all types
        if (expenseType.equals("All")) {
            totalExpense = dbHelper.getTotalExpenseForMonth(year, month);
        } else {
            // Otherwise, calculate total expense for the specified type
            totalExpense = dbHelper.getTotalExpenseForTypeAndMonth(expenseType, year, month);
        }

        // Format the total expense as needed and set it to the monthlyExpenseTextView
        String formattedTotalExpense;
        if (totalExpense % 1 == 0) {
            // If the total expense is a whole number, display it without decimal places
            formattedTotalExpense = String.format(Locale.getDefault(), "%.0f", totalExpense);
        } else {
            // If the total expense has decimal places, display it with two decimal places
            formattedTotalExpense = String.format(Locale.getDefault(), "%.2f", totalExpense);
        }
        monthlyExpenseTextView.setText("Total ₹:" + formattedTotalExpense);
    }


    private void updateCalendarDecorators(String selectedType) {
        // Retrieve types from the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Fetch data for the selected type
        List<DatabaseHelper.EventData> filteredEventData;
        if (selectedType.equals("All")) {
            // Show all events if "All" is selected
            filteredEventData = eventDates;
        } else {
            filteredEventData = dbHelper.getDataForEventType(selectedType);
        }

        calendarView.removeDecorators();

        // Map to store total amount for each date
        Map<String, Integer> dateAmountMap = new HashMap<>();

        // Calculate total amount for all events
        int totalAmountAll = 0;

        // Calculate total amount for filtered event data
        for (DatabaseHelper.EventData eventData : filteredEventData) {
            String date = eventData.getDate();
            int amount = eventData.getAmount();

            // Update total amount for all events
            totalAmountAll += amount;

            // Update total amount for each date
            int totalAmount = dateAmountMap.getOrDefault(date, 0);
            totalAmount += amount;
            dateAmountMap.put(date, totalAmount);
        }

        // Add decorators for each date with the total amount
        for (Map.Entry<String, Integer> entry : dateAmountMap.entrySet()) {
            String date = entry.getKey();
            int totalAmount = entry.getValue();

            CustomDayDecorator decorator = new CustomDayDecorator(date, totalAmount, context);
            calendarView.addDecorator(decorator);
        }

        // Add decorator to display total amount for all events
        CustomDayDecorator totalAllDecorator = new CustomDayDecorator("Total All", totalAmountAll, context);
        calendarView.addDecorator(totalAllDecorator);
    }
}
