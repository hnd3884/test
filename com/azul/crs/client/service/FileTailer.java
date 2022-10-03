package com.azul.crs.client.service;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import com.azul.crs.shared.Utils;
import java.io.File;

public class FileTailer implements ClientService
{
    private static final int DEFAULT_DELAY_TIMEOUT = 1000;
    private static final int DEFAULT_BUFSIZE = 4096;
    private static final int EOF = -1;
    protected final String serviceName;
    protected final File file;
    protected final byte[] inputBuf;
    protected final long delayTimeout;
    protected final boolean fromEnd;
    protected final boolean completeOnStop;
    protected final FileTailerListener listener;
    protected volatile boolean running;
    protected volatile long deadlineTimeCount;
    protected Thread thread;
    
    protected FileTailer(final String serviceName, final File file, final FileTailerListener listener, final long delayTimeout, final boolean fromEnd, final boolean completeOnStop, final int bufSize) {
        this.file = file;
        this.delayTimeout = delayTimeout;
        this.fromEnd = fromEnd;
        this.completeOnStop = completeOnStop;
        this.inputBuf = new byte[bufSize];
        this.listener = listener;
        this.serviceName = ((serviceName == null) ? super.serviceName() : serviceName);
    }
    
    @Override
    public String serviceName() {
        return this.serviceName;
    }
    
    @Override
    public synchronized void start() {
        if (this.running) {
            throw new IllegalStateException(this.serviceName() + " is running already");
        }
        this.running = true;
        (this.thread = new Thread(this::run)).setDaemon(true);
        this.thread.setName("CRSFileTailer");
        this.thread.start();
    }
    
    @Override
    public synchronized void stop(final long deadline) {
        if (!this.running) {
            throw new IllegalStateException("File tailer has not been started");
        }
        try {
            this.running = false;
            this.thread.interrupt();
            this.deadlineTimeCount = deadline;
            final long timeoutMs = -Utils.elapsedTimeMillis(deadline);
            if (timeoutMs > 0L) {
                this.thread.join(timeoutMs);
            }
        }
        catch (final InterruptedException ex) {}
    }
    
    protected void run() {
        FileInputStream reader = null;
        try {
            long checkTime = 0L;
            long position = 0L;
            this.logger().info("looking for file %s", this.file.getName());
            while (this.running && reader == null) {
                try {
                    reader = new FileInputStream(this.file);
                }
                catch (final FileNotFoundException e) {
                    this.listener.fileNotFound();
                }
                if (reader == null) {
                    Thread.sleep(this.delayTimeout);
                }
                else {
                    try {
                        position = (this.fromEnd ? reader.skip(this.file.length()) : this.readBytes(reader));
                        checkTime = this.file.lastModified();
                    }
                    catch (final IOException ioe) {
                        this.listener.handle(ioe);
                    }
                }
            }
            this.logger().info("tailing file %s", this.file.getName());
            while (this.running) {
                final boolean newer = this.file.lastModified() > checkTime;
                final long length = this.file.length();
                if (length < position) {
                    this.listener.fileRotated("");
                    try (final FileInputStream saved = reader) {
                        reader = new FileInputStream(this.file);
                        this.readBytes(saved);
                        position = 0L;
                    }
                    catch (final FileNotFoundException e2) {
                        this.listener.fileNotFound();
                        Thread.sleep(this.delayTimeout);
                    }
                }
                else {
                    if (length > position) {
                        position += this.readBytes(reader);
                        checkTime = this.file.lastModified();
                    }
                    else if (newer) {
                        try {
                            reader = new FileInputStream(this.file);
                            position = this.readBytes(reader);
                            checkTime = this.file.lastModified();
                        }
                        catch (final FileNotFoundException e2) {
                            this.listener.fileNotFound();
                            Thread.sleep(this.delayTimeout);
                        }
                    }
                    Thread.sleep(this.delayTimeout);
                }
            }
        }
        catch (final InterruptedException e3) {
            Thread.currentThread().interrupt();
            this.listener.interrupted();
        }
        catch (final Exception e4) {
            this.listener.handle(e4);
        }
        finally {
            this.closeReader(reader);
            this.running = false;
        }
    }
    
    protected void closeReader(final FileInputStream reader) {
        if (reader != null) {
            try {
                if (this.completeOnStop) {
                    this.readBytes(reader);
                }
                reader.close();
            }
            catch (final IOException e) {
                this.listener.handle(e);
            }
        }
    }
    
    protected long readBytes(final FileInputStream reader) {
        int total = 0;
        try {
            while (this.running || (this.completeOnStop && Utils.currentTimeCount() < this.deadlineTimeCount)) {
                final int num = (reader.available() > 0) ? reader.read(this.inputBuf) : -1;
                if (num <= 0) {
                    break;
                }
                total += num;
                this.listener.handle(this.inputBuf, num);
            }
            this.listener.eofReached();
            return total;
        }
        catch (final IOException ioe) {
            this.listener.handle(ioe);
            return total;
        }
    }
    
    public static class Builder<T extends Builder>
    {
        protected File file;
        protected FileTailerListener listener;
        protected long delayTimeout;
        protected int bufSize;
        protected boolean fromEnd;
        protected boolean completeOnStop;
        protected String serviceName;
        
        public Builder(final File file) {
            this.delayTimeout = 1000L;
            this.bufSize = 4096;
            this.fromEnd = false;
            this.completeOnStop = true;
            this.file = file;
        }
        
        public T listener(final FileTailerListener listener) {
            this.listener = listener;
            return (T)this;
        }
        
        public T delayTimeout(final long delayTimeout) {
            this.delayTimeout = delayTimeout;
            return (T)this;
        }
        
        public T bufSize(final int bufSize) {
            this.bufSize = bufSize;
            return (T)this;
        }
        
        public T fromEnd(final boolean fromEnd) {
            this.fromEnd = fromEnd;
            return (T)this;
        }
        
        public T completeOnStop(final boolean completeOnStop) {
            this.completeOnStop = completeOnStop;
            return (T)this;
        }
        
        public T serviceName(final String serviceName) {
            this.serviceName = serviceName;
            return (T)this;
        }
        
        public FileTailer build() {
            return new FileTailer(this.serviceName, this.file, this.listener, this.delayTimeout, this.fromEnd, this.completeOnStop, this.bufSize);
        }
    }
}
