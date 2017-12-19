package com.redfox.nimbustodo.ui.interfaces;

import android.view.View;
import android.widget.ImageView;

public interface AdapterCallBack {
    public void onRowClick(int position, View view, ImageView sharedImv);
    public void onLongRowClick(int position, View view);

}
