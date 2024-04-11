package com.example.calendertext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.style.LineBackgroundSpan;

import androidx.core.content.ContextCompat;

public class CalendarViewSpan implements LineBackgroundSpan {
    private String text;
    private Context context;

    public CalendarViewSpan(String text, Context context) {
        this.text = text;
        this.context = context;
    }

    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom,
                               CharSequence text, int start, int end, int lnum) {
        text = this.text;

        switch(text.length()) {
            case 4: text = "   "+text;
                break;
            case 5: text = "  "+text;
                break;
            case 6: text = " "+text;
        }

        // Calculate the center of the date rectangle
        float centerX = (left + right) / 2f;

        // Set text size and style
        p.setTypeface(Typeface.DEFAULT_BOLD); // Set the text style to bold
        int color = ContextCompat.getColor(context, R.color.colorText);
        p.setColor(color);

        // Adjust the y-coordinate to position the text below the date
        float textY = bottom + 25;

        // Calculate the width of the text
        float textWidth = p.measureText(text.toString());

        // Calculate the x-coordinate to center the text
        float textX = centerX - (textWidth / 2);

        // Draw the text
        c.drawText(text.toString(), textX, textY, p);
    }

}
