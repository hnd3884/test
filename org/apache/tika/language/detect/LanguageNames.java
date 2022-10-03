package org.apache.tika.language.detect;

import java.util.Locale;

public class LanguageNames
{
    public static String makeName(final String language, final String script, final String region) {
        final Locale locale = new Locale.Builder().setLanguage(language).setScript(script).setRegion(region).build();
        return locale.toLanguageTag();
    }
    
    public static String normalizeName(final String languageTag) {
        final Locale locale = Locale.forLanguageTag(languageTag);
        return locale.toLanguageTag();
    }
    
    public static boolean isMacroLanguage(final String languageTag) {
        final Locale locale = Locale.forLanguageTag(languageTag);
        return false;
    }
    
    public static boolean hasMacroLanguage(final String languageTag) {
        final Locale locale = Locale.forLanguageTag(languageTag);
        return false;
    }
    
    public static String getMacroLanguage(final String languageTag) {
        return languageTag;
    }
    
    public static boolean equals(final String languageTagA, final String languageTagB) {
        final Locale localeA = Locale.forLanguageTag(languageTagA);
        final Locale localeB = Locale.forLanguageTag(languageTagB);
        return localeA.equals(localeB);
    }
}
