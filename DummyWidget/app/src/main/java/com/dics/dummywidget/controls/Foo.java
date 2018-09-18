package com.dics.dummywidget.controls;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.dics.widgetframework.Control;

public class Foo {
    private static int f0 = 0;
    private static int f1 = 1;

    public static class NestedControl extends Control {
        public NestedControl(Context context, ImageView iv) {
            super(context, iv);
        }

        @Override
        public void run() {
            Toast.makeText(context, ""+f0, Toast.LENGTH_LONG).show();
            int next = f1 + f0;
            f0 = f1;
            f1 = next;
        }
    }

    public static class ResetControl extends Control {
        public ResetControl(Context context, ImageView iv) {
            super(context, iv);
        }

        @Override
        public void run() {
            f0 = 0;
            f1 = 1;
            Toast.makeText(context, "Reset", Toast.LENGTH_LONG).show();
        }
    }
}
