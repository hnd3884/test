package com.octo.captcha.component.word.wordgenerator;

import java.util.Locale;
import java.security.SecureRandom;
import java.util.Random;

public class RandomWordGenerator implements WordGenerator
{
    private char[] possiblesChars;
    private Random myRandom;
    
    public RandomWordGenerator(final String s) {
        this.myRandom = new SecureRandom();
        this.possiblesChars = s.toCharArray();
    }
    
    public String getWord(final Integer n) {
        final StringBuffer sb = new StringBuffer(n);
        for (int i = 0; i < n; ++i) {
            sb.append(this.possiblesChars[this.myRandom.nextInt(this.possiblesChars.length)]);
        }
        return sb.toString();
    }
    
    public String getWord(final Integer n, final Locale locale) {
        return this.getWord(n);
    }
}
