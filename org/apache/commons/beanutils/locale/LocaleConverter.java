package org.apache.commons.beanutils.locale;

import org.apache.commons.beanutils.Converter;

public interface LocaleConverter extends Converter
{
     <T> T convert(final Class<T> p0, final Object p1, final String p2);
}
