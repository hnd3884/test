package org.apache.tika.utils;

import java.util.regex.Matcher;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Iterator;
import java.util.Locale;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Pattern;

public class CharsetUtils
{
    private static final Pattern CHARSET_NAME_PATTERN;
    private static final Pattern ISO_NAME_PATTERN;
    private static final Pattern CP_NAME_PATTERN;
    private static final Pattern WIN_NAME_PATTERN;
    private static final Map<String, Charset> COMMON_CHARSETS;
    private static Method getCharsetICU;
    private static Method isSupportedICU;
    
    private static Map<String, Charset> initCommonCharsets(final String... names) {
        final Map<String, Charset> charsets = new HashMap<String, Charset>();
        for (final String name : names) {
            try {
                final Charset charset = Charset.forName(name);
                CharsetUtils.COMMON_CHARSETS.put(name.toLowerCase(Locale.ENGLISH), charset);
                for (final String alias : charset.aliases()) {
                    CharsetUtils.COMMON_CHARSETS.put(alias.toLowerCase(Locale.ENGLISH), charset);
                }
            }
            catch (final IllegalArgumentException ex) {}
        }
        return charsets;
    }
    
    public static boolean isSupported(final String charsetName) {
        try {
            return (CharsetUtils.isSupportedICU != null && (boolean)CharsetUtils.isSupportedICU.invoke(null, charsetName)) || Charset.isSupported(charsetName);
        }
        catch (final IllegalCharsetNameException e) {
            return false;
        }
        catch (final IllegalArgumentException e2) {
            return false;
        }
        catch (final Exception e3) {
            return false;
        }
    }
    
    public static String clean(final String charsetName) {
        try {
            return forName(charsetName).name();
        }
        catch (final IllegalArgumentException e) {
            return null;
        }
    }
    
    public static Charset forName(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        final Matcher m = CharsetUtils.CHARSET_NAME_PATTERN.matcher(name);
        if (!m.matches()) {
            throw new IllegalCharsetNameException(name);
        }
        name = m.group(1);
        final String lower = name.toLowerCase(Locale.ENGLISH);
        Charset charset = CharsetUtils.COMMON_CHARSETS.get(lower);
        if (charset != null) {
            return charset;
        }
        if ("none".equals(lower) || "no".equals(lower)) {
            throw new IllegalCharsetNameException(name);
        }
        final Matcher iso = CharsetUtils.ISO_NAME_PATTERN.matcher(lower);
        final Matcher cp = CharsetUtils.CP_NAME_PATTERN.matcher(lower);
        final Matcher win = CharsetUtils.WIN_NAME_PATTERN.matcher(lower);
        if (iso.matches()) {
            name = "iso-8859-" + iso.group(1);
            charset = CharsetUtils.COMMON_CHARSETS.get(name);
        }
        else if (cp.matches()) {
            name = "cp" + cp.group(1);
            charset = CharsetUtils.COMMON_CHARSETS.get(name);
        }
        else if (win.matches()) {
            name = "windows-" + win.group(1);
            charset = CharsetUtils.COMMON_CHARSETS.get(name);
        }
        if (charset != null) {
            return charset;
        }
        if (CharsetUtils.getCharsetICU != null) {
            try {
                final Charset cs = (Charset)CharsetUtils.getCharsetICU.invoke(null, name);
                if (cs != null) {
                    return cs;
                }
            }
            catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {}
        }
        return Charset.forName(name);
    }
    
    static {
        CHARSET_NAME_PATTERN = Pattern.compile("[ \\\"]*([^ >,;\\\"]+).*");
        ISO_NAME_PATTERN = Pattern.compile(".*8859-(\\d+)");
        CP_NAME_PATTERN = Pattern.compile("cp-(\\d+)");
        WIN_NAME_PATTERN = Pattern.compile("win-?(\\d+)");
        COMMON_CHARSETS = new HashMap<String, Charset>();
        CharsetUtils.getCharsetICU = null;
        CharsetUtils.isSupportedICU = null;
        initCommonCharsets("Big5", "EUC-JP", "EUC-KR", "x-EUC-TW", "GB18030", "IBM855", "IBM866", "ISO-2022-CN", "ISO-2022-JP", "ISO-2022-KR", "ISO-8859-1", "ISO-8859-2", "ISO-8859-3", "ISO-8859-4", "ISO-8859-5", "ISO-8859-6", "ISO-8859-7", "ISO-8859-8", "ISO-8859-9", "ISO-8859-11", "ISO-8859-13", "ISO-8859-15", "KOI8-R", "x-MacCyrillic", "SHIFT_JIS", "UTF-8", "UTF-16BE", "UTF-16LE", "windows-1251", "windows-1252", "windows-1253", "windows-1255");
        CharsetUtils.COMMON_CHARSETS.put("iso-8851-1", CharsetUtils.COMMON_CHARSETS.get("iso-8859-1"));
        CharsetUtils.COMMON_CHARSETS.put("windows", CharsetUtils.COMMON_CHARSETS.get("windows-1252"));
        CharsetUtils.COMMON_CHARSETS.put("koi8r", CharsetUtils.COMMON_CHARSETS.get("koi8-r"));
        Class<?> icuCharset = null;
        try {
            icuCharset = CharsetUtils.class.getClassLoader().loadClass("com.ibm.icu.charset.CharsetICU");
        }
        catch (final ClassNotFoundException ex) {}
        if (icuCharset != null) {
            try {
                CharsetUtils.getCharsetICU = icuCharset.getMethod("forNameICU", String.class);
            }
            catch (final Throwable t) {
                throw new RuntimeException(t);
            }
            try {
                CharsetUtils.isSupportedICU = icuCharset.getMethod("isSupported", String.class);
            }
            catch (final Throwable t2) {}
        }
    }
}
