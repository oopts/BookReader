package com.example.texttest.View;


import java.util.List;


public interface IPage {
    ITxtLine getLine(int index);

    void addLine(ITxtLine line);

    void addLineTo(ITxtLine line, int index);

    void setLines(List<ITxtLine> lines);

    TxtFileMsg.TxtChar getFirstChar();

    TxtFileMsg.TxtChar getLastChar();

    ITxtLine getFirstLine();

    ITxtLine getLastLine();

    List<ITxtLine> getLines();

    ICursor<ITxtLine> getLineCursor();

    int getLineNum();

    int CurrentIndex();

    Boolean HasData();

}
