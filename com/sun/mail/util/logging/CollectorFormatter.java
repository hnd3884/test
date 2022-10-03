package com.sun.mail.util.logging;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.Comparator;
import java.util.logging.Formatter;

public class CollectorFormatter extends Formatter
{
    private static final long INIT_TIME;
    private final String fmt;
    private final Formatter formatter;
    private final Comparator<? super LogRecord> comparator;
    private LogRecord last;
    private long count;
    private long generation;
    private long thrown;
    private long minMillis;
    private long maxMillis;
    
    public CollectorFormatter() {
        this.generation = 1L;
        this.minMillis = CollectorFormatter.INIT_TIME;
        this.maxMillis = Long.MIN_VALUE;
        final String p = this.getClass().getName();
        this.fmt = this.initFormat(p);
        this.formatter = this.initFormatter(p);
        this.comparator = this.initComparator(p);
    }
    
    public CollectorFormatter(final String format) {
        this.generation = 1L;
        this.minMillis = CollectorFormatter.INIT_TIME;
        this.maxMillis = Long.MIN_VALUE;
        final String p = this.getClass().getName();
        this.fmt = ((format == null) ? this.initFormat(p) : format);
        this.formatter = this.initFormatter(p);
        this.comparator = this.initComparator(p);
    }
    
    public CollectorFormatter(final String format, final Formatter f, final Comparator<? super LogRecord> c) {
        this.generation = 1L;
        this.minMillis = CollectorFormatter.INIT_TIME;
        this.maxMillis = Long.MIN_VALUE;
        final String p = this.getClass().getName();
        this.fmt = ((format == null) ? this.initFormat(p) : format);
        this.formatter = f;
        this.comparator = c;
    }
    
    @Override
    public String format(final LogRecord record) {
        if (record == null) {
            throw new NullPointerException();
        }
        boolean accepted;
        do {
            final LogRecord peek = this.peek();
            final LogRecord update = this.apply((peek != null) ? peek : record, record);
            if (peek != update) {
                update.getSourceMethodName();
                accepted = this.acceptAndUpdate(peek, update);
            }
            else {
                accepted = this.accept(peek, record);
            }
        } while (!accepted);
        return "";
    }
    
    @Override
    public String getTail(final Handler h) {
        super.getTail(h);
        return this.formatRecord(h, true);
    }
    
    @Override
    public String toString() {
        String result;
        try {
            result = this.formatRecord(null, false);
        }
        catch (final RuntimeException ignore) {
            result = super.toString();
        }
        return result;
    }
    
    protected LogRecord apply(final LogRecord t, final LogRecord u) {
        if (t == null || u == null) {
            throw new NullPointerException();
        }
        if (this.comparator != null) {
            return (this.comparator.compare(t, u) >= 0) ? t : u;
        }
        return u;
    }
    
    private synchronized boolean accept(final LogRecord e, final LogRecord u) {
        final long millis = u.getMillis();
        final Throwable ex = u.getThrown();
        if (this.last == e) {
            final long count = this.count + 1L;
            this.count = count;
            if (count != 1L) {
                this.minMillis = Math.min(this.minMillis, millis);
            }
            else {
                this.minMillis = millis;
            }
            this.maxMillis = Math.max(this.maxMillis, millis);
            if (ex != null) {
                ++this.thrown;
            }
            return true;
        }
        return false;
    }
    
    private synchronized void reset(final long min) {
        if (this.last != null) {
            this.last = null;
            ++this.generation;
        }
        this.count = 0L;
        this.thrown = 0L;
        this.minMillis = min;
        this.maxMillis = Long.MIN_VALUE;
    }
    
    private String formatRecord(final Handler h, final boolean reset) {
        final LogRecord record;
        final long c;
        final long g;
        final long t;
        final long msl;
        long msh;
        final long now;
        synchronized (this) {
            record = this.last;
            c = this.count;
            g = this.generation;
            t = this.thrown;
            msl = this.minMillis;
            msh = this.maxMillis;
            now = System.currentTimeMillis();
            if (c == 0L) {
                msh = now;
            }
            if (reset) {
                this.reset(msh);
            }
        }
        final Formatter f = this.formatter;
        String head;
        String msg;
        String tail;
        if (f != null) {
            synchronized (f) {
                head = f.getHead(h);
                msg = ((record != null) ? f.format(record) : "");
                tail = f.getTail(h);
            }
        }
        else {
            head = "";
            msg = ((record != null) ? this.formatMessage(record) : "");
            tail = "";
        }
        Locale l = null;
        if (record != null) {
            final ResourceBundle rb = record.getResourceBundle();
            l = ((rb == null) ? null : rb.getLocale());
        }
        MessageFormat mf;
        if (l == null) {
            mf = new MessageFormat(this.fmt);
        }
        else {
            mf = new MessageFormat(this.fmt, l);
        }
        return mf.format(new Object[] { this.finish(head), this.finish(msg), this.finish(tail), c, c - 1L, t, c - t, msl, msh, msh - msl, CollectorFormatter.INIT_TIME, now, now - CollectorFormatter.INIT_TIME, g });
    }
    
    protected String finish(final String s) {
        return s.trim();
    }
    
    private synchronized LogRecord peek() {
        return this.last;
    }
    
    private synchronized boolean acceptAndUpdate(final LogRecord e, final LogRecord u) {
        if (this.accept(e, u)) {
            this.last = u;
            return true;
        }
        return false;
    }
    
    private String initFormat(final String p) {
        String v = LogManagerProperties.fromLogManager(p.concat(".format"));
        if (v == null || v.length() == 0) {
            v = "{0}{1}{2}{4,choice,-1#|0#|0<... {4,number,integer} more}\n";
        }
        return v;
    }
    
    private Formatter initFormatter(final String p) {
        final String v = LogManagerProperties.fromLogManager(p.concat(".formatter"));
        Formatter f;
        if (v != null && v.length() != 0) {
            if (!"null".equalsIgnoreCase(v)) {
                try {
                    f = LogManagerProperties.newFormatter(v);
                    return f;
                }
                catch (final RuntimeException re) {
                    throw re;
                }
                catch (final Exception e) {
                    throw new UndeclaredThrowableException(e);
                }
            }
            f = null;
        }
        else {
            f = Formatter.class.cast(new CompactFormatter());
        }
        return f;
    }
    
    private Comparator<? super LogRecord> initComparator(final String p) {
        final String name = LogManagerProperties.fromLogManager(p.concat(".comparator"));
        final String reverse = LogManagerProperties.fromLogManager(p.concat(".comparator.reverse"));
        Comparator<? super LogRecord> c;
        try {
            if (name != null && name.length() != 0) {
                if (!"null".equalsIgnoreCase(name)) {
                    c = LogManagerProperties.newComparator(name);
                    if (Boolean.parseBoolean(reverse)) {
                        assert c != null;
                        c = LogManagerProperties.reverseOrder(c);
                    }
                }
                else {
                    if (reverse != null) {
                        throw new IllegalArgumentException("No comparator to reverse.");
                    }
                    c = null;
                }
            }
            else {
                if (reverse != null) {
                    throw new IllegalArgumentException("No comparator to reverse.");
                }
                c = Comparator.class.cast(SeverityComparator.getInstance());
            }
        }
        catch (final RuntimeException re) {
            throw re;
        }
        catch (final Exception e) {
            throw new UndeclaredThrowableException(e);
        }
        return c;
    }
    
    static {
        INIT_TIME = System.currentTimeMillis();
    }
}
