package com.redfox.nimbustodo.util.common_util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class UtilExtra {
    private static final boolean LOG_DEBUG = true;


    public static void uiCheck(String TAG) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (LOG_DEBUG)
                Log.e(TAG, "UI");
        } else {
            if (LOG_DEBUG)
                Log.e(TAG, "NON UI");
        }
    }

    public static void showToast(Context context, String whatMsg) {
        Toast.makeText(context, whatMsg, Toast.LENGTH_SHORT).show();
    }

    public static String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    public static void shareDataOnClick(Context context, String Title, String extra) {
        String shareBody = "Title : " + Title + "\n" +
                " Content : " + extra;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plan");
        intent.putExtra(Intent.EXTRA_SUBJECT, "HEY ");
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(intent, "Share your Note with ..."));

    }


    public static void dialogArchiveInfo(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Important Info !! ");
        builder.setMessage("When you archive a note/notes, it will be stored into Archive section "
                + "and those notes will be automatically deleted by the System after certain period of time");
        builder.setCancelable(true);
        builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

}
