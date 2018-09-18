package com.dics.widgetframework;

import android.content.Context;
import android.widget.ImageView;

public abstract class Control {
    protected Context context;
    protected ImageView iv;

    public Control(Context context, ImageView iv) {
        this.context = context;
        this.iv = iv;
    }

    public void setImageView(ImageView iv) {
        this.iv = iv;
    }

    public ImageView getImageView() {
        return iv;
    }

    public abstract void run();
}
