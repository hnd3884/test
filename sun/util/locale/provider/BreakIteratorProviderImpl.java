package sun.util.locale.provider;

import java.util.MissingResourceException;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.Set;
import java.text.spi.BreakIteratorProvider;

public class BreakIteratorProviderImpl extends BreakIteratorProvider implements AvailableLanguageTags
{
    private static final int CHARACTER_INDEX = 0;
    private static final int WORD_INDEX = 1;
    private static final int LINE_INDEX = 2;
    private static final int SENTENCE_INDEX = 3;
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    
    public BreakIteratorProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
        this.type = type;
        this.langtags = langtags;
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        return LocaleProviderAdapter.toLocaleArray(this.langtags);
    }
    
    @Override
    public BreakIterator getWordInstance(final Locale locale) {
        return this.getBreakInstance(locale, 1, "WordData", "WordDictionary");
    }
    
    @Override
    public BreakIterator getLineInstance(final Locale locale) {
        return this.getBreakInstance(locale, 2, "LineData", "LineDictionary");
    }
    
    @Override
    public BreakIterator getCharacterInstance(final Locale locale) {
        return this.getBreakInstance(locale, 0, "CharacterData", "CharacterDictionary");
    }
    
    @Override
    public BreakIterator getSentenceInstance(final Locale locale) {
        return this.getBreakInstance(locale, 3, "SentenceData", "SentenceDictionary");
    }
    
    private BreakIterator getBreakInstance(final Locale locale, final int n, final String s, final String s2) {
        if (locale == null) {
            throw new NullPointerException();
        }
        final LocaleResources localeResources = LocaleProviderAdapter.forJRE().getLocaleResources(locale);
        final String[] array = (String[])localeResources.getBreakIteratorInfo("BreakIteratorClasses");
        final String s3 = (String)localeResources.getBreakIteratorInfo(s);
        try {
            final String s4 = array[n];
            switch (s4) {
                case "RuleBasedBreakIterator": {
                    return new RuleBasedBreakIterator(s3);
                }
                case "DictionaryBasedBreakIterator": {
                    return new DictionaryBasedBreakIterator(s3, (String)localeResources.getBreakIteratorInfo(s2));
                }
                default: {
                    throw new IllegalArgumentException("Invalid break iterator class \"" + array[n] + "\"");
                }
            }
        }
        catch (final IOException | MissingResourceException | IllegalArgumentException ex) {
            throw new InternalError(((Throwable)ex).toString(), (Throwable)ex);
        }
    }
    
    @Override
    public Set<String> getAvailableLanguageTags() {
        return this.langtags;
    }
    
    @Override
    public boolean isSupportedLocale(final Locale locale) {
        return LocaleProviderAdapter.isSupportedLocale(locale, this.type, this.langtags);
    }
}
