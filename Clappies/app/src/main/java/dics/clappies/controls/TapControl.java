package dics.clappies.controls;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.dics.widgetframework.Control;

public class TapControl extends Control {

    public TapControl(Context context, ImageView iv) {
        super(context, iv);
    }

    @Override
    public void run() {
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            CharSequence text = clipboard.getPrimaryClip().getItemAt(0).coerceToText(context);
            String newText = TextUtils.join("\uD83D\uDC4F", text.toString().toUpperCase().split(" "));
            ClipData clip = ClipData.newPlainText("clap", newText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "You got clapped!", Toast.LENGTH_SHORT).show();
        }
    }
}
