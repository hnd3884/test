package org.apache.commons.lang;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class StringUtils
{
    public static final String EMPTY = "";
    private static final int PAD_LIMIT = 8192;
    private static final String[] PADDING;
    
    public static boolean isEmpty(final String str) {
        return str == null || str.length() == 0;
    }
    
    public static boolean isNotEmpty(final String str) {
        return str != null && str.length() > 0;
    }
    
    public static boolean isBlank(final String str) {
        final int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isNotBlank(final String str) {
        final int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    
    public static String clean(final String str) {
        return (str == null) ? "" : str.trim();
    }
    
    public static String trim(final String str) {
        return (str == null) ? null : str.trim();
    }
    
    public static String trimToNull(final String str) {
        final String ts = trim(str);
        return (ts == null || ts.length() == 0) ? null : ts;
    }
    
    public static String trimToEmpty(final String str) {
        return (str == null) ? "" : str.trim();
    }
    
    public static String strip(final String str) {
        return strip(str, null);
    }
    
    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        str = strip(str, null);
        return (str.length() == 0) ? null : str;
    }
    
    public static String stripToEmpty(final String str) {
        return (str == null) ? "" : strip(str, null);
    }
    
    public static String strip(String str, final String stripChars) {
        if (str == null || str.length() == 0) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }
    
    public static String stripStart(final String str, final String stripChars) {
        final int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while (start != strLen && Character.isWhitespace(str.charAt(start))) {
                ++start;
            }
        }
        else {
            if (stripChars.length() == 0) {
                return str;
            }
            while (start != strLen && stripChars.indexOf(str.charAt(start)) != -1) {
                ++start;
            }
        }
        return str.substring(start);
    }
    
    public static String stripEnd(final String str, final String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }
        if (stripChars == null) {
            while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                --end;
            }
        }
        else {
            if (stripChars.length() == 0) {
                return str;
            }
            while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
                --end;
            }
        }
        return str.substring(0, end);
    }
    
    public static String[] stripAll(final String[] strs) {
        return stripAll(strs, null);
    }
    
    public static String[] stripAll(final String[] strs, final String stripChars) {
        final int strsLen;
        if (strs == null || (strsLen = strs.length) == 0) {
            return strs;
        }
        final String[] newArr = new String[strsLen];
        for (int i = 0; i < strsLen; ++i) {
            newArr[i] = strip(strs[i], stripChars);
        }
        return newArr;
    }
    
    public static boolean equals(final String str1, final String str2) {
        return (str1 == null) ? (str2 == null) : str1.equals(str2);
    }
    
    public static boolean equalsIgnoreCase(final String str1, final String str2) {
        return (str1 == null) ? (str2 == null) : str1.equalsIgnoreCase(str2);
    }
    
    public static int indexOf(final String str, final char searchChar) {
        if (str == null || str.length() == 0) {
            return -1;
        }
        return str.indexOf(searchChar);
    }
    
    public static int indexOf(final String str, final char searchChar, final int startPos) {
        if (str == null || str.length() == 0) {
            return -1;
        }
        return str.indexOf(searchChar, startPos);
    }
    
    public static int indexOf(final String str, final String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.indexOf(searchStr);
    }
    
    public static int indexOf(final String str, final String searchStr, final int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        if (searchStr.length() == 0 && startPos >= str.length()) {
            return str.length();
        }
        return str.indexOf(searchStr, startPos);
    }
    
    public static int lastIndexOf(final String str, final char searchChar) {
        if (str == null || str.length() == 0) {
            return -1;
        }
        return str.lastIndexOf(searchChar);
    }
    
    public static int lastIndexOf(final String str, final char searchChar, final int startPos) {
        if (str == null || str.length() == 0) {
            return -1;
        }
        return str.lastIndexOf(searchChar, startPos);
    }
    
    public static int lastIndexOf(final String str, final String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr);
    }
    
    public static int lastIndexOf(final String str, final String searchStr, final int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr, startPos);
    }
    
    public static boolean contains(final String str, final char searchChar) {
        return str != null && str.length() != 0 && str.indexOf(searchChar) >= 0;
    }
    
    public static boolean contains(final String str, final String searchStr) {
        return str != null && searchStr != null && str.indexOf(searchStr) >= 0;
    }
    
    public static int indexOfAny(final String str, final char[] searchChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length == 0) {
            return -1;
        }
        for (int i = 0; i < str.length(); ++i) {
            final char ch = str.charAt(i);
            for (int j = 0; j < searchChars.length; ++j) {
                if (searchChars[j] == ch) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static int indexOfAny(final String str, final String searchChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length() == 0) {
            return -1;
        }
        return indexOfAny(str, searchChars.toCharArray());
    }
    
    public static int indexOfAnyBut(final String str, final char[] searchChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length == 0) {
            return -1;
        }
        int i = 0;
    Label_0024:
        while (i < str.length()) {
            final char ch = str.charAt(i);
            for (int j = 0; j < searchChars.length; ++j) {
                if (searchChars[j] == ch) {
                    ++i;
                    continue Label_0024;
                }
            }
            return i;
        }
        return -1;
    }
    
    public static int indexOfAnyBut(final String str, final String searchChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length() == 0) {
            return -1;
        }
        for (int i = 0; i < str.length(); ++i) {
            if (searchChars.indexOf(str.charAt(i)) < 0) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean containsOnly(final String str, final char[] valid) {
        return valid != null && str != null && (str.length() == 0 || (valid.length != 0 && indexOfAnyBut(str, valid) == -1));
    }
    
    public static boolean containsOnly(final String str, final String validChars) {
        return str != null && validChars != null && containsOnly(str, validChars.toCharArray());
    }
    
    public static boolean containsNone(final String str, final char[] invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        final int strSize = str.length();
        final int validSize = invalidChars.length;
        for (int i = 0; i < strSize; ++i) {
            final char ch = str.charAt(i);
            for (int j = 0; j < validSize; ++j) {
                if (invalidChars[j] == ch) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean containsNone(final String str, final String invalidChars) {
        return str == null || invalidChars == null || containsNone(str, invalidChars.toCharArray());
    }
    
    public static int indexOfAny(final String str, final String[] searchStrs) {
        if (str == null || searchStrs == null) {
            return -1;
        }
        final int sz = searchStrs.length;
        int ret = Integer.MAX_VALUE;
        int tmp = 0;
        for (final String search : searchStrs) {
            if (search != null) {
                tmp = str.indexOf(search);
                if (tmp != -1) {
                    if (tmp < ret) {
                        ret = tmp;
                    }
                }
            }
        }
        return (ret == Integer.MAX_VALUE) ? -1 : ret;
    }
    
    public static int lastIndexOfAny(final String str, final String[] searchStrs) {
        if (str == null || searchStrs == null) {
            return -1;
        }
        final int sz = searchStrs.length;
        int ret = -1;
        int tmp = 0;
        for (final String search : searchStrs) {
            if (search != null) {
                tmp = str.lastIndexOf(search);
                if (tmp > ret) {
                    ret = tmp;
                }
            }
        }
        return ret;
    }
    
    public static String substring(final String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start += str.length();
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return "";
        }
        return str.substring(start);
    }
    
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end += str.length();
        }
        if (start < 0) {
            start += str.length();
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }
    
    public static String left(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }
    
    public static String right(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }
    
    public static String mid(final String str, int pos, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos > str.length()) {
            return "";
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }
    
    public static String substringBefore(final String str, final String separator) {
        if (str == null || separator == null || str.length() == 0) {
            return str;
        }
        if (separator.length() == 0) {
            return "";
        }
        final int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }
    
    public static String substringAfter(final String str, final String separator) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (separator == null) {
            return "";
        }
        final int pos = str.indexOf(separator);
        if (pos == -1) {
            return "";
        }
        return str.substring(pos + separator.length());
    }
    
    public static String substringBeforeLast(final String str, final String separator) {
        if (str == null || separator == null || str.length() == 0 || separator.length() == 0) {
            return str;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }
    
    public static String substringAfterLast(final String str, final String separator) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (separator == null || separator.length() == 0) {
            return "";
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == str.length() - separator.length()) {
            return "";
        }
        return str.substring(pos + separator.length());
    }
    
    public static String substringBetween(final String str, final String tag) {
        return substringBetween(str, tag, tag);
    }
    
    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != -1) {
            final int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }
    
    public static String getNestedString(final String str, final String tag) {
        return substringBetween(str, tag, tag);
    }
    
    public static String getNestedString(final String str, final String open, final String close) {
        return substringBetween(str, open, close);
    }
    
    public static String[] split(final String str) {
        return split(str, null, -1);
    }
    
    public static String[] split(final String str, final char separatorChar) {
        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final List list = new ArrayList();
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }
                start = ++i;
            }
            else {
                match = true;
                ++i;
            }
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }
    
    public static String[] split(final String str, final String separatorChars) {
        return split(str, separatorChars, -1);
    }
    
    public static String[] split(final String str, final String separatorChars, final int max) {
        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        if (separatorChars == null) {
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                }
                else {
                    match = true;
                    ++i;
                }
            }
        }
        else if (separatorChars.length() == 1) {
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                }
                else {
                    match = true;
                    ++i;
                }
            }
        }
        else {
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                }
                else {
                    match = true;
                    ++i;
                }
            }
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }
    
    public static String concatenate(final Object[] array) {
        return join(array, null);
    }
    
    public static String join(final Object[] array) {
        return join(array, null);
    }
    
    public static String join(final Object[] array, final char separator) {
        if (array == null) {
            return null;
        }
        final int arraySize = array.length;
        final int bufSize = (arraySize == 0) ? 0 : ((((array[0] == null) ? 16 : array[0].toString().length()) + 1) * arraySize);
        final StringBuffer buf = new StringBuffer(bufSize);
        for (int i = 0; i < arraySize; ++i) {
            if (i > 0) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
    
    public static String join(final Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        final int arraySize = array.length;
        final int bufSize = (arraySize == 0) ? 0 : (arraySize * (((array[0] == null) ? 16 : array[0].toString().length()) + ((separator != null) ? separator.length() : 0)));
        final StringBuffer buf = new StringBuffer(bufSize);
        for (int i = 0; i < arraySize; ++i) {
            if (separator != null && i > 0) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
    
    public static String join(final Iterator iterator, final char separator) {
        if (iterator == null) {
            return null;
        }
        final StringBuffer buf = new StringBuffer(256);
        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
            if (iterator.hasNext()) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }
    
    public static String join(final Iterator iterator, final String separator) {
        if (iterator == null) {
            return null;
        }
        final StringBuffer buf = new StringBuffer(256);
        while (iterator.hasNext()) {
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
            if (separator != null && iterator.hasNext()) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }
    
    public static String deleteSpaces(final String str) {
        if (str == null) {
            return null;
        }
        return CharSetUtils.delete(str, " \t\r\n\b");
    }
    
    public static String deleteWhitespace(final String str) {
        if (str == null) {
            return null;
        }
        final int sz = str.length();
        final StringBuffer buffer = new StringBuffer(sz);
        for (int i = 0; i < sz; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                buffer.append(str.charAt(i));
            }
        }
        return buffer.toString();
    }
    
    public static String replaceOnce(final String text, final String repl, final String with) {
        return replace(text, repl, with, 1);
    }
    
    public static String replace(final String text, final String repl, final String with) {
        return replace(text, repl, with, -1);
    }
    
    public static String replace(final String text, final String repl, final String with, int max) {
        if (text == null || repl == null || with == null || repl.length() == 0 || max == 0) {
            return text;
        }
        final StringBuffer buf = new StringBuffer(text.length());
        int start = 0;
        int end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();
            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }
    
    public static String replaceChars(final String str, final char searchChar, final char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }
    
    public static String replaceChars(final String str, final String searchChars, final String replaceChars) {
        if (str == null || str.length() == 0 || searchChars == null || searchChars.length() == 0) {
            return str;
        }
        final char[] chars = str.toCharArray();
        int len = chars.length;
        boolean modified = false;
        for (int i = 0, isize = searchChars.length(); i < isize; ++i) {
            final char searchChar = searchChars.charAt(i);
            if (replaceChars == null || i >= replaceChars.length()) {
                int pos = 0;
                for (int j = 0; j < len; ++j) {
                    if (chars[j] != searchChar) {
                        chars[pos++] = chars[j];
                    }
                    else {
                        modified = true;
                    }
                }
                len = pos;
            }
            else {
                for (int k = 0; k < len; ++k) {
                    if (chars[k] == searchChar) {
                        chars[k] = replaceChars.charAt(i);
                        modified = true;
                    }
                }
            }
        }
        if (!modified) {
            return str;
        }
        return new String(chars, 0, len);
    }
    
    public static String overlayString(final String text, final String overlay, final int start, final int end) {
        return new StringBuffer(start + overlay.length() + text.length() - end + 1).append(text.substring(0, start)).append(overlay).append(text.substring(end)).toString();
    }
    
    public static String overlay(final String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = "";
        }
        final int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            final int temp = start;
            start = end;
            end = temp;
        }
        return new StringBuffer(len + start - end + overlay.length() + 1).append(str.substring(0, start)).append(overlay).append(str.substring(end)).toString();
    }
    
    public static String chomp(final String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (str.length() != 1) {
            int lastIdx = str.length() - 1;
            final char last = str.charAt(lastIdx);
            if (last == '\n') {
                if (str.charAt(lastIdx - 1) == '\r') {
                    --lastIdx;
                }
            }
            else if (last != '\r') {
                ++lastIdx;
            }
            return str.substring(0, lastIdx);
        }
        final char ch = str.charAt(0);
        if (ch == '\r' || ch == '\n') {
            return "";
        }
        return str;
    }
    
    public static String chomp(final String str, final String separator) {
        if (str == null || str.length() == 0 || separator == null) {
            return str;
        }
        if (str.endsWith(separator)) {
            return str.substring(0, str.length() - separator.length());
        }
        return str;
    }
    
    public static String chompLast(final String str) {
        return chompLast(str, "\n");
    }
    
    public static String chompLast(final String str, final String sep) {
        if (str.length() == 0) {
            return str;
        }
        final String sub = str.substring(str.length() - sep.length());
        if (sep.equals(sub)) {
            return str.substring(0, str.length() - sep.length());
        }
        return str;
    }
    
    public static String getChomp(final String str, final String sep) {
        final int idx = str.lastIndexOf(sep);
        if (idx == str.length() - sep.length()) {
            return sep;
        }
        if (idx != -1) {
            return str.substring(idx);
        }
        return "";
    }
    
    public static String prechomp(final String str, final String sep) {
        final int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(idx + sep.length());
        }
        return str;
    }
    
    public static String getPrechomp(final String str, final String sep) {
        final int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(0, idx + sep.length());
        }
        return "";
    }
    
    public static String chop(final String str) {
        if (str == null) {
            return null;
        }
        final int strLen = str.length();
        if (strLen < 2) {
            return "";
        }
        final int lastIdx = strLen - 1;
        final String ret = str.substring(0, lastIdx);
        final char last = str.charAt(lastIdx);
        if (last == '\n' && ret.charAt(lastIdx - 1) == '\r') {
            return ret.substring(0, lastIdx - 1);
        }
        return ret;
    }
    
    public static String chopNewline(final String str) {
        int lastIdx = str.length() - 1;
        if (lastIdx <= 0) {
            return "";
        }
        final char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                --lastIdx;
            }
        }
        else {
            ++lastIdx;
        }
        return str.substring(0, lastIdx);
    }
    
    public static String escape(final String str) {
        return StringEscapeUtils.escapeJava(str);
    }
    
    public static String repeat(final String str, final int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        final int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= 8192) {
            return padding(repeat, str.charAt(0));
        }
        final int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1: {
                final char ch = str.charAt(0);
                final char[] output1 = new char[outputLength];
                for (int i = repeat - 1; i >= 0; --i) {
                    output1[i] = ch;
                }
                return new String(output1);
            }
            case 2: {
                final char ch2 = str.charAt(0);
                final char ch3 = str.charAt(1);
                final char[] output2 = new char[outputLength];
                for (int j = repeat * 2 - 2; j >= 0; --j, --j) {
                    output2[j] = ch2;
                    output2[j + 1] = ch3;
                }
                return new String(output2);
            }
            default: {
                final StringBuffer buf = new StringBuffer(outputLength);
                for (int k = 0; k < repeat; ++k) {
                    buf.append(str);
                }
                return buf.toString();
            }
        }
    }
    
    private static String padding(final int repeat, final char padChar) {
        String pad = StringUtils.PADDING[padChar];
        if (pad == null) {
            pad = String.valueOf(padChar);
        }
        while (pad.length() < repeat) {
            pad = pad.concat(pad);
        }
        StringUtils.PADDING[padChar] = pad;
        return pad.substring(0, repeat);
    }
    
    public static String rightPad(final String str, final int size) {
        return rightPad(str, size, ' ');
    }
    
    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(padding(pads, padChar));
    }
    
    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (padStr == null || padStr.length() == 0) {
            padStr = " ";
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= 8192) {
            return rightPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return str.concat(padStr);
        }
        if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        }
        final char[] padding = new char[pads];
        final char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; ++i) {
            padding[i] = padChars[i % padLen];
        }
        return str.concat(new String(padding));
    }
    
    public static String leftPad(final String str, final int size) {
        return leftPad(str, size, ' ');
    }
    
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return padding(pads, padChar).concat(str);
    }
    
    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (padStr == null || padStr.length() == 0) {
            padStr = " ";
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= 8192) {
            return leftPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return padStr.concat(str);
        }
        if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        }
        final char[] padding = new char[pads];
        final char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; ++i) {
            padding[i] = padChars[i % padLen];
        }
        return new String(padding).concat(str);
    }
    
    public static String center(final String str, final int size) {
        return center(str, size, ' ');
    }
    
    public static String center(String str, final int size, final char padChar) {
        if (str == null || size <= 0) {
            return str;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }
    
    public static String center(String str, final int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (padStr == null || padStr.length() == 0) {
            padStr = " ";
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }
    
    public static String upperCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }
    
    public static String lowerCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }
    
    public static String capitalize(final String str) {
        final int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
    }
    
    public static String capitalise(final String str) {
        return capitalize(str);
    }
    
    public static String uncapitalize(final String str) {
        final int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
    }
    
    public static String uncapitalise(final String str) {
        return uncapitalize(str);
    }
    
    public static String swapCase(final String str) {
        final int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        final StringBuffer buffer = new StringBuffer(strLen);
        char ch = '\0';
        for (int i = 0; i < strLen; ++i) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            }
            else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            }
            else if (Character.isLowerCase(ch)) {
                ch = Character.toUpperCase(ch);
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }
    
    public static String capitaliseAllWords(final String str) {
        return WordUtils.capitalize(str);
    }
    
    public static int countMatches(final String str, final String sub) {
        if (str == null || str.length() == 0 || sub == null || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        for (int idx = 0; (idx = str.indexOf(sub, idx)) != -1; idx += sub.length()) {
            ++count;
        }
        return count;
    }
    
    public static boolean isAlpha(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAlphaSpace(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isLetter(str.charAt(i)) && str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAlphanumeric(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAlphanumericSpace(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isLetterOrDigit(str.charAt(i)) && str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isNumeric(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isNumericSpace(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isWhitespace(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static String defaultString(final String str) {
        return (str == null) ? "" : str;
    }
    
    public static String defaultString(final String str, final String defaultStr) {
        return (str == null) ? defaultStr : str;
    }
    
    public static String reverse(final String str) {
        if (str == null) {
            return null;
        }
        return new StringBuffer(str).reverse().toString();
    }
    
    public static String reverseDelimited(final String str, final char separatorChar) {
        if (str == null) {
            return null;
        }
        final String[] strs = split(str, separatorChar);
        ArrayUtils.reverse(strs);
        return join(strs, separatorChar);
    }
    
    public static String reverseDelimitedString(final String str, final String separatorChars) {
        if (str == null) {
            return null;
        }
        final String[] strs = split(str, separatorChars);
        ArrayUtils.reverse(strs);
        if (separatorChars == null) {
            return join(strs, ' ');
        }
        return join(strs, separatorChars);
    }
    
    public static String abbreviate(final String str, final int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }
    
    public static String abbreviate(final String str, int offset, final int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if (str.length() - offset < maxWidth - 3) {
            offset = str.length() - (maxWidth - 3);
        }
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + "...";
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if (offset + (maxWidth - 3) < str.length()) {
            return "..." + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return "..." + str.substring(str.length() - (maxWidth - 3));
    }
    
    public static String difference(final String str1, final String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        final int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return "";
        }
        return str2.substring(at);
    }
    
    public static int indexOfDifference(final String str1, final String str2) {
        if (str1 == str2) {
            return -1;
        }
        if (str1 == null || str2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < str1.length() && i < str2.length() && str1.charAt(i) == str2.charAt(i); ++i) {}
        if (i < str2.length() || i < str1.length()) {
            return i;
        }
        return -1;
    }
    
    public static int getLevenshteinDistance(final String s, final String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        final int n = s.length();
        final int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        final int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; ++i) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; ++j) {
            d[0][j] = j;
        }
        for (int i = 1; i <= n; ++i) {
            final char s_i = s.charAt(i - 1);
            for (int j = 1; j <= m; ++j) {
                final char t_j = t.charAt(j - 1);
                int cost;
                if (s_i == t_j) {
                    cost = 0;
                }
                else {
                    cost = 1;
                }
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }
        return d[n][m];
    }
    
    private static int min(int a, final int b, final int c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }
    
    static {
        (PADDING = new String[65535])[32] = "                                                                ";
    }
}
