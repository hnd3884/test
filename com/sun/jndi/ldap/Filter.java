package com.sun.jndi.ldap;

import javax.naming.NamingException;
import java.io.IOException;
import javax.naming.directory.InvalidSearchFilterException;

final class Filter
{
    private static final boolean dbg = false;
    private static int dbgIndent;
    static final int LDAP_FILTER_AND = 160;
    static final int LDAP_FILTER_OR = 161;
    static final int LDAP_FILTER_NOT = 162;
    static final int LDAP_FILTER_EQUALITY = 163;
    static final int LDAP_FILTER_SUBSTRINGS = 164;
    static final int LDAP_FILTER_GE = 165;
    static final int LDAP_FILTER_LE = 166;
    static final int LDAP_FILTER_PRESENT = 135;
    static final int LDAP_FILTER_APPROX = 168;
    static final int LDAP_FILTER_EXT = 169;
    static final int LDAP_FILTER_EXT_RULE = 129;
    static final int LDAP_FILTER_EXT_TYPE = 130;
    static final int LDAP_FILTER_EXT_VAL = 131;
    static final int LDAP_FILTER_EXT_DN = 132;
    static final int LDAP_SUBSTRING_INITIAL = 128;
    static final int LDAP_SUBSTRING_ANY = 129;
    static final int LDAP_SUBSTRING_FINAL = 130;
    
    static void encodeFilterString(final BerEncoder berEncoder, final String s, final boolean b) throws IOException, NamingException {
        if (s == null || s.equals("")) {
            throw new InvalidSearchFilterException("Empty filter");
        }
        byte[] array;
        if (b) {
            array = s.getBytes("UTF8");
        }
        else {
            array = s.getBytes("8859_1");
        }
        encodeFilter(berEncoder, array, 0, array.length);
    }
    
    private static void encodeFilter(final BerEncoder berEncoder, final byte[] array, final int n, final int n2) throws IOException, NamingException {
        if (n2 - n <= 0) {
            throw new InvalidSearchFilterException("Empty filter");
        }
        int n3 = 0;
        final int[] array2 = { n };
        while (array2[0] < n2) {
            Label_0352: {
                switch (array[array2[0]]) {
                    case 40: {
                        final int[] array3 = array2;
                        final int n4 = 0;
                        ++array3[n4];
                        ++n3;
                        switch (array[array2[0]]) {
                            case 38: {
                                encodeComplexFilter(berEncoder, array, 160, array2, n2);
                                --n3;
                                break Label_0352;
                            }
                            case 124: {
                                encodeComplexFilter(berEncoder, array, 161, array2, n2);
                                --n3;
                                break Label_0352;
                            }
                            case 33: {
                                encodeComplexFilter(berEncoder, array, 162, array2, n2);
                                --n3;
                                break Label_0352;
                            }
                            default: {
                                int n5 = 1;
                                int n6 = 0;
                                int n7;
                                for (n7 = array2[0]; n7 < n2 && n5 > 0; ++n7) {
                                    if (n6 == 0) {
                                        if (array[n7] == 40) {
                                            ++n5;
                                        }
                                        else if (array[n7] == 41) {
                                            --n5;
                                        }
                                    }
                                    if (array[n7] == 92 && n6 == 0) {
                                        n6 = 1;
                                    }
                                    else {
                                        n6 = 0;
                                    }
                                    if (n5 > 0) {}
                                }
                                if (n5 != 0) {
                                    throw new InvalidSearchFilterException("Unbalanced parenthesis");
                                }
                                encodeSimpleFilter(berEncoder, array, array2[0], n7);
                                array2[0] = n7 + 1;
                                --n3;
                                break Label_0352;
                            }
                        }
                        break;
                    }
                    case 41: {
                        berEncoder.endSeq();
                        final int[] array4 = array2;
                        final int n8 = 0;
                        ++array4[n8];
                        --n3;
                        break;
                    }
                    case 32: {
                        final int[] array5 = array2;
                        final int n9 = 0;
                        ++array5[n9];
                        break;
                    }
                    default: {
                        encodeSimpleFilter(berEncoder, array, array2[0], n2);
                        array2[0] = n2;
                        break;
                    }
                }
            }
            if (n3 < 0) {
                throw new InvalidSearchFilterException("Unbalanced parenthesis");
            }
        }
        if (n3 != 0) {
            throw new InvalidSearchFilterException("Unbalanced parenthesis");
        }
    }
    
