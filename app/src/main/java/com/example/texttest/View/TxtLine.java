package com.example.texttest.View;


import java.util.ArrayList;
import java.util.List;


public class TxtLine implements ITxtLine, ICursor<TxtFileMsg.TxtChar> {
    private int CurrentIndex;
    private List<TxtFileMsg.TxtChar> chars = null;

    public TxtLine() {
    }


    @Override
    public int CurrentIndex() {
        return CurrentIndex;
    }

    @Override
    public Boolean HasData() {
        return getCount() != 0;
    }

    @Override
    public List<TxtFileMsg.TxtChar> getTxtChars() {
        return chars;
    }

    @Override
    public void addChar(TxtFileMsg.TxtChar txtChar) {
        if (chars == null) {
            chars = new ArrayList<>();
        }
        chars.add(txtChar);

    }


    @Override
    public int getCount() {
        return chars == null ? 0 : chars.size();
    }

    @Override
    public void moveToPosition(int index) {
        if (index < 0 || index >= getCount()) {
            throw new ArrayIndexOutOfBoundsException(" moveToPosition index OutOfBoundsException");
        }
        CurrentIndex = index;
    }

    @Override
    public int getCharNum() {
        return getCount();
    }

    @Override
    public void moveToFirst() {
        CurrentIndex = 0;
        Current();
    }

    @Override
    public TxtFileMsg.TxtChar getFirstChar() {
        CurrentIndex = 0;
        return Current();
    }

    @Override
    public void moveToLast() {
        CurrentIndex = getCount() - 1;
        if (CurrentIndex < 0) {
            CurrentIndex = 0;
        }
        Current();
    }

    @Override
    public TxtFileMsg.TxtChar getLastChar() {
        CurrentIndex = getCount() - 1;
        if (CurrentIndex < 0) {
            CurrentIndex = 0;
        }
        return Current();
    }

    @Override
    public void moveToNext() {
        CurrentIndex++;
        if (CurrentIndex >= getCount()) {
            CurrentIndex = getCount() - 1;
        }
        if (CurrentIndex < 0) {
            CurrentIndex = 0;
        }
        moveToPosition(CurrentIndex);
    }

    @Override
    public TxtFileMsg.TxtChar getChar(int index) {
        if (index < 0 || index >= getCount()) {
            throw new ArrayIndexOutOfBoundsException(" moveToPosition index OutOfBoundsException");
        }
        return chars == null ? null : chars.get(index);
    }

    @Override
    public void moveToPrevious() {
        CurrentIndex--;
        if (CurrentIndex < 0) {
            CurrentIndex = 0;
        }
        moveToPosition(CurrentIndex);
    }

    @Override
    public boolean isFirst() {
        return CurrentIndex == 0;
    }

    @Override
    public ICursor<TxtFileMsg.TxtChar> getCharCursor() {
        return this;
    }

    @Override
    public boolean isLast() {
        return CurrentIndex == getCount() - 1;
    }

    @Override
    public String getLineStr() {
        String str = "";
        for (TxtFileMsg.TxtChar txtChar : chars) {
            str = str + txtChar.getValue();
        }
        return str;
    }


    @Override
    public boolean isBeforeFirst() {
        return BeforeFirst;
    }

    @Override
    public char[] getLineChar() {
        return (getLineStr() + "").toCharArray();
    }

    @Override
    public boolean isAfterLast() {
        return AfterLast;
    }

    private Boolean AfterLast = false;
    private Boolean BeforeFirst = false;

    @Override
    public TxtFileMsg.TxtChar Pre() {
        CurrentIndex--;
        if (CurrentIndex < 0) {
            CurrentIndex = 0;
        }
        moveToPosition(CurrentIndex);
        return Current();
    }

    @Override
    public TxtFileMsg.TxtChar Next() {
        CurrentIndex++;
        if (CurrentIndex >= getCount()) {
            CurrentIndex = getCount() - 1;
        }
        if (CurrentIndex < 0) {
            CurrentIndex = 0;
        }
        moveToPosition(CurrentIndex);
        return Current();
    }

    @Override
    public TxtFileMsg.TxtChar Current() {
        if (isLast()) {
            AfterLast = true;
        } else {
            AfterLast = false;
        }
        if (isFirst()) {
            BeforeFirst = true;
        } else {
            BeforeFirst = false;
        }
        return chars == null ? null : getChar(CurrentIndex);
    }

    @Override
    public void Clear() {
        if (chars != null) {
            chars.clear();
        }
    }

    @Override
    public String toString() {
        return "" + getLineStr();
    }
}
