package com.example.texttest.View;

import android.graphics.Color;

public class NumChar extends TxtFileMsg.TxtChar {

    public NumChar(char aChar) {
        super(aChar);
    }
    private final int DefaultTextColor = Color.parseColor("#45a1cd");
    @Override
    public int getTextColor() {
        return DefaultTextColor;
    }

    @Override
    public int getCharType() {
        return Char_Num;
    }
}
