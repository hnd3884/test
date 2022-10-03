package com.octo.captcha.component.word.wordgenerator;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.word.SizeSortedWordList;
import java.util.Locale;
import com.octo.captcha.component.word.DictionaryReader;

public class ComposeDictionaryWordGenerator extends DictionaryWordGenerator
{
    public ComposeDictionaryWordGenerator(final DictionaryReader dictionaryReader) {
        super(dictionaryReader);
    }
    
    @Override
    public String getWord(final Integer n, final Locale locale) {
        final SizeSortedWordList wordList = this.getWordList(locale);
        final int n2 = n / 2;
        String s = null;
        for (int i = n2; i < 50; ++i) {
            s = wordList.getNextWord(new Integer(n2 + i));
            if (s != null) {
                s = s.substring(0, n2);
                break;
            }
        }
        String s2 = null;
        for (int j = n2; j < 50; ++j) {
            s2 = wordList.getNextWord(new Integer(n - n2 + j));
            if (s2 != null) {
                s2 = s2.substring(s2.length() - n + n2, s2.length());
                break;
            }
        }
        return this.checkAndFindSmaller(s, n2, locale) + this.checkAndFindSmaller(s2, n - n2, locale);
    }
    
    private String checkAndFindSmaller(String word, final int n, final Locale locale) {
        if (word == null) {
            if (n <= 1) {
                throw new CaptchaException("No word of length : " + n + " exists in dictionnary! please " + "update your dictionary or your range!");
            }
            word = this.getWord(new Integer(n), locale);
        }
        return word;
    }
}
