package org.apache.lucene.analysis.miscellaneous;

import java.text.SimpleDateFormat;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import java.util.Locale;
import java.text.DateFormat;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class DateRecognizerFilterFactory extends TokenFilterFactory
{
    public static final String DATE_PATTERN = "datePattern";
    public static final String LOCALE = "locale";
    private final DateFormat dateFormat;
    private final Locale locale;
    
    public DateRecognizerFilterFactory(final Map<String, String> args) {
        super(args);
        this.locale = this.getLocale(this.get(args, "locale"));
        this.dateFormat = this.getDataFormat(this.get(args, "datePattern"));
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new DateRecognizerFilter(input, this.dateFormat);
    }
    
    private Locale getLocale(final String localeStr) {
        if (localeStr == null) {
            return Locale.ENGLISH;
        }
        return new Locale.Builder().setLanguageTag(localeStr).build();
    }
    
    public DateFormat getDataFormat(final String datePattern) {
        if (datePattern != null) {
            return new SimpleDateFormat(datePattern, this.locale);
        }
        return DateFormat.getDateInstance(2, this.locale);
    }
}
