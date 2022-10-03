package com.azul.crs.client.service;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;

public class GCRotatingLogTailer extends FileTailer
{
    private static final String CURRENT = "current";
    private long startTime;
    private int logCount;
    
    protected GCRotatingLogTailer(final String serviceName, final File file, final FileTailerListener listener, final long delayTimeout, final boolean completeOnStop, final int bufSize, final int logCount, final long startTime) {
        super(serviceName, file, listener, delayTimeout, false, completeOnStop, bufSize);
        this.logCount = logCount;
        this.startTime = startTime;
    }
    
    @Override
    protected void run() {
        FileInputStream reader = null;
        try {
            final String logName = this.file.getPath();
            int logNum = 0;
            File logFile = null;
            long checkTime = this.startTime;
            this.logger().info("looking for current file of GC log %s", logName);
            while (this.running) {
                try {
                    logNum = 0;
                    logFile = new File(String.format("%s.%d.%s", logName, logNum, "current"));
                    if (logFile.lastModified() > checkTime) {
                        reader = new FileInputStream(logFile);
                        checkTime = logFile.lastModified();
                        this.readBytes(reader);
                        break;
                    }
                }
                catch (final FileNotFoundException e) {
                    this.listener.fileNotFound();
                }
                logNum = (logNum + 1) % this.logCount;
                Thread.sleep(this.delayTimeout / this.logCount);
            }
            this.logger().info("tailing GC log starting from file %s", logFile.getName());
            while (this.running) {
                this.readBytes(reader);
                if (!logFile.exists()) {
                    while (this.running) {
                        logNum = (logNum + 1) % this.logCount;
                        logFile = new File(String.format("%s.%d.%s", logName, logNum, "current"));
                        if (logFile.lastModified() > checkTime) {
                            try (final FileInputStream saved = reader) {
                                reader = new FileInputStream(logFile);
                                checkTime = logFile.lastModified();
                                this.listener.fileRotated("current log number " + logNum);
                                this.readBytes(reader);
                            }
                            catch (final FileNotFoundException ex) {}
                        }
                        logFile = new File(String.format("%s.%d", logName, logNum));
                        if (logFile.lastModified() > checkTime) {
                            try (final FileInputStream saved = reader) {
                                reader = new FileInputStream(logFile);
                                checkTime = logFile.lastModified();
                                this.listener.fileRotated("next log number " + logNum);
                                this.readBytes(reader);
                            }
                            catch (final FileNotFoundException e) {
                                this.listener.fileNotFound();
                            }
                        }
                        logNum = (logNum - 1) % this.logCount;
                    }
                }
                Thread.sleep(this.delayTimeout);
            }
        }
        catch (final InterruptedException e2) {
            Thread.currentThread().interrupt();
            this.listener.interrupted();
        }
        catch (final Exception e3) {
            this.listener.handle(e3);
        }
        finally {
            this.closeReader(reader);
            this.running = false;
        }
    }
    
    public static class Builder extends FileTailer.Builder<Builder>
    {
        private int logCount;
        private long startTime;
        
        public Builder(final File file) {
            super(file);
        }
        
        public Builder logCount(final int logCount) {
            this.logCount = logCount;
            return this;
        }
        
        public Builder startTime(final long startTime) {
            this.startTime = startTime;
            return this;
        }
        
        @Override
        public GCRotatingLogTailer build() {
            return new GCRotatingLogTailer(this.serviceName, this.file, this.listener, this.delayTimeout, this.completeOnStop, this.bufSize, this.logCount, this.startTime);
        }
    }
}
