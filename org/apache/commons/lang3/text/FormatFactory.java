package org.apache.commons.lang3.text;

import java.text.Format;
import java.util.Locale;

@Deprecated
public interface FormatFactory
{
    Format getFormat(final String p0, final String p1, final Locale p2);
}
