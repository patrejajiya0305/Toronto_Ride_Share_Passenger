package com.sample.packnmovers;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static void alertMessageOk(final Activity activity, String strTitle, String strMessage) {
        final androidx.appcompat.app.AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        if (strTitle.length() > 0) {
            alertbox.setTitle(strTitle);
        }
        alertbox.setMessage(strMessage);
        alertbox.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
       /* alertbox.setPositiveButton(android.R.string.ok, (arg0, arg1) -> {
        });*/
        alertbox.show();
    }

    public static boolean isEmailValidate(String email) {
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
