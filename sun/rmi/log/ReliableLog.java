package sun.rmi.log;

import java.io.FileNotFoundException;
import java.io.FileDescriptor;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import sun.security.action.GetPropertyAction;
import java.io.RandomAccessFile;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.lang.reflect.Constructor;
import java.io.File;

public class ReliableLog
{
    public static final int PreferredMajorVersion = 0;
    public static final int PreferredMinorVersion = 2;
    private boolean Debug;
    private static String snapshotPrefix;
    private static String logfilePrefix;
    private static String versionFile;
    private static String newVersionFile;
    private static int intBytes;
    private static long diskPageSize;
    private File dir;
    private int version;
    private String logName;
    private LogFile log;
    private long snapshotBytes;
    private long logBytes;
    private int logEntries;
    private long lastSnapshot;
    private long lastLog;
    private LogHandler handler;
    private final byte[] intBuf;
    private int majorFormatVersion;
    private int minorFormatVersion;
    private static final Constructor<? extends LogFile> logClassConstructor;
    
    public ReliableLog(final String s, final LogHandler handler, final boolean b) throws IOException {
        this.Debug = false;
        this.version = 0;
        this.logName = null;
        this.log = null;
        this.snapshotBytes = 0L;
        this.logBytes = 0L;
        this.logEntries = 0;
        this.lastSnapshot = 0L;
        this.lastLog = 0L;
        this.intBuf = new byte[4];
        this.majorFormatVersion = 0;
        this.minorFormatVersion = 0;
        this.Debug = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.rmi.log.debug"));
        this.dir = new File(s);
        if ((!this.dir.exists() || !this.dir.isDirectory()) && !this.dir.mkdir()) {
            throw new IOException("could not create directory for log: " + s);
        }
        this.handler = handler;
        this.lastSnapshot = 0L;
        this.lastLog = 0L;
        this.getVersion();
        if (this.version == 0) {
            try {
                this.snapshot(handler.initialSnapshot());
            }
            catch (final IOException ex) {
                throw ex;
            }
            catch (final Exception ex2) {
                throw new IOException("initial snapshot failed with exception: " + ex2);
            }
        }
    }
    
    public ReliableLog(final String s, final LogHandler logHandler) throws IOException {
        this(s, logHandler, false);
    }
    
    public synchronized Object recover() throws IOException {
        if (this.Debug) {
            System.err.println("log.debug: recover()");
        }
        if (this.version == 0) {
            return null;
        }
        final String versionName = this.versionName(ReliableLog.snapshotPrefix);
        final File file = new File(versionName);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        while (true) {
            if (this.Debug) {
                System.err.println("log.debug: recovering from " + versionName);
                Object recover;
                try {
                    try {
                        recover = this.handler.recover(bufferedInputStream);
                    }
                    catch (final IOException ex) {
                        throw ex;
                    }
                    catch (final Exception ex2) {
                        if (this.Debug) {
                            System.err.println("log.debug: recovery failed: " + ex2);
                        }
                        throw new IOException("log recover failed with exception: " + ex2);
                    }
                    this.snapshotBytes = file.length();
                }
                finally {
                    bufferedInputStream.close();
                }
                return this.recoverUpdates(recover);
            }
            continue;
        }
    }
    
    public synchronized void update(final Object o) throws IOException {
        this.update(o, true);
    }
    
    public synchronized void update(final Object o, final boolean b) throws IOException {
        if (this.log == null) {
            throw new IOException("log is inaccessible, it may have been corrupted or closed");
        }
        final long filePointer = this.log.getFilePointer();
        final boolean checkSpansBoundary = this.log.checkSpansBoundary(filePointer);
        this.writeInt(this.log, checkSpansBoundary ? Integer.MIN_VALUE : 0);
        try {
            this.handler.writeUpdate(new LogOutputStream(this.log), o);
        }
        catch (final IOException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw (IOException)new IOException("write update failed").initCause(ex2);
        }
        this.log.sync();
        final long filePointer2 = this.log.getFilePointer();
        final int n = (int)(filePointer2 - filePointer - ReliableLog.intBytes);
        this.log.seek(filePointer);
        if (checkSpansBoundary) {
            this.writeInt(this.log, n | Integer.MIN_VALUE);
            this.log.sync();
            this.log.seek(filePointer);
            this.log.writeByte(n >> 24);
            this.log.sync();
        }
        else {
            this.writeInt(this.log, n);
            this.log.sync();
        }
        this.log.seek(filePointer2);
        this.logBytes = filePointer2;
        this.lastLog = System.currentTimeMillis();
        ++this.logEntries;
    }
    
