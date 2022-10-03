package org.apache.commons.text.similarity;

import java.util.Locale;

public class FuzzyScore
{
    private final Locale locale;
    
    public FuzzyScore(final Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale must not be null");
        }
        this.locale = locale;
    }
    
    public Integer fuzzyScore(final CharSequence term, final CharSequence query) {
        if (term == null || query == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        final String termLowerCase = term.toString().toLowerCase(this.locale);
        final String queryLowerCase = query.toString().toLowerCase(this.locale);
        int score = 0;
        int termIndex = 0;
        int previousMatchingCharacterIndex = Integer.MIN_VALUE;
        for (int queryIndex = 0; queryIndex < queryLowerCase.length(); ++queryIndex) {
            final char queryChar = queryLowerCase.charAt(queryIndex);
            for (boolean termCharacterMatchFound = false; termIndex < termLowerCase.length() && !termCharacterMatchFound; ++termIndex) {
                final char termChar = termLowerCase.charAt(termIndex);
                if (queryChar == termChar) {
                    ++score;
                    if (previousMatchingCharacterIndex + 1 == termIndex) {
                        score += 2;
                    }
                    previousMatchingCharacterIndex = termIndex;
                    termCharacterMatchFound = true;
                }
            }
        }
        return score;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
}
