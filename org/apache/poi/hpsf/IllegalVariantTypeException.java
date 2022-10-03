package org.apache.poi.hpsf;

import org.apache.poi.util.HexDump;

public class IllegalVariantTypeException extends VariantTypeException
{
    public IllegalVariantTypeException(final long variantType, final Object value, final String msg) {
        super(variantType, value, msg);
    }
    
    public IllegalVariantTypeException(final long variantType, final Object value) {
        this(variantType, value, "The variant type " + variantType + " (" + Variant.getVariantName(variantType) + ", " + HexDump.toHex(variantType) + ") is illegal in this context.");
    }
}