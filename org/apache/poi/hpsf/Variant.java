package org.apache.poi.hpsf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Variant
{
    public static final int VT_EMPTY = 0;
    public static final int VT_NULL = 1;
    public static final int VT_I2 = 2;
    public static final int VT_I4 = 3;
    public static final int VT_R4 = 4;
    public static final int VT_R8 = 5;
    public static final int VT_CY = 6;
    public static final int VT_DATE = 7;
    public static final int VT_BSTR = 8;
    public static final int VT_DISPATCH = 9;
    public static final int VT_ERROR = 10;
    public static final int VT_BOOL = 11;
    public static final int VT_VARIANT = 12;
    public static final int VT_UNKNOWN = 13;
    public static final int VT_DECIMAL = 14;
    public static final int VT_I1 = 16;
    public static final int VT_UI1 = 17;
    public static final int VT_UI2 = 18;
    public static final int VT_UI4 = 19;
    public static final int VT_I8 = 20;
    public static final int VT_UI8 = 21;
    public static final int VT_INT = 22;
    public static final int VT_UINT = 23;
    public static final int VT_VOID = 24;
    public static final int VT_HRESULT = 25;
    public static final int VT_PTR = 26;
    public static final int VT_SAFEARRAY = 27;
    public static final int VT_CARRAY = 28;
    public static final int VT_USERDEFINED = 29;
    public static final int VT_LPSTR = 30;
    public static final int VT_LPWSTR = 31;
    public static final int VT_FILETIME = 64;
    public static final int VT_BLOB = 65;
    public static final int VT_STREAM = 66;
    public static final int VT_STORAGE = 67;
    public static final int VT_STREAMED_OBJECT = 68;
    public static final int VT_STORED_OBJECT = 69;
    public static final int VT_BLOB_OBJECT = 70;
    public static final int VT_CF = 71;
    public static final int VT_CLSID = 72;
    public static final int VT_VERSIONED_STREAM = 73;
    public static final int VT_VECTOR = 4096;
    public static final int VT_ARRAY = 8192;
    public static final int VT_BYREF = 16384;
    public static final int VT_RESERVED = 32768;
    public static final int VT_ILLEGAL = 65535;
    public static final int VT_ILLEGALMASKED = 4095;
    public static final int VT_TYPEMASK = 4095;
    private static final Map<Long, String> numberToName;
    private static final Map<Long, Integer> numberToLength;
    public static final Integer LENGTH_UNKNOWN;
    public static final Integer LENGTH_VARIABLE;
    public static final Integer LENGTH_0;
    public static final Integer LENGTH_2;
    public static final Integer LENGTH_4;
    public static final Integer LENGTH_8;
    private static final Object[][] NUMBER_TO_NAME_LIST;
    
    public static String getVariantName(final long variantType) {
        long vt = variantType;
        String name = "";
        if ((vt & 0x1000L) != 0x0L) {
            name = "Vector of ";
            vt -= 4096L;
        }
        else if ((vt & 0x2000L) != 0x0L) {
            name = "Array of ";
            vt -= 8192L;
        }
        else if ((vt & 0x4000L) != 0x0L) {
            name = "ByRef of ";
            vt -= 16384L;
        }
        name += Variant.numberToName.get(vt);
        return name.isEmpty() ? "unknown variant type" : name;
    }
    
    public static int getVariantLength(final long variantType) {
        final Integer length = Variant.numberToLength.get(variantType);
        return (length != null) ? length : Variant.LENGTH_UNKNOWN;
    }
    
    static {
        LENGTH_UNKNOWN = -2;
        LENGTH_VARIABLE = -1;
        LENGTH_0 = 0;
        LENGTH_2 = 2;
        LENGTH_4 = 4;
        LENGTH_8 = 8;
        NUMBER_TO_NAME_LIST = new Object[][] { { 0L, "VT_EMPTY", Variant.LENGTH_0 }, { 1L, "VT_NULL", Variant.LENGTH_UNKNOWN }, { 2L, "VT_I2", Variant.LENGTH_2 }, { 3L, "VT_I4", Variant.LENGTH_4 }, { 4L, "VT_R4", Variant.LENGTH_4 }, { 5L, "VT_R8", Variant.LENGTH_8 }, { 6L, "VT_CY", Variant.LENGTH_UNKNOWN }, { 7L, "VT_DATE", Variant.LENGTH_UNKNOWN }, { 8L, "VT_BSTR", Variant.LENGTH_UNKNOWN }, { 9L, "VT_DISPATCH", Variant.LENGTH_UNKNOWN }, { 10L, "VT_ERROR", Variant.LENGTH_UNKNOWN }, { 11L, "VT_BOOL", Variant.LENGTH_UNKNOWN }, { 12L, "VT_VARIANT", Variant.LENGTH_UNKNOWN }, { 13L, "VT_UNKNOWN", Variant.LENGTH_UNKNOWN }, { 14L, "VT_DECIMAL", Variant.LENGTH_UNKNOWN }, { 16L, "VT_I1", Variant.LENGTH_UNKNOWN }, { 17L, "VT_UI1", Variant.LENGTH_UNKNOWN }, { 18L, "VT_UI2", Variant.LENGTH_UNKNOWN }, { 19L, "VT_UI4", Variant.LENGTH_UNKNOWN }, { 20L, "VT_I8", Variant.LENGTH_UNKNOWN }, { 21L, "VT_UI8", Variant.LENGTH_UNKNOWN }, { 22L, "VT_INT", Variant.LENGTH_UNKNOWN }, { 23L, "VT_UINT", Variant.LENGTH_UNKNOWN }, { 24L, "VT_VOID", Variant.LENGTH_UNKNOWN }, { 25L, "VT_HRESULT", Variant.LENGTH_UNKNOWN }, { 26L, "VT_PTR", Variant.LENGTH_UNKNOWN }, { 27L, "VT_SAFEARRAY", Variant.LENGTH_UNKNOWN }, { 28L, "VT_CARRAY", Variant.LENGTH_UNKNOWN }, { 29L, "VT_USERDEFINED", Variant.LENGTH_UNKNOWN }, { 30L, "VT_LPSTR", Variant.LENGTH_VARIABLE }, { 31L, "VT_LPWSTR", Variant.LENGTH_UNKNOWN }, { 64L, "VT_FILETIME", Variant.LENGTH_8 }, { 65L, "VT_BLOB", Variant.LENGTH_UNKNOWN }, { 66L, "VT_STREAM", Variant.LENGTH_UNKNOWN }, { 67L, "VT_STORAGE", Variant.LENGTH_UNKNOWN }, { 68L, "VT_STREAMED_OBJECT", Variant.LENGTH_UNKNOWN }, { 69L, "VT_STORED_OBJECT", Variant.LENGTH_UNKNOWN }, { 70L, "VT_BLOB_OBJECT", Variant.LENGTH_UNKNOWN }, { 71L, "VT_CF", Variant.LENGTH_UNKNOWN }, { 72L, "VT_CLSID", Variant.LENGTH_UNKNOWN } };
        final Map<Long, String> number2Name = new HashMap<Long, String>(Variant.NUMBER_TO_NAME_LIST.length, 1.0f);
        final Map<Long, Integer> number2Len = new HashMap<Long, Integer>(Variant.NUMBER_TO_NAME_LIST.length, 1.0f);
        for (final Object[] nn : Variant.NUMBER_TO_NAME_LIST) {
            number2Name.put((Long)nn[0], (String)nn[1]);
            number2Len.put((Long)nn[0], (Integer)nn[2]);
        }
        numberToName = Collections.unmodifiableMap((Map<? extends Long, ? extends String>)number2Name);
        numberToLength = Collections.unmodifiableMap((Map<? extends Long, ? extends Integer>)number2Len);
    }
}
