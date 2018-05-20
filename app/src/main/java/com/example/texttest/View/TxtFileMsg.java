package com.example.texttest.View;

import android.graphics.Color;



public class TxtFileMsg {
    public String FilePath;
    public String FileName;
    public String HexName;
    public long FileSize;
    public int  CurrentParagraphIndex;
    public int  CurrentCharIndex;
    public int PreParagraphIndex =-1;
    public int PreCharIndex = -1;
    public String FileCode;

    public static class TxtChar {
        public static final int Char_text = 0x01;
        public static final int Char_Num = 0x02;
        public static final int Char_En = 0x03;

        public char Char;
        public int ParagraphIndex;
        public float CharWidth = 0;
        public int CharIndex;
        public int TextColor = Color.BLACK;
        public int PositionX;
        public int PositionY;
        public int Left;
        public int Right;
        public int Bottom;
        public int Top;

        public TxtChar(char aChar) {
            Char = aChar;
        }

        public int getTextColor() {
            return TextColor;
        }

        public int getCharType() {
            return Char_text;
        }

        public char getValue() {
            return Char;
        }

        public String getValueStr() {
            return String.valueOf(getValue());
        }

        @Override
        public String toString() {
            return "TxtChar{" +
                    "Char=" + Char +
                    ", ParagraphIndex=" + ParagraphIndex +
                    ", CharWidth=" + CharWidth +
                    ", CharIndex=" + CharIndex +
                    ", TextColor=" + TextColor +
                    ", PositionX=" + PositionX +
                    ", PositionY=" + PositionY +
                    ", Left=" + Left +
                    ", Right=" + Right +
                    ", Bottom=" + Bottom +
                    ", Top=" + Top +
                    '}';
        }

        @Override
        public boolean equals(Object obj) {
            TxtChar to = (TxtChar) obj;
            if (to != null) {
                return ParagraphIndex == to.ParagraphIndex && CharIndex == to.CharIndex && Char == to.Char;
            }
            return false;
        }
    }
}
