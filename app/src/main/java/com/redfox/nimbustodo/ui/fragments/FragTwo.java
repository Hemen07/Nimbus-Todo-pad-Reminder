package com.redfox.nimbustodo.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redfox.nimbustodo.R;


public class FragTwo extends Fragment {

    public static FragTwo getInstance() {
        return new FragTwo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_two, container, false);
        return v;
    }
}
