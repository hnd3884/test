package org.bouncycastle.i18n;

import java.util.TimeZone;
import java.util.Locale;
import java.io.UnsupportedEncodingException;

public class LocaleString extends LocalizedMessage
{
    public LocaleString(final String s, final String s2) {
        super(s, s2);
    }
    
    public LocaleString(final String s, final String s2, final String s3) throws NullPointerException, UnsupportedEncodingException {
        super(s, s2, s3);
    }
    
    public LocaleString(final String s, final String s2, final String s3, final Object[] array) throws NullPointerException, UnsupportedEncodingException {
        super(s, s2, s3, array);
    }
    
    public String getLocaleString(final Locale locale) {
        return this.getEntry(null, locale, null);
    }
}
