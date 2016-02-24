package com.nathansass.petal.models;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * Created by nathansass on 2/24/16.
 */
public class DateSettings implements DatePickerDialog.OnDateSetListener {
    Context context;

    public DateSettings(Context context) {
        this.context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        Toast toast = Toast.makeText(context, "Date: " + monthOfYear + "/" +dayOfMonth + "/" + year, Toast.LENGTH_LONG);
        toast.show();

    }
}
