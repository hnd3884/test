package org.apache.xerces.impl.xpath;

import org.apache.xerces.xs.ShortList;
import org.apache.xerces.impl.dv.xs.TypeValidator;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;

class DataMatcher
{
    public static boolean compareActualValues(final Object o, final Object o2, final int n, final XSSimpleTypeDecl xsSimpleTypeDecl) {
        final TypeValidator typeValidator = xsSimpleTypeDecl.getTypeValidator();
        if (xsSimpleTypeDecl.getOrdered() == 0) {
            if (n == 0) {
                return xsSimpleTypeDecl.isEqual(o, o2);
            }
            return n == 1 && !xsSimpleTypeDecl.isEqual(o, o2);
        }
        else {
            switch (n) {
                case 0: {
                    return typeValidator.compare(o, o2) == 0;
                }
                case 1: {
                    return typeValidator.compare(o, o2) != 0;
                }
                case 3: {
                    return typeValidator.compare(o, o2) > 0;
                }
                case 5: {
                    return typeValidator.compare(o, o2) >= 0;
                }
                case 2: {
                    return typeValidator.compare(o, o2) < 0;
                }
                case 4: {
                    return typeValidator.compare(o, o2) <= 0;
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    public static boolean isComparable(final short n, final short n2, final ShortList list, final ShortList list2) {
        final short convertToPrimitiveKind = convertToPrimitiveKind(n);
        final short convertToPrimitiveKind2 = convertToPrimitiveKind(n2);
        if (convertToPrimitiveKind != convertToPrimitiveKind2) {
            return (convertToPrimitiveKind == 1 && convertToPrimitiveKind2 == 2) || (convertToPrimitiveKind == 2 && convertToPrimitiveKind2 == 1);
        }
        if (convertToPrimitiveKind == 44 || convertToPrimitiveKind == 43) {
            final int n3 = (list != null) ? list.getLength() : 0;
            if (n3 != ((list2 != null) ? list2.getLength() : 0)) {
                return false;
            }
            for (int i = 0; i < n3; ++i) {
                final short convertToPrimitiveKind3 = convertToPrimitiveKind(list.item(i));
                final short convertToPrimitiveKind4 = convertToPrimitiveKind(list2.item(i));
                if (convertToPrimitiveKind3 != convertToPrimitiveKind4 && (convertToPrimitiveKind3 != 1 || convertToPrimitiveKind4 != 2) && (convertToPrimitiveKind3 != 2 || convertToPrimitiveKind4 != 1)) {
                    return false;
                }
            }
        }
        return true;
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
        return n;
    }
}
