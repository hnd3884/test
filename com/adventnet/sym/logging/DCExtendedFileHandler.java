package com.adventnet.sym.logging;

import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.LogRecord;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.LinkOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.logging.ErrorManager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.LoggingPermission;
import java.util.logging.LogManager;
import java.util.logging.Formatter;
import java.util.logging.XMLFormatter;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.security.Permission;
import java.util.HashMap;
import java.util.Set;
import java.io.File;
import java.nio.channels.FileChannel;
import java.util.logging.StreamHandler;

public class DCExtendedFileHandler extends StreamHandler
{
    private MeteredStream meter;
    private boolean append;
    private int limit;
    private int count;
    private String pattern;
    private String lockFileName;
    private FileChannel lockFileChannel;
    private File[] files;
    private static final int DEFAULT_MAX_LOCKS = 100;
    private static int maxLocks;
    private static final Set<String> LOCKS;
    static String backupLogDir;
    final String logDirec;
    private static HashMap<String, Integer> backupLogProp;
    private static HashMap<String, Integer> logProp;
    private final Permission controlPermission;
    
    protected static HashMap getLogProp() {
        return DCExtendedFileHandler.logProp;
    }
    
    protected static HashMap getBackupLogProp() {
        return DCExtendedFileHandler.backupLogProp;
    }
    
    protected static synchronized void setBackupLogProp(final String fname, final int incrementBy) {
        final int temp = DCExtendedFileHandler.backupLogProp.getOrDefault(fname, 0);
        DCExtendedFileHandler.backupLogProp.put(fname, temp + incrementBy);
    }
    
