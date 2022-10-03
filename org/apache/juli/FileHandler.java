package org.apache.juli;

import java.util.concurrent.Executors;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;
import java.util.Calendar;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.logging.LogManager;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.logging.LogRecord;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;
import java.util.concurrent.locks.ReadWriteLock;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.logging.Handler;

public class FileHandler extends Handler
{
    public static final int DEFAULT_MAX_DAYS = -1;
    private static final ExecutorService DELETE_FILES_SERVICE;
    private volatile String date;
    private String directory;
    private String prefix;
    private String suffix;
    private boolean rotatable;
    private int maxDays;
    private volatile PrintWriter writer;
    protected final ReadWriteLock writerLock;
    private int bufferSize;
    private Pattern pattern;
    
    public FileHandler() {
        this(null, null, null, -1);
    }
    
    public FileHandler(final String directory, final String prefix, final String suffix) {
        this(directory, prefix, suffix, -1);
    }
    
    public FileHandler(final String directory, final String prefix, final String suffix, final int maxDays) {
        this.date = "";
        this.directory = null;
        this.prefix = null;
        this.suffix = null;
        this.rotatable = true;
        this.maxDays = -1;
        this.writer = null;
        this.writerLock = new ReentrantReadWriteLock();
        this.bufferSize = -1;
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
            if (this.rotatable && !this.date.equals(tsDate)) {
                this.writerLock.readLock().unlock();
                this.writerLock.writeLock().lock();
                try {
                    if (!this.date.equals(tsDate)) {
                        this.closeWriter();
                        this.date = tsDate;
                        this.openWriter();
                        this.clean();
                    }
                }
                finally {
                    this.writerLock.readLock().lock();
                    this.writerLock.writeLock().unlock();
                }
            }
            String result = null;
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
        this.date = ts.toString().substring(0, 10);
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
        this.pattern = Pattern.compile("^(" + Pattern.quote(this.prefix) + ")\\d{4}-\\d{1,2}-\\d{1,2}(" + Pattern.quote(this.suffix) + ")$");
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
                this.setFormatter(new OneLineFormatter());
            }
        }
        else {
            this.setFormatter(new OneLineFormatter());
        }
        this.setErrorManager(new ErrorManager());
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
    
    protected void open() {
        this.openWriter();
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
            final File pathname = new File(dir.getAbsoluteFile(), this.prefix + (this.rotatable ? this.date : "") + this.suffix);
            final File parent = pathname.getParentFile();
            if (!parent.mkdirs() && !parent.isDirectory()) {
                this.reportError("Unable to create [" + parent + "]", null, 4);
                this.writer = null;
                return;
            }
            final String encoding = this.getEncoding();
            fos = new FileOutputStream(pathname, true);
            os = ((this.bufferSize > 0) ? new BufferedOutputStream(fos, this.bufferSize) : fos);
            (this.writer = new PrintWriter((encoding != null) ? new OutputStreamWriter(os, encoding) : new OutputStreamWriter(os), false)).write(this.getFormatter().getHead(this));
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
        if (this.maxDays <= 0) {
            return;
        }
        FileHandler.DELETE_FILES_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                try (final DirectoryStream<Path> files = FileHandler.this.streamFilesForDelete()) {
                    for (final Path file : files) {
                        Files.delete(file);
                    }
                }
                catch (final IOException e) {
                    Handler.this.reportError("Unable to delete log files older than [" + FileHandler.this.maxDays + "] days", null, 0);
                }
            }
        });
    }
    
    private DirectoryStream<Path> streamFilesForDelete() throws IOException {
        final Date maxDaysOffset = this.getMaxDaysOffset();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return Files.newDirectoryStream(new File(this.directory).toPath(), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(final Path path) throws IOException {
                boolean result = false;
                final String date = FileHandler.this.obtainDateFromPath(path);
                if (date != null) {
                    try {
                        final Date dateFromFile = formatter.parse(date);
                        result = dateFromFile.before(maxDaysOffset);
                    }
                    catch (final ParseException ex) {}
                }
                return result;
            }
        });
    }
    
    private String obtainDateFromPath(final Path path) {
        final Path fileName = path.getFileName();
        if (fileName == null) {
            return null;
        }
        String date = fileName.toString();
        if (this.pattern.matcher(date).matches()) {
            date = date.substring(this.prefix.length());
            return date.substring(0, date.length() - this.suffix.length());
        }
        return null;
    }
    
    private Date getMaxDaysOffset() {
        final Calendar cal = Calendar.getInstance();
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        cal.add(5, -this.maxDays);
        return cal.getTime();
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
                        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                            @Override
                            public Void run() {
                                Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
                                return null;
                            }
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
                        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                            @Override
                            public Void run() {
                                Thread.currentThread().setContextClassLoader(loader);
                                return null;
                            }
                        });
                    }
                    else {
                        Thread.currentThread().setContextClassLoader(loader);
                    }
                }
            }
        });
    }
}
