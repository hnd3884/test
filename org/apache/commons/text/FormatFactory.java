package org.apache.commons.text;

import java.text.Format;
import java.util.Locale;

public interface FormatFactory
{
    Format getFormat(final String p0, final String p1, final Locale p2);
}
