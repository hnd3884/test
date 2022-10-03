package org.apache.lucene.queryparser.flexible.core.util;

public final class StringUtils
{
    public static String toString(final Object obj) {
        if (obj != null) {
            return obj.toString();
        }
        return null;
    }
}
