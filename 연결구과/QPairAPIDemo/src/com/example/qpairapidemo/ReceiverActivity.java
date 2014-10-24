/*
 * Copyright © 2013 LG Electronics
 */

package com.example.qpairapidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReceiverActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText("Received intent:\n\n");
        addContentView(tv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Intent i = getIntent();
        //get intent data from peerIntent
        if ("broadcast".equals(i.getStringExtra("delivered_through"))) {
            tv.append("==================\n");
            tv.append("SENT FOR BROADCAST\n");
            tv.append(String.valueOf(i.getLongExtra("passed_time", -1))+"\n");
            tv.append("==================\n\n\n");
            i = i.getParcelableExtra("original_intent");
        } else if ("service".equals(i.getStringExtra("delivered_through"))) {
            tv.append("================\n");
            tv.append("SENT FOR SERVICE\n");
            tv.append("================\n\n\n");
            i = i.getParcelableExtra("original_intent");
        } else {
            tv.append("=================\n");
            tv.append("SENT FOR ACTIVITY\n");
            tv.append("=================\n\n\n");
        }

        tv.append(stringFromIntent(i));
    }

    private CharSequence stringFromIntent(Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("action    = ").append(intent.getAction()).append("\n");
        sb.append("component = ").append(intent.getComponent()).append("\n");
        sb.append("data      = ").append(intent.getDataString()).append("\n");
        sb.append("flags     = ").append(intent.getFlags()).append("\n");
        sb.append("package   = ").append(intent.getPackage()).append("\n");
        sb.append("type      = ").append(intent.getType()).append("\n");

        sb.append("categories:\n");
        if (null != intent.getCategories()) {
            for (String c: intent.getCategories()) sb.append("\t").append(c).append("\n");
        } else sb.append("\tnone\n");

        sb.append("extras:\n");
        for (String k: intent.getExtras().keySet()) {
            sb.append("\t").append(k).append("\t= ");
            Object v = intent.getExtras().get(k);
            sb.append(stringFromObjectOrArray(v)).append("\n");

        }
        return sb.toString();
    }

    private String stringFromObjectOrArray(Object v) {
        if (v.getClass().isArray()) {
            try {
                // http://stackoverflow.com/a/8466263
                Class clazz = Object[].class.isAssignableFrom(v.getClass())
                        ? Object[].class : v.getClass();
                Method method = Arrays.class.getMethod("toString", new Class[] { clazz } );
                return (String)method.invoke(null, v);
            } catch (NoSuchMethodException ignored) {
            } catch (IllegalAccessException ignored) {
            } catch (InvocationTargetException ignored) {
            }
        }
        return String.valueOf(v);
    }
}
