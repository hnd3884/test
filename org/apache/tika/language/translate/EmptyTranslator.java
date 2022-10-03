package org.apache.tika.language.translate;

public class EmptyTranslator implements Translator
{
    @Override
    public String translate(final String text, final String sourceLanguage, final String targetLanguage) {
        return null;
    }
    
    @Override
    public String translate(final String text, final String targetLanguage) {
        return null;
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
}
