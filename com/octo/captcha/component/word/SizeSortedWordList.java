package com.octo.captcha.component.word;

import java.util.Locale;

public interface SizeSortedWordList
{
    Locale getLocale();
    
    void addWord(final String p0);
    
    Integer getMinWord();
    
    Integer getMaxWord();
    
    String getNextWord(final Integer p0);
}
