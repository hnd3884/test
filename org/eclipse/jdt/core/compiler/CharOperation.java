package org.eclipse.jdt.core.compiler;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

public final class CharOperation
{
    public static final char[] NO_CHAR;
    public static final char[][] NO_CHAR_CHAR;
    public static final String[] NO_STRINGS;
    
    static {
        NO_CHAR = new char[0];
        NO_CHAR_CHAR = new char[0][];
        NO_STRINGS = new String[0];
    }
    
    public static final char[] append(char[] array, final char suffix) {
        if (array == null) {
            return new char[] { suffix };
        }
        final int length = array.length;
        System.arraycopy(array, 0, array = new char[length + 1], 0, length);
        array[length] = suffix;
        return array;
    }
    
    public static final char[] append(char[] target, final char[] suffix) {
        if (suffix == null || suffix.length == 0) {
            return target;
        }
        final int targetLength = target.length;
        final int subLength = suffix.length;
        final int newTargetLength = targetLength + subLength;
        if (newTargetLength > targetLength) {
            System.arraycopy(target, 0, target = new char[newTargetLength], 0, targetLength);
        }
        System.arraycopy(suffix, 0, target, targetLength, subLength);
        return target;
    }
    
    public static final char[] append(char[] target, final int index, final char[] array, final int start, final int end) {
        final int targetLength = target.length;
        final int subLength = end - start;
        final int newTargetLength = subLength + index;
        if (newTargetLength > targetLength) {
            System.arraycopy(target, 0, target = new char[newTargetLength * 2], 0, index);
        }
        System.arraycopy(array, start, target, index, subLength);
        return target;
    }
    
    public static final char[][] arrayConcat(final char[][] first, final char[][] second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        final int length1 = first.length;
        final int length2 = second.length;
        final char[][] result = new char[length1 + length2][];
        System.arraycopy(first, 0, result, 0, length1);
        System.arraycopy(second, 0, result, length1, length2);
        return result;
    }
    
    public static final boolean camelCaseMatch(final char[] pattern, final char[] name) {
        return pattern == null || (name != null && camelCaseMatch(pattern, 0, pattern.length, name, 0, name.length, false));
    }
    
    public static final boolean camelCaseMatch(final char[] pattern, final char[] name, final boolean samePartCount) {
        return pattern == null || (name != null && camelCaseMatch(pattern, 0, pattern.length, name, 0, name.length, samePartCount));
    }
    
    public static final boolean camelCaseMatch(final char[] pattern, final int patternStart, final int patternEnd, final char[] name, final int nameStart, final int nameEnd) {
        return camelCaseMatch(pattern, patternStart, patternEnd, name, nameStart, nameEnd, false);
    }
    
