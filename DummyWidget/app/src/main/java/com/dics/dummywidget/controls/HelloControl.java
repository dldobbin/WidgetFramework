package com.dics.dummywidget.controls;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.dics.widgetframework.Control;

public class HelloControl extends Control {
    public HelloControl(Context context, ImageView iv) {
        super(context, iv);
    }

    @Override
    public void run() {
        Toast.makeText(context, "Hello Control!", Toast.LENGTH_LONG).show();
    }
}
