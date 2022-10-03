package com.sun.mail.util.logging;

import java.util.Collections;
import java.util.Formattable;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;

public class CompactFormatter extends Formatter
{
    private final String fmt;
    
    private static Class<?>[] loadDeclaredClasses() {
        return new Class[] { Alternate.class };
    }
    
    public CompactFormatter() {
        final String p = this.getClass().getName();
        this.fmt = this.initFormat(p);
    }
    
    public CompactFormatter(final String format) {
        final String p = this.getClass().getName();
        this.fmt = ((format == null) ? this.initFormat(p) : format);
    }
    
    @Override
    public String format(final LogRecord record) {
        final ResourceBundle rb = record.getResourceBundle();
        final Locale l = (rb == null) ? null : rb.getLocale();
        final String msg = this.formatMessage(record);
        final String thrown = this.formatThrown(record);
        final String err = this.formatError(record);
        final Object[] params = { this.formatZonedDateTime(record), this.formatSource(record), this.formatLoggerName(record), this.formatLevel(record), msg, thrown, new Alternate(msg, thrown), new Alternate(thrown, msg), record.getSequenceNumber(), this.formatThreadID(record), err, new Alternate(msg, err), new Alternate(err, msg), this.formatBackTrace(record), record.getResourceBundleName(), record.getMessage() };
        if (l == null) {
            return String.format(this.fmt, params);
        }
        return String.format(l, this.fmt, params);
    }
    
    @Override
    public String formatMessage(final LogRecord record) {
        String msg = super.formatMessage(record);
        msg = replaceClassName(msg, record.getThrown());
        msg = replaceClassName(msg, record.getParameters());
        return msg;
    }
    
    public String formatMessage(final Throwable t) {
        String r;
        if (t != null) {
            final Throwable apply = this.apply(t);
            final String m = apply.getLocalizedMessage();
            final String s = apply.toString();
            final String sn = simpleClassName(apply.getClass());
            if (!isNullOrSpaces(m)) {
                if (s.contains(m)) {
                    if (s.startsWith(apply.getClass().getName()) || s.startsWith(sn)) {
                        r = replaceClassName(m, t);
                    }
                    else {
                        r = replaceClassName(simpleClassName(s), t);
                    }
                }
                else {
                    r = replaceClassName(simpleClassName(s) + ": " + m, t);
                }
            }
            else {
                r = replaceClassName(simpleClassName(s), t);
            }
            if (!r.contains(sn)) {
                r = sn + ": " + r;
            }
        }
        else {
            r = "";
        }
        return r;
    }
    
    public String formatLevel(final LogRecord record) {
        return record.getLevel().getLocalizedName();
    }
    
    public String formatSource(final LogRecord record) {
        String source = record.getSourceClassName();
        if (source != null) {
            if (record.getSourceMethodName() != null) {
                source = simpleClassName(source) + " " + record.getSourceMethodName();
            }
            else {
                source = simpleClassName(source);
            }
        }
        else {
            source = simpleClassName(record.getLoggerName());
        }
        return source;
    }
    
    public String formatLoggerName(final LogRecord record) {
        return simpleClassName(record.getLoggerName());
    }
    
    public Number formatThreadID(final LogRecord record) {
        return (long)record.getThreadID() & 0xFFFFFFFFL;
    }
    
    public String formatThrown(final LogRecord record) {
        final Throwable t = record.getThrown();
        String msg;
        if (t != null) {
            final String site = this.formatBackTrace(record);
            msg = this.formatMessage(t) + (isNullOrSpaces(site) ? "" : (' ' + site));
        }
        else {
            msg = "";
        }
        return msg;
    }
    
    public String formatError(final LogRecord record) {
        return this.formatMessage(record.getThrown());
    }
    
