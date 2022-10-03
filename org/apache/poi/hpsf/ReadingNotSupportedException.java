package org.apache.poi.hpsf;

public class ReadingNotSupportedException extends UnsupportedVariantTypeException
{
    public ReadingNotSupportedException(final long variantType, final Object value) {
        super(variantType, value);
    }
}
