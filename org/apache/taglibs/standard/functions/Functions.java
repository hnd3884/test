package org.apache.taglibs.standard.functions;

import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.StringTokenizer;
import org.apache.taglibs.standard.util.EscapeXML;

public class Functions
{
    public static String toUpperCase(final String input) {
        return input.toUpperCase();
    }
    
    public static String toLowerCase(final String input) {
        return input.toLowerCase();
    }
    
    public static int indexOf(final String input, final String substring) {
        return input.indexOf(substring);
    }
    
    public static boolean contains(final String input, final String substring) {
        return input.contains(substring);
    }
    
    public static boolean containsIgnoreCase(final String input, final String substring) {
        return contains(input.toUpperCase(), substring.toUpperCase());
    }
    
    public static boolean startsWith(final String input, final String prefix) {
        return input.startsWith(prefix);
    }
    
    public static boolean endsWith(final String input, final String suffix) {
        return input.endsWith(suffix);
    }
    
    public static String substring(final String input, int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            beginIndex = 0;
        }
        if (endIndex < 0 || endIndex > input.length()) {
            endIndex = input.length();
        }
        if (endIndex < beginIndex) {
            return "";
        }
        return input.substring(beginIndex, endIndex);
    }
    
    public static String substringAfter(final String input, final String substring) {
        final int index = input.indexOf(substring);
        if (index == -1) {
            return "";
        }
        return input.substring(index + substring.length());
    }
    
    public static String substringBefore(final String input, final String substring) {
        final int index = input.indexOf(substring);
        if (index == -1) {
            return "";
        }
        return input.substring(0, index);
    }
    
    public static String escapeXml(final String input) {
        return EscapeXML.escape(input);
    }
    
    public static String trim(final String input) {
        return input.trim();
    }
    
    public static String replace(final String input, final String before, final String after) {
        if (before.length() == 0) {
            return input;
        }
        return input.replace(before, after);
    }
    
    public static String[] split(final String input, final String delimiters) {
        if (input.length() == 0 || delimiters.length() == 0) {
            return new String[] { input };
        }
        final StringTokenizer tok = new StringTokenizer(input, delimiters);
        final String[] array = new String[tok.countTokens()];
        int i = 0;
        while (tok.hasMoreTokens()) {
            array[i++] = tok.nextToken();
        }
        return array;
    }
    
    public static String join(final String[] array, final String separator) {
        if (array == null || array.length == 0) {
            return "";
        }
        if (array.length == 1) {
            return (array[0] == null) ? "null" : array[0];
        }
        final StringBuilder buf = new StringBuilder();
        buf.append(array[0]);
        for (int i = 1; i < array.length; ++i) {
            buf.append(separator).append(array[i]);
        }
        return buf.toString();
    }
    
    public static int length(final Object obj) throws JspTagException {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof String) {
            return ((String)obj).length();
        }
        if (obj instanceof Collection) {
            return ((Collection)obj).size();
        }
        if (obj instanceof Map) {
            return ((Map)obj).size();
        }
        if (obj instanceof Iterator) {
            int count = 0;
            final Iterator iter = (Iterator)obj;
            while (iter.hasNext()) {
                ++count;
                iter.next();
            }
            return count;
        }
        if (obj instanceof Enumeration) {
            final Enumeration enum_ = (Enumeration)obj;
            int count2 = 0;
            while (enum_.hasMoreElements()) {
                ++count2;
                enum_.nextElement();
            }
            return count2;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj);
        }
        throw new JspTagException(Resources.getMessage("PARAM_BAD_VALUE"));
    }
}
