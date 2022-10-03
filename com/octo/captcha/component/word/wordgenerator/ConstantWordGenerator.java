package com.octo.captcha.component.word.wordgenerator;

import java.util.Locale;
import com.octo.captcha.CaptchaException;

public class ConstantWordGenerator implements WordGenerator
{
    String constantString;
    
    public ConstantWordGenerator(final String constantString) {
        this.constantString = constantString;
        if (constantString == null || constantString.length() == 0) {
            throw new CaptchaException("ConstantWordGenerator must be built with a non empty string");
        }
    }
    
    @Override
    public String getWord(final Integer length) {
        final StringBuilder toCut = new StringBuilder(this.constantString);
        while (toCut.length() < length) {
            toCut.append(this.constantString);
        }
        return toCut.substring(0, length);
    }
    
    @Override
    public String getWord(final Integer length, final Locale locale) {
        return this.getWord(length);
    }
}
