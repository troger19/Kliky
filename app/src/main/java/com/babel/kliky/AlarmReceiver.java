package com.babel.kliky;

/**
 * Created by jan.babel on 29/12/2016.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String mobileNumber = sharedPref.getString(MyPreferencesActivity.MOBILE_NUMBER_PREF, "+421905856454");
        String messageFromPreferences = sharedPref.getString(MyPreferencesActivity.SMS_MESSAGE_PREF, "Zase sa flakas!?");
        boolean shouldSendSms = sharedPref.getBoolean(MyPreferencesActivity.SENDING_SMS, false);
        String balance = "";
        int max = 0,sum = 0;
        Bundle bd = intent.getExtras();

        if (bd != null) {
            balance = (String) bd.get(MainActivity.BALANCE);
            max =  bd.getInt(MainActivity.MAX);
            sum =  bd.getInt(MainActivity.SUM);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageFromPreferences).append(" max:").append(max).append(" sum: ").append(sum).append(" balance: ").append(balance);

        if (shouldSendSms) {
            Toast.makeText(context, "Sending SMS", Toast.LENGTH_SHORT).show();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobileNumber, null, stringBuilder.toString(), null, null);
        }
    }
}