package com.example.texttest.View;


public interface ICursor<T> {
    int getCount();
    void moveToPosition(int var1);

    void moveToFirst();

    void moveToLast();

    void moveToNext();

    void moveToPrevious();

    boolean isFirst();

    boolean isLast();

    boolean isBeforeFirst();

    boolean isAfterLast();

    T Pre();

    T Next();

    T Current();
}