    private static Constructor<? extends LogFile> getLogClassConstructor() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.log.class"));
        if (s != null) {
            try {
                return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                    @Override
                    public ClassLoader run() {
                        return ClassLoader.getSystemClassLoader();
                    }
                }).loadClass(s).asSubclass(LogFile.class).getConstructor(String.class, String.class);
            }
            catch (final Exception ex) {
                System.err.println("Exception occurred:");
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    public synchronized void snapshot(final Object o) throws IOException {
        final int version = this.version;
        this.incrVersion();
        final File file = new File(this.versionName(ReliableLog.snapshotPrefix));
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            try {
                this.handler.snapshot(fileOutputStream, o);
            }
            catch (final IOException ex) {
                throw ex;
            }
            catch (final Exception ex2) {
                throw new IOException("snapshot failed", ex2);
            }
            this.lastSnapshot = System.currentTimeMillis();
        }
        finally {
            fileOutputStream.close();
            this.snapshotBytes = file.length();
        }
        this.openLogFile(true);
        this.writeVersionFile(true);
        this.commitToNewVersion();
        this.deleteSnapshot(version);
        this.deleteLogFile(version);
    }
    
    public synchronized void close() throws IOException {
        if (this.log == null) {
            return;
        }
        try {
            this.log.close();
        }
        finally {
            this.log = null;
        }
    }
    
    public long snapshotSize() {
        return this.snapshotBytes;
    }
    
    public long logSize() {
        return this.logBytes;
    }
    
    private void writeInt(final DataOutput dataOutput, final int n) throws IOException {
        this.intBuf[0] = (byte)(n >> 24);
        this.intBuf[1] = (byte)(n >> 16);
        this.intBuf[2] = (byte)(n >> 8);
        this.intBuf[3] = (byte)n;
        dataOutput.write(this.intBuf);
    }
    
    private String fName(final String s) {
        return this.dir.getPath() + File.separator + s;
    }
    
    private String versionName(final String s) {
        return this.versionName(s, 0);
    }
    
    private String versionName(final String s, int n) {
        n = ((n == 0) ? this.version : n);
        return this.fName(s) + String.valueOf(n);
    }
    
    private void incrVersion() {
        do {
            ++this.version;
        } while (this.version == 0);
    }
    
    private void deleteFile(final String s) throws IOException {
        if (!new File(s).delete()) {
            throw new IOException("couldn't remove file: " + s);
        }
    }
    
    private void deleteNewVersionFile() throws IOException {
        this.deleteFile(this.fName(ReliableLog.newVersionFile));
    }
    
    private void deleteSnapshot(final int n) throws IOException {
        if (n == 0) {
            return;
        }
        this.deleteFile(this.versionName(ReliableLog.snapshotPrefix, n));
    }
    
    private void deleteLogFile(final int n) throws IOException {
        if (n == 0) {
            return;
        }
        this.deleteFile(this.versionName(ReliableLog.logfilePrefix, n));
    }
    
    private void openLogFile(final boolean b) throws IOException {
        try {
            this.close();
        }
        catch (final IOException ex) {}
        this.logName = this.versionName(ReliableLog.logfilePrefix);
        try {
            this.log = ((ReliableLog.logClassConstructor == null) ? new LogFile(this.logName, "rw") : ReliableLog.logClassConstructor.newInstance(this.logName, "rw"));
        }
        catch (final Exception ex2) {
            throw (IOException)new IOException("unable to construct LogFile instance").initCause(ex2);
        }
        if (b) {
            this.initializeLogFile();
        }
    }
    
    private void initializeLogFile() throws IOException {
        this.log.setLength(0L);
        this.majorFormatVersion = 0;
        this.writeInt(this.log, 0);
        this.minorFormatVersion = 2;
        this.writeInt(this.log, 2);
        this.logBytes = ReliableLog.intBytes * 2;
        this.logEntries = 0;
    }
    
    private void writeVersionFile(final boolean b) throws IOException {
        String s;
        if (b) {
            s = ReliableLog.newVersionFile;
        }
        else {
            s = ReliableLog.versionFile;
        }
        try (final FileOutputStream fileOutputStream = new FileOutputStream(this.fName(s));
             final DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {
            this.writeInt(dataOutputStream, this.version);
        }
    }
    
    private void createFirstVersion() throws IOException {
        this.version = 0;
        this.writeVersionFile(false);
    }
    
    private void commitToNewVersion() throws IOException {
        this.writeVersionFile(false);
        this.deleteNewVersionFile();
    }
    
    private int readVersion(final String s) throws IOException {
        try (final DataInputStream dataInputStream = new DataInputStream(new FileInputStream(s))) {
            return dataInputStream.readInt();
        }
    }
    
    private void getVersion() throws IOException {
        try {
            this.version = this.readVersion(this.fName(ReliableLog.newVersionFile));
            this.commitToNewVersion();
        }
        catch (final IOException ex) {
            try {
                this.deleteNewVersionFile();
            }
            catch (final IOException ex2) {}
            try {
                this.version = this.readVersion(this.fName(ReliableLog.versionFile));
            }
            catch (final IOException ex3) {
                this.createFirstVersion();
            }
        }
    }
    
    private Object recoverUpdates(Object update) throws IOException {
        this.logBytes = 0L;
        this.logEntries = 0;
        if (this.version == 0) {
            return update;
        }
        final String versionName = this.versionName(ReliableLog.logfilePrefix);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(versionName));
        final DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
        if (this.Debug) {
            System.err.println("log.debug: reading updates from " + versionName);
        }
        try {
            this.majorFormatVersion = dataInputStream.readInt();
            this.logBytes += ReliableLog.intBytes;
            this.minorFormatVersion = dataInputStream.readInt();
            this.logBytes += ReliableLog.intBytes;
        }
        catch (final EOFException ex) {
            this.openLogFile(true);
            bufferedInputStream = null;
        }
        if (this.majorFormatVersion != 0) {
            if (this.Debug) {
                System.err.println("log.debug: major version mismatch: " + this.majorFormatVersion + "." + this.minorFormatVersion);
            }
            throw new IOException("Log file " + this.logName + " has a version " + this.majorFormatVersion + "." + this.minorFormatVersion + " format, and this implementation  understands only version " + 0 + "." + 2);
        }
        try {
            while (bufferedInputStream != null) {
                int int1;
                try {
                    int1 = dataInputStream.readInt();
                }
                catch (final EOFException ex2) {
                    if (this.Debug) {
                        System.err.println("log.debug: log was sync'd cleanly");
                    }
                    break;
                }
                if (int1 <= 0) {
                    if (this.Debug) {
                        System.err.println("log.debug: last update incomplete, updateLen = 0x" + Integer.toHexString(int1));
                        break;
                    }
                    break;
                }
                else if (bufferedInputStream.available() < int1) {
                    if (this.Debug) {
                        System.err.println("log.debug: log was truncated");
                        break;
                    }
                    break;
                }
                else {
                    if (this.Debug) {
                        System.err.println("log.debug: rdUpdate size " + int1);
                    }
                    try {
                        update = this.handler.readUpdate(new LogInputStream(bufferedInputStream, int1), update);
                    }
                    catch (final IOException ex3) {
                        throw ex3;
                    }
                    catch (final Exception ex4) {
                        ex4.printStackTrace();
                        throw new IOException("read update failed with exception: " + ex4);
                    }
                    this.logBytes += ReliableLog.intBytes + int1;
                    ++this.logEntries;
                }
            }
        }
        finally {
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
        }
        if (this.Debug) {
            System.err.println("log.debug: recovered updates: " + this.logEntries);
        }
        this.openLogFile(false);
        if (this.log == null) {
            throw new IOException("rmid's log is inaccessible, it may have been corrupted or closed");
        }
        this.log.seek(this.logBytes);
        this.log.setLength(this.logBytes);
        return update;
    }
    
    static {
        ReliableLog.snapshotPrefix = "Snapshot.";
        ReliableLog.logfilePrefix = "Logfile.";
        ReliableLog.versionFile = "Version_Number";
        ReliableLog.newVersionFile = "New_Version_Number";
        ReliableLog.intBytes = 4;
        ReliableLog.diskPageSize = 512L;
        logClassConstructor = getLogClassConstructor();
    }
    
    public static class LogFile extends RandomAccessFile
    {
        private final FileDescriptor fd;
        
        public LogFile(final String s, final String s2) throws FileNotFoundException, IOException {
            super(s, s2);
            this.fd = this.getFD();
        }
        
        protected void sync() throws IOException {
            this.fd.sync();
        }
        
        protected boolean checkSpansBoundary(final long n) {
            return n % 512L > 508L;
        }
    }
}
