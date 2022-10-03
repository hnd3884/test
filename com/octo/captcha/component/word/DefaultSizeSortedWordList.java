package com.octo.captcha.component.word;

import java.util.ArrayList;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Locale;
import java.util.TreeMap;

public class DefaultSizeSortedWordList implements SizeSortedWordList
{
    private TreeMap sortedWords;
    private Locale locale;
    private Random myRandom;
    
    public DefaultSizeSortedWordList(final Locale locale) {
        this.sortedWords = new TreeMap();
        this.myRandom = new SecureRandom();
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public void addWord(final String s) {
        final Integer n = new Integer(s.length());
        if (this.sortedWords.containsKey(n)) {
            final ArrayList list = this.sortedWords.get(n);
            list.add(s);
            this.sortedWords.put(n, list);
        }
        else {
            final ArrayList list2 = new ArrayList();
            list2.add(s);
            this.sortedWords.put(n, list2);
        }
    }
    
    public Integer getMinWord() {
        return this.sortedWords.firstKey();
    }
    
    public Integer getMaxWord() {
        return this.sortedWords.lastKey();
    }
    
    public String getNextWord(final Integer n) {
        if (this.sortedWords.containsKey(n)) {
            final ArrayList list = this.sortedWords.get(n);
            return (String)list.get(this.myRandom.nextInt(list.size()));
        }
        return null;
    }
}
