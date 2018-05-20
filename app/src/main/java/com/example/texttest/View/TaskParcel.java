package com.example.texttest.View;

public interface TaskParcel {
    void onNextTask(TaskParcel parcel, TxtReaderContext readerContext);
    void onBack(TxtMsg msg);
}
