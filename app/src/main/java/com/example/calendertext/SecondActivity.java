package com.example.calendertext;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SecondActivity extends AppCompatActivity {
    private FloatingActionButton button;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private String selectedDate;
    private String selectedType;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.stoolbar1);
        dbHelper = new DatabaseHelper(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        button = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectedDate = getIntent().getStringExtra(DateDetailsActivity.SELECTED_DATE);
        if (selectedDate != null && !selectedDate.isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
            try {
                Date date = inputFormat.parse(selectedDate);
                String formattedDate = outputFormat.format(date);
                getSupportActionBar().setTitle(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate != null && !selectedDate.isEmpty()) {
                    Intent intent = new Intent(SecondActivity.this, DateDetailsActivity.class);
                    intent.putExtra(DateDetailsActivity.SELECTED_DATE, selectedDate);
                    startActivity(intent);
                } else {
                    Toast.makeText(SecondActivity.this, "Error: Selected date is null or empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchDataFromDatabase(String selectedDate, String selectedType) {
        if (selectedDate != null && !selectedDate.isEmpty()) {
            List<DatabaseHelper.EventData> eventDataList;
            if (selectedType != null && !selectedType.equals("All")) {
                // Fetch data for the selected date and type
                eventDataList = dbHelper.getDataForDateAndType(selectedDate, selectedType);
            } else {
                // Fetch data only for the selected date
                eventDataList = dbHelper.getDataForDate(selectedDate);
            }

            if (!eventDataList.isEmpty()) {
                // Assuming MyAdapter accepts a List<EventData> in its constructor
                adapter = new MyAdapter(this, eventDataList);
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setAdapter(null); // Clear the adapter to remove previous data
                Toast.makeText(this, "No events found for the selected date and type", Toast.LENGTH_SHORT).show();
            }

            // Set the toolbar title with the selected date
            Toolbar toolbar = findViewById(R.id.stoolbar1);
            TextView toolbarTitle = toolbar.findViewById(R.id.Test);
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
            try {
                Date date = inputFormat.parse(selectedDate);
                String formattedDate = outputFormat.format(date);
                toolbarTitle.setText("Selected Date : \n"+ formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            recyclerView.setAdapter(null); // Clear the adapter to remove previous data
            Toast.makeText(this, "Error: Selected date is null or empty", Toast.LENGTH_SHORT).show();
        }
    }

     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem filterItem = menu.findItem(R.id.menu_dropdown);
        Spinner spinner = (Spinner) filterItem.getActionView();

        List<String> types = dbHelper.getAllTypes();

        // Add "All" as the default option
        types.add(0, "All");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set "All" as the default selection
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedType = (String) parentView.getItemAtPosition(position);
                fetchDataFromDatabase(selectedDate, selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        return true;
    }

}
