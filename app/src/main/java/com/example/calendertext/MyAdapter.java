package com.example.calendertext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<DatabaseHelper.EventData> eventDataList;
    private Context context;

    public MyAdapter(Context context, List<DatabaseHelper.EventData> eventDataList) {
        this.context = context;
        this.eventDataList = eventDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseHelper.EventData eventData = eventDataList.get(position);
        holder.bind(eventData);
    }

    @Override
    public int getItemCount() {
        return eventDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textamt, textexpense, textdate, txttype;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textamt = itemView.findViewById(R.id.amountTextView);
            textexpense = itemView.findViewById(R.id.expenseTextView);
            textdate = itemView.findViewById(R.id.dateTextView);
            txttype = itemView.findViewById(R.id.typeTextView);
        }

        public void bind(DatabaseHelper.EventData eventData) {
            textamt.setText(String.valueOf(eventData.getAmount()));
            textexpense.setText(eventData.getExpenseName());
            textdate.setText(formatDate(eventData.getDate())); // Format and set the date
            txttype.setText(eventData.getType());
        }

        // Method to format the date
        private String formatDate(String date) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
            try {
                Date parsedDate = inputFormat.parse(date);
                return outputFormat.format(parsedDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return ""; // Return empty string if parsing fails
            }
        }
    }
}

