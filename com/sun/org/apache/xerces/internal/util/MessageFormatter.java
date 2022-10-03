package com.sun.org.apache.xerces.internal.util;

import java.util.MissingResourceException;
import java.util.Locale;

public interface MessageFormatter
{
    String formatMessage(final Locale p0, final String p1, final Object[] p2) throws MissingResourceException;
}