    protected static void loadProp() {
        FileInputStream fis = null;
        final Properties props = new Properties();
        final String fname = System.getProperty("server.home") + File.separator + "conf" + File.separator + "logging.properties";
        try {
            if (new File(fname).exists()) {
                fis = new FileInputStream(fname);
                props.load(fis);
                fis.close();
                final File directory = new File(DCExtendedFileHandler.backupLogDir);
                final Enumeration<String> enums = (Enumeration<String>)props.propertyNames();
                while (enums.hasMoreElements()) {
                    String propName = enums.nextElement();
                    if (propName.trim().endsWith("com.adventnet.sym.logging.DCExtendedFileHandler.pattern")) {
                        final String filepattern = "-" + props.getProperty(propName).split("/logs/")[1].replace("%g", "");
                        propName = propName.replaceAll(".pattern", ".backupcount");
                        final int backup_count = (Integer.valueOf(props.getProperty(propName)) > 50) ? 50 : Integer.valueOf(props.getProperty(propName));
                        DCExtendedFileHandler.logProp.put(filepattern, backup_count);
                        final File[] backfiles = directory.listFiles((d, s) -> s.contains(s2));
                        DCExtendedFileHandler.backupLogProp.put(filepattern, backfiles.length);
                    }
                }
            }
        }
        catch (final Exception e) {
            throw new Error("Unexpected exception " + e);
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException ex) {}
        }
    }
    
    private void open(final File fname, final boolean append) throws IOException {
        int len = 0;
        if (append) {
            len = (int)fname.length();
        }
        final FileOutputStream fout = new FileOutputStream(fname.toString(), append);
        final BufferedOutputStream bout = new BufferedOutputStream(fout);
        this.setOutputStream(this.meter = new MeteredStream(bout, len));
    }
    
    private void configure() {
        final String cname = this.getClass().getName();
        this.pattern = getStringProperty(cname + ".pattern", "%h/java%u.log");
        this.limit = this.getIntProperty(cname + ".limit", 0);
        if (this.limit < 0) {
            this.limit = 0;
        }
        this.count = this.getIntProperty(cname + ".count", 1);
        if (this.count <= 0) {
            this.count = 1;
        }
        this.append = this.getBooleanProperty(cname + ".append", false);
        this.setLevel(this.getLevelProperty(cname + ".level", Level.ALL));
        this.setFilter(this.getFilterProperty(cname + ".filter", null));
        this.setFormatter(this.getFormatterProperty(cname + ".formatter", new XMLFormatter()));
        try {
            this.setEncoding(getStringProperty(cname + ".encoding", null));
        }
        catch (final Exception ex) {
            try {
                this.setEncoding(null);
            }
            catch (final Exception ex2) {}
        }
    }
    
    private static String getStringProperty(final String name, final String defaultValue) {
        final String val = LogManager.getLogManager().getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        return val.trim();
    }
    
    int getIntProperty(final String name, final int defaultValue) {
        final String val = LogManager.getLogManager().getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        }
        catch (final Exception ex) {
            return defaultValue;
        }
    }
    
    boolean getBooleanProperty(final String name, final boolean defaultValue) {
        String val = LogManager.getLogManager().getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        val = val.toLowerCase();
        return val.equals("true") || val.equals("1") || (!val.equals("false") && !val.equals("0") && defaultValue);
    }
    
    Level getLevelProperty(final String name, final Level defaultValue) {
        final String val = LogManager.getLogManager().getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        final Level l = Level.parse(val.trim());
        return (l != null) ? l : defaultValue;
    }
    
    Filter getFilterProperty(final String name, final Filter defaultValue) {
        final String val = LogManager.getLogManager().getProperty(name);
        try {
            if (val != null) {
                final Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(val);
                return (Filter)clz.newInstance();
            }
        }
        catch (final Exception ex) {}
        return defaultValue;
    }
    
    Formatter getFormatterProperty(final String name, final Formatter defaultValue) {
        final String val = LogManager.getLogManager().getProperty(name);
        try {
            if (val != null) {
                final Class<?> clz = ClassLoader.getSystemClassLoader().loadClass(val);
                return (Formatter)clz.newInstance();
            }
        }
        catch (final Exception ex) {}
        return defaultValue;
    }
    
    public DCExtendedFileHandler() throws IOException, SecurityException {
        this.logDirec = System.getProperty("server.home") + File.separator + "logs";
        this.controlPermission = new LoggingPermission("control", null);
        this.checkPermission();
        this.configure();
        this.openFiles();
    }
    
    public DCExtendedFileHandler(final String pattern) throws IOException, SecurityException {
        this.logDirec = System.getProperty("server.home") + File.separator + "logs";
        this.controlPermission = new LoggingPermission("control", null);
        if (pattern.length() < 1) {
            throw new IllegalArgumentException();
        }
        this.checkPermission();
        this.configure();
        this.pattern = pattern;
        this.limit = 0;
        this.count = 1;
        this.openFiles();
    }
    
    public DCExtendedFileHandler(final String pattern, final boolean append) throws IOException, SecurityException {
        this.logDirec = System.getProperty("server.home") + File.separator + "logs";
        this.controlPermission = new LoggingPermission("control", null);
        if (pattern.length() < 1) {
            throw new IllegalArgumentException();
        }
        this.checkPermission();
        this.configure();
        this.pattern = pattern;
        this.limit = 0;
        this.count = 1;
        this.append = append;
        this.openFiles();
    }
    
    public DCExtendedFileHandler(final String pattern, final int limit, final int count) throws IOException, SecurityException {
        this.logDirec = System.getProperty("server.home") + File.separator + "logs";
        this.controlPermission = new LoggingPermission("control", null);
        if (limit < 0 || count < 1 || pattern.length() < 1) {
            throw new IllegalArgumentException();
        }
        this.checkPermission();
        this.configure();
        this.pattern = pattern;
        this.limit = limit;
        this.count = count;
        this.openFiles();
    }
    
    public DCExtendedFileHandler(final String pattern, final int limit, final int count, final boolean append) throws IOException, SecurityException {
        this.logDirec = System.getProperty("server.home") + File.separator + "logs";
        this.controlPermission = new LoggingPermission("control", null);
        if (limit < 0 || count < 1 || pattern.length() < 1) {
            throw new IllegalArgumentException();
        }
        this.checkPermission();
        this.configure();
        this.pattern = pattern;
        this.limit = limit;
        this.count = count;
        this.append = append;
        this.openFiles();
    }
    
    private boolean isParentWritable(final Path path) {
        Path parent = path.getParent();
        if (parent == null) {
            parent = path.toAbsolutePath().getParent();
        }
        return parent != null && Files.isWritable(parent);
    }
    
    @Override
    void checkPermission() {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(this.controlPermission);
        }
    }
    
    private void openFiles() throws IOException {
        this.checkPermission();
        if (this.count < 1) {
            throw new IllegalArgumentException("file count = " + this.count);
        }
        if (this.limit < 0) {
            this.limit = 0;
        }
        final InitializationErrorManager em = new InitializationErrorManager();
        this.setErrorManager(em);
        int unique = -1;
        while (++unique <= DCExtendedFileHandler.maxLocks) {
            this.lockFileName = this.generate(this.pattern, 0, unique).toString() + ".lck";
            Label_0413: {
                synchronized (DCExtendedFileHandler.LOCKS) {
                    if (DCExtendedFileHandler.LOCKS.contains(this.lockFileName)) {
                        continue;
                    }
                    final Path lockFilePath = Paths.get(this.lockFileName, new String[0]);
                    FileChannel channel = null;
                    int retries = -1;
                    boolean fileCreated = false;
                    while (channel == null && retries++ < 1) {
                        try {
                            channel = FileChannel.open(lockFilePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                            fileCreated = true;
                        }
                        catch (final FileAlreadyExistsException ix) {
                            if (Files.isRegularFile(lockFilePath, LinkOption.NOFOLLOW_LINKS) && this.isParentWritable(lockFilePath)) {
                                try {
                                    channel = FileChannel.open(lockFilePath, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                                    continue;
                                }
                                catch (final NoSuchFileException x) {
                                    continue;
                                }
                                catch (final IOException x2) {}
                                break;
                                continue;
                            }
                            break;
                        }
                    }
                    if (channel == null) {
                        continue;
                    }
                    this.lockFileChannel = channel;
                    boolean available;
                    try {
                        available = (this.lockFileChannel.tryLock() != null);
                    }
                    catch (final IOException ix2) {
                        available = fileCreated;
                    }
                    catch (final OverlappingFileLockException x3) {
                        available = false;
                    }
                    if (available) {
                        DCExtendedFileHandler.LOCKS.add(this.lockFileName);
                        break Label_0413;
                    }
                    this.lockFileChannel.close();
                }
                continue;
            }
            this.files = new File[this.count];
            for (int i = 0; i < this.count; ++i) {
                this.files[i] = this.generate(this.pattern, i, unique);
            }
            if (this.append) {
                this.open(this.files[0], true);
            }
            else {
                this.rotate();
            }
            final Exception ex = em.lastException;
            if (ex == null) {
                this.setErrorManager(new ErrorManager());
                return;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            if (ex instanceof SecurityException) {
                throw (SecurityException)ex;
            }
            throw new IOException("Exception: " + ex);
        }
        throw new IOException("Couldn't get lock for " + this.pattern + ", maxLocks: " + DCExtendedFileHandler.maxLocks);
    }
    
    private File generate(final String pattern, final int generation, final int unique) throws IOException {
        File file = null;
        String word = "";
        int ix = 0;
        boolean sawg = false;
        boolean sawu = false;
        while (ix < pattern.length()) {
            final char ch = pattern.charAt(ix);
            ++ix;
            char ch2 = '\0';
            if (ix < pattern.length()) {
                ch2 = Character.toLowerCase(pattern.charAt(ix));
            }
            if (ch == '/') {
                if (file == null) {
                    file = new File(word);
                }
                else {
                    file = new File(file, word);
                }
                word = "";
            }
            else {
                if (ch == '%') {
                    if (ch2 == 't') {
                        String tmpDir = System.getProperty("java.io.tmpdir");
                        if (tmpDir == null) {
                            tmpDir = System.getProperty("user.home");
                        }
                        file = new File(tmpDir);
                        ++ix;
                        word = "";
                        continue;
                    }
                    if (ch2 == 'h') {
                        file = new File(System.getProperty("user.home"));
                        if (isSetUID()) {
                            throw new IOException("can't use %h in set UID program");
                        }
                        ++ix;
                        word = "";
                        continue;
                    }
                    else {
                        if (ch2 == 'g') {
                            word += generation;
                            sawg = true;
                            ++ix;
                            continue;
                        }
                        if (ch2 == 'u') {
                            word += unique;
                            sawu = true;
                            ++ix;
                            continue;
                        }
                        if (ch2 == '%') {
                            word += "%";
                            ++ix;
                            continue;
                        }
                    }
                }
                word += ch;
            }
        }
        if (this.count > 1 && !sawg) {
            word = word + "." + generation;
        }
        if (unique > 0 && !sawu) {
            word = word + "." + unique;
        }
        if (word.length() > 0) {
            if (file == null) {
                file = new File(word);
            }
            else {
                file = new File(file, word);
            }
        }
        return file;
    }
    
    private synchronized void rotate() {
        final Level oldLevel = this.getLevel();
        this.setLevel(Level.OFF);
        super.close();
        for (int i = this.count - 2; i >= 0; --i) {
            final File f1 = this.files[i];
            final File f2 = this.files[i + 1];
            if (f1.exists()) {
                if (f2.exists()) {
                    this.move(f2);
                }
                f1.renameTo(f2);
            }
        }
        try {
            this.open(this.files[0], false);
        }
        catch (final IOException ix) {
            this.reportError(null, ix, 4);
        }
        this.setLevel(oldLevel);
    }
    
    @Override
    public synchronized void publish(final LogRecord record) {
        if (!this.isLoggable(record)) {
            return;
        }
        super.publish(record);
        this.flush();
        if (this.limit > 0 && this.meter.written >= this.limit) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    DCExtendedFileHandler.this.rotate();
                    return null;
                }
            });
        }
    }
    
    @Override
    public synchronized void close() throws SecurityException {
        super.close();
        if (this.lockFileName == null) {
            return;
        }
        try {
            this.lockFileChannel.close();
        }
        catch (final Exception ex) {}
        synchronized (DCExtendedFileHandler.LOCKS) {
            DCExtendedFileHandler.LOCKS.remove(this.lockFileName);
        }
        new File(this.lockFileName).delete();
        this.lockFileName = null;
        this.lockFileChannel = null;
    }
    
    private static native boolean isSetUID();
    
    private synchronized void move(final File file) {
        final String filepattern = "-" + this.pattern.split("/logs/")[1].replace("%g", "");
        final Date theCreatedDate = new Date(System.currentTimeMillis());
        final String dateFormat = new SimpleDateFormat("dd-MMM-yyyy---hh-mm-ss.SSS").format(theCreatedDate);
        final String backupLogPath = DCExtendedFileHandler.backupLogDir + File.separator + dateFormat + filepattern;
        final File backupfile = new File(backupLogPath);
        file.renameTo(backupfile);
        setBackupLogProp(filepattern, 1);
    }
    
    static {
        LOCKS = new HashSet<String>();
        DCExtendedFileHandler.backupLogDir = System.getProperty("server.home") + File.separator + "logs" + File.separator + "backup_logs";
        DCExtendedFileHandler.backupLogProp = new HashMap<String, Integer>();
        DCExtendedFileHandler.logProp = new HashMap<String, Integer>();
        final String serverHome = System.getProperty("server.home");
        final String logDir = serverHome + File.separator + "logs";
        final File file = new File(logDir);
        if (!file.exists()) {
            file.mkdir();
        }
        final String backupLogDir = logDir + File.separator + "backup_logs";
        final File backupfile = new File(backupLogDir);
        if (!backupfile.exists()) {
            backupfile.mkdir();
        }
        DCExtendedFileHandler.maxLocks = AccessController.doPrivileged((PrivilegedAction<Integer>)new PrivilegedAction<Integer>() {
            @Override
            public Integer run() {
                return Integer.getInteger("jdk.internal.DCExtendedFileHandlerLogging.maxLocks", 100);
            }
        });
        if (DCExtendedFileHandler.maxLocks <= 0) {
            DCExtendedFileHandler.maxLocks = 100;
        }
        loadProp();
    }
    
    private class MeteredStream extends OutputStream
    {
        final OutputStream out;
        int written;
        
        MeteredStream(final OutputStream out, final int written) {
            this.out = out;
            this.written = written;
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.out.write(b);
            ++this.written;
        }
        
        @Override
        public void write(final byte[] buff) throws IOException {
            this.out.write(buff);
            this.written += buff.length;
        }
        
        @Override
        public void write(final byte[] buff, final int off, final int len) throws IOException {
            this.out.write(buff, off, len);
            this.written += len;
        }
        
        @Override
        public void flush() throws IOException {
            this.out.flush();
        }
        
        @Override
        public void close() throws IOException {
            this.out.close();
        }
    }
    
    private static class InitializationErrorManager extends ErrorManager
    {
        Exception lastException;
        
        @Override
        public void error(final String msg, final Exception ex, final int code) {
            this.lastException = ex;
        }
    }
}
