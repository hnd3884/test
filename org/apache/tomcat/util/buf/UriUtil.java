package org.apache.tomcat.util.buf;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.util.regex.Pattern;

public final class UriUtil
{
    private static final char[] HEX;
    private static final Pattern PATTERN_EXCLAMATION_MARK;
    private static final Pattern PATTERN_CARET;
    private static final Pattern PATTERN_ASTERISK;
    private static final Pattern PATTERN_CUSTOM;
    private static final String REPLACE_CUSTOM;
    private static final String WAR_SEPARATOR;
    
    private UriUtil() {
    }
    
    private static boolean isSchemeChar(final char c) {
        return Character.isLetterOrDigit(c) || c == '+' || c == '-' || c == '.';
    }
    
    public static boolean hasScheme(final CharSequence uri) {
        for (int len = uri.length(), i = 0; i < len; ++i) {
            final char c = uri.charAt(i);
            if (c == ':') {
                return i > 0;
            }
            if (!isSchemeChar(c)) {
                return false;
            }
        }
        return false;
    }
    
    public static URL buildJarUrl(final File jarFile) throws MalformedURLException {
        return buildJarUrl(jarFile, null);
    }
    
    public static URL buildJarUrl(final File jarFile, final String entryPath) throws MalformedURLException {
        return buildJarUrl(jarFile.toURI().toString(), entryPath);
    }
    
    public static URL buildJarUrl(final String fileUrlString) throws MalformedURLException {
        return buildJarUrl(fileUrlString, null);
    }
    
    public static URL buildJarUrl(final String fileUrlString, final String entryPath) throws MalformedURLException {
        final String safeString = makeSafeForJarUrl(fileUrlString);
        final StringBuilder sb = new StringBuilder();
        sb.append(safeString);
        sb.append("!/");
        if (entryPath != null) {
            sb.append(makeSafeForJarUrl(entryPath));
        }
        return new URL("jar", null, -1, sb.toString());
    }
    
    public static URL buildJarSafeUrl(final File file) throws MalformedURLException {
        final String safe = makeSafeForJarUrl(file.toURI().toString());
        return new URL(safe);
    }
    
    private static String makeSafeForJarUrl(final String input) {
        String tmp = UriUtil.PATTERN_EXCLAMATION_MARK.matcher(input).replaceAll("%21/");
        tmp = UriUtil.PATTERN_CARET.matcher(tmp).replaceAll("%5e/");
        tmp = UriUtil.PATTERN_ASTERISK.matcher(tmp).replaceAll("%2a/");
        if (UriUtil.PATTERN_CUSTOM != null) {
            tmp = UriUtil.PATTERN_CUSTOM.matcher(tmp).replaceAll(UriUtil.REPLACE_CUSTOM);
        }
        return tmp;
    }
    
    public static URL warToJar(final URL warUrl) throws MalformedURLException {
        String file = warUrl.getFile();
        if (file.contains("*/")) {
            file = file.replaceFirst("\\*/", "!/");
        }
        else if (file.contains("^/")) {
            file = file.replaceFirst("\\^/", "!/");
        }
        else if (UriUtil.PATTERN_CUSTOM != null) {
            file = file.replaceFirst(UriUtil.PATTERN_CUSTOM.pattern(), "!/");
        }
        return new URL("jar", warUrl.getHost(), warUrl.getPort(), file);
    }
    
    public static String getWarSeparator() {
        return UriUtil.WAR_SEPARATOR;
    }
    
    public static boolean isAbsoluteURI(final String path) {
        if (path.startsWith("file:/")) {
            return true;
        }
        int i;
        for (i = 0; i < path.length() && isSchemeChar(path.charAt(i)); ++i) {}
        return i != 0 && (i + 2 < path.length() && path.charAt(i++) == ':' && path.charAt(i++) == '/' && path.charAt(i) == '/');
    }
    
    static {
        HEX = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        PATTERN_EXCLAMATION_MARK = Pattern.compile("!/");
        PATTERN_CARET = Pattern.compile("\\^/");
        PATTERN_ASTERISK = Pattern.compile("\\*/");
        final String custom = System.getProperty("org.apache.tomcat.util.buf.UriUtil.WAR_SEPARATOR");
        if (custom == null) {
            WAR_SEPARATOR = "*/";
            PATTERN_CUSTOM = null;
            REPLACE_CUSTOM = null;
        }
        else {
            WAR_SEPARATOR = custom + "/";
            PATTERN_CUSTOM = Pattern.compile(Pattern.quote(UriUtil.WAR_SEPARATOR));
            final StringBuilder sb = new StringBuilder(custom.length() * 3);
            final byte[] arr$;
            final byte[] ba = arr$ = custom.getBytes();
            for (final byte toEncode : arr$) {
                sb.append('%');
                final int low = toEncode & 0xF;
                final int high = (toEncode & 0xF0) >> 4;
                sb.append(UriUtil.HEX[high]);
                sb.append(UriUtil.HEX[low]);
            }
            REPLACE_CUSTOM = sb.toString();
        }
    }
}
