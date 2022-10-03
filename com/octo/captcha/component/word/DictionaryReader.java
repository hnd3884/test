package com.octo.captcha.component.word;

import java.util.Locale;

public interface DictionaryReader
{
    SizeSortedWordList getWordList();
    
    SizeSortedWordList getWordList(final Locale p0);
}
