package dics.clappies.controls;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.dics.widgetframework.Control;

import dics.clappies.activities.TextActivity;

public class NewTextControl extends Control {

    public NewTextControl(Context context, ImageView iv) {
        super(context, iv);
    }

    @Override
    public void run() {
        Intent intent = new Intent(context, TextActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
