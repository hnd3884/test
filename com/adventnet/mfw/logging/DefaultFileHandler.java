package com.adventnet.mfw.logging;

import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.security.AccessController;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.ErrorManager;
import org.apache.juli.OneLineFormatter;
import java.util.logging.Formatter;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.logging.LogRecord;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.ExecutorService;
import java.util.logging.Handler;

public class DefaultFileHandler extends Handler
{
    public static final int DEFAULT_MAX_DAYS = -1;
    private static final ExecutorService DELETE_FILES_SERVICE;
    private volatile String date;
    private String directory;
    private String prefix;
    private String suffix;
    private boolean rotatable;
    private int maxDays;
    private volatile MeteredPrintWriter writer;
    protected final ReadWriteLock writerLock;
    private int bufferSize;
    private long limit;
    private int sizeRotateCount;
    private HandlerInfo info;
    private boolean firstSet;
    
    public DefaultFileHandler() {
        this(null, null, null, -1);
    }
    
    public DefaultFileHandler(final String directory, final String prefix, final String suffix) {
        this(directory, prefix, suffix, -1);
    }
    
    public DefaultFileHandler(final String directory, final String prefix, final String suffix, final int maxDays) {
        this.date = "";
        this.rotatable = true;
        this.writer = null;
        this.writerLock = new ReentrantReadWriteLock();
        this.bufferSize = -1;
        this.sizeRotateCount = 0;
        this.firstSet = true;
        this.directory = directory;
        this.prefix = prefix;
        this.suffix = suffix;
        this.maxDays = maxDays;
        this.configure();
        this.openWriter();
        this.clean();
    }
    
    @Override
    public void publish(final LogRecord record) {
        if (!this.isLoggable(record)) {
            return;
        }
        final Timestamp ts = new Timestamp(System.currentTimeMillis());
        final String tsDate = ts.toString().substring(0, 10);
        this.writerLock.readLock().lock();
        try {
            if (this.needToRotateForDate(tsDate)) {
                this.rotateForDate(tsDate);
            }
            else if (this.needToRotateForSize()) {
                this.rotateForSize(tsDate);
            }
            String result;
            try {
                result = this.getFormatter().format(record);
            }
            catch (final Exception e) {
                this.reportError(null, e, 5);
                return;
            }
            try {
                if (this.writer != null) {
                    this.writer.write(result);
                    if (this.bufferSize < 0) {
                        this.writer.flush();
                    }
                }
                else {
                    this.reportError("FileHandler is closed or not yet initialized, unable to log [" + result + "]", null, 1);
                }
            }
            catch (final Exception e) {
                this.reportError(null, e, 1);
            }
        }
        finally {
            this.writerLock.readLock().unlock();
        }
    }
    
    private void rotateForSize(final String tsDate) {
        this.writerLock.readLock().unlock();
        this.writerLock.writeLock().lock();
        try {
            if (this.fastCheckRotate(tsDate)) {
                this.closeWriter();
                this.date = tsDate;
                ++this.sizeRotateCount;
                this.openWriter();
                this.clean();
            }
        }
        finally {
            this.writerLock.readLock().lock();
            this.writerLock.writeLock().unlock();
        }
    }
    
    private void rotateForDate(final String tsDate) {
        this.writerLock.readLock().unlock();
        this.writerLock.writeLock().lock();
        try {
            if (this.fastCheckRotate(tsDate)) {
                this.closeWriter();
                this.date = tsDate;
                this.sizeRotateCount = 0;
                this.openWriter();
                this.clean();
            }
        }
        finally {
            this.writerLock.readLock().lock();
            this.writerLock.writeLock().unlock();
        }
    }
    
    private boolean fastCheckRotate(final String tsDate) {
        return !this.date.equals(tsDate) || this.writer.written >= this.limit;
    }
    
    private boolean needToRotateForDate(final String tsDate) {
        return this.rotatable && !this.date.equals(tsDate);
    }
    
    private boolean needToRotateForSize() {
        return this.rotatable && this.writer.written >= this.limit;
    }
    
    @Override
    public void close() {
        this.closeWriter();
    }
    
    protected void closeWriter() {
        this.writerLock.writeLock().lock();
        try {
            if (this.writer == null) {
                return;
            }
            this.writer.write(this.getFormatter().getTail(this));
            this.writer.flush();
            this.writer.close();
            this.writer = null;
            this.date = "";
        }
        catch (final Exception e) {
            this.reportError(null, e, 3);
        }
        finally {
            this.writerLock.writeLock().unlock();
        }
    }
    
