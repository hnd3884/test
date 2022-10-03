package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.concurrent.Executor;
import java.util.ArrayList;
import java.util.logging.Level;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.List;
import java.lang.ref.ReferenceQueue;
import java.util.logging.Logger;
import java.lang.ref.WeakReference;

final class WeakDataFile extends WeakReference<DataFile>
{
    private static final Logger LOGGER;
    private static ReferenceQueue<DataFile> refQueue;
    private static List<WeakDataFile> refList;
    private final File file;
    private final RandomAccessFile raf;
    private static boolean hasCleanUpExecutor;
    
    WeakDataFile(final DataFile df, final File file) {
        super(df, WeakDataFile.refQueue);
        WeakDataFile.refList.add(this);
        this.file = file;
        try {
            this.raf = new RandomAccessFile(file, "rw");
        }
        catch (final IOException ioe) {
            throw new MIMEParsingException(ioe);
        }
        if (!WeakDataFile.hasCleanUpExecutor) {
            drainRefQueueBounded();
        }
    }
    
    synchronized void read(final long pointer, final byte[] buf, final int offset, final int length) {
        try {
            this.raf.seek(pointer);
            this.raf.readFully(buf, offset, length);
        }
        catch (final IOException ioe) {
            throw new MIMEParsingException(ioe);
        }
    }
    
    synchronized long writeTo(final long pointer, final byte[] data, final int offset, final int length) {
        try {
            this.raf.seek(pointer);
            this.raf.write(data, offset, length);
            return this.raf.getFilePointer();
        }
        catch (final IOException ioe) {
            throw new MIMEParsingException(ioe);
        }
    }
    
    void close() {
        if (WeakDataFile.LOGGER.isLoggable(Level.FINE)) {
            WeakDataFile.LOGGER.log(Level.FINE, "Deleting file = {0}", this.file.getName());
        }
        WeakDataFile.refList.remove(this);
        try {
            this.raf.close();
            final boolean deleted = this.file.delete();
            if (!deleted && WeakDataFile.LOGGER.isLoggable(Level.INFO)) {
                WeakDataFile.LOGGER.log(Level.INFO, "File {0} was not deleted", this.file.getAbsolutePath());
            }
        }
        catch (final IOException ioe) {
            throw new MIMEParsingException(ioe);
        }
    }
    
    void renameTo(final File f) {
        if (WeakDataFile.LOGGER.isLoggable(Level.FINE)) {
            WeakDataFile.LOGGER.log(Level.FINE, "Moving file={0} to={1}", new Object[] { this.file, f });
        }
        WeakDataFile.refList.remove(this);
        try {
            this.raf.close();
            final boolean renamed = this.file.renameTo(f);
            if (!renamed && WeakDataFile.LOGGER.isLoggable(Level.INFO)) {
                WeakDataFile.LOGGER.log(Level.INFO, "File {0} was not moved to {1}", new Object[] { this.file.getAbsolutePath(), f.getAbsolutePath() });
            }
        }
        catch (final IOException ioe) {
            throw new MIMEParsingException(ioe);
        }
    }
    
    static void drainRefQueueBounded() {
        WeakDataFile weak;
        while ((weak = (WeakDataFile)WeakDataFile.refQueue.poll()) != null) {
            if (WeakDataFile.LOGGER.isLoggable(Level.FINE)) {
                WeakDataFile.LOGGER.log(Level.FINE, "Cleaning file = {0} from reference queue.", weak.file);
            }
            weak.close();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(WeakDataFile.class.getName());
        WeakDataFile.refQueue = new ReferenceQueue<DataFile>();
        WeakDataFile.refList = new ArrayList<WeakDataFile>();
        WeakDataFile.hasCleanUpExecutor = false;
        final CleanUpExecutorFactory executorFactory = CleanUpExecutorFactory.newInstance();
        if (executorFactory != null) {
            if (WeakDataFile.LOGGER.isLoggable(Level.FINE)) {
                WeakDataFile.LOGGER.log(Level.FINE, "Initializing clean up executor for MIMEPULL: {0}", executorFactory.getClass().getName());
            }
            final Executor executor = executorFactory.getExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            while (true) {
                                final WeakDataFile weak = (WeakDataFile)WeakDataFile.refQueue.remove();
                                if (WeakDataFile.LOGGER.isLoggable(Level.FINE)) {
                                    WeakDataFile.LOGGER.log(Level.FINE, "Cleaning file = {0} from reference queue.", weak.file);
                                }
                                weak.close();
                            }
                        }
                        catch (final InterruptedException ex) {
                            continue;
                        }
                        break;
                    }
                }
            });
            WeakDataFile.hasCleanUpExecutor = true;
        }
    }
}
