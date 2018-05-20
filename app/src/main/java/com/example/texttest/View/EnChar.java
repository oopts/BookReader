package com.example.texttest.View;

import android.graphics.Color;

public class EnChar extends TxtFileMsg.TxtChar {
    public EnChar(char aChar) {
        super(aChar);
    }
    private final int DefaultTextColor = Color.parseColor("#45a1cd");
    @Override
    public int getTextColor() {
        return DefaultTextColor;
    }

    @Override
    public int getCharType() {
        return Char_En;
    }
}

