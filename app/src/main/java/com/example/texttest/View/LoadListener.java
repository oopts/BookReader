package com.example.texttest.View;

import com.example.texttest.View.TxtMsg;

public interface LoadListener {
    void onSuccess();
    void onFail(TxtMsg txtMsg);
}
