package com.kd.appoxy;

import android.graphics.Typeface;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class BindingSets {
    @BindingAdapter("setFace")
    public static  void setFace(TextView view, Typeface face){
        if (face!=null)
            view.setTypeface(face);
    }
}
