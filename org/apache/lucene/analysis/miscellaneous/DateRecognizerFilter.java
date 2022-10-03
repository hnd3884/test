package org.apache.lucene.analysis.miscellaneous;

import java.text.ParseException;
import java.util.Locale;
import org.apache.lucene.analysis.TokenStream;
import java.text.DateFormat;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;

public class DateRecognizerFilter extends FilteringTokenFilter
{
    public static final String DATE_TYPE = "date";
    private final CharTermAttribute termAtt;
    private final DateFormat dateFormat;
    
    public DateRecognizerFilter(final TokenStream input) {
        this(input, null);
    }
    
    public DateRecognizerFilter(final TokenStream input, final DateFormat dateFormat) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.dateFormat = ((dateFormat != null) ? dateFormat : DateFormat.getDateInstance(2, Locale.ENGLISH));
    }
    
    public boolean accept() {
        try {
            this.dateFormat.parse(this.termAtt.toString());
            return true;
        }
        catch (final ParseException e) {
            return false;
        }
    }
}
