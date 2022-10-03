package com.adventnet.sym.logging;

import java.util.HashSet;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.LogRecord;
import java.io.BufferedReader;
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
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Formatter;
import java.util.logging.XMLFormatter;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.security.Permission;
import java.io.FileWriter;
import java.util.Set;
import java.io.File;
import java.nio.channels.FileChannel;
import java.util.logging.StreamHandler;

public class SensitiveFileHandler extends StreamHandler
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
    private static final Set<String> locks;
    private volatile boolean allowRotation;
    private static char[][] matchingList;
    private static FileWriter sensitiveFileWriter;
    private static long writtenSize;
    private static String storedPath;
    private static boolean stopParser;
    private static int size;
    private final Permission controlPermission;
    
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
    
    private static void sensitiveConfigure() {
        final String serverHome = System.getProperty("server.home");
        final Properties sensitiveProps = new Properties();
        FileInputStream fis = null;
        String sensitiveKeyString = null;
        try {
            sensitiveKeyString = serverHome + File.separator + "conf" + File.separator + System.getProperty("sensitive.logger.params", "logger_params.conf");
            fis = new FileInputStream(sensitiveKeyString);
            sensitiveProps.load(fis);
            SensitiveFileHandler.stopParser = Boolean.parseBoolean(sensitiveProps.getProperty("enable.sensitive.filter", "false"));
            SensitiveFileHandler.storedPath = serverHome + File.separator + "logs" + File.separator + sensitiveProps.getProperty("logged.sensitive.path", "sensitive-log.txt");
            final File storedFile = new File(SensitiveFileHandler.storedPath);
            SensitiveFileHandler.sensitiveFileWriter = new FileWriter(storedFile, true);
            SensitiveFileHandler.writtenSize = storedFile.length();
        }
        catch (final Exception e) {
            e.printStackTrace();
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException e2) {
                e2.printStackTrace();
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException e3) {
                e3.printStackTrace();
            }
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
    
    public SensitiveFileHandler() throws IOException, SecurityException {
        this.allowRotation = true;
        this.controlPermission = new LoggingPermission("control", null);
        this.checkPermission();
        this.configure();
        this.openFiles();
    }
    
    public SensitiveFileHandler(final String pattern) throws IOException, SecurityException {
        this.allowRotation = true;
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
    
    public SensitiveFileHandler(final String pattern, final boolean append) throws IOException, SecurityException {
        this.allowRotation = true;
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
    
    public SensitiveFileHandler(final String pattern, final int limit, final int count) throws IOException, SecurityException {
        this.allowRotation = true;
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
    
    public SensitiveFileHandler(final String pattern, final int limit, final int count, final boolean append) throws IOException, SecurityException {
        this.allowRotation = true;
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
        while (++unique <= SensitiveFileHandler.maxLocks) {
            this.lockFileName = this.generate(this.pattern, 0, unique).toString() + ".lck";
            Label_0413: {
                synchronized (SensitiveFileHandler.locks) {
                    if (SensitiveFileHandler.locks.contains(this.lockFileName)) {
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
                        SensitiveFileHandler.locks.add(this.lockFileName);
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
        throw new IOException("Couldn't get lock for " + this.pattern + ", maxLocks: " + SensitiveFileHandler.maxLocks);
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
                    f2.delete();
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
    
    public static void initiateDataProvider(final String[] match, final Properties props) {
        int i;
        for (i = 0; i < match.length && i < 100; ++i) {
            if (match[i].length() >= 3) {
                SensitiveFileHandler.matchingList[i] = match[i].toCharArray();
            }
        }
        SensitiveFileHandler.size = i;
        SensitiveFileHandler.stopParser = Boolean.parseBoolean(props.getProperty("enable.sensitive.filter", "false"));
    }
    
    private void hideAndCollectSensitiveData() {
        final long maxSize = 45000000L;
        if (SensitiveFileHandler.size > 0) {
            StringBuilder stringBuilder = null;
            BufferedReader bufferedReader = null;
            FileWriter fileWriter = null;
            Boolean sensitiveFound = false;
            this.allowRotation = false;
            try {
                final File f1 = this.files[1];
                if (f1.exists()) {
                    bufferedReader = Files.newBufferedReader(Paths.get(f1.getPath(), new String[0]));
                    stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        boolean sensitiveDataFoundInLine = false;
                        for (int j = 0; j < SensitiveFileHandler.size; ++j) {
                            final String matching = new String(SensitiveFileHandler.matchingList[j]);
                            if (matching != null && !matching.equalsIgnoreCase("") && !matching.isEmpty() && line.contains(matching)) {
                                line = line.replace(matching, "*****");
                                sensitiveDataFoundInLine = true;
                                sensitiveFound = true;
                            }
                        }
                        if (SensitiveFileHandler.writtenSize > maxSize) {
                            rotateSensitiveWriter();
                        }
                        if (sensitiveDataFoundInLine) {
                            SensitiveFileHandler.writtenSize += line.length();
                            SensitiveFileHandler.sensitiveFileWriter.write(f1.getName() + ":" + line + "\n");
                            SensitiveFileHandler.sensitiveFileWriter.flush();
                        }
                        stringBuilder.append(line + "\n");
                    }
                    if (sensitiveFound) {
                        fileWriter = new FileWriter(f1);
                        fileWriter.write(stringBuilder.toString());
                        fileWriter.flush();
                    }
                }
            }
            catch (final IOException e) {
                throw new Error("Unexpected exception " + e);
            }
            finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                }
                catch (final Exception e2) {
                    throw new Error("Unexpected exception " + e2);
                }
                this.allowRotation = true;
            }
        }
    }
    
    public static void rotateSensitiveWriter() {
        final String serverHome = System.getProperty("server.home");
        final String dirPath = serverHome + File.separator + "logs" + File.separator;
        final String rotateFile = new File(SensitiveFileHandler.storedPath).getName().replace(".txt", "1.txt");
        final String source = SensitiveFileHandler.storedPath;
        final String des = dirPath + rotateFile;
        final File sourcePath = new File(source);
        final File desPath = new File(des);
        try {
            if (SensitiveFileHandler.sensitiveFileWriter != null) {
                SensitiveFileHandler.sensitiveFileWriter.close();
            }
            desPath.delete();
            if (!desPath.exists()) {
                sourcePath.renameTo(desPath);
            }
            SensitiveFileHandler.sensitiveFileWriter = new FileWriter(sourcePath);
            SensitiveFileHandler.writtenSize = 0L;
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public synchronized void publish(final LogRecord record) {
        if (!this.isLoggable(record)) {
            return;
        }
        super.publish(record);
        this.flush();
        if (this.limit > 0 && this.meter.written >= this.limit && this.allowRotation) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    SensitiveFileHandler.this.rotate();
                    if (SensitiveFileHandler.stopParser) {
                        SensitiveFileHandler.this.hideAndCollectSensitiveData();
                    }
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
        synchronized (SensitiveFileHandler.locks) {
            SensitiveFileHandler.locks.remove(this.lockFileName);
        }
        new File(this.lockFileName).delete();
        this.lockFileName = null;
        this.lockFileChannel = null;
    }
    
    private static native boolean isSetUID();
    
    static {
        locks = new HashSet<String>();
        SensitiveFileHandler.matchingList = new char[100][64];
        final String serverHome = System.getProperty("server.home");
        final String logDir = serverHome + File.separator + "logs";
        final File file = new File(logDir);
        if (!file.exists()) {
            file.mkdir();
        }
        SensitiveFileHandler.maxLocks = AccessController.doPrivileged((PrivilegedAction<Integer>)new PrivilegedAction<Integer>() {
            @Override
            public Integer run() {
                return Integer.getInteger("jdk.internal.SensitiveFileHandlerLogging.maxLocks", 100);
            }
        });
        if (SensitiveFileHandler.maxLocks <= 0) {
            SensitiveFileHandler.maxLocks = 100;
        }
        sensitiveConfigure();
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
