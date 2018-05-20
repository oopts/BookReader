package com.example.texttest.View;


public interface IPageDataPipeline {
    IPage getPageStartFromProgress(int paragraphIndex, int charIndex);
    IPage getPageEndToProgress(int paragraphIndex, int charIndex);

}
