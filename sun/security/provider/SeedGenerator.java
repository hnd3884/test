package sun.security.provider;

import java.io.FileInputStream;
import java.security.PrivilegedExceptionAction;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.security.AccessController;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.util.Enumeration;
import java.util.Properties;
import java.nio.file.Path;
import java.util.Random;
import java.nio.file.Files;
import java.io.File;
import java.security.PrivilegedAction;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import sun.security.util.Debug;

abstract class SeedGenerator
{
    private static SeedGenerator instance;
    private static final Debug debug;
    
    public static void generateSeed(final byte[] array) {
        SeedGenerator.instance.getSeedBytes(array);
    }
    
    abstract void getSeedBytes(final byte[] p0);
    
    static byte[] getSystemEntropy() {
        MessageDigest instance;
        try {
            instance = MessageDigest.getInstance("SHA");
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new InternalError("internal error: SHA-1 not available.", ex);
        }
        instance.update((byte)System.currentTimeMillis());
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    final Properties properties = System.getProperties();
                    final Enumeration<?> propertyNames = properties.propertyNames();
                    while (propertyNames.hasMoreElements()) {
                        final String s = (String)propertyNames.nextElement();
                        instance.update(s.getBytes());
                        instance.update(properties.getProperty(s).getBytes());
                    }
                    addNetworkAdapterInfo(instance);
                    final File file = new File(properties.getProperty("java.io.tmpdir"));
                    int n = 0;
                    try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(file.toPath())) {
                        final Random random = new Random();
                        for (final Path path : directoryStream) {
                            if (n < 512 || random.nextBoolean()) {
                                instance.update(path.getFileName().toString().getBytes());
                            }
                            if (n++ > 1024) {
                                break;
                            }
                        }
                    }
                }
                catch (final Exception ex) {
                    instance.update((byte)ex.hashCode());
                }
                final Runtime runtime = Runtime.getRuntime();
                final byte[] access$100 = longToByteArray(runtime.totalMemory());
                instance.update(access$100, 0, access$100.length);
                final byte[] access$101 = longToByteArray(runtime.freeMemory());
                instance.update(access$101, 0, access$101.length);
                return null;
            }
        });
        return instance.digest();
    }
    
    private static void addNetworkAdapterInfo(final MessageDigest messageDigest) {
        try {
            final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                final NetworkInterface networkInterface = networkInterfaces.nextElement();
                messageDigest.update(networkInterface.toString().getBytes());
                if (!networkInterface.isVirtual()) {
                    final byte[] hardwareAddress = networkInterface.getHardwareAddress();
                    if (hardwareAddress != null) {
                        messageDigest.update(hardwareAddress);
                        break;
                    }
                    continue;
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    private static byte[] longToByteArray(long n) {
        final byte[] array = new byte[8];
        for (int i = 0; i < 8; ++i) {
            array[i] = (byte)n;
            n >>= 8;
        }
        return array;
    }
    
    static {
        debug = Debug.getInstance("provider");
        final String seedSource = SunEntries.getSeedSource();
        Label_0209: {
            if (!seedSource.equals("file:/dev/random")) {
                if (!seedSource.equals("file:/dev/urandom")) {
                    if (seedSource.length() != 0) {
                        try {
                            SeedGenerator.instance = new URLSeedGenerator(seedSource);
                            if (SeedGenerator.debug != null) {
                                SeedGenerator.debug.println("Using URL seed generator reading from " + seedSource);
                            }
                        }
                        catch (final IOException ex) {
                            if (SeedGenerator.debug != null) {
                                SeedGenerator.debug.println("Failed to create seed generator with " + seedSource + ": " + ex.toString());
                            }
                        }
                    }
                    break Label_0209;
                }
            }
            try {
                SeedGenerator.instance = new NativeSeedGenerator(seedSource);
                if (SeedGenerator.debug != null) {
                    SeedGenerator.debug.println("Using operating system seed generator" + seedSource);
                }
            }
            catch (final IOException ex2) {
                if (SeedGenerator.debug != null) {
                    SeedGenerator.debug.println("Failed to use operating system seed generator: " + ex2.toString());
                }
            }
        }
        if (SeedGenerator.instance == null) {
            if (SeedGenerator.debug != null) {
                SeedGenerator.debug.println("Using default threaded seed generator");
            }
            SeedGenerator.instance = new ThreadedSeedGenerator();
        }
    }
    
    private static class ThreadedSeedGenerator extends SeedGenerator implements Runnable
    {
        private byte[] pool;
        private int start;
        private int end;
        private int count;
        ThreadGroup seedGroup;
        private static byte[] rndTab;
        
        ThreadedSeedGenerator() {
            this.pool = new byte[20];
            final int n = 0;
            this.end = n;
            this.start = n;
            try {
                MessageDigest.getInstance("SHA");
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new InternalError("internal error: SHA-1 not available.", ex);
            }
            final ThreadGroup[] array = { null };
            final Thread thread = AccessController.doPrivileged((PrivilegedAction<Thread>)new PrivilegedAction<Thread>() {
                @Override
                public Thread run() {
                    ThreadGroup threadGroup;
                    ThreadGroup parent;
                    for (threadGroup = Thread.currentThread().getThreadGroup(); (parent = threadGroup.getParent()) != null; threadGroup = parent) {}
                    array[0] = new ThreadGroup(threadGroup, "SeedGenerator ThreadGroup");
                    final Thread thread = new Thread(array[0], ThreadedSeedGenerator.this, "SeedGenerator Thread");
                    thread.setPriority(1);
                    thread.setDaemon(true);
                    return thread;
                }
            });
            this.seedGroup = array[0];
            thread.start();
        }
        
        @Override
        public final void run() {
            try {
                while (true) {
                    synchronized (this) {
                        while (this.count >= this.pool.length) {
                            this.wait();
                        }
                    }
                    byte b = 0;
                    int n2;
                    int n3;
                    for (int n = n2 = 0; n2 < 64000 && n < 6; n2 += n3, ++n) {
                        try {
                            new Thread(this.seedGroup, new BogusThread(), "SeedGenerator Thread").start();
                        }
                        catch (final Exception ex) {
                            throw new InternalError("internal error: SeedGenerator thread creation error.", ex);
                        }
                        n3 = 0;
                        while (System.currentTimeMillis() < System.currentTimeMillis() + 250L) {
                            synchronized (this) {}
                            ++n3;
                        }
                        b ^= ThreadedSeedGenerator.rndTab[n3 % 255];
                    }
                    synchronized (this) {
                        this.pool[this.end] = b;
                        ++this.end;
                        ++this.count;
                        if (this.end >= this.pool.length) {
                            this.end = 0;
                        }
                        this.notifyAll();
                    }
                }
            }
            catch (final Exception ex2) {
                throw new InternalError("internal error: SeedGenerator thread generated an exception.", ex2);
            }
        }
        
        @Override
        void getSeedBytes(final byte[] array) {
            for (int i = 0; i < array.length; ++i) {
                array[i] = this.getSeedByte();
            }
        }
        
        byte getSeedByte() {
            try {
                synchronized (this) {
                    while (this.count <= 0) {
                        this.wait();
                    }
                }
            }
            catch (final Exception ex) {
                if (this.count <= 0) {
                    throw new InternalError("internal error: SeedGenerator thread generated an exception.", ex);
                }
            }
            final byte b;
            synchronized (this) {
                b = this.pool[this.start];
                this.pool[this.start] = 0;
                ++this.start;
                --this.count;
                if (this.start == this.pool.length) {
                    this.start = 0;
                }
                this.notifyAll();
            }
            return b;
        }
        
        static {
            ThreadedSeedGenerator.rndTab = new byte[] { 56, 30, -107, -6, -86, 25, -83, 75, -12, -64, 5, -128, 78, 21, 16, 32, 70, -81, 37, -51, -43, -46, -108, 87, 29, 17, -55, 22, -11, -111, -115, 84, -100, 108, -45, -15, -98, 72, -33, -28, 31, -52, -37, -117, -97, -27, 93, -123, 47, 126, -80, -62, -93, -79, 61, -96, -65, -5, -47, -119, 14, 89, 81, -118, -88, 20, 67, -126, -113, 60, -102, 55, 110, 28, 85, 121, 122, -58, 2, 45, 43, 24, -9, 103, -13, 102, -68, -54, -101, -104, 19, 13, -39, -26, -103, 62, 77, 51, 44, 111, 73, 18, -127, -82, 4, -30, 11, -99, -74, 40, -89, 42, -76, -77, -94, -35, -69, 35, 120, 76, 33, -73, -7, 82, -25, -10, 88, 125, -112, 58, 83, 95, 6, 10, 98, -34, 80, 15, -91, 86, -19, 52, -17, 117, 49, -63, 118, -90, 36, -116, -40, -71, 97, -53, -109, -85, 109, -16, -3, 104, -95, 68, 54, 34, 26, 114, -1, 106, -121, 3, 66, 0, 100, -84, 57, 107, 119, -42, 112, -61, 1, 48, 38, 12, -56, -57, 39, -106, -72, 41, 7, 71, -29, -59, -8, -38, 79, -31, 124, -124, 8, 91, 116, 99, -4, 9, -36, -78, 63, -49, -67, -87, 59, 101, -32, 92, 94, 53, -41, 115, -66, -70, -122, 50, -50, -22, -20, -18, -21, 23, -2, -48, 96, 65, -105, 123, -14, -110, 69, -24, -120, -75, 74, 127, -60, 113, 90, -114, 105, 46, 27, -125, -23, -44, 64 };
        }
        
        private static class BogusThread implements Runnable
        {
            @Override
            public final void run() {
                try {
                    for (int i = 0; i < 5; ++i) {
                        Thread.sleep(50L);
                    }
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    static class URLSeedGenerator extends SeedGenerator
    {
        private String deviceName;
        private InputStream seedStream;
        
        URLSeedGenerator(final String deviceName) throws IOException {
            if (deviceName == null) {
                throw new IOException("No random source specified");
            }
            this.deviceName = deviceName;
            this.init();
        }
        
        private void init() throws IOException {
            final URL url = new URL(this.deviceName);
            try {
                this.seedStream = AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction<InputStream>() {
                    @Override
                    public InputStream run() throws IOException {
                        if (url.getProtocol().equalsIgnoreCase("file")) {
                            return new FileInputStream(SunEntries.getDeviceFile(url));
                        }
                        return url.openStream();
                    }
                });
            }
            catch (final Exception ex) {
                throw new IOException("Failed to open " + this.deviceName, ex.getCause());
            }
        }
        
        @Override
        void getSeedBytes(final byte[] array) {
            final int length = array.length;
            int i = 0;
            try {
                while (i < length) {
                    final int read = this.seedStream.read(array, i, length - i);
                    if (read < 0) {
                        throw new InternalError("URLSeedGenerator " + this.deviceName + " reached end of file");
                    }
                    i += read;
                }
            }
            catch (final IOException ex) {
                throw new InternalError("URLSeedGenerator " + this.deviceName + " generated exception: " + ex.getMessage(), ex);
            }
        }
    }
}