    @Override
    public void flush() {
        this.writerLock.readLock().lock();
        try {
            if (this.writer == null) {
                return;
            }
            this.writer.flush();
        }
        catch (final Exception e) {
            this.reportError(null, e, 2);
        }
        finally {
            this.writerLock.readLock().unlock();
        }
    }
    
    private void configure() {
        final Timestamp ts = new Timestamp(System.currentTimeMillis());
        final String tsString = ts.toString().substring(0, 19);
        this.date = tsString.substring(0, 10);
        final String className = this.getClass().getName();
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        this.rotatable = Boolean.parseBoolean(this.getProperty(className + ".rotatable", "true"));
        if (this.directory == null) {
            this.directory = this.getProperty(className + ".directory", "logs");
        }
        if (this.prefix == null) {
            this.prefix = this.getProperty(className + ".prefix", "juli.");
        }
        if (this.suffix == null) {
            this.suffix = this.getProperty(className + ".suffix", ".log");
        }
        final boolean shouldCheckForRedundantSeparator = !this.rotatable && !this.prefix.isEmpty() && !this.suffix.isEmpty();
        if (shouldCheckForRedundantSeparator && this.prefix.charAt(this.prefix.length() - 1) == this.suffix.charAt(0)) {
            this.suffix = this.suffix.substring(1);
        }
        final String sMaxDays = this.getProperty(className + ".maxDays", String.valueOf(-1));
        if (this.maxDays <= 0) {
            try {
                this.maxDays = Integer.parseInt(sMaxDays);
            }
            catch (final NumberFormatException ex) {}
        }
        final String sBufferSize = this.getProperty(className + ".bufferSize", String.valueOf(this.bufferSize));
        try {
            this.bufferSize = Integer.parseInt(sBufferSize);
        }
        catch (final NumberFormatException ex2) {}
        final String encoding = this.getProperty(className + ".encoding", null);
        if (encoding != null && encoding.length() > 0) {
            try {
                this.setEncoding(encoding);
            }
            catch (final UnsupportedEncodingException ex3) {}
        }
        this.setLevel(Level.parse(this.getProperty(className + ".level", "" + Level.ALL)));
        final String filterName = this.getProperty(className + ".filter", null);
        if (filterName != null) {
            try {
                this.setFilter((Filter)cl.loadClass(filterName).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]));
            }
            catch (final Exception ex4) {}
        }
        final String formatterName = this.getProperty(className + ".formatter", null);
        if (formatterName != null) {
            try {
                this.setFormatter((Formatter)cl.loadClass(formatterName).getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]));
            }
            catch (final Exception e) {
                this.setFormatter((Formatter)new OneLineFormatter());
            }
        }
        else {
            this.setFormatter((Formatter)new OneLineFormatter());
        }
        this.setErrorManager(new ErrorManager());
        final String limitName = this.getProperty(className + ".limit", this.getProperty("java.util.logging.FileHandler.limit", "67108864"));
        this.limit = Long.parseLong(limitName);
        final String archiveDir = this.getProperty(className + ".archive.dir", "../logs/archive/");
        final String logDir = this.getProperty(className + ".directory", "../logs");
        final String handlerPreFix = this.getProperty(className + ".prefix", "juli.");
        final String handlerSuffix = this.getProperty(className + ".suffix", ".log");
        final boolean isArchiveEnabled = Boolean.parseBoolean(this.getProperty(className + ".archive.enable", "false"));
        final int days = Integer.parseInt(this.getProperty(className + ".archive.interval", "5"));
        final int maxFilesPerDay = Integer.parseInt(this.getProperty(className + ".perday", "3"));
        LogsArchiver.addHandlerInfo(this.info = new HandlerInfo(className, handlerPreFix, handlerSuffix, logDir, archiveDir, days, isArchiveEnabled, maxFilesPerDay));
    }
    
    private String getProperty(final String name, final String defaultValue) {
        String value = LogManager.getLogManager().getProperty(name);
        if (value == null) {
            value = defaultValue;
        }
        else {
            value = value.trim();
        }
        return value;
    }
    
    protected void openWriter() {
        final File dir = new File(this.directory);
        if (!dir.mkdirs() && !dir.isDirectory()) {
            this.reportError("Unable to create [" + dir + "]", null, 4);
            this.writer = null;
            return;
        }
        this.writerLock.writeLock().lock();
        FileOutputStream fos = null;
        OutputStream os = null;
        try {
            File pathname;
            if (this.firstSet) {
                final String actualFileNamePrefix = this.prefix + (this.rotatable ? this.date : "");
                final File[] filesInLogDir = dir.listFiles((d, name) -> name != null && name.startsWith(actualFileNamePrefix));
                if (filesInLogDir == null || filesInLogDir.length == 0) {
                    final String actualFileName = actualFileNamePrefix + this.suffix;
                    pathname = new File(dir, actualFileName);
                }
                else {
                    Arrays.sort(filesInLogDir, Comparator.comparingLong(File::lastModified));
                    pathname = filesInLogDir[filesInLogDir.length - 1];
                    final String simpleName = pathname.getName();
                    if (!simpleName.equals(actualFileNamePrefix + this.suffix)) {
                        final int startPos = actualFileNamePrefix.length() + 1;
                        final String posSubString = simpleName.substring(startPos, simpleName.lastIndexOf(this.suffix));
                        this.sizeRotateCount = Integer.parseInt(posSubString);
                    }
                }
                this.firstSet = false;
            }
            else {
                final String actualFileName2 = this.prefix + (this.rotatable ? (this.date + ((this.sizeRotateCount == 0) ? "" : ("_" + this.sizeRotateCount))) : "") + this.suffix;
                pathname = new File(dir, actualFileName2);
            }
            final File parent = pathname.getParentFile();
            if (!parent.mkdirs() && !parent.isDirectory()) {
                this.reportError("Unable to create [" + parent + "]", null, 4);
                this.writer = null;
                return;
            }
            final String encoding = this.getEncoding();
            fos = new FileOutputStream(pathname, true);
            os = ((this.bufferSize > 0) ? new BufferedOutputStream(fos, this.bufferSize) : fos);
            (this.writer = new MeteredPrintWriter((encoding != null) ? new OutputStreamWriter(os, encoding) : new OutputStreamWriter(os), false, pathname.length())).write(this.getFormatter().getHead(this));
        }
        catch (final Exception e) {
            this.reportError(null, e, 4);
            this.writer = null;
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final IOException ex) {}
            }
            if (os != null) {
                try {
                    os.close();
                }
                catch (final IOException ex2) {}
            }
        }
        finally {
            this.writerLock.writeLock().unlock();
        }
    }
    
    private void clean() {
        DefaultFileHandler.DELETE_FILES_SERVICE.submit(() -> {
            try {
                final FilenameFilter filter = (d, name) -> {
                    final void void1;
                    if (name != null) {
                        if (name.startsWith(this.prefix + (this.rotatable ? this.date : ""))) {
                            return (boolean)void1;
                        }
                    }
                    return (boolean)void1;
                };
                LogsArchiver.archiveLogs(this.info, filter);
            }
            catch (final Exception e) {
                this.reportError(e.getMessage(), e, 0);
            }
        });
    }
    
    static {
        DELETE_FILES_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactory() {
            private static final String NAME_PREFIX = "FileHandlerLogFilesCleaner-";
            private final boolean isSecurityEnabled;
            private final ThreadGroup group;
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            
            {
                final SecurityManager s = System.getSecurityManager();
                if (s == null) {
                    this.isSecurityEnabled = false;
                    this.group = Thread.currentThread().getThreadGroup();
                }
                else {
                    this.isSecurityEnabled = true;
                    this.group = s.getThreadGroup();
                }
            }
            
            @Override
            public Thread newThread(final Runnable r) {
                final ClassLoader loader = Thread.currentThread().getContextClassLoader();
                try {
                    if (this.isSecurityEnabled) {
                        AccessController.doPrivileged(() -> {
                            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                            return null;
                        });
                    }
                    else {
                        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                    }
                    final Thread t = new Thread(this.group, r, "FileHandlerLogFilesCleaner-" + this.threadNumber.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
                finally {
                    if (this.isSecurityEnabled) {
                        AccessController.doPrivileged(() -> {
                            Thread.currentThread().setContextClassLoader(loader);
                            return null;
                        });
                    }
                    else {
                        Thread.currentThread().setContextClassLoader(loader);
                    }
                }
            }
        });
    }
    
    private static final class MeteredPrintWriter
    {
        private final PrintWriter printWriter;
        long written;
        
        public MeteredPrintWriter(final Writer out, final boolean autoFlush, final long length) {
            this.printWriter = new PrintWriter(out, autoFlush);
            this.written = length;
        }
        
        public void write(final String result) {
            this.printWriter.write(result);
            this.written += result.length();
        }
        
        public void flush() {
            this.printWriter.flush();
        }
        
        public void close() {
            this.printWriter.close();
        }
    }
}
