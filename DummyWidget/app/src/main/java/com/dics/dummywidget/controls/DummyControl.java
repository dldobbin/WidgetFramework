package com.dics.dummywidget.controls;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.dics.widgetframework.Control;

public class DummyControl extends Control {
    public DummyControl(Context context, ImageView iv) {
        super(context, iv);
    }

    @Override
    public void run() {
        Toast.makeText(context, "This is a dummy control", Toast.LENGTH_LONG).show();
    }
}
