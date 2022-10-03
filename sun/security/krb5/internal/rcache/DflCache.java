package sun.security.krb5.internal.rcache;

import java.nio.BufferUnderflowException;
import java.nio.file.LinkOption;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.nio.channels.SeekableByteChannel;
import java.io.Closeable;
import java.io.IOException;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.KerberosTime;
import java.io.File;
import java.nio.file.Path;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.ReplayCache;

public class DflCache extends ReplayCache
{
    private static final int KRB5_RV_VNO = 1281;
    private static final int EXCESSREPS = 30;
    private final String source;
    private static int uid;
    
    public DflCache(final String source) {
        this.source = source;
    }
    
    private static String defaultPath() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.io.tmpdir"));
    }
    
    private static String defaultFile(String s) {
        int n = s.indexOf(47);
        if (n == -1) {
            n = s.indexOf(64);
        }
        if (n != -1) {
            s = s.substring(0, n);
        }
        if (DflCache.uid != -1) {
            s = s + "_" + DflCache.uid;
        }
        return s;
    }
    
    private static Path getFileName(String substring, final String s) {
        String s2;
        String s3;
        if (substring.equals("dfl")) {
            s2 = defaultPath();
            s3 = defaultFile(s);
        }
        else {
            if (!substring.startsWith("dfl:")) {
                throw new IllegalArgumentException();
            }
            substring = substring.substring(4);
            int lastIndex = substring.lastIndexOf(47);
            final int lastIndex2 = substring.lastIndexOf(92);
            if (lastIndex2 > lastIndex) {
                lastIndex = lastIndex2;
            }
            if (lastIndex == -1) {
                s2 = defaultPath();
                s3 = substring;
            }
            else if (new File(substring).isDirectory()) {
                s2 = substring;
                s3 = defaultFile(s);
            }
            else {
                s2 = null;
                s3 = substring;
            }
        }
        return new File(s2, s3).toPath();
    }
    
    @Override
    public void checkAndStore(final KerberosTime kerberosTime, final AuthTimeWithHash authTimeWithHash) throws KrbApErrException {
        try {
            this.checkAndStore0(kerberosTime, authTimeWithHash);
        }
        catch (final IOException ex) {
            final KrbApErrException ex2 = new KrbApErrException(60);
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private synchronized void checkAndStore0(final KerberosTime kerberosTime, final AuthTimeWithHash authTimeWithHash) throws IOException, KrbApErrException {
        final Path fileName = getFileName(this.source, authTimeWithHash.server);
        int n = 0;
        try (final Storage storage = new Storage()) {
            try {
                n = storage.loadAndCheck(fileName, authTimeWithHash, kerberosTime);
            }
            catch (final IOException ex) {
                create(fileName);
                n = storage.loadAndCheck(fileName, authTimeWithHash, kerberosTime);
            }
            storage.append(authTimeWithHash);
        }
        if (n > 30) {
            expunge(fileName, kerberosTime);
        }
    }
    
    static {
        try {
            final Class<?> forName = Class.forName("com.sun.security.auth.module.UnixSystem");
            DflCache.uid = (int)(long)forName.getMethod("getUid", (Class[])new Class[0]).invoke(forName.newInstance(), new Object[0]);
        }
        catch (final Exception ex) {
            DflCache.uid = -1;
        }
    }
    
    private static class Storage implements Closeable
    {
        SeekableByteChannel chan;
        
        private static void create(final Path path) throws IOException {
            final SeekableByteChannel noClose = createNoClose(path);
            final Throwable t = null;
            if (noClose != null) {
                if (t != null) {
                    try {
                        noClose.close();
                    }
                    catch (final Throwable t2) {
                        t.addSuppressed(t2);
                    }
                }
                else {
                    noClose.close();
                }
            }
            makeMine(path);
        }
        
        private static void makeMine(final Path path) throws IOException {
            try {
                final HashSet set = new HashSet();
                set.add(PosixFilePermission.OWNER_READ);
                set.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(path, set);
            }
            catch (final UnsupportedOperationException ex) {}
        }
        
        private static SeekableByteChannel createNoClose(final Path path) throws IOException {
            final SeekableByteChannel byteChannel = Files.newByteChannel(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            final ByteBuffer allocate = ByteBuffer.allocate(6);
            allocate.putShort((short)1281);
            allocate.order(ByteOrder.nativeOrder());
            allocate.putInt(KerberosTime.getDefaultSkew());
            allocate.flip();
            byteChannel.write(allocate);
            return byteChannel;
        }
        
        private static void expunge(final Path p0, final KerberosTime p1) throws IOException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: invokeinterface java/nio/file/Path.getParent:()Ljava/nio/file/Path;
            //     6: ldc             "rcache"
            //     8: aconst_null    
            //     9: iconst_0       
            //    10: anewarray       Ljava/nio/file/attribute/FileAttribute;
            //    13: invokestatic    java/nio/file/Files.createTempFile:(Ljava/nio/file/Path;Ljava/lang/String;Ljava/lang/String;[Ljava/nio/file/attribute/FileAttribute;)Ljava/nio/file/Path;
            //    16: astore_2       
            //    17: aload_0        
            //    18: iconst_0       
            //    19: anewarray       Ljava/nio/file/OpenOption;
            //    22: invokestatic    java/nio/file/Files.newByteChannel:(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Ljava/nio/channels/SeekableByteChannel;
            //    25: astore_3       
            //    26: aconst_null    
            //    27: astore          4
            //    29: aload_2        
            //    30: invokestatic    sun/security/krb5/internal/rcache/DflCache$Storage.createNoClose:(Ljava/nio/file/Path;)Ljava/nio/channels/SeekableByteChannel;
            //    33: astore          5
            //    35: aconst_null    
            //    36: astore          6
            //    38: aload_1        
            //    39: invokevirtual   sun/security/krb5/internal/KerberosTime.getSeconds:()I
            //    42: aload_3        
            //    43: invokestatic    sun/security/krb5/internal/rcache/DflCache$Storage.readHeader:(Ljava/nio/channels/SeekableByteChannel;)I
            //    46: isub           
            //    47: i2l            
            //    48: lstore          7
            //    50: aload_3        
            //    51: invokestatic    sun/security/krb5/internal/rcache/AuthTime.readFrom:(Ljava/nio/channels/SeekableByteChannel;)Lsun/security/krb5/internal/rcache/AuthTime;
            //    54: astore          9
            //    56: aload           9
            //    58: getfield        sun/security/krb5/internal/rcache/AuthTime.ctime:I
            //    61: i2l            
            //    62: lload           7
            //    64: lcmp           
            //    65: ifle            89
            //    68: aload           9
            //    70: iconst_1       
            //    71: invokevirtual   sun/security/krb5/internal/rcache/AuthTime.encode:(Z)[B
            //    74: invokestatic    java/nio/ByteBuffer.wrap:([B)Ljava/nio/ByteBuffer;
            //    77: astore          10
            //    79: aload           5
            //    81: aload           10
            //    83: invokeinterface java/nio/channels/SeekableByteChannel.write:(Ljava/nio/ByteBuffer;)I
            //    88: pop            
            //    89: goto            50
            //    92: astore          9
            //    94: goto            97
            //    97: aload           5
            //    99: ifnull          192
            //   102: aload           6
            //   104: ifnull          129
            //   107: aload           5
            //   109: invokeinterface java/nio/channels/SeekableByteChannel.close:()V
            //   114: goto            192
            //   117: astore          7
            //   119: aload           6
            //   121: aload           7
            //   123: invokevirtual   java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
            //   126: goto            192
            //   129: aload           5
            //   131: invokeinterface java/nio/channels/SeekableByteChannel.close:()V
            //   136: goto            192
            //   139: astore          7
            //   141: aload           7
            //   143: astore          6
            //   145: aload           7
            //   147: athrow         
            //   148: astore          11
            //   150: aload           5
            //   152: ifnull          189
            //   155: aload           6
            //   157: ifnull          182
            //   160: aload           5
            //   162: invokeinterface java/nio/channels/SeekableByteChannel.close:()V
            //   167: goto            189
            //   170: astore          12
            //   172: aload           6
            //   174: aload           12
            //   176: invokevirtual   java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
            //   179: goto            189
            //   182: aload           5
            //   184: invokeinterface java/nio/channels/SeekableByteChannel.close:()V
            //   189: aload           11
            //   191: athrow         
            //   192: aload_3        
            //   193: ifnull          281
            //   196: aload           4
            //   198: ifnull          222
            //   201: aload_3        
            //   202: invokeinterface java/nio/channels/SeekableByteChannel.close:()V
            //   207: goto            281
            //   210: astore          5
            //   212: aload           4
            //   214: aload           5
            //   216: invokevirtual   java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
            //   219: goto            281
            //   222: aload_3        
            //   223: invokeinterface java/nio/channels/SeekableByteChannel.close:()V
            //   228: goto            281
            //   231: astore          5
            //   233: aload           5
            //   235: astore          4
            //   237: aload           5
            //   239: athrow         
            //   240: astore          13
            //   242: aload_3        
            //   243: ifnull          278
            //   246: aload           4
            //   248: ifnull          272
            //   251: aload_3        
            //   252: invokeinterface java/nio/channels/SeekableByteChannel.close:()V
            //   257: goto            278
            //   260: astore          14
            //   262: aload           4
            //   264: aload           14
            //   266: invokevirtual   java/lang/Throwable.addSuppressed:(Ljava/lang/Throwable;)V
            //   269: goto            278
            //   272: aload_3        
            //   273: invokeinterface java/nio/channels/SeekableByteChannel.close:()V
            //   278: aload           13
            //   280: athrow         
            //   281: aload_2        
            //   282: invokestatic    sun/security/krb5/internal/rcache/DflCache$Storage.makeMine:(Ljava/nio/file/Path;)V
            //   285: aload_2        
            //   286: aload_0        
            //   287: iconst_2       
            //   288: anewarray       Ljava/nio/file/CopyOption;
            //   291: dup            
            //   292: iconst_0       
            //   293: getstatic       java/nio/file/StandardCopyOption.REPLACE_EXISTING:Ljava/nio/file/StandardCopyOption;
            //   296: aastore        
            //   297: dup            
            //   298: iconst_1       
            //   299: getstatic       java/nio/file/StandardCopyOption.ATOMIC_MOVE:Ljava/nio/file/StandardCopyOption;
            //   302: aastore        
            //   303: invokestatic    java/nio/file/Files.move:(Ljava/nio/file/Path;Ljava/nio/file/Path;[Ljava/nio/file/CopyOption;)Ljava/nio/file/Path;
            //   306: pop            
            //   307: return         
            //    Exceptions:
            //  throws java.io.IOException
            //    StackMapTable: 00 14 FF 00 32 00 08 07 00 5F 07 00 69 07 00 5F 07 00 60 07 00 61 07 00 60 07 00 61 04 00 00 26 42 07 00 6A FA 00 04 53 07 00 61 0B 49 07 00 61 48 07 00 61 FF 00 15 00 0C 07 00 5F 07 00 69 07 00 5F 07 00 60 07 00 61 07 00 60 07 00 61 00 00 00 00 07 00 61 00 01 07 00 61 0B 06 FF 00 02 00 05 07 00 5F 07 00 69 07 00 5F 07 00 60 07 00 61 00 00 51 07 00 61 0B 48 07 00 61 48 07 00 61 FF 00 13 00 0E 07 00 5F 07 00 69 07 00 5F 07 00 60 07 00 61 00 00 00 00 00 00 00 00 07 00 61 00 01 07 00 61 0B 05 FF 00 02 00 03 07 00 5F 07 00 69 07 00 5F 00 00
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                               
            //  -----  -----  -----  -----  -----------------------------------
            //  50     89     92     231    Ljava/nio/BufferUnderflowException;
            //  107    114    117    129    Ljava/lang/Throwable;
            //  38     97     139    148    Ljava/lang/Throwable;
            //  38     97     148    192    Any
            //  160    167    170    182    Ljava/lang/Throwable;
            //  139    150    148    192    Any
            //  201    207    210    222    Ljava/lang/Throwable;
            //  29     192    231    240    Ljava/lang/Throwable;
            //  29     192    240    281    Any
            //  251    257    260    272    Ljava/lang/Throwable;
            //  231    242    240    281    Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IndexOutOfBoundsException: Index: 139, Size: 139
            //     at java.util.ArrayList.rangeCheck(Unknown Source)
            //     at java.util.ArrayList.get(Unknown Source)
            //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
            //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3611)
            //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3476)
            //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        private int loadAndCheck(final Path path, final AuthTimeWithHash authTimeWithHash, final KerberosTime kerberosTime) throws IOException, KrbApErrException {
            int n = 0;
            if (Files.isSymbolicLink(path)) {
                throw new IOException("Symlink not accepted");
            }
            try {
                final Set<PosixFilePermission> posixFilePermissions = Files.getPosixFilePermissions(path, new LinkOption[0]);
                if (DflCache.uid != -1 && (int)Files.getAttribute(path, "unix:uid", new LinkOption[0]) != DflCache.uid) {
                    throw new IOException("Not mine");
                }
                if (posixFilePermissions.contains(PosixFilePermission.GROUP_READ) || posixFilePermissions.contains(PosixFilePermission.GROUP_WRITE) || posixFilePermissions.contains(PosixFilePermission.GROUP_EXECUTE) || posixFilePermissions.contains(PosixFilePermission.OTHERS_READ) || posixFilePermissions.contains(PosixFilePermission.OTHERS_WRITE) || posixFilePermissions.contains(PosixFilePermission.OTHERS_EXECUTE)) {
                    throw new IOException("Accessible by someone else");
                }
            }
            catch (final UnsupportedOperationException ex) {}
            this.chan = Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.READ);
            final long n2 = kerberosTime.getSeconds() - readHeader(this.chan);
            long position = 0L;
            boolean b = false;
            try {
                while (true) {
                    position = this.chan.position();
                    final AuthTime from = AuthTime.readFrom(this.chan);
                    if (from instanceof AuthTimeWithHash) {
                        if (authTimeWithHash.equals(from)) {
                            throw new KrbApErrException(34);
                        }
                        if (authTimeWithHash.isSameIgnoresHash(from)) {
                            b = true;
                        }
                    }
                    else if (authTimeWithHash.isSameIgnoresHash(from) && !b) {
                        throw new KrbApErrException(34);
                    }
                    if (from.ctime < n2) {
                        ++n;
                    }
                    else {
                        --n;
                    }
                }
            }
            catch (final BufferUnderflowException ex2) {
                this.chan.position(position);
                return n;
            }
        }
        
        private static int readHeader(final SeekableByteChannel seekableByteChannel) throws IOException {
            final ByteBuffer allocate = ByteBuffer.allocate(6);
            seekableByteChannel.read(allocate);
            if (allocate.getShort(0) != 1281) {
                throw new IOException("Not correct rcache version");
            }
            allocate.order(ByteOrder.nativeOrder());
            return allocate.getInt(2);
        }
        
        private void append(final AuthTimeWithHash authTimeWithHash) throws IOException {
            this.chan.write(ByteBuffer.wrap(authTimeWithHash.encode(true)));
            this.chan.write(ByteBuffer.wrap(authTimeWithHash.encode(false)));
        }
        
        @Override
        public void close() throws IOException {
            if (this.chan != null) {
                this.chan.close();
            }
            this.chan = null;
        }
    }
}
