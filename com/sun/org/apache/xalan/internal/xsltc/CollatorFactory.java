package com.sun.org.apache.xalan.internal.xsltc;

import java.util.Locale;
import java.text.Collator;

public interface CollatorFactory
{
    Collator getCollator(final String p0, final String p1);
    
    Collator getCollator(final Locale p0);
}
