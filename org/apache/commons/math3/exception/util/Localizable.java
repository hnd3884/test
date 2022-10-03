package org.apache.commons.math3.exception.util;

import java.util.Locale;
import java.io.Serializable;

public interface Localizable extends Serializable
{
    String getSourceString();
    
    String getLocalizedString(final Locale p0);
}
