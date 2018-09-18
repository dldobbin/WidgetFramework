package com.dics.widgetframework;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class WidgetService extends Service {
    private WindowManager mWindowManager;
    private RelativeLayout mFloatingView;
    private List<Control> controls;
    private boolean isLongClicking = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            controls = createControls();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Control mainControl = controls.get(controls.size()-1);

        mFloatingView = (RelativeLayout)LayoutInflater.from(this).inflate(com.dics.widgetframework.R.layout.activity_main,null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        mFloatingView.setPadding(0,dp(25),dp(25),dp(25));
        for (Control control : controls) {
            mFloatingView.addView(control.iv);
        }

        mainControl.getImageView().setOnTouchListener(new View.OnTouchListener() {
            private int initialY;
            private float initialTouchY;
            private boolean isLongPressing = false;
            private boolean hasMoved = false;

            private Handler handler = new Handler();
            private Runnable mLongPressed = new Runnable() {
                @Override
                public void run() {
                    for (int i=0; i<controls.size()-1; i++) {
                        /* Animate controls */
                        startAnimationForView(controls.get(i).getImageView(), i, controls.size()-1, 45);
                    }
                    isLongPressing = true;
                }
            };
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = params.y;
                        initialTouchY = event.getRawY();
                        handler.postDelayed(mLongPressed, 1000);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!hasMoved && !isLongPressing) {
                            mainControl.run();
                        }
                        else if (isLongPressing) {
                            for (int i = 0; i < controls.size() - 1; i++) {
                                if (eventIsInView(event, controls.get(i).getImageView())) {
                                    controls.get(i).run();
                                }
                                startAnimationForView(controls.get(i).getImageView(), i, controls.size() - 1, 0);
                            }
                            isLongPressing = false;
                        }
                        handler.removeCallbacks(mLongPressed);
                        hasMoved = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (!isLongPressing) {
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            hasMoved = hasMoved || (Math.abs(event.getRawY() - initialTouchY) > 10);
                            handler.removeCallbacks(mLongPressed);
                            handler.postDelayed(mLongPressed, 1000);
                            return true;
                        }
                }
                return false;
            }

            private boolean eventIsInView(MotionEvent motionEvent, View view) {
                Rect rect = new Rect();
                view.getHitRect(rect);
                return rect.contains((int)motionEvent.getX()+dp(25),(int)motionEvent.getY()+dp(25));
            }
        });
    }

    private void startAnimationForView(View view, int i, int tot, float radius) {
        double angle = Math.PI/2 - Math.PI/tot/2 - (i*Math.PI/tot);
        PropertyValuesHolder x = PropertyValuesHolder.ofFloat("translationX", dp(radius*(float)Math.cos(angle)));
        PropertyValuesHolder y = PropertyValuesHolder.ofFloat("translationY", -dp(radius*(float)Math.sin(angle)));
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, x, y);
        animator.start();
    }

    private List<Control> createControls() throws IOException, XmlPullParserException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Control> controls = new ArrayList<>();
        XmlResourceParser parser = getResources().getXml(R.xml.controls);
        parser.next();
        parser.next();
        parser.require(XmlPullParser.START_TAG, null, "main");
        Control mainControl = createControl(parser, 60, 60);
        parser.nextTag();
        while(parser.getName().equals("control")) {
            parser.require(XmlPullParser.START_TAG, null, "control");

            controls.add(createControl(parser, 20, 20));

            parser.nextTag();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, null, "main");
        //we add the mainControl last so that it renders on top of the other controls
        controls.add(mainControl);

        return controls;
    }

    private Control createControl(XmlPullParser parser, float width, float height) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String classname = parser.getAttributeValue(null, "classname");
        String drawable = parser.getAttributeValue(null, "drawable");

        Class<?> clazz = Class.forName(classname);
        Constructor ctor = clazz.getConstructor(Context.class, ImageView.class);
        return (Control)ctor.newInstance(this, createImageView(drawable, width, height));
    }

    private ImageView createImageView(String name, float width, float height) {
        ImageView iv = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp(width), dp(height));
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        iv.setLayoutParams(params);
        iv.setImageResource(getResources().getIdentifier(name, "drawable", getPackageName()));
        iv.setPadding(dp(-25), 0, 0, 0);
        return iv;
    }

    private int dp(float value) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,getResources().getDisplayMetrics());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}