    private static int hexchar2int(final byte b) {
        if (b >= 48 && b <= 57) {
            return b - 48;
        }
        if (b >= 65 && b <= 70) {
            return b - 65 + 10;
        }
        if (b >= 97 && b <= 102) {
            return b - 97 + 10;
        }
        return -1;
    }
    
    static byte[] unescapeFilterValue(final byte[] array, final int n, final int n2) throws NamingException {
        int n3 = 0;
        int n4 = 0;
        final byte[] array2 = new byte[n2 - n];
        int n5 = 0;
        for (int i = n; i < n2; ++i) {
            final byte b = array[i];
            if (n3 != 0) {
                final int hexchar2int;
                if ((hexchar2int = hexchar2int(b)) < 0) {
                    if (n4 == 0) {
                        throw new InvalidSearchFilterException("invalid escape sequence: " + array);
                    }
                    n3 = 0;
                    array2[n5++] = b;
                }
                else if (n4 != 0) {
                    array2[n5] = (byte)(hexchar2int << 4);
                    n4 = 0;
                }
                else {
                    final byte[] array3 = array2;
                    final int n6 = n5++;
                    array3[n6] |= (byte)hexchar2int;
                    n3 = 0;
                }
            }
            else if (b != 92) {
                array2[n5++] = b;
                n3 = 0;
            }
            else {
                n3 = (n4 = 1);
            }
        }
        final byte[] array4 = new byte[n5];
        System.arraycopy(array2, 0, array4, 0, n5);
        return array4;
    }
    
