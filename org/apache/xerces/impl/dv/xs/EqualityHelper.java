package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.xs.ShortList;

public class EqualityHelper
{
    private EqualityHelper() {
    }
    
    public static boolean isEqual(final Object o, final Object o2, final short n, final short n2, final ShortList list, final ShortList list2, final short n3) {
        if (n3 == 4) {
            return isEqual11(o, o2, n, n2, list, list2);
        }
        return isEqual(o, o2, n, n2, list, list2);
    }
    
    public static boolean isEqual(final ValidatedInfo validatedInfo, final ValidatedInfo validatedInfo2, final short n) {
        if (n == 4) {
            return isEqual11(validatedInfo.actualValue, validatedInfo2.actualValue, validatedInfo.actualValueType, validatedInfo2.actualValueType, validatedInfo.itemValueTypes, validatedInfo2.itemValueTypes);
        }
        return isEqual(validatedInfo.actualValue, validatedInfo2.actualValue, validatedInfo.actualValueType, validatedInfo2.actualValueType, validatedInfo.itemValueTypes, validatedInfo2.itemValueTypes);
    }
    
    private static boolean isEqual(final Object o, final Object o2, final short n, final short n2, final ShortList list, final ShortList list2) {
        return isTypeComparable(n, n2, list, list2) && (o == o2 || (o != null && o2 != null && o.equals(o2)));
    }
    
    private static boolean isTypeComparable(final short n, final short n2, final ShortList list, final ShortList list2) {
        final short convertToPrimitiveKind = convertToPrimitiveKind(n);
        final short convertToPrimitiveKind2 = convertToPrimitiveKind(n2);
        if (convertToPrimitiveKind == convertToPrimitiveKind2) {
            return convertToPrimitiveKind != 44 || isListTypeComparable(list, list2);
        }
        return (convertToPrimitiveKind == 1 && convertToPrimitiveKind2 == 2) || (convertToPrimitiveKind == 2 && convertToPrimitiveKind2 == 1);
    }
    
    private static boolean isEqual11(Object value, Object value2, final short n, final short n2, final ShortList list, final ShortList list2) {
        if (!isType11Comparable(n, n2, list, list2)) {
            return false;
        }
        if (value == value2) {
            return true;
        }
        if (value == null || value2 == null) {
            return false;
        }
        if (value instanceof ListDV.ListData) {
            if (!(value2 instanceof ListDV.ListData)) {
                final ListDV.ListData listData = (ListDV.ListData)value;
                if (listData.getLength() != 1) {
                    return false;
                }
                value = listData.get(0);
            }
        }
        else if (value2 instanceof ListDV.ListData) {
            final ListDV.ListData listData2 = (ListDV.ListData)value2;
            if (listData2.getLength() != 1) {
                return false;
            }
            value2 = listData2.get(0);
        }
        return value.equals(value2);
    }
    
    private static boolean isType11Comparable(final short n, final short n2, final ShortList list, final ShortList list2) {
        final short convertToPrimitiveKind = convertToPrimitiveKind(n);
        final short convertToPrimitiveKind2 = convertToPrimitiveKind(n2);
        if (convertToPrimitiveKind == convertToPrimitiveKind2) {
            return convertToPrimitiveKind != 44 || isListTypeComparable(list, list2);
        }
        if (convertToPrimitiveKind == 44) {
            return list != null && list.getLength() == 1 && isType11Comparable(list.item(0), convertToPrimitiveKind2, null, null);
        }
        if (convertToPrimitiveKind2 == 44) {
            return list2 != null && list2.getLength() == 1 && isType11Comparable(convertToPrimitiveKind, list2.item(0), null, null);
        }
        return (convertToPrimitiveKind == 1 && convertToPrimitiveKind2 == 2) || (convertToPrimitiveKind == 2 && convertToPrimitiveKind2 == 1);
    }
    
    private static short convertToPrimitiveKind(final short n) {
        if (n <= 20) {
            return n;
        }
        if (n <= 29) {
            return 2;
        }
        if (n <= 42) {
            return 4;
        }
        if (n == 44 || n == 43) {
            return 44;
        }
        return n;
    }
    
    private static boolean isListTypeComparable(final ShortList list, final ShortList list2) {
        final int n = (list != null) ? list.getLength() : 0;
        if (n != ((list2 != null) ? list2.getLength() : 0)) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            final short convertToPrimitiveKind = convertToPrimitiveKind(list.item(i));
            final short convertToPrimitiveKind2 = convertToPrimitiveKind(list2.item(i));
            if (convertToPrimitiveKind != convertToPrimitiveKind2 && (convertToPrimitiveKind != 1 || convertToPrimitiveKind2 != 2) && (convertToPrimitiveKind != 2 || convertToPrimitiveKind2 != 1)) {
                return false;
            }
        }
        return true;
    }
}
