package com.mauthe.crud;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Ugo on 20/09/2014.
 */
public class utils {


    private static void applyFontTypeFace(ViewGroup group, Typeface font) {

        int count = group.getChildCount();

        View v;

        for(int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if(v instanceof TextView || v instanceof Button || v instanceof EditText ) {
                ((TextView) v).setTypeface(font);

            }
            else if(v instanceof ViewGroup)
                applyFontTypeFace((ViewGroup)v, font);
        }
    }

    public static void changeFontTypeFace(Context context, ViewGroup root) {
        Typeface aFont = Typeface.createFromAsset(context.getAssets(), "valera_round.otf");
        applyFontTypeFace(root, aFont);
    }




}
