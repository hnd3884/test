package com.octo.captcha.component.word;

import java.util.StringTokenizer;
import java.util.ResourceBundle;
import java.util.Locale;

public class FileDictionary implements DictionaryReader
{
    private String myBundle;
    
    public FileDictionary(final String myBundle) {
        this.myBundle = myBundle;
    }
    
    public SizeSortedWordList getWordList() {
        return this.generateWordList(Locale.getDefault(), ResourceBundle.getBundle(this.myBundle));
    }
    
    public SizeSortedWordList getWordList(final Locale locale) {
        return this.generateWordList(locale, ResourceBundle.getBundle(this.myBundle, locale));
    }
    
    protected SizeSortedWordList generateWordList(final Locale locale, final ResourceBundle resourceBundle) {
        final DefaultSizeSortedWordList list = new DefaultSizeSortedWordList(locale);
        final StringTokenizer stringTokenizer = new StringTokenizer(resourceBundle.getString("words"), ";");
        for (int countTokens = stringTokenizer.countTokens(), i = 0; i < countTokens; ++i) {
            list.addWord(stringTokenizer.nextToken());
        }
        return list;
    }
}
