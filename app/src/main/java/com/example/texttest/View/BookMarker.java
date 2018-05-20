package com.example.texttest.View;


public class BookMarker {
    public String Title;
    public int StartParagraphIndex;
    public int StartCharIndex;

    @Override
    public String toString() {
        return "BookMarker{" +
                "Title='" + Title + '\'' +
                ", StartParagraphIndex=" + StartParagraphIndex +
                ", StartCharIndex=" + StartCharIndex +
                '}';
    }
}
