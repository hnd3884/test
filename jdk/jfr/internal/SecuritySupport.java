package jdk.jfr.internal;

import java.nio.file.Paths;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.SimpleFileVisitor;
import jdk.jfr.Recording;
import java.util.Objects;
import jdk.jfr.FlightRecorderListener;
import java.io.BufferedReader;
import java.security.ProtectionDomain;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ReflectPermission;
import java.lang.reflect.Method;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.OpenOption;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.LinkOption;
import java.nio.file.FileVisitor;
import java.nio.file.CopyOption;
import java.io.File;
import java.util.PropertyPermission;
import jdk.jfr.FlightRecorderPermission;
import jdk.jfr.FlightRecorder;
import jdk.jfr.Event;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.security.PrivilegedAction;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Callable;
import sun.misc.Unsafe;

public final class SecuritySupport
{
    private static final Unsafe unsafe;
    public static final SafePath JFC_DIRECTORY;
    static final SafePath USER_HOME;
    static final SafePath JAVA_IO_TMPDIR;
    
    private static <U> U doPrivilegedIOWithReturn(final Callable<U> callable) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<U>)new PrivilegedExceptionAction<U>() {
                @Override
                public U run() throws Exception {
                    return callable.call();
                }
            }, null);
        }
        catch (final PrivilegedActionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new IOException("Unexpected error during I/O operation. " + cause.getMessage(), cause);
        }
    }
    
    private static void doPriviligedIO(final RunnableWithCheckedException ex) throws IOException {
        doPrivilegedIOWithReturn(() -> {
            ex2.run();
            return null;
        });
    }
    
    private static void doPrivileged(final Runnable runnable, final Permission... array) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                runnable.run();
                return null;
            }
        }, null, array);
    }
    
    private static void doPrivileged(final Runnable runnable) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                runnable.run();
                return null;
            }
        });
    }
    
    private static <T> T doPrivilegedWithReturn(final CallableWithoutCheckException<T> ex, final Permission... array) {
        return AccessController.doPrivileged((PrivilegedAction<T>)new PrivilegedAction<T>() {
            @Override
            public T run() {
                return ex.call();
            }
        }, null, array);
    }
    
    public static List<SafePath> getPredefinedJFCFiles() {
        final ArrayList list = new ArrayList();
        try {
            final Iterator<Path> iterator = doPrivilegedIOWithReturn(() -> Files.newDirectoryStream(SecuritySupport.JFC_DIRECTORY.toPath(), "*").iterator());
            while (iterator.hasNext()) {
                final Path path = iterator.next();
                if (path.toString().endsWith(".jfc")) {
                    list.add(new SafePath(path));
                }
            }
        }
        catch (final IOException ex) {
            Logger.log(LogTag.JFR, LogLevel.WARN, "Could not access .jfc-files in " + SecuritySupport.JFC_DIRECTORY + ", " + ex.getMessage());
        }
        return list;
    }
    
    static void makeVisibleToJFR(final Class<?> clazz) {
    }
    
    static void addHandlerExport(final Class<?> clazz) {
    }
    
    public static void registerEvent(final Class<? extends Event> clazz) {
        doPrivileged(() -> FlightRecorder.register(clazz2), new FlightRecorderPermission("registerEvent"));
    }
    
    static boolean getBooleanProperty(final String s) {
        return doPrivilegedWithReturn(() -> Boolean.getBoolean(s2), new PropertyPermission(s, "read"));
    }
    
    private static SafePath getPathInProperty(final String s, final String s2) {
        return doPrivilegedWithReturn(() -> {
            System.getProperty(s3);
            final String s5;
            if (s5 == null) {
                return null;
            }
            else {
                if (s4 == null) {
                    new(java.io.File.class)();
                    new File(s5);
                }
                else {
                    new(java.io.File.class)();
                    new File(s5, s4);
                }
                final File file;
                return new SafePath(file.getAbsolutePath());
            }
        }, new PropertyPermission("*", "read"));
    }
    
    static Thread createRecorderThread(final ThreadGroup threadGroup, final ClassLoader classLoader) {
        final Thread thread = doPrivilegedWithReturn(() -> new Thread(threadGroup2, "JFR Recorder Thread"), new RuntimePermission("modifyThreadGroup"), new RuntimePermission("modifyThread"));
        doPrivileged(() -> thread2.setContextClassLoader(contextClassLoader), new RuntimePermission("setContextClassLoader"), new RuntimePermission("modifyThread"));
        return thread;
    }
    
    static void registerShutdownHook(final Thread thread) {
        doPrivileged(() -> Runtime.getRuntime().addShutdownHook(thread2), new RuntimePermission("shutdownHooks"));
    }
    
    static void setUncaughtExceptionHandler(final Thread thread, final Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        doPrivileged(() -> thread2.setUncaughtExceptionHandler(uncaughtExceptionHandler2), new RuntimePermission("modifyThread"));
    }
    
    static void moveReplace(final SafePath safePath, final SafePath safePath2) throws IOException {
        doPrivilegedIOWithReturn(() -> Files.move(safePath3.toPath(), safePath4.toPath(), new CopyOption[0]));
    }
    
    static void clearDirectory(final SafePath safePath) throws IOException {
        doPriviligedIO(() -> Files.walkFileTree(safePath2.toPath(), new DirectoryCleaner()));
    }
    
    static SafePath toRealPath(final SafePath safePath) throws Exception {
        return new SafePath(doPrivilegedIOWithReturn(() -> safePath2.toPath().toRealPath(new LinkOption[0])));
    }
    
    static boolean existDirectory(final SafePath safePath) throws IOException {
        return doPrivilegedIOWithReturn(() -> Files.exists(safePath2.toPath(), new LinkOption[0]));
    }
    
    static RandomAccessFile createRandomAccessFile(final SafePath safePath) throws Exception {
        return doPrivilegedIOWithReturn(() -> new RandomAccessFile(safePath2.toPath().toFile(), "rw"));
    }
    
    public static InputStream newFileInputStream(final SafePath safePath) throws IOException {
        return doPrivilegedIOWithReturn(() -> Files.newInputStream(safePath2.toPath(), new OpenOption[0]));
    }
    
    public static long getFileSize(final SafePath safePath) throws IOException {
        return doPrivilegedIOWithReturn(() -> Files.size(safePath2.toPath()));
    }
    
    static SafePath createDirectories(final SafePath safePath) throws IOException {
        return new SafePath(doPrivilegedIOWithReturn(() -> Files.createDirectories(safePath2.toPath(), (FileAttribute<?>[])new FileAttribute[0])));
    }
    
    public static boolean exists(final SafePath safePath) throws IOException {
        return doPrivilegedIOWithReturn(() -> Files.exists(safePath2.toPath(), new LinkOption[0]));
    }
    
    public static boolean isDirectory(final SafePath safePath) throws IOException {
        return doPrivilegedIOWithReturn(() -> Files.isDirectory(safePath2.toPath(), new LinkOption[0]));
    }
    
    static void delete(final SafePath safePath) throws IOException {
        doPriviligedIO(() -> Files.delete(safePath2.toPath()));
    }
    
    static boolean isWritable(final SafePath safePath) throws IOException {
        return doPrivilegedIOWithReturn(() -> Files.isWritable(safePath2.toPath()));
    }
    
    static void deleteOnExit(final SafePath safePath) {
        doPrivileged(() -> safePath2.toPath().toFile().deleteOnExit());
    }
    
    static ReadableByteChannel newFileChannelToRead(final SafePath safePath) throws IOException {
        return doPrivilegedIOWithReturn(() -> FileChannel.open(safePath2.toPath(), StandardOpenOption.READ));
    }
    
    public static InputStream getResourceAsStream(final String s) throws IOException {
        return doPrivilegedIOWithReturn(() -> SecuritySupport.class.getResourceAsStream(s2));
    }
    
    public static Reader newFileReader(final SafePath safePath) throws FileNotFoundException, IOException {
        return doPrivilegedIOWithReturn(() -> Files.newBufferedReader(safePath2.toPath()));
    }
    
    static void touch(final SafePath safePath) throws IOException {
        doPriviligedIO(() -> new RandomAccessFile(safePath2.toPath().toFile(), "rw").close());
    }
    
    static void setAccessible(final Method method) {
        doPrivileged(() -> method2.setAccessible(true), new ReflectPermission("suppressAccessChecks"));
    }
    
    static void setAccessible(final Field field) {
        doPrivileged(() -> field2.setAccessible(true), new ReflectPermission("suppressAccessChecks"));
    }
    
    static void setAccessible(final Constructor<?> constructor) {
        doPrivileged(() -> constructor2.setAccessible(true), new ReflectPermission("suppressAccessChecks"));
    }
    
    static void ensureClassIsInitialized(final Class<?> clazz) {
        SecuritySupport.unsafe.ensureClassInitialized(clazz);
    }
    
    static Class<?> defineClass(final String s, final byte[] array, final ClassLoader classLoader) {
        return SecuritySupport.unsafe.defineClass(s, array, 0, array.length, classLoader, null);
    }
    
    static Thread createThreadWitNoPermissions(final String s, final Runnable runnable) {
        return doPrivilegedWithReturn(() -> new Thread(runnable2, s2), new Permission[0]);
    }
    
    static void setDaemonThread(final Thread thread, final boolean b) {
        doPrivileged(() -> thread2.setDaemon(daemon), new RuntimePermission("modifyThread"));
    }
    
    public static SafePath getAbsolutePath(final SafePath safePath) throws IOException {
        return new SafePath(doPrivilegedIOWithReturn(() -> safePath2.toPath().toAbsolutePath()));
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        JFC_DIRECTORY = getPathInProperty("java.home", "lib/jfr");
        USER_HOME = getPathInProperty("user.home", null);
        JAVA_IO_TMPDIR = getPathInProperty("java.io.tmpdir", null);
    }
    
    static final class SecureRecorderListener implements FlightRecorderListener
    {
        private final AccessControlContext context;
        private final FlightRecorderListener changeListener;
        
        SecureRecorderListener(final AccessControlContext accessControlContext, final FlightRecorderListener flightRecorderListener) {
            this.context = Objects.requireNonNull(accessControlContext);
            this.changeListener = Objects.requireNonNull(flightRecorderListener);
        }
        
        @Override
        public void recordingStateChanged(final Recording recording) {
            AccessController.doPrivileged(() -> {
                try {
                    this.changeListener.recordingStateChanged(recording2);
                }
                catch (final Throwable t) {
                    Logger.log(LogTag.JFR, LogLevel.WARN, "Unexpected exception in listener " + this.changeListener.getClass() + " at recording state change");
                }
                return null;
            }, this.context);
        }
        
        @Override
        public void recorderInitialized(final FlightRecorder flightRecorder) {
            AccessController.doPrivileged(() -> {
                try {
                    this.changeListener.recorderInitialized(flightRecorder2);
                }
                catch (final Throwable t) {
                    Logger.log(LogTag.JFR, LogLevel.WARN, "Unexpected exception in listener " + this.changeListener.getClass() + " when initializing FlightRecorder");
                }
                return null;
            }, this.context);
        }
        
        public FlightRecorderListener getChangeListener() {
            return this.changeListener;
        }
    }
    
    private static final class DirectoryCleaner extends SimpleFileVisitor<Path>
    {
        @Override
        public FileVisitResult visitFile(final Path path, final BasicFileAttributes basicFileAttributes) throws IOException {
            Files.delete(path);
            return FileVisitResult.CONTINUE;
        }
        
        @Override
        public FileVisitResult postVisitDirectory(final Path path, final IOException ex) throws IOException {
            if (ex != null) {
                throw ex;
            }
            Files.delete(path);
            return FileVisitResult.CONTINUE;
        }
    }
    
    public static final class SafePath
    {
        private final Path path;
        private final String text;
        
        public SafePath(final Path path) {
            this.text = path.toString();
            this.path = Paths.get(this.text, new String[0]);
        }
        
        public SafePath(final String s) {
            this(Paths.get(s, new String[0]));
        }
        
        public Path toPath() {
            return this.path;
        }
        
        @Override
        public String toString() {
            return this.text;
        }
    }
    
    private interface CallableWithoutCheckException<T>
    {
        T call();
    }
    
    private interface RunnableWithCheckedException
    {
        void run() throws Exception;
    }
}
