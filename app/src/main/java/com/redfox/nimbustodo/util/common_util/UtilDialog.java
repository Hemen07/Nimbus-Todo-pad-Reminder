package com.redfox.nimbustodo.util.common_util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;


public class UtilDialog {
    private Context context;
    private AlertDialog alertDialog = null;
    private AlertDialogListener callBack;
    private String whichOperation;

    public UtilDialog(Context context, AlertDialogListener callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    public void showAlertDialog(String whichOperation, String title, String message, String positive, String negative, String neutral, boolean isCancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        this.whichOperation = whichOperation;
        builder.setTitle("");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnOperation(1);
            }
        });
        builder.setNeutralButton(neutral, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnOperation(0);
            }
        });
        alertDialog = builder.create();


        alertDialog.show();
    }


    public void btnOperation(int which) {
        if (which == 1) {
            callBack.onPosBtnClicked(whichOperation);
            if (alertDialog != null)
                alertDialog.dismiss();

        } else if (which == 0) {
            callBack.onNeutralClick();
            if (alertDialog != null)
                alertDialog.dismiss();
        }
    }

    public interface AlertDialogListener {
        public void onPosBtnClicked(String whichOperation);

        public void onNegBtnClicked();

        public void onNeutralClick();
    }

}

