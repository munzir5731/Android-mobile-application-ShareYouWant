package com.example.abdullahal_munzir.shareyouwant;

import android.text.format.DateFormat;

/**
 * Created by Abdullah Al-Munzir on 27-Mar-18.
 */

public class DateTime {
    public String convertDate(String dateInMilliseconds,String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }
}
