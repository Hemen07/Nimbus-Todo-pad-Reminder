package com.redfox.nimbustodo.util.common_util;


import com.redfox.nimbustodo.R;
import com.redfox.nimbustodo.ui.interfaces.TagImageCallBacks;

public class UtilTagImage {

    public static void imagePicker(int id, TagImageCallBacks tagImageCallBacks) {
        switch (id) {
            case 0:
                tagImageCallBacks.imageListener(R.drawable.tag_sports);
                break;
            case 1:
                tagImageCallBacks.imageListener(R.drawable.tag_event);
                break;
            case 2:
                tagImageCallBacks.imageListener(R.drawable.tag_work);
                break;
            case 3:
                tagImageCallBacks.imageListener(R.drawable.tag_exercise);
                break;
            case 4:
                tagImageCallBacks.imageListener(R.drawable.tag_meetings);
                break;
            case 5:
                tagImageCallBacks.imageListener(R.drawable.tag_study);
                break;
            case 6:
                tagImageCallBacks.imageListener(R.drawable.tag_travel);
                break;
            case 7:
                tagImageCallBacks.imageListener(R.drawable.tag_shop);
                break;
            case 8:
                tagImageCallBacks.imageListener(R.drawable.tag_default);
                break;
            case 9:
                tagImageCallBacks.imageListener(R.drawable.tag_alien);
                break;
            default:
                break;

        }
    }
}