    public static final boolean camelCaseMatch(final char[] pattern, final int patternStart, int patternEnd, final char[] name, final int nameStart, int nameEnd, final boolean samePartCount) {
        if (name == null) {
            return false;
        }
        if (pattern == null) {
            return true;
        }
        if (patternEnd < 0) {
            patternEnd = pattern.length;
        }
        if (nameEnd < 0) {
            nameEnd = name.length;
        }
        if (patternEnd <= patternStart) {
            return nameEnd <= nameStart;
        }
        if (nameEnd <= nameStart) {
            return false;
        }
        if (name[nameStart] != pattern[patternStart]) {
            return false;
        }
        int iPattern = patternStart;
        int iName = nameStart;
    Label_0072:
        while (true) {
            ++iPattern;
            ++iName;
            if (iPattern == patternEnd) {
                if (!samePartCount || iName == nameEnd) {
                    return true;
                }
                while (iName != nameEnd) {
                    final char nameChar = name[iName];
                    if (nameChar < '\u0080') {
                        if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[nameChar] & 0x20) != 0x0) {
                            return false;
                        }
                    }
                    else if (!Character.isJavaIdentifierPart(nameChar) || Character.isUpperCase(nameChar)) {
                        return false;
                    }
                    ++iName;
                }
                return true;
            }
            else {
                if (iName == nameEnd) {
                    return false;
                }
                final char patternChar;
                if ((patternChar = pattern[iPattern]) == name[iName]) {
                    continue;
                }
                if (patternChar < '\u0080') {
                    if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[patternChar] & 0x24) == 0x0) {
                        return false;
                    }
                }
                else if (Character.isJavaIdentifierPart(patternChar) && !Character.isUpperCase(patternChar) && !Character.isDigit(patternChar)) {
                    return false;
                }
                while (iName != nameEnd) {
                    final char nameChar = name[iName];
                    if (nameChar < '\u0080') {
                        final int charNature = ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[nameChar];
                        if ((charNature & 0x90) != 0x0) {
                            ++iName;
                        }
                        else if ((charNature & 0x4) != 0x0) {
                            if (patternChar == nameChar) {
                                continue Label_0072;
                            }
                            ++iName;
                        }
                        else {
                            if (patternChar != nameChar) {
                                return false;
                            }
                            continue Label_0072;
                        }
                    }
                    else if (Character.isJavaIdentifierPart(nameChar) && !Character.isUpperCase(nameChar)) {
                        ++iName;
                    }
                    else if (Character.isDigit(nameChar)) {
                        if (patternChar == nameChar) {
                            continue Label_0072;
                        }
                        ++iName;
                    }
                    else {
                        if (patternChar != nameChar) {
                            return false;
                        }
                        continue Label_0072;
                    }
                }
                return false;
            }
        }
    }
    
    public static final boolean substringMatch(final String pattern, final String name) {
        return pattern == null || pattern.length() == 0 || (name != null && checkSubstringMatch(pattern.toCharArray(), name.toCharArray()));
    }
    
    public static final boolean substringMatch(final char[] pattern, final char[] name) {
        return pattern == null || pattern.length == 0 || (name != null && checkSubstringMatch(pattern, name));
    }
    
    private static final boolean checkSubstringMatch(final char[] pattern, final char[] name) {
        for (int nidx = 0; nidx < name.length - pattern.length + 1; ++nidx) {
            int pidx = 0;
            while (pidx < pattern.length) {
                if (Character.toLowerCase(name[nidx + pidx]) != Character.toLowerCase(pattern[pidx])) {
                    if (name[nidx + pidx] == '(' || name[nidx + pidx] == ':') {
                        return false;
                    }
                    break;
                }
                else {
                    if (pidx == pattern.length - 1) {
                        return true;
                    }
                    ++pidx;
                }
            }
        }
        return false;
    }
    
    public static String[] charArrayToStringArray(final char[][] charArrays) {
        if (charArrays == null) {
            return null;
        }
        final int length = charArrays.length;
        if (length == 0) {
            return CharOperation.NO_STRINGS;
        }
        final String[] strings = new String[length];
        for (int i = 0; i < length; ++i) {
            strings[i] = new String(charArrays[i]);
        }
        return strings;
    }
    
    public static String charToString(final char[] charArray) {
        if (charArray == null) {
            return null;
        }
        return new String(charArray);
    }
    
    public static final char[][] arrayConcat(final char[][] first, final char[] second) {
        if (second == null) {
            return first;
        }
        if (first == null) {
            return new char[][] { second };
        }
        final int length = first.length;
        final char[][] result = new char[length + 1][];
        System.arraycopy(first, 0, result, 0, length);
        result[length] = second;
        return result;
    }
    
    public static final int compareTo(final char[] array1, final char[] array2) {
        final int length1 = array1.length;
        final int length2 = array2.length;
        for (int min = Math.min(length1, length2), i = 0; i < min; ++i) {
            if (array1[i] != array2[i]) {
                return array1[i] - array2[i];
            }
        }
        return length1 - length2;
    }
    
    public static final int compareTo(final char[] array1, final char[] array2, final int start, final int end) {
        final int length1 = array1.length;
        final int length2 = array2.length;
        int min = Math.min(length1, length2);
        min = Math.min(min, end);
        for (int i = start; i < min; ++i) {
            if (array1[i] != array2[i]) {
                return array1[i] - array2[i];
            }
        }
        return length1 - length2;
    }
    
    public static final int compareWith(final char[] array, final char[] prefix) {
        final int arrayLength = array.length;
        final int prefixLength = prefix.length;
        int min = Math.min(arrayLength, prefixLength);
        int i = 0;
        while (min-- != 0) {
            final char c1 = array[i];
            final char c2 = prefix[i++];
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        if (prefixLength == i) {
            return 0;
        }
        return -1;
    }
    
    public static final char[] concat(final char[] first, final char[] second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        final int length1 = first.length;
        final int length2 = second.length;
        final char[] result = new char[length1 + length2];
        System.arraycopy(first, 0, result, 0, length1);
        System.arraycopy(second, 0, result, length1, length2);
        return result;
    }
    
    public static final char[] concat(final char[] first, final char[] second, final char[] third) {
        if (first == null) {
            return concat(second, third);
        }
        if (second == null) {
            return concat(first, third);
        }
        if (third == null) {
            return concat(first, second);
        }
        final int length1 = first.length;
        final int length2 = second.length;
        final int length3 = third.length;
        final char[] result = new char[length1 + length2 + length3];
        System.arraycopy(first, 0, result, 0, length1);
        System.arraycopy(second, 0, result, length1, length2);
        System.arraycopy(third, 0, result, length1 + length2, length3);
        return result;
    }
    
    public static final char[] concat(final char[] first, final char[] second, final char separator) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        final int length1 = first.length;
        if (length1 == 0) {
            return second;
        }
        final int length2 = second.length;
        if (length2 == 0) {
            return first;
        }
        final char[] result = new char[length1 + length2 + 1];
        System.arraycopy(first, 0, result, 0, length1);
        result[length1] = separator;
        System.arraycopy(second, 0, result, length1 + 1, length2);
        return result;
    }
    
    public static final char[] concat(final char[] first, final char sep1, final char[] second, final char sep2, final char[] third) {
        if (first == null) {
            return concat(second, third, sep2);
        }
        if (second == null) {
            return concat(first, third, sep1);
        }
        if (third == null) {
            return concat(first, second, sep1);
        }
        final int length1 = first.length;
        final int length2 = second.length;
        final int length3 = third.length;
        final char[] result = new char[length1 + length2 + length3 + 2];
        System.arraycopy(first, 0, result, 0, length1);
        result[length1] = sep1;
        System.arraycopy(second, 0, result, length1 + 1, length2);
        result[length1 + length2 + 1] = sep2;
        System.arraycopy(third, 0, result, length1 + length2 + 2, length3);
        return result;
    }
    
    public static final char[] concatNonEmpty(final char[] first, final char[] second, final char separator) {
        if (first == null || first.length == 0) {
            return second;
        }
        if (second == null || second.length == 0) {
            return first;
        }
        return concat(first, second, separator);
    }
    
    public static final char[] concatNonEmpty(final char[] first, final char sep1, final char[] second, final char sep2, final char[] third) {
        if (first == null || first.length == 0) {
            return concatNonEmpty(second, third, sep2);
        }
        if (second == null || second.length == 0) {
            return concatNonEmpty(first, third, sep1);
        }
        if (third == null || third.length == 0) {
            return concatNonEmpty(first, second, sep1);
        }
        return concat(first, sep1, second, sep2, third);
    }
    
    public static final char[] concat(final char prefix, final char[] array, final char suffix) {
        if (array == null) {
            return new char[] { prefix, suffix };
        }
        final int length = array.length;
        final char[] result = new char[length + 2];
        result[0] = prefix;
        System.arraycopy(array, 0, result, 1, length);
        result[length + 1] = suffix;
        return result;
    }
    
    public static final char[] concatWith(final char[] name, final char[][] array, final char separator) {
        final int nameLength = (name == null) ? 0 : name.length;
        if (nameLength == 0) {
            return concatWith(array, separator);
        }
        final int length = (array == null) ? 0 : array.length;
        if (length == 0) {
            return name;
        }
        int size = nameLength;
        int index = length;
        while (--index >= 0) {
            if (array[index].length > 0) {
                size += array[index].length + 1;
            }
        }
        final char[] result = new char[size];
        index = size;
        for (int i = length - 1; i >= 0; --i) {
            final int subLength = array[i].length;
            if (subLength > 0) {
                index -= subLength;
                System.arraycopy(array[i], 0, result, index, subLength);
                result[--index] = separator;
            }
        }
        System.arraycopy(name, 0, result, 0, nameLength);
        return result;
    }
    
    public static final char[] concatWith(final char[][] array, final char[] name, final char separator) {
        final int nameLength = (name == null) ? 0 : name.length;
        if (nameLength == 0) {
            return concatWith(array, separator);
        }
        final int length = (array == null) ? 0 : array.length;
        if (length == 0) {
            return name;
        }
        int size = nameLength;
        int index = length;
        while (--index >= 0) {
            if (array[index].length > 0) {
                size += array[index].length + 1;
            }
        }
        final char[] result = new char[size];
        index = 0;
        for (int i = 0; i < length; ++i) {
            final int subLength = array[i].length;
            if (subLength > 0) {
                System.arraycopy(array[i], 0, result, index, subLength);
                index += subLength;
                result[index++] = separator;
            }
        }
        System.arraycopy(name, 0, result, index, nameLength);
        return result;
    }
    
    public static final char[] concatWith(final char[][] array, final char separator) {
        int length = (array == null) ? 0 : array.length;
        if (length == 0) {
            return CharOperation.NO_CHAR;
        }
        int size = length - 1;
        int index = length;
        while (--index >= 0) {
            if (array[index].length == 0) {
                --size;
            }
            else {
                size += array[index].length;
            }
        }
        if (size <= 0) {
            return CharOperation.NO_CHAR;
        }
        final char[] result = new char[size];
        index = length;
        while (--index >= 0) {
            length = array[index].length;
            if (length > 0) {
                System.arraycopy(array[index], 0, result, size -= length, length);
                if (--size < 0) {
                    continue;
                }
                result[size] = separator;
            }
        }
        return result;
    }
    
    public static final char[] concatWithAll(final char[][] array, final char separator) {
        int length = (array == null) ? 0 : array.length;
        if (length == 0) {
            return CharOperation.NO_CHAR;
        }
        int size = length - 1;
        int index = length;
        while (--index >= 0) {
            size += array[index].length;
        }
        final char[] result = new char[size];
        index = length;
        while (--index >= 0) {
            length = array[index].length;
            if (length > 0) {
                System.arraycopy(array[index], 0, result, size -= length, length);
            }
            if (--size >= 0) {
                result[size] = separator;
            }
        }
        return result;
    }
    
    public static final boolean contains(final char character, final char[][] array) {
        int i = array.length;
        while (--i >= 0) {
            final char[] subarray = array[i];
            int j = subarray.length;
            while (--j >= 0) {
                if (subarray[j] == character) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static final boolean contains(final char character, final char[] array) {
        int i = array.length;
        while (--i >= 0) {
            if (array[i] == character) {
                return true;
            }
        }
        return false;
    }
    
    public static final boolean contains(final char[] characters, final char[] array) {
        int i = array.length;
        while (--i >= 0) {
            int j = characters.length;
            while (--j >= 0) {
                if (array[i] == characters[j]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static final char[][] deepCopy(final char[][] toCopy) {
        final int toCopyLength = toCopy.length;
        final char[][] result = new char[toCopyLength][];
        for (int i = 0; i < toCopyLength; ++i) {
            final char[] toElement = toCopy[i];
            final int toElementLength = toElement.length;
            final char[] resultElement = new char[toElementLength];
            System.arraycopy(toElement, 0, resultElement, 0, toElementLength);
            result[i] = resultElement;
        }
        return result;
    }
    
    public static final boolean endsWith(final char[] array, final char[] toBeFound) {
        int i = toBeFound.length;
        final int j = array.length - i;
        if (j < 0) {
            return false;
        }
        while (--i >= 0) {
            if (toBeFound[i] != array[i + j]) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean equals(final char[][] first, final char[][] second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        int i = first.length;
        while (--i >= 0) {
            if (!equals(first[i], second[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean equals(final char[][] first, final char[][] second, final boolean isCaseSensitive) {
        if (isCaseSensitive) {
            return equals(first, second);
        }
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        int i = first.length;
        while (--i >= 0) {
            if (!equals(first[i], second[i], false)) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean equals(final char[] first, final char[] second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        int i = first.length;
        while (--i >= 0) {
            if (first[i] != second[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean equals(final char[] first, final char[] second, final int secondStart, final int secondEnd) {
        return equals(first, second, secondStart, secondEnd, true);
    }
    
    public static final boolean equals(final char[] first, final char[] second, final int secondStart, final int secondEnd, final boolean isCaseSensitive) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != secondEnd - secondStart) {
            return false;
        }
        if (isCaseSensitive) {
            int i = first.length;
            while (--i >= 0) {
                if (first[i] != second[i + secondStart]) {
                    return false;
                }
            }
        }
        else {
            int i = first.length;
            while (--i >= 0) {
                if (ScannerHelper.toLowerCase(first[i]) != ScannerHelper.toLowerCase(second[i + secondStart])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static final boolean equals(final char[] first, final char[] second, final boolean isCaseSensitive) {
        if (isCaseSensitive) {
            return equals(first, second);
        }
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        if (first.length != second.length) {
            return false;
        }
        int i = first.length;
        while (--i >= 0) {
            if (ScannerHelper.toLowerCase(first[i]) != ScannerHelper.toLowerCase(second[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean fragmentEquals(final char[] fragment, final char[] name, final int startIndex, final boolean isCaseSensitive) {
        final int max = fragment.length;
        if (name.length < max + startIndex) {
            return false;
        }
        if (isCaseSensitive) {
            int i = max;
            while (--i >= 0) {
                if (fragment[i] != name[i + startIndex]) {
                    return false;
                }
            }
            return true;
        }
        int i = max;
        while (--i >= 0) {
            if (ScannerHelper.toLowerCase(fragment[i]) != ScannerHelper.toLowerCase(name[i + startIndex])) {
                return false;
            }
        }
        return true;
    }
    
    public static final int hashCode(final char[] array) {
        final int hash = Arrays.hashCode(array);
        return hash & Integer.MAX_VALUE;
    }
    
    public static boolean isWhitespace(final char c) {
        return c < '\u0080' && (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x100) != 0x0;
    }
    
    public static final int indexOf(final char toBeFound, final char[] array) {
        return indexOf(toBeFound, array, 0);
    }
    
    public static final int indexOf(final char[] toBeFound, final char[] array, final boolean isCaseSensitive) {
        return indexOf(toBeFound, array, isCaseSensitive, 0);
    }
    
    public static final int indexOf(final char[] toBeFound, final char[] array, final boolean isCaseSensitive, final int start) {
        return indexOf(toBeFound, array, isCaseSensitive, start, array.length);
    }
    
    public static final int indexOf(final char[] toBeFound, final char[] array, final boolean isCaseSensitive, final int start, final int end) {
        final int arrayLength = end;
        final int toBeFoundLength = toBeFound.length;
        if (toBeFoundLength > arrayLength || start < 0) {
            return -1;
        }
        if (toBeFoundLength == 0) {
            return 0;
        }
        if (toBeFoundLength != arrayLength) {
            if (isCaseSensitive) {
            Label_0172:
                for (int i = start, max = arrayLength - toBeFoundLength + 1; i < max; ++i) {
                    if (array[i] == toBeFound[0]) {
                        for (int j = 1; j < toBeFoundLength; ++j) {
                            if (array[i + j] != toBeFound[j]) {
                                continue Label_0172;
                            }
                        }
                        return i;
                    }
                }
            }
            else {
            Label_0258:
                for (int i = start, max = arrayLength - toBeFoundLength + 1; i < max; ++i) {
                    if (ScannerHelper.toLowerCase(array[i]) == ScannerHelper.toLowerCase(toBeFound[0])) {
                        for (int j = 1; j < toBeFoundLength; ++j) {
                            if (ScannerHelper.toLowerCase(array[i + j]) != ScannerHelper.toLowerCase(toBeFound[j])) {
                                continue Label_0258;
                            }
                        }
                        return i;
                    }
                }
            }
            return -1;
        }
        if (isCaseSensitive) {
            for (int i = start; i < arrayLength; ++i) {
                if (array[i] != toBeFound[i]) {
                    return -1;
                }
            }
            return 0;
        }
        for (int i = start; i < arrayLength; ++i) {
            if (ScannerHelper.toLowerCase(array[i]) != ScannerHelper.toLowerCase(toBeFound[i])) {
                return -1;
            }
        }
        return 0;
    }
    
    public static final int indexOf(final char toBeFound, final char[] array, final int start) {
        for (int i = start; i < array.length; ++i) {
            if (toBeFound == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static final int indexOf(final char toBeFound, final char[] array, final int start, final int end) {
        for (int i = start; i < end; ++i) {
            if (toBeFound == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static final int lastIndexOf(final char toBeFound, final char[] array) {
        int i = array.length;
        while (--i >= 0) {
            if (toBeFound == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static final int lastIndexOf(final char toBeFound, final char[] array, final int startIndex) {
        int i = array.length;
        while (--i >= startIndex) {
            if (toBeFound == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static final int lastIndexOf(final char toBeFound, final char[] array, final int startIndex, final int endIndex) {
        int i = endIndex;
        while (--i >= startIndex) {
            if (toBeFound == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static final char[] lastSegment(final char[] array, final char separator) {
        final int pos = lastIndexOf(separator, array);
        if (pos < 0) {
            return array;
        }
        return subarray(array, pos + 1, array.length);
    }
    
    public static final boolean match(final char[] pattern, final char[] name, final boolean isCaseSensitive) {
        return name != null && (pattern == null || match(pattern, 0, pattern.length, name, 0, name.length, isCaseSensitive));
    }
    
    public static final boolean match(final char[] pattern, final int patternStart, int patternEnd, final char[] name, final int nameStart, int nameEnd, final boolean isCaseSensitive) {
        if (name == null) {
            return false;
        }
        if (pattern == null) {
            return true;
        }
        int iPattern = patternStart;
        int iName = nameStart;
        if (patternEnd < 0) {
            patternEnd = pattern.length;
        }
        if (nameEnd < 0) {
            nameEnd = name.length;
        }
        char patternChar = '\0';
        while (iPattern != patternEnd) {
            if ((patternChar = pattern[iPattern]) == '*') {
                int segmentStart;
                if (patternChar == '*') {
                    segmentStart = ++iPattern;
                }
                else {
                    segmentStart = 0;
                }
                int prefixStart = iName;
                while (iName < nameEnd) {
                    if (iPattern == patternEnd) {
                        iPattern = segmentStart;
                        iName = ++prefixStart;
                    }
                    else if ((patternChar = pattern[iPattern]) == '*') {
                        segmentStart = ++iPattern;
                        if (segmentStart == patternEnd) {
                            return true;
                        }
                        prefixStart = iName;
                    }
                    else if ((isCaseSensitive ? name[iName] : ScannerHelper.toLowerCase(name[iName])) != patternChar && patternChar != '?') {
                        iPattern = segmentStart;
                        iName = ++prefixStart;
                    }
                    else {
                        ++iName;
                        ++iPattern;
                    }
                }
                return segmentStart == patternEnd || (iName == nameEnd && iPattern == patternEnd) || (iPattern == patternEnd - 1 && pattern[iPattern] == '*');
            }
            if (iName == nameEnd) {
                return false;
            }
            if (patternChar != (isCaseSensitive ? name[iName] : ScannerHelper.toLowerCase(name[iName])) && patternChar != '?') {
                return false;
            }
            ++iName;
            ++iPattern;
        }
        return iName == nameEnd;
    }
    
    public static final boolean pathMatch(final char[] pattern, final char[] filepath, final boolean isCaseSensitive, final char pathSeparator) {
        if (filepath == null) {
            return false;
        }
        if (pattern == null) {
            return true;
        }
        int pSegmentStart = (pattern[0] == pathSeparator) ? 1 : 0;
        final int pLength = pattern.length;
        int pSegmentEnd = indexOf(pathSeparator, pattern, pSegmentStart + 1);
        if (pSegmentEnd < 0) {
            pSegmentEnd = pLength;
        }
        final boolean freeTrailingDoubleStar = pattern[pLength - 1] == pathSeparator;
        final int fLength = filepath.length;
        int fSegmentStart;
        if (filepath[0] != pathSeparator) {
            fSegmentStart = 0;
        }
        else {
            fSegmentStart = 1;
        }
        if (fSegmentStart != pSegmentStart) {
            return false;
        }
        int fSegmentEnd = indexOf(pathSeparator, filepath, fSegmentStart + 1);
        if (fSegmentEnd < 0) {
            fSegmentEnd = fLength;
        }
        while (pSegmentStart < pLength && (pSegmentEnd != pLength || !freeTrailingDoubleStar) && (pSegmentEnd != pSegmentStart + 2 || pattern[pSegmentStart] != '*' || pattern[pSegmentStart + 1] != '*')) {
            if (fSegmentStart >= fLength) {
                return false;
            }
            if (!match(pattern, pSegmentStart, pSegmentEnd, filepath, fSegmentStart, fSegmentEnd, isCaseSensitive)) {
                return false;
            }
            pSegmentEnd = indexOf(pathSeparator, pattern, pSegmentStart = pSegmentEnd + 1);
            if (pSegmentEnd < 0) {
                pSegmentEnd = pLength;
            }
            fSegmentEnd = indexOf(pathSeparator, filepath, fSegmentStart = fSegmentEnd + 1);
            if (fSegmentEnd >= 0) {
                continue;
            }
            fSegmentEnd = fLength;
        }
        int pSegmentRestart;
        if ((pSegmentStart >= pLength && freeTrailingDoubleStar) || (pSegmentEnd == pSegmentStart + 2 && pattern[pSegmentStart] == '*' && pattern[pSegmentStart + 1] == '*')) {
            pSegmentEnd = indexOf(pathSeparator, pattern, pSegmentStart = pSegmentEnd + 1);
            if (pSegmentEnd < 0) {
                pSegmentEnd = pLength;
            }
            pSegmentRestart = pSegmentStart;
        }
        else {
            if (pSegmentStart >= pLength) {
                return fSegmentStart >= fLength;
            }
            pSegmentRestart = 0;
        }
        int fSegmentRestart = fSegmentStart;
        while (fSegmentStart < fLength) {
            if (pSegmentStart >= pLength) {
                if (freeTrailingDoubleStar) {
                    return true;
                }
                pSegmentEnd = indexOf(pathSeparator, pattern, pSegmentStart = pSegmentRestart);
                if (pSegmentEnd < 0) {
                    pSegmentEnd = pLength;
                }
                fSegmentRestart = indexOf(pathSeparator, filepath, fSegmentRestart + 1);
                if (fSegmentRestart < 0) {
                    fSegmentRestart = fLength;
                }
                else {
                    ++fSegmentRestart;
                }
                fSegmentEnd = indexOf(pathSeparator, filepath, fSegmentStart = fSegmentRestart);
                if (fSegmentEnd >= 0) {
                    continue;
                }
                fSegmentEnd = fLength;
            }
            else if (pSegmentEnd == pSegmentStart + 2 && pattern[pSegmentStart] == '*' && pattern[pSegmentStart + 1] == '*') {
                pSegmentEnd = indexOf(pathSeparator, pattern, pSegmentStart = pSegmentEnd + 1);
                if (pSegmentEnd < 0) {
                    pSegmentEnd = pLength;
                }
                pSegmentRestart = pSegmentStart;
                fSegmentRestart = fSegmentStart;
                if (pSegmentStart >= pLength) {
                    return true;
                }
                continue;
            }
            else if (!match(pattern, pSegmentStart, pSegmentEnd, filepath, fSegmentStart, fSegmentEnd, isCaseSensitive)) {
                pSegmentEnd = indexOf(pathSeparator, pattern, pSegmentStart = pSegmentRestart);
                if (pSegmentEnd < 0) {
                    pSegmentEnd = pLength;
                }
                fSegmentRestart = indexOf(pathSeparator, filepath, fSegmentRestart + 1);
                if (fSegmentRestart < 0) {
                    fSegmentRestart = fLength;
                }
                else {
                    ++fSegmentRestart;
                }
                fSegmentEnd = indexOf(pathSeparator, filepath, fSegmentStart = fSegmentRestart);
                if (fSegmentEnd >= 0) {
                    continue;
                }
                fSegmentEnd = fLength;
            }
            else {
                pSegmentEnd = indexOf(pathSeparator, pattern, pSegmentStart = pSegmentEnd + 1);
                if (pSegmentEnd < 0) {
                    pSegmentEnd = pLength;
                }
                fSegmentEnd = indexOf(pathSeparator, filepath, fSegmentStart = fSegmentEnd + 1);
                if (fSegmentEnd >= 0) {
                    continue;
                }
                fSegmentEnd = fLength;
            }
        }
        return pSegmentRestart >= pSegmentEnd || (fSegmentStart >= fLength && pSegmentStart >= pLength) || (pSegmentStart == pLength - 2 && pattern[pSegmentStart] == '*' && pattern[pSegmentStart + 1] == '*') || (pSegmentStart == pLength && freeTrailingDoubleStar);
    }
    
    public static final int occurencesOf(final char toBeFound, final char[] array) {
        int count = 0;
        for (int i = 0; i < array.length; ++i) {
            if (toBeFound == array[i]) {
                ++count;
            }
        }
        return count;
    }
    
    public static final int occurencesOf(final char toBeFound, final char[] array, final int start) {
        int count = 0;
        for (int i = start; i < array.length; ++i) {
            if (toBeFound == array[i]) {
                ++count;
            }
        }
        return count;
    }
    
    public static final int parseInt(final char[] array, final int start, final int length) throws NumberFormatException {
        if (length != 1) {
            return Integer.parseInt(new String(array, start, length));
        }
        final int result = array[start] - '0';
        if (result < 0 || result > 9) {
            throw new NumberFormatException("invalid digit");
        }
        return result;
    }
    
    public static final boolean prefixEquals(final char[] prefix, final char[] name) {
        final int max = prefix.length;
        if (name.length < max) {
            return false;
        }
        int i = max;
        while (--i >= 0) {
            if (prefix[i] != name[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean prefixEquals(final char[] prefix, final char[] name, final boolean isCaseSensitive) {
        return prefixEquals(prefix, name, isCaseSensitive, 0);
    }
    
    public static final boolean prefixEquals(final char[] prefix, final char[] name, final boolean isCaseSensitive, final int startIndex) {
        final int max = prefix.length;
        if (name.length - startIndex < max) {
            return false;
        }
        if (isCaseSensitive) {
            int i = max;
            while (--i >= 0) {
                if (prefix[i] != name[startIndex + i]) {
                    return false;
                }
            }
            return true;
        }
        int i = max;
        while (--i >= 0) {
            if (ScannerHelper.toLowerCase(prefix[i]) != ScannerHelper.toLowerCase(name[startIndex + i])) {
                return false;
            }
        }
        return true;
    }
    
    public static final char[] remove(final char[] array, final char toBeRemoved) {
        if (array == null) {
            return null;
        }
        final int length = array.length;
        if (length == 0) {
            return array;
        }
        char[] result = null;
        int count = 0;
        for (int i = 0; i < length; ++i) {
            final char c = array[i];
            if (c == toBeRemoved) {
                if (result == null) {
                    result = new char[length];
                    System.arraycopy(array, 0, result, 0, i);
                    count = i;
                }
            }
            else if (result != null) {
                result[count++] = c;
            }
        }
        if (result == null) {
            return array;
        }
        System.arraycopy(result, 0, result = new char[count], 0, count);
        return result;
    }
    
    public static final void replace(final char[] array, final char toBeReplaced, final char replacementChar) {
        if (toBeReplaced != replacementChar) {
            for (int i = 0, max = array.length; i < max; ++i) {
                if (array[i] == toBeReplaced) {
                    array[i] = replacementChar;
                }
            }
        }
    }
    
    public static final void replace(final char[] array, final char[] toBeReplaced, final char replacementChar) {
        replace(array, toBeReplaced, replacementChar, 0, array.length);
    }
    
    public static final void replace(final char[] array, final char[] toBeReplaced, final char replacementChar, final int start, final int end) {
        int i = end;
        while (--i >= start) {
            int j = toBeReplaced.length;
            while (--j >= 0) {
                if (array[i] == toBeReplaced[j]) {
                    array[i] = replacementChar;
                }
            }
        }
    }
    
    public static final char[] replace(final char[] array, final char[] toBeReplaced, final char[] replacementChars) {
        final int max = array.length;
        final int replacedLength = toBeReplaced.length;
        final int replacementLength = replacementChars.length;
        int[] starts = new int[5];
        int occurrenceCount = 0;
        if (!equals(toBeReplaced, replacementChars)) {
            int i = 0;
            while (i < max) {
                final int index = indexOf(toBeReplaced, array, true, i);
                if (index == -1) {
                    ++i;
                }
                else {
                    if (occurrenceCount == starts.length) {
                        System.arraycopy(starts, 0, starts = new int[occurrenceCount * 2], 0, occurrenceCount);
                    }
                    starts[occurrenceCount++] = index;
                    i = index + replacedLength;
                }
            }
        }
        if (occurrenceCount == 0) {
            return array;
        }
        final char[] result = new char[max + occurrenceCount * (replacementLength - replacedLength)];
        int inStart = 0;
        int outStart = 0;
        for (int j = 0; j < occurrenceCount; ++j) {
            final int offset = starts[j] - inStart;
            System.arraycopy(array, inStart, result, outStart, offset);
            inStart += offset;
            outStart += offset;
            System.arraycopy(replacementChars, 0, result, outStart, replacementLength);
            inStart += replacedLength;
            outStart += replacementLength;
        }
        System.arraycopy(array, inStart, result, outStart, max - inStart);
        return result;
    }
    
    public static final char[] replaceOnCopy(final char[] array, final char toBeReplaced, final char replacementChar) {
        char[] result = null;
        for (int i = 0, length = array.length; i < length; ++i) {
            final char c = array[i];
            if (c == toBeReplaced) {
                if (result == null) {
                    result = new char[length];
                    System.arraycopy(array, 0, result, 0, i);
                }
                result[i] = replacementChar;
            }
            else if (result != null) {
                result[i] = c;
            }
        }
        if (result == null) {
            return array;
        }
        return result;
    }
    
    public static final char[][] splitAndTrimOn(final char divider, final char[] array) {
        final int length = (array == null) ? 0 : array.length;
        if (length == 0) {
            return CharOperation.NO_CHAR_CHAR;
        }
        int wordCount = 1;
        for (int i = 0; i < length; ++i) {
            if (array[i] == divider) {
                ++wordCount;
            }
        }
        final char[][] split = new char[wordCount][];
        int last = 0;
        int currentWord = 0;
        for (int j = 0; j < length; ++j) {
            if (array[j] == divider) {
                int start = last;
                int end = j - 1;
                while (start < j) {
                    if (array[start] != ' ') {
                        break;
                    }
                    ++start;
                }
                while (end > start && array[end] == ' ') {
                    --end;
                }
                split[currentWord] = new char[end - start + 1];
                System.arraycopy(array, start, split[currentWord++], 0, end - start + 1);
                last = j + 1;
            }
        }
        int start2 = last;
        int end2 = length - 1;
        while (start2 < length) {
            if (array[start2] != ' ') {
                break;
            }
            ++start2;
        }
        while (end2 > start2 && array[end2] == ' ') {
            --end2;
        }
        split[currentWord] = new char[end2 - start2 + 1];
        System.arraycopy(array, start2, split[currentWord++], 0, end2 - start2 + 1);
        return split;
    }
    
    public static final char[][] splitOn(final char divider, final char[] array) {
        final int length = (array == null) ? 0 : array.length;
        if (length == 0) {
            return CharOperation.NO_CHAR_CHAR;
        }
        int wordCount = 1;
        for (int i = 0; i < length; ++i) {
            if (array[i] == divider) {
                ++wordCount;
            }
        }
        final char[][] split = new char[wordCount][];
        int last = 0;
        int currentWord = 0;
        for (int j = 0; j < length; ++j) {
            if (array[j] == divider) {
                split[currentWord] = new char[j - last];
                System.arraycopy(array, last, split[currentWord++], 0, j - last);
                last = j + 1;
            }
        }
        System.arraycopy(array, last, split[currentWord] = new char[length - last], 0, length - last);
        return split;
    }
    
    public static final char[][] splitOn(final char divider, final char[] array, final int start, final int end) {
        final int length = (array == null) ? 0 : array.length;
        if (length == 0 || start > end) {
            return CharOperation.NO_CHAR_CHAR;
        }
        int wordCount = 1;
        for (int i = start; i < end; ++i) {
            if (array[i] == divider) {
                ++wordCount;
            }
        }
        final char[][] split = new char[wordCount][];
        int last = start;
        int currentWord = 0;
        for (int j = start; j < end; ++j) {
            if (array[j] == divider) {
                split[currentWord] = new char[j - last];
                System.arraycopy(array, last, split[currentWord++], 0, j - last);
                last = j + 1;
            }
        }
        System.arraycopy(array, last, split[currentWord] = new char[end - last], 0, end - last);
        return split;
    }
    
    public static final char[][] splitOnWithEnclosures(final char divider, final char openEncl, final char closeEncl, final char[] array, final int start, final int end) {
        final int length = (array == null) ? 0 : array.length;
        if (length == 0 || start > end) {
            return CharOperation.NO_CHAR_CHAR;
        }
        int wordCount = 1;
        int enclCount = 0;
        for (int i = start; i < end; ++i) {
            if (array[i] == openEncl) {
                ++enclCount;
            }
            else if (array[i] == divider) {
                ++wordCount;
            }
        }
        if (enclCount == 0) {
            return splitOn(divider, array, start, end);
        }
        int nesting = 0;
        if (openEncl == divider || closeEncl == divider) {
            return CharOperation.NO_CHAR_CHAR;
        }
        final int[][] splitOffsets = new int[wordCount][2];
        int last = start;
        int currentWord = 0;
        int prevOffset = start;
        for (int j = start; j < end; ++j) {
            if (array[j] == openEncl) {
                ++nesting;
            }
            else if (array[j] == closeEncl) {
                if (nesting > 0) {
                    --nesting;
                }
            }
            else if (array[j] == divider && nesting == 0) {
                splitOffsets[currentWord][0] = prevOffset;
                final int[] array2 = splitOffsets[currentWord++];
                final int n = 1;
                final int n2 = j;
                array2[n] = n2;
                last = n2;
                prevOffset = last + 1;
            }
        }
        if (last < end - 1) {
            splitOffsets[currentWord][0] = prevOffset;
            splitOffsets[currentWord++][1] = end;
        }
        final char[][] split = new char[currentWord][];
        for (int k = 0; k < currentWord; ++k) {
            final int sStart = splitOffsets[k][0];
            final int sEnd = splitOffsets[k][1];
            final int size = sEnd - sStart;
            System.arraycopy(array, sStart, split[k] = new char[size], 0, size);
        }
        return split;
    }
    
    public static final char[][] subarray(final char[][] array, final int start, int end) {
        if (end == -1) {
            end = array.length;
        }
        if (start > end) {
            return null;
        }
        if (start < 0) {
            return null;
        }
        if (end > array.length) {
            return null;
        }
        final char[][] result = new char[end - start][];
        System.arraycopy(array, start, result, 0, end - start);
        return result;
    }
    
    public static final char[] subarray(final char[] array, final int start, int end) {
        if (end == -1) {
            end = array.length;
        }
        if (start > end) {
            return null;
        }
        if (start < 0) {
            return null;
        }
        if (end > array.length) {
            return null;
        }
        final char[] result = new char[end - start];
        System.arraycopy(array, start, result, 0, end - start);
        return result;
    }
    
    public static final char[] toLowerCase(final char[] chars) {
        if (chars == null) {
            return null;
        }
        final int length = chars.length;
        char[] lowerChars = null;
        for (int i = 0; i < length; ++i) {
            final char c = chars[i];
            final char lc = ScannerHelper.toLowerCase(c);
            if (c != lc || lowerChars != null) {
                if (lowerChars == null) {
                    System.arraycopy(chars, 0, lowerChars = new char[length], 0, i);
                }
                lowerChars[i] = lc;
            }
        }
        return (lowerChars == null) ? chars : lowerChars;
    }
    
    public static final char[] toUpperCase(final char[] chars) {
        if (chars == null) {
            return null;
        }
        final int length = chars.length;
        char[] upperChars = null;
        for (int i = 0; i < length; ++i) {
            final char c = chars[i];
            final char lc = ScannerHelper.toUpperCase(c);
            if (c != lc || upperChars != null) {
                if (upperChars == null) {
                    System.arraycopy(chars, 0, upperChars = new char[length], 0, i);
                }
                upperChars[i] = lc;
            }
        }
        return (upperChars == null) ? chars : upperChars;
    }
    
    public static final char[] trim(final char[] chars) {
        if (chars == null) {
            return null;
        }
        int start = 0;
        final int length = chars.length;
        int end = length - 1;
        while (start < length) {
            if (chars[start] != ' ') {
                break;
            }
            ++start;
        }
        while (end > start && chars[end] == ' ') {
            --end;
        }
        if (start != 0 || end != length - 1) {
            return subarray(chars, start, end + 1);
        }
        return chars;
    }
    
    public static final String toString(final char[][] array) {
        final char[] result = concatWith(array, '.');
        return new String(result);
    }
    
    public static final String[] toStrings(final char[][] array) {
        if (array == null) {
            return CharOperation.NO_STRINGS;
        }
        final int length = array.length;
        if (length == 0) {
            return CharOperation.NO_STRINGS;
        }
        final String[] result = new String[length];
        for (int i = 0; i < length; ++i) {
            result[i] = new String(array[i]);
        }
        return result;
    }
}
