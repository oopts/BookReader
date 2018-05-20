package com.example.texttest.View;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;


public interface ITextSelectDrawer {
    void drawSelectedChar(TxtFileMsg.TxtChar selectedChar, Canvas canvas, Paint paint);

    void drawSelectedLines(List<ITxtLine> selectedLines, Canvas canvas, Paint paint);
}
