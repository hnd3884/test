package java.text.spi;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class BreakIteratorProvider extends LocaleServiceProvider
{
    protected BreakIteratorProvider() {
    }
    
    public abstract BreakIterator getWordInstance(final Locale p0);
    
    public abstract BreakIterator getLineInstance(final Locale p0);
    
    public abstract BreakIterator getCharacterInstance(final Locale p0);
    
    public abstract BreakIterator getSentenceInstance(final Locale p0);
}
