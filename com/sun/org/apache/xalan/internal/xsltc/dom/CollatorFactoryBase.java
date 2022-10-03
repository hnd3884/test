package com.sun.org.apache.xalan.internal.xsltc.dom;

import java.text.Collator;
import java.util.Locale;
import com.sun.org.apache.xalan.internal.xsltc.CollatorFactory;

public class CollatorFactoryBase implements CollatorFactory
{
    public static final Locale DEFAULT_LOCALE;
    public static final Collator DEFAULT_COLLATOR;
    
    @Override
    public Collator getCollator(final String lang, final String country) {
        return Collator.getInstance(new Locale(lang, country));
    }
    
    @Override
    public Collator getCollator(final Locale locale) {
        if (locale == CollatorFactoryBase.DEFAULT_LOCALE) {
            return CollatorFactoryBase.DEFAULT_COLLATOR;
        }
        return Collator.getInstance(locale);
    }
    
    static {
        DEFAULT_LOCALE = Locale.getDefault();
        DEFAULT_COLLATOR = Collator.getInstance();
    }
}
