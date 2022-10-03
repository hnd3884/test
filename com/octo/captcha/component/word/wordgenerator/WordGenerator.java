package com.octo.captcha.component.word.wordgenerator;

import java.util.Locale;

public interface WordGenerator
{
    String getWord(final Integer p0);
    
    String getWord(final Integer p0, final Locale p1);
}
