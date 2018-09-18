package com.dics.widgetframework;

import android.app.Service;
import android.content.Context;
import android.widget.ImageView;

public class CloseControl extends Control {
    public CloseControl(Context context, ImageView iv) {
        super(context, iv);
    }

    @Override
    public void run() {
        ((Service)context).stopSelf();
    }
}
