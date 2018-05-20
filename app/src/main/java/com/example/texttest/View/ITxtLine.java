package com.example.texttest.View;


import java.util.List;

public interface ITxtLine {
    List<TxtFileMsg.TxtChar> getTxtChars();

    int getCharNum();

    TxtFileMsg.TxtChar getFirstChar();

    TxtFileMsg.TxtChar getLastChar();

    TxtFileMsg.TxtChar getChar(int index);

    ICursor<TxtFileMsg.TxtChar> getCharCursor();

    String getLineStr();

    char[] getLineChar();

    Boolean HasData();

    void addChar(TxtFileMsg.TxtChar txtChar);

    void Clear();

    int CurrentIndex();
}
