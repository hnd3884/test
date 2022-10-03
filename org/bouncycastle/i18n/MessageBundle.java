package org.bouncycastle.i18n;

import java.util.TimeZone;
import java.util.Locale;
import java.io.UnsupportedEncodingException;

public class MessageBundle extends TextBundle
{
    public static final String TITLE_ENTRY = "title";
    
    public MessageBundle(final String s, final String s2) throws NullPointerException {
        super(s, s2);
    }
    
    public MessageBundle(final String s, final String s2, final String s3) throws NullPointerException, UnsupportedEncodingException {
        super(s, s2, s3);
    }
    
    public MessageBundle(final String s, final String s2, final Object[] array) throws NullPointerException {
        super(s, s2, array);
    }
    
    public MessageBundle(final String s, final String s2, final String s3, final Object[] array) throws NullPointerException, UnsupportedEncodingException {
        super(s, s2, s3, array);
    }
    
    public String getTitle(final Locale locale, final TimeZone timeZone) throws MissingEntryException {
        return this.getEntry("title", locale, timeZone);
    }
    
    public String getTitle(final Locale locale) throws MissingEntryException {
        return this.getEntry("title", locale, TimeZone.getDefault());
    }
}
