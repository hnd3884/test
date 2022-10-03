package org.bouncycastle.i18n;

import java.util.TimeZone;
import java.util.Locale;
import java.io.UnsupportedEncodingException;

public class TextBundle extends LocalizedMessage
{
    public static final String TEXT_ENTRY = "text";
    
    public TextBundle(final String s, final String s2) throws NullPointerException {
        super(s, s2);
    }
    
    public TextBundle(final String s, final String s2, final String s3) throws NullPointerException, UnsupportedEncodingException {
        super(s, s2, s3);
    }
    
    public TextBundle(final String s, final String s2, final Object[] array) throws NullPointerException {
        super(s, s2, array);
    }
    
    public TextBundle(final String s, final String s2, final String s3, final Object[] array) throws NullPointerException, UnsupportedEncodingException {
        super(s, s2, s3, array);
    }
    
    public String getText(final Locale locale, final TimeZone timeZone) throws MissingEntryException {
        return this.getEntry("text", locale, timeZone);
    }
    
    public String getText(final Locale locale) throws MissingEntryException {
        return this.getEntry("text", locale, TimeZone.getDefault());
    }
}
