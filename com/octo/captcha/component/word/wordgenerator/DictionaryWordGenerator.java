package com.octo.captcha.component.word.wordgenerator;

import com.octo.captcha.component.word.DefaultSizeSortedWordList;
import com.octo.captcha.component.word.SizeSortedWordList;
import com.octo.captcha.CaptchaException;
import java.util.HashMap;
import com.octo.captcha.component.word.DictionaryReader;
import java.util.Locale;

public class DictionaryWordGenerator implements WordGenerator
{
    private Locale defaultLocale;
    private DictionaryReader factory;
    private HashMap localizedwords;
    
    public DictionaryWordGenerator(final DictionaryReader factory) {
        this.localizedwords = new HashMap();
        this.factory = factory;
        this.defaultLocale = this.factory.getWordList().getLocale();
        this.localizedwords.put(this.defaultLocale, this.factory.getWordList());
    }
    
    public final String getWord(final Integer n) {
        return this.getWord(n, this.defaultLocale);
    }
    
    public String getWord(final Integer n, final Locale locale) {
        final String nextWord = this.getWordList(locale).getNextWord(n);
        if (nextWord == null) {
            throw new CaptchaException("No word of length : " + n + " exists in dictionnary! please " + "update your dictionary or your range!");
        }
        return nextWord;
    }
    
    final SizeSortedWordList getWordList(final Locale locale) {
        SizeSortedWordList wordList;
        if (this.localizedwords.containsKey(locale)) {
            wordList = this.localizedwords.get(locale);
        }
        else {
            wordList = this.factory.getWordList(locale);
            this.localizedwords.put(locale, wordList);
        }
        return wordList;
    }
}
