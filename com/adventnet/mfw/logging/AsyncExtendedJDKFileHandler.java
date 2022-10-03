package com.adventnet.mfw.logging;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.FileHandler;

public class AsyncExtendedJDKFileHandler extends FileHandler
{
    protected static LoggerThread logger;
    private static final int OVERFLOW_DROP_LAST = 1;
    private static final int OVERFLOW_DROP_FIRST = 2;
    private static final int OVERFLOW_DROP_FLUSH = 3;
    private static final int OVERFLOW_DROP_CURRENT = 4;
    private static final int OVERFLOW_DROP_TYPE;
    private static final int DEFAULT_MAX_RECORDS;
    protected static LinkedBlockingDeque<LogEntry> queue;
    protected volatile boolean closed;
    
    public AsyncExtendedJDKFileHandler() throws IOException, SecurityException {
        this.closed = false;
    }
    
    public AsyncExtendedJDKFileHandler(final String pattern) throws IOException, SecurityException {
        super(pattern);
        this.closed = false;
    }
    
    public AsyncExtendedJDKFileHandler(final String pattern, final boolean append) throws IOException, SecurityException {
        super(pattern, append);
        this.closed = false;
    }
    
    public AsyncExtendedJDKFileHandler(final String pattern, final int limit, final int count) throws IOException, SecurityException {
        super(pattern, limit, count);
        this.closed = false;
    }
    
    public AsyncExtendedJDKFileHandler(final String pattern, final int limit, final int count, final boolean append) throws IOException, SecurityException {
        super(pattern, limit, count, append);
        this.closed = false;
    }
    
    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        super.close();
    }
    
    @Override
    public void publish(final LogRecord record) {
        if (!this.isLoggable(record)) {
            return;
        }
        final LogEntry entry = new LogEntry(record, this);
        boolean added = false;
        try {
            while (!added && !AsyncExtendedJDKFileHandler.queue.offer(entry)) {
                switch (AsyncExtendedJDKFileHandler.OVERFLOW_DROP_TYPE) {
                    case 1: {
                        AsyncExtendedJDKFileHandler.queue.pollLast();
                        continue;
                    }
                    case 2: {
                        AsyncExtendedJDKFileHandler.queue.pollFirst();
                        continue;
                    }
                    case 3: {
                        added = AsyncExtendedJDKFileHandler.queue.offer(entry, 1000L, TimeUnit.MILLISECONDS);
                        continue;
                    }
                    case 4: {
                        added = true;
                        continue;
                    }
                }
            }
        }
        catch (final InterruptedException x) {
            Thread.interrupted();
        }
    }
    
    protected void publishInternal(final LogRecord record) {
        super.publish(record);
    }
    
    static {
        AsyncExtendedJDKFileHandler.logger = new LoggerThread();
        OVERFLOW_DROP_TYPE = Integer.parseInt(System.getProperty("asyncOverflowDropType", "1"));
        DEFAULT_MAX_RECORDS = Integer.parseInt(System.getProperty("asyncMaxRecordCount", "10000"));
        AsyncExtendedJDKFileHandler.queue = new LinkedBlockingDeque<LogEntry>(AsyncExtendedJDKFileHandler.DEFAULT_MAX_RECORDS);
        final String serverHome = System.getProperty("server.home");
        final String logDir = serverHome + File.separator + "logs";
        final File file = new File(logDir);
        if (!file.exists()) {
            file.mkdir();
        }
        AsyncExtendedJDKFileHandler.logger.start();
    }
    
    protected static class LoggerThread extends Thread
    {
        protected boolean run;
        
        public LoggerThread() {
            this.setDaemon(this.run = true);
            this.setName("AsyncExtendedJDKFileHandler-" + System.identityHashCode(this));
        }
        
        @Override
        public void run() {
            while (this.run) {
                try {
                    final LogEntry entry = AsyncExtendedJDKFileHandler.queue.poll(1000L, TimeUnit.MILLISECONDS);
                    if (entry == null) {
                        continue;
                    }
                    entry.flush();
                }
                catch (final Exception x) {
                    x.printStackTrace();
                }
            }
        }
    }
    
    protected static class LogEntry
    {
        private LogRecord record;
        private AsyncExtendedJDKFileHandler handler;
        
        public LogEntry(final LogRecord record, final AsyncExtendedJDKFileHandler handler) {
            this.record = record;
            this.handler = handler;
        }
        
        public boolean flush() {
            if (this.handler.closed) {
                return false;
            }
            this.handler.publishInternal(this.record);
            return true;
        }
    }
}
