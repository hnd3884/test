package com.octo.captcha.component.word.wordgenerator;

import java.util.Locale;

public class DummyWordGenerator implements WordGenerator
{
    private String word;
    
    public DummyWordGenerator(final String s) {
        this.word = "JCAPTCHA";
        this.word = ((s == null || "".equals(s)) ? this.word : s);
    }
    
    public String getWord(final Integer n) {
        final int n2 = n % this.word.length();
        String substring = "";
        final int n3 = (n - n2) / this.word.length();
        if (n2 > 0) {
            substring = this.word.substring(0, n2);
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n3; ++i) {
            sb.append(this.word);
        }
        sb.append(substring);
        return sb.toString();
    }
    
    public String getWord(final Integer n, final Locale locale) {
        return this.getWord(n);
    }
}