    public String formatBackTrace(final LogRecord record) {
        String site = "";
        final Throwable t = record.getThrown();
        if (t != null) {
            final Throwable root = this.apply(t);
            StackTraceElement[] trace = root.getStackTrace();
            site = this.findAndFormat(trace);
            if (isNullOrSpaces(site)) {
                int limit = 0;
                for (Throwable c = t; c != null; c = c.getCause()) {
                    final StackTraceElement[] ste = c.getStackTrace();
                    site = this.findAndFormat(ste);
                    if (!isNullOrSpaces(site)) {
                        break;
                    }
                    if (trace.length == 0) {
                        trace = ste;
                    }
                    if (++limit == 65536) {
                        break;
                    }
                }
                if (isNullOrSpaces(site) && trace.length != 0) {
                    site = this.formatStackTraceElement(trace[0]);
                }
            }
        }
        return site;
    }
    
    private String findAndFormat(final StackTraceElement[] trace) {
        String site = "";
        for (final StackTraceElement s : trace) {
            if (!this.ignore(s)) {
                site = this.formatStackTraceElement(s);
                break;
            }
        }
        if (isNullOrSpaces(site)) {
            for (final StackTraceElement s : trace) {
                if (!this.defaultIgnore(s)) {
                    site = this.formatStackTraceElement(s);
                    break;
                }
            }
        }
        return site;
    }
    
    private String formatStackTraceElement(final StackTraceElement s) {
        String v = simpleClassName(s.getClassName());
        String result;
        if (v != null) {
            result = s.toString().replace(s.getClassName(), v);
        }
        else {
            result = s.toString();
        }
        v = simpleFileName(s.getFileName());
        if (v != null && result.startsWith(v)) {
            result = result.replace(s.getFileName(), "");
        }
        return result;
    }
    
    protected Throwable apply(final Throwable t) {
        return SeverityComparator.getInstance().apply(t);
    }
    
    protected boolean ignore(final StackTraceElement s) {
        return this.isUnknown(s) || this.defaultIgnore(s);
    }
    
    protected String toAlternate(final String s) {
        return (s != null) ? s.replaceAll("[\\x00-\\x1F\\x7F]+", "") : null;
    }
    
    private Comparable<?> formatZonedDateTime(final LogRecord record) {
        Comparable<?> zdt = LogManagerProperties.getZonedDateTime(record);
        if (zdt == null) {
            zdt = new Date(record.getMillis());
        }
        return zdt;
    }
    
    private boolean defaultIgnore(final StackTraceElement s) {
        return this.isSynthetic(s) || this.isStaticUtility(s) || this.isReflection(s);
    }
    
    private boolean isStaticUtility(final StackTraceElement s) {
        try {
            return LogManagerProperties.isStaticUtilityClass(s.getClassName());
        }
        catch (final RuntimeException ex) {}
        catch (final Exception | LinkageError exception | LinkageError) {}
        final String cn = s.getClassName();
        return (cn.endsWith("s") && !cn.endsWith("es")) || cn.contains("Util") || cn.endsWith("Throwables");
    }
    
    private boolean isSynthetic(final StackTraceElement s) {
        return s.getMethodName().indexOf(36) > -1;
    }
    
    private boolean isUnknown(final StackTraceElement s) {
        return s.getLineNumber() < 0;
    }
    
    private boolean isReflection(final StackTraceElement s) {
        try {
            return LogManagerProperties.isReflectionClass(s.getClassName());
        }
        catch (final RuntimeException ex) {}
        catch (final Exception | LinkageError exception | LinkageError) {}
        return s.getClassName().startsWith("java.lang.reflect.") || s.getClassName().startsWith("sun.reflect.");
    }
    
    private String initFormat(final String p) {
        String v = LogManagerProperties.fromLogManager(p.concat(".format"));
        if (isNullOrSpaces(v)) {
            v = "%7$#.160s%n";
        }
        return v;
    }
    
    private static String replaceClassName(String msg, final Throwable t) {
        if (!isNullOrSpaces(msg)) {
            int limit = 0;
            for (Throwable c = t; c != null; c = c.getCause()) {
                final Class<?> k = c.getClass();
                msg = msg.replace(k.getName(), simpleClassName(k));
                if (++limit == 65536) {
                    break;
                }
            }
        }
        return msg;
    }
    
