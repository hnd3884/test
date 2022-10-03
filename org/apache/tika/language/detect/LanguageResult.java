package org.apache.tika.language.detect;

import java.util.Locale;

public class LanguageResult
{
    public static final LanguageResult NULL;
    private final String language;
    private final LanguageConfidence confidence;
    private final float rawScore;
    
    public LanguageResult(final String language, final LanguageConfidence confidence, final float rawScore) {
        this.language = language;
        this.confidence = confidence;
        this.rawScore = rawScore;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public float getRawScore() {
        return this.rawScore;
    }
    
    public LanguageConfidence getConfidence() {
        return this.confidence;
    }
    
    public boolean isReasonablyCertain() {
        return this.confidence == LanguageConfidence.HIGH;
    }
    
    public boolean isUnknown() {
        return this.confidence == LanguageConfidence.NONE;
    }
    
    public boolean isLanguage(final String language) {
        final String[] targetLanguage = language.split("\\-");
        final String[] resultLanguage = this.language.split("\\-");
        for (int minLength = Math.min(targetLanguage.length, resultLanguage.length), i = 0; i < minLength; ++i) {
            if (!targetLanguage[i].equalsIgnoreCase(resultLanguage[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format(Locale.US, "%s: %s (%f)", this.language, this.confidence, this.rawScore);
    }
    
    static {
        NULL = new LanguageResult("", LanguageConfidence.NONE, 0.0f);
    }
}
