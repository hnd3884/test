package org.antlr.v4.runtime.misc;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class Utils
{
    public static <T> String join(final Iterator<T> iter, final String separator) {
        final StringBuilder buf = new StringBuilder();
        while (iter.hasNext()) {
            buf.append(iter.next());
            if (iter.hasNext()) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }
    
    public static <T> String join(final T[] array, final String separator) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }
    
    public static int numNonnull(final Object[] data) {
        int n = 0;
        if (data == null) {
            return n;
        }
        for (final Object o : data) {
            if (o != null) {
                ++n;
            }
        }
        return n;
    }
    
    public static <T> void removeAllElements(final Collection<T> data, final T value) {
        if (data == null) {
            return;
        }
        while (data.contains(value)) {
            data.remove(value);
        }
    }
    
    public static String escapeWhitespace(final String s, final boolean escapeSpaces) {
        final StringBuilder buf = new StringBuilder();
        for (final char c : s.toCharArray()) {
            if (c == ' ' && escapeSpaces) {
                buf.append('·');
            }
            else if (c == '\t') {
                buf.append("\\t");
            }
            else if (c == '\n') {
                buf.append("\\n");
            }
            else if (c == '\r') {
                buf.append("\\r");
            }
            else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
    
    public static void writeFile(final String fileName, final String content) throws IOException {
        writeFile(fileName, content, null);
    }
    
    public static void writeFile(final String fileName, final String content, final String encoding) throws IOException {
        final File f = new File(fileName);
        final FileOutputStream fos = new FileOutputStream(f);
        OutputStreamWriter osw;
        if (encoding != null) {
            osw = new OutputStreamWriter(fos, encoding);
        }
        else {
            osw = new OutputStreamWriter(fos);
        }
        try {
            osw.write(content);
        }
        finally {
            osw.close();
        }
    }
    
    public static char[] readFile(final String fileName) throws IOException {
        return readFile(fileName, null);
    }
    
    public static char[] readFile(final String fileName, final String encoding) throws IOException {
        final File f = new File(fileName);
        final int size = (int)f.length();
        final FileInputStream fis = new FileInputStream(fileName);
        InputStreamReader isr;
        if (encoding != null) {
            isr = new InputStreamReader(fis, encoding);
        }
        else {
            isr = new InputStreamReader(fis);
        }
        char[] data = null;
        try {
            data = new char[size];
            final int n = isr.read(data);
            if (n < data.length) {
                data = Arrays.copyOf(data, n);
            }
        }
        finally {
            isr.close();
        }
        return data;
    }
    
    public static Map<String, Integer> toMap(final String[] keys) {
        final Map<String, Integer> m = new HashMap<String, Integer>();
        for (int i = 0; i < keys.length; ++i) {
            m.put(keys[i], i);
        }
        return m;
    }
    
    public static char[] toCharArray(final IntegerList data) {
        if (data == null) {
            return null;
        }
        final char[] cdata = new char[data.size()];
        for (int i = 0; i < data.size(); ++i) {
            cdata[i] = (char)data.get(i);
        }
        return cdata;
    }
    
    public static IntervalSet toSet(final BitSet bits) {
        final IntervalSet s = new IntervalSet(new int[0]);
        for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            s.add(i);
        }
        return s;
    }
}
