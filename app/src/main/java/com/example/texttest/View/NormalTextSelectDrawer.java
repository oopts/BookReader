package com.example.texttest.View;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.List;

public class NormalTextSelectDrawer implements ITextSelectDrawer {
    private final Path path = new Path();

    @Override
    public void drawSelectedChar(TxtFileMsg.TxtChar selectedChar, Canvas canvas, Paint paint) {
        if (selectedChar != null) {
            ELogger.log("onPressSelectText","drawSelectedChar");
            path.reset();
            path.moveTo(selectedChar.Left, selectedChar.Top);
            path.lineTo(selectedChar.Right, selectedChar.Top);
            path.lineTo(selectedChar.Right, selectedChar.Bottom);
            path.lineTo(selectedChar.Left, selectedChar.Bottom);
            path.lineTo(selectedChar.Left, selectedChar.Top);
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public void drawSelectedLines(List<ITxtLine> selectedLines, Canvas canvas, Paint paint) {

        for (ITxtLine line : selectedLines) {
             ELogger.log("onPressSelectText3",line.getLineStr());
            if (line.getTxtChars() != null && line.getTxtChars().size() > 0) {

                TxtFileMsg.TxtChar fistChar = line.getTxtChars().get(0);
                TxtFileMsg.TxtChar lastChar = line.getTxtChars().get(line.getTxtChars().size() - 1);

                float fw = fistChar.CharWidth;
                float lw = lastChar.CharWidth;

                RectF rect = new RectF(fistChar.Left, fistChar.Top,
                        lastChar.Right, lastChar.Bottom);

                canvas.drawRoundRect(rect, fw / 2,
                        paint.getTextSize() / 2, paint);

            }
        }
    }


}
