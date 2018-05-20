package com.example.texttest.View;


import com.example.texttest.View.TxtMsg;

public interface ILoadListener {
    void onSuccess();

    void onFail(TxtMsg txtMsg);

    void onMessage(String message);
}