    private static String replaceClassName(String msg, final Object[] p) {
        if (!isNullOrSpaces(msg) && p != null) {
            for (final Object o : p) {
                if (o != null) {
                    final Class<?> k = o.getClass();
                    msg = msg.replace(k.getName(), simpleClassName(k));
                }
            }
        }
        return msg;
    }
    
    private static String simpleClassName(final Class<?> k) {
        try {
            return k.getSimpleName();
        }
        catch (final InternalError internalError) {
            return simpleClassName(k.getName());
        }
    }
    
    private static String simpleClassName(String name) {
        if (name != null) {
            int cursor = 0;
            int sign = -1;
            int prev;
            int dot = prev = -1;
            while (cursor < name.length()) {
                final int c = name.codePointAt(cursor);
                if (!Character.isJavaIdentifierPart(c)) {
                    if (c == 46) {
                        if (dot + 1 == cursor || dot + 1 == sign) {
                            return name;
                        }
                        prev = dot;
                        dot = cursor;
                    }
                    else {
                        if (dot + 1 == cursor) {
                            dot = prev;
                            break;
                        }
                        break;
                    }
                }
                else if (c == 36) {
                    sign = cursor;
                }
                cursor += Character.charCount(c);
            }
            if (dot > -1 && ++dot < cursor && ++sign < cursor) {
                name = name.substring((sign > dot) ? sign : dot);
            }
        }
        return name;
    }
    
    private static String simpleFileName(String name) {
        if (name != null) {
            final int index = name.lastIndexOf(46);
            name = ((index > -1) ? name.substring(0, index) : name);
        }
        return name;
    }
    
    private static boolean isNullOrSpaces(final String s) {
        return s == null || s.trim().length() == 0;
    }
    
    static {
        loadDeclaredClasses();
    }
    
    private class Alternate implements Formattable
    {
        private final String left;
        private final String right;
        
        Alternate(final String left, final String right) {
            this.left = String.valueOf(left);
            this.right = String.valueOf(right);
        }
        
        @Override
        public void formatTo(final java.util.Formatter formatter, final int flags, final int width, int precision) {
            String l = this.left;
            String r = this.right;
            if ((flags & 0x2) == 0x2) {
                l = l.toUpperCase(formatter.locale());
                r = r.toUpperCase(formatter.locale());
            }
            if ((flags & 0x4) == 0x4) {
                l = CompactFormatter.this.toAlternate(l);
                r = CompactFormatter.this.toAlternate(r);
            }
            if (precision <= 0) {
                precision = Integer.MAX_VALUE;
            }
            int fence = Math.min(l.length(), precision);
            if (fence > precision >> 1) {
                fence = Math.max(fence - r.length(), fence >> 1);
            }
            if (fence > 0) {
                if (fence > l.length() && Character.isHighSurrogate(l.charAt(fence - 1))) {
                    --fence;
                }
                l = l.substring(0, fence);
            }
            r = r.substring(0, Math.min(precision - fence, r.length()));
            if (width > 0) {
                final int half = width >> 1;
                if (l.length() < half) {
                    l = this.pad(flags, l, half);
                }
                if (r.length() < half) {
                    r = this.pad(flags, r, half);
                }
            }
            final Object[] empty = Collections.emptySet().toArray();
            formatter.format(l, empty);
            if (l.length() != 0 && r.length() != 0) {
                formatter.format("|", empty);
            }
            formatter.format(r, empty);
        }
        
        private String pad(final int flags, final String s, final int length) {
            final int padding = length - s.length();
            final StringBuilder b = new StringBuilder(length);
            if ((flags & 0x1) == 0x1) {
                for (int i = 0; i < padding; ++i) {
                    b.append(' ');
                }
                b.append(s);
            }
            else {
                b.append(s);
                for (int i = 0; i < padding; ++i) {
                    b.append(' ');
                }
            }
            return b.toString();
        }
    }
}
