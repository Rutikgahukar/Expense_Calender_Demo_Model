package com.example.calendertext;

import android.content.Context;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.calendertext.CalendarViewSpan;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Locale;

public class CustomDayDecorator implements DayViewDecorator {
    private String thisDateString;
    private double price;
    private Context mainContext;

    public CustomDayDecorator(String thisDateString, double price, Context mainContext) {
        this.thisDateString = thisDateString;
        this.price = price;
        this.mainContext = mainContext;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        int year = day.getYear();
        int month = day.getMonth(); // Adjusting month value to match your format
        int dayOfMonth = day.getDay();

        String currentDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, dayOfMonth);

        boolean shouldDecorate = currentDate.equals(thisDateString);
        return shouldDecorate;
    }

    @Override
    public void decorate(DayViewFacade view) {
        this.price = Math.round(this.price * 100.0) / 100.0;
        int roundedPrice = (int) this.price;
        String formattedPrice = "₹" + roundedPrice;
        Log.d("CustomDayDecorator", "Formatted price: " + formattedPrice);

        CalendarViewSpan cvs = new CalendarViewSpan(formattedPrice, mainContext);
        view.addSpan(cvs);
        view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(mainContext, R.color.colorDates)));
    }
}