    private static int indexOf(final byte[] array, final char c, final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            if (array[i] == c) {
                return i;
            }
        }
        return -1;
    }
    
    private static int indexOf(final byte[] array, final String s, final int n, final int n2) {
        final int index = indexOf(array, s.charAt(0), n, n2);
        if (index >= 0) {
            for (int i = 1; i < s.length(); ++i) {
                if (array[index + i] != s.charAt(i)) {
                    return -1;
                }
            }
        }
        return index;
    }
    
    private static int findUnescaped(final byte[] array, final char c, int i, final int n) {
        while (i < n) {
            final int index = indexOf(array, c, i, n);
            int n2 = 0;
            for (int n3 = index - 1; n3 >= i && array[n3] == 92; --n3, ++n2) {}
            if (index == i || index == -1 || n2 % 2 == 0) {
                return index;
            }
            i = index + 1;
        }
        return -1;
    }
    
    private static void encodeSimpleFilter(final BerEncoder berEncoder, final byte[] array, final int n, final int n2) throws IOException, NamingException {
        final int index;
        if ((index = indexOf(array, '=', n, n2)) == -1) {
            throw new InvalidSearchFilterException("Missing 'equals'");
        }
        final int n3 = index + 1;
        int n4 = 0;
        int n5 = 0;
        switch (array[index - 1]) {
            case 60: {
                n4 = 166;
                n5 = index - 1;
                break;
            }
            case 62: {
                n4 = 165;
                n5 = index - 1;
                break;
            }
            case 126: {
                n4 = 168;
                n5 = index - 1;
                break;
            }
            case 58: {
                n4 = 169;
                n5 = index - 1;
                break;
            }
            default: {
                n5 = index;
                n4 = 0;
                break;
            }
        }
        int n6 = -1;
        int n7 = -1;
        if ((array[n] >= 48 && array[n] <= 57) || (array[n] >= 65 && array[n] <= 90) || (array[n] >= 97 && array[n] <= 122)) {
            final boolean b = array[n] >= 48 && array[n] <= 57;
            int i = n + 1;
            while (i < n5) {
                if (array[i] == 59) {
                    if (b && array[i - 1] == 46) {
                        throw new InvalidSearchFilterException("invalid attribute description");
                    }
                    n6 = i;
                    break;
                }
                else if (array[i] == 58 && n4 == 169) {
                    if (b && array[i - 1] == 46) {
                        throw new InvalidSearchFilterException("invalid attribute description");
                    }
                    n7 = i;
                    break;
                }
                else {
                    if (b) {
                        if ((array[i] == 46 && array[i - 1] == 46) || (array[i] != 46 && (array[i] < 48 || array[i] > 57))) {
                            throw new InvalidSearchFilterException("invalid attribute description");
                        }
                    }
                    else if (array[i] != 45 && array[i] != 95 && (array[i] < 48 || array[i] > 57) && (array[i] < 65 || array[i] > 90) && (array[i] < 97 || array[i] > 122)) {
                        throw new InvalidSearchFilterException("invalid attribute description");
                    }
                    ++i;
                }
            }
        }
        else {
            if (n4 != 169 || array[n] != 58) {
                throw new InvalidSearchFilterException("invalid attribute description");
            }
            n7 = n;
        }
        if (n6 > 0) {
            for (int j = n6 + 1; j < n5; ++j) {
                if (array[j] == 59) {
                    if (array[j - 1] == 59) {
                        throw new InvalidSearchFilterException("invalid attribute description");
                    }
                }
                else if (array[j] == 58 && n4 == 169) {
                    if (array[j - 1] == 59) {
                        throw new InvalidSearchFilterException("invalid attribute description");
                    }
                    n7 = j;
                    break;
                }
                else if (array[j] != 45 && array[j] != 95 && (array[j] < 48 || array[j] > 57) && (array[j] < 65 || array[j] > 90) && (array[j] < 97 || array[j] > 122)) {
                    throw new InvalidSearchFilterException("invalid attribute description");
                }
            }
        }
        if (n7 > 0) {
            int n8 = 0;
            for (int k = n7 + 1; k < n5; ++k) {
                if (array[k] == 58) {
                    throw new InvalidSearchFilterException("invalid attribute description");
                }
                if ((array[k] < 48 || array[k] > 57) && (array[k] < 65 || array[k] > 90) && (array[k] < 97 || array[k] > 122)) {
                    throw new InvalidSearchFilterException("invalid attribute description");
                }
                final boolean b2 = array[k] >= 48 && array[k] <= 57;
                int l = ++k;
                while (l < n5) {
                    if (array[l] == 58) {
                        if (n8 != 0) {
                            throw new InvalidSearchFilterException("invalid attribute description");
                        }
                        if (b2 && array[l - 1] == 46) {
                            throw new InvalidSearchFilterException("invalid attribute description");
                        }
                        n8 = 1;
                        break;
                    }
                    else {
                        if (b2) {
                            if ((array[l] == 46 && array[l - 1] == 46) || (array[l] != 46 && (array[l] < 48 || array[l] > 57))) {
                                throw new InvalidSearchFilterException("invalid attribute description");
                            }
                        }
                        else if (array[l] != 45 && array[l] != 95 && (array[l] < 48 || array[l] > 57) && (array[l] < 65 || array[l] > 90) && (array[l] < 97 || array[l] > 122)) {
                            throw new InvalidSearchFilterException("invalid attribute description");
                        }
                        ++l;
                        ++k;
                    }
                }
            }
        }
        if (array[n5 - 1] == 46 || array[n5 - 1] == 59 || array[n5 - 1] == 58) {
            throw new InvalidSearchFilterException("invalid attribute description");
        }
        if (n5 == index) {
            if (findUnescaped(array, '*', n3, n2) == -1) {
                n4 = 163;
            }
            else {
                if (array[n3] != 42 || n3 != n2 - 1) {
                    encodeSubstringFilter(berEncoder, array, n, n5, n3, n2);
                    return;
                }
                n4 = 135;
            }
        }
        if (n4 == 135) {
            berEncoder.encodeOctetString(array, n4, n, n5 - n);
        }
        else if (n4 == 169) {
            encodeExtensibleMatch(berEncoder, array, n, n5, n3, n2);
        }
        else {
            berEncoder.beginSeq(n4);
            berEncoder.encodeOctetString(array, 4, n, n5 - n);
            berEncoder.encodeOctetString(unescapeFilterValue(array, n3, n2), 4);
            berEncoder.endSeq();
        }
    }
    
    private static void encodeSubstringFilter(final BerEncoder berEncoder, final byte[] array, final int n, final int n2, final int n3, final int n4) throws IOException, NamingException {
        berEncoder.beginSeq(164);
        berEncoder.encodeOctetString(array, 4, n, n2 - n);
        berEncoder.beginSeq(48);
        int n5;
        int unescaped;
        for (n5 = n3; (unescaped = findUnescaped(array, '*', n5, n4)) != -1; n5 = unescaped + 1) {
            if (n5 == n3) {
                if (n5 < unescaped) {
                    berEncoder.encodeOctetString(unescapeFilterValue(array, n5, unescaped), 128);
                }
            }
            else if (n5 < unescaped) {
                berEncoder.encodeOctetString(unescapeFilterValue(array, n5, unescaped), 129);
            }
        }
        if (n5 < n4) {
            berEncoder.encodeOctetString(unescapeFilterValue(array, n5, n4), 130);
        }
        berEncoder.endSeq();
        berEncoder.endSeq();
    }
    
    private static void encodeComplexFilter(final BerEncoder berEncoder, final byte[] array, final int n, final int[] array2, final int n2) throws IOException, NamingException {
        final int n3 = 0;
        ++array2[n3];
        berEncoder.beginSeq(n);
        final int[] rightParen = findRightParen(array, array2, n2);
        encodeFilterList(berEncoder, array, n, rightParen[0], rightParen[1]);
        berEncoder.endSeq();
    }
    
    private static int[] findRightParen(final byte[] array, final int[] array2, final int n) throws IOException, NamingException {
        int n2 = 1;
        int n3 = 0;
        int n4;
        for (n4 = array2[0]; n4 < n && n2 > 0; ++n4) {
            if (n3 == 0) {
                if (array[n4] == 40) {
                    ++n2;
                }
                else if (array[n4] == 41) {
                    --n2;
                }
            }
            if (array[n4] == 92 && n3 == 0) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n2 > 0) {}
        }
        if (n2 != 0) {
            throw new InvalidSearchFilterException("Unbalanced parenthesis");
        }
        final int[] array3 = { array2[0], n4 };
        array2[0] = n4 + 1;
        return array3;
    }
    
    private static void encodeFilterList(final BerEncoder berEncoder, final byte[] array, final int n, final int n2, final int n3) throws IOException, NamingException {
        final int[] array2 = { 0 };
        int n4 = 0;
        array2[0] = n2;
        while (array2[0] < n3) {
            if (!Character.isSpaceChar((char)array[array2[0]])) {
                if (n == 162 && n4 > 0) {
                    throw new InvalidSearchFilterException("Filter (!) cannot be followed by more than one filters");
                }
                if (array[array2[0]] != 40) {
                    final int[] rightParen = findRightParen(array, array2, n3);
                    final int n5 = rightParen[1] - rightParen[0];
                    final byte[] array3 = new byte[n5 + 2];
                    System.arraycopy(array, rightParen[0], array3, 1, n5);
                    array3[0] = 40;
                    array3[n5 + 1] = 41;
                    encodeFilter(berEncoder, array3, 0, array3.length);
                    ++n4;
                }
            }
            final int[] array4 = array2;
            final int n6 = 0;
            ++array4[n6];
        }
    }
    
    private static void encodeExtensibleMatch(final BerEncoder berEncoder, final byte[] array, final int n, final int n2, final int n3, final int n4) throws IOException, NamingException {
        boolean b = false;
        berEncoder.beginSeq(169);
        final int index;
        if ((index = indexOf(array, ':', n, n2)) >= 0) {
            final int index2;
            if ((index2 = indexOf(array, ":dn", index, n2)) >= 0) {
                b = true;
            }
            final int index3;
            if ((index3 = indexOf(array, ':', index + 1, n2)) >= 0 || index2 == -1) {
                if (index2 == index) {
                    berEncoder.encodeOctetString(array, 129, index3 + 1, n2 - (index3 + 1));
                }
                else if (index2 == index3 && index2 >= 0) {
                    berEncoder.encodeOctetString(array, 129, index + 1, index3 - (index + 1));
                }
                else {
                    berEncoder.encodeOctetString(array, 129, index + 1, n2 - (index + 1));
                }
            }
            if (index > n) {
                berEncoder.encodeOctetString(array, 130, n, index - n);
            }
        }
        else {
            berEncoder.encodeOctetString(array, 130, n, n2 - n);
        }
        berEncoder.encodeOctetString(unescapeFilterValue(array, n3, n4), 131);
        berEncoder.encodeBoolean(b, 132);
        berEncoder.endSeq();
    }
    
    private static void dprint(final String s) {
        dprint(s, new byte[0], 0, 0);
    }
    
    private static void dprint(final String s, final byte[] array) {
        dprint(s, array, 0, array.length);
    }
    
    private static void dprint(final String s, final byte[] array, final int n, final int n2) {
        String string = "  ";
        int dbgIndent = Filter.dbgIndent;
        while (dbgIndent-- > 0) {
            string += "  ";
        }
        System.err.print(string + s);
        for (int i = n; i < n2; ++i) {
            System.err.print((char)array[i]);
        }
        System.err.println();
    }
    
    static {
        Filter.dbgIndent = 0;
    }
}
