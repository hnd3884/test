package org.apache.juli;

import java.util.LinkedHashMap;
import java.lang.management.ThreadInfo;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.StringWriter;
import java.util.logging.LogRecord;
import java.util.logging.LogManager;
import java.lang.management.ThreadMXBean;
import java.util.logging.Formatter;

public class OneLineFormatter extends Formatter
{
    private static final String UNKNOWN_THREAD_NAME = "Unknown thread with ID ";
    private static final Object threadMxBeanLock;
    private static volatile ThreadMXBean threadMxBean;
    private static final int THREAD_NAME_CACHE_SIZE = 10000;
    private static final ThreadLocal<ThreadNameCache> threadNameCache;
    private static final String DEFAULT_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss.SSS";
    private static final int globalCacheSize = 30;
    private static final int localCacheSize = 5;
    private ThreadLocal<DateFormatCache> localDateCache;
    private volatile MillisHandling millisHandling;
    
    public OneLineFormatter() {
        this.millisHandling = MillisHandling.APPEND;
        String timeFormat = LogManager.getLogManager().getProperty(OneLineFormatter.class.getName() + ".timeFormat");
        if (timeFormat == null) {
            timeFormat = "dd-MMM-yyyy HH:mm:ss.SSS";
        }
        this.setTimeFormat(timeFormat);
    }
    
    public void setTimeFormat(final String timeFormat) {
        String cachedTimeFormat;
        if (timeFormat.endsWith(".SSS")) {
            cachedTimeFormat = timeFormat.substring(0, timeFormat.length() - 4);
            this.millisHandling = MillisHandling.APPEND;
        }
        else if (timeFormat.contains("SSS")) {
            this.millisHandling = MillisHandling.REPLACE_SSS;
            cachedTimeFormat = timeFormat;
        }
        else if (timeFormat.contains("SS")) {
            this.millisHandling = MillisHandling.REPLACE_SS;
            cachedTimeFormat = timeFormat;
        }
        else if (timeFormat.contains("S")) {
            this.millisHandling = MillisHandling.REPLACE_S;
            cachedTimeFormat = timeFormat;
        }
        else {
            this.millisHandling = MillisHandling.NONE;
            cachedTimeFormat = timeFormat;
        }
        final DateFormatCache globalDateCache = new DateFormatCache(30, cachedTimeFormat, null);
        this.localDateCache = new ThreadLocal<DateFormatCache>() {
            @Override
            protected DateFormatCache initialValue() {
                return new DateFormatCache(5, cachedTimeFormat, globalDateCache);
            }
        };
    }
    
    public String getTimeFormat() {
        return this.localDateCache.get().getTimeFormat();
    }
    
    @Override
    public String format(final LogRecord record) {
        final StringBuilder sb = new StringBuilder();
        this.addTimestamp(sb, record.getMillis());
        sb.append(' ');
        sb.append(record.getLevel().getLocalizedName());
        sb.append(' ');
        sb.append('[');
        if (Thread.currentThread() instanceof AsyncFileHandler.LoggerThread) {
            sb.append(getThreadName(record.getThreadID()));
        }
        else {
            sb.append(Thread.currentThread().getName());
        }
        sb.append(']');
        sb.append(' ');
        sb.append(record.getSourceClassName());
        sb.append('.');
        sb.append(record.getSourceMethodName());
        sb.append(' ');
        sb.append(this.formatMessage(record));
        sb.append(System.lineSeparator());
        if (record.getThrown() != null) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new IndentingPrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            sb.append(sw.getBuffer());
        }
        return sb.toString();
    }
    
    protected void addTimestamp(final StringBuilder buf, final long timestamp) {
        final String cachedTimeStamp = this.localDateCache.get().getFormat(timestamp);
        if (this.millisHandling == MillisHandling.NONE) {
            buf.append(cachedTimeStamp);
        }
        else if (this.millisHandling == MillisHandling.APPEND) {
            buf.append(cachedTimeStamp);
            final long frac = timestamp % 1000L;
            buf.append('.');
            if (frac < 100L) {
                if (frac < 10L) {
                    buf.append('0');
                    buf.append('0');
                }
                else {
                    buf.append('0');
                }
            }
            buf.append(frac);
        }
        else {
            final long frac = timestamp % 1000L;
            final int insertStart = cachedTimeStamp.indexOf(35);
            buf.append(cachedTimeStamp.subSequence(0, insertStart));
            if (frac < 100L && this.millisHandling == MillisHandling.REPLACE_SSS) {
                buf.append('0');
                if (frac < 10L) {
                    buf.append('0');
                }
            }
            else if (frac < 10L && this.millisHandling == MillisHandling.REPLACE_SS) {
                buf.append('0');
            }
            buf.append(frac);
            if (this.millisHandling == MillisHandling.REPLACE_SSS) {
                buf.append(cachedTimeStamp.substring(insertStart + 3));
            }
            else if (this.millisHandling == MillisHandling.REPLACE_SS) {
                buf.append(cachedTimeStamp.substring(insertStart + 2));
            }
            else {
                buf.append(cachedTimeStamp.substring(insertStart + 1));
            }
        }
    }
    
    private static String getThreadName(final int logRecordThreadId) {
        final Map<Integer, String> cache = OneLineFormatter.threadNameCache.get();
        String result = cache.get(logRecordThreadId);
        if (result != null) {
            return result;
        }
        if (logRecordThreadId > 1073741823) {
            result = "Unknown thread with ID " + logRecordThreadId;
        }
        else {
            if (OneLineFormatter.threadMxBean == null) {
                synchronized (OneLineFormatter.threadMxBeanLock) {
                    if (OneLineFormatter.threadMxBean == null) {
                        OneLineFormatter.threadMxBean = ManagementFactory.getThreadMXBean();
                    }
                }
            }
            final ThreadInfo threadInfo = OneLineFormatter.threadMxBean.getThreadInfo(logRecordThreadId);
            if (threadInfo == null) {
                return Long.toString(logRecordThreadId);
            }
            result = threadInfo.getThreadName();
        }
        cache.put(logRecordThreadId, result);
        return result;
    }
    
    static {
        threadMxBeanLock = new Object();
        OneLineFormatter.threadMxBean = null;
        threadNameCache = new ThreadLocal<ThreadNameCache>() {
            @Override
            protected ThreadNameCache initialValue() {
                return new ThreadNameCache(10000);
            }
        };
    }
    
    private static class ThreadNameCache extends LinkedHashMap<Integer, String>
    {
        private static final long serialVersionUID = 1L;
        private final int cacheSize;
        
        public ThreadNameCache(final int cacheSize) {
            this.cacheSize = cacheSize;
        }
        
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Integer, String> eldest) {
            return this.size() > this.cacheSize;
        }
    }
    
    private static class IndentingPrintWriter extends PrintWriter
    {
        public IndentingPrintWriter(final Writer out) {
            super(out);
        }
        
        @Override
        public void println(final Object x) {
            super.print('\t');
            super.println(x);
        }
    }
    
    private enum MillisHandling
    {
        NONE, 
        APPEND, 
        REPLACE_S, 
        REPLACE_SS, 
        REPLACE_SSS;
    }
}
