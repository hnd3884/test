package com.sun.org.apache.xml.internal.utils.res;

import java.util.ListResourceBundle;

public abstract class XResourceBundleBase extends ListResourceBundle
{
    public abstract String getMessageKey(final int p0);
    
    public abstract String getWarningKey(final int p0);
}
