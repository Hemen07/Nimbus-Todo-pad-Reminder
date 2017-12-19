package com.redfox.nimbustodo.util.common_util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UtilStorage {
    public static void saveProfileImage(Context context, Bitmap bitmap) {
        FileOutputStream fileOutputStream = null;
        String name = "profile.png";
        try {
            fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getProfileImage(Context context) {
        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;
        try {
            fileInputStream = context.openFileInput("profile.png");
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;

    }


}
