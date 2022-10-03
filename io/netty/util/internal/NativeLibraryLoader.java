package io.netty.util.internal;

import java.util.Set;
import java.util.Collection;
import java.util.EnumSet;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;
import io.netty.util.internal.logging.InternalLogger;

public final class NativeLibraryLoader
{
    private static final InternalLogger logger;
    private static final String NATIVE_RESOURCE_HOME = "META-INF/native/";
    private static final File WORKDIR;
    private static final boolean DELETE_NATIVE_LIB_AFTER_LOADING;
    private static final boolean TRY_TO_PATCH_SHADED_ID;
    private static final byte[] UNIQUE_ID_BYTES;
    
    public static void loadFirstAvailable(final ClassLoader loader, final String... names) {
        final List<Throwable> suppressed = new ArrayList<Throwable>();
        final int length = names.length;
        int i = 0;
        while (i < length) {
            final String name = names[i];
            try {
                load(name, loader);
                return;
            }
            catch (final Throwable t) {
                suppressed.add(t);
                ++i;
                continue;
            }
            break;
        }
        final IllegalArgumentException iae = new IllegalArgumentException("Failed to load any of the given libraries: " + Arrays.toString(names));
        ThrowableUtil.addSuppressedAndClear(iae, suppressed);
        throw iae;
    }
    
    private static String calculatePackagePrefix() {
        final String maybeShaded = NativeLibraryLoader.class.getName();
        final String expected = "io!netty!util!internal!NativeLibraryLoader".replace('!', '.');
        if (!maybeShaded.endsWith(expected)) {
            throw new UnsatisfiedLinkError(String.format("Could not find prefix added to %s to get %s. When shading, only adding a package prefix is supported", expected, maybeShaded));
        }
        return maybeShaded.substring(0, maybeShaded.length() - expected.length());
    }
    
    public static void load(final String originalName, final ClassLoader loader) {
        final String packagePrefix = calculatePackagePrefix().replace('.', '_');
        final String name = packagePrefix + originalName;
        final List<Throwable> suppressed = new ArrayList<Throwable>();
        try {
            loadLibrary(loader, name, false);
        }
        catch (final Throwable ex) {
            suppressed.add(ex);
            final String libname = System.mapLibraryName(name);
            final String path = "META-INF/native/" + libname;
            InputStream in = null;
            OutputStream out = null;
            File tmpFile = null;
            Label_0117: {
                if (loader == null) {
                    final URL url = ClassLoader.getSystemResource(path);
                    break Label_0117;
                }
                URL url = loader.getResource(path);
                try {
                    if (url == null) {
                        if (!PlatformDependent.isOsx()) {
                            final FileNotFoundException fnf = new FileNotFoundException(path);
                            ThrowableUtil.addSuppressedAndClear(fnf, suppressed);
                            throw fnf;
                        }
                        final String fileName = path.endsWith(".jnilib") ? ("META-INF/native/lib" + name + ".dynlib") : ("META-INF/native/lib" + name + ".jnilib");
                        if (loader == null) {
                            url = ClassLoader.getSystemResource(fileName);
                        }
                        else {
                            url = loader.getResource(fileName);
                        }
                        if (url == null) {
                            final FileNotFoundException fnf2 = new FileNotFoundException(fileName);
                            ThrowableUtil.addSuppressedAndClear(fnf2, suppressed);
                            throw fnf2;
                        }
                    }
                    final int index = libname.lastIndexOf(46);
                    final String prefix = libname.substring(0, index);
                    final String suffix = libname.substring(index);
                    tmpFile = PlatformDependent.createTempFile(prefix, suffix, NativeLibraryLoader.WORKDIR);
                    in = url.openStream();
                    out = new FileOutputStream(tmpFile);
                    if (shouldShadedLibraryIdBePatched(packagePrefix)) {
                        patchShadedLibraryId(in, out, originalName, name);
                    }
                    else {
                        final byte[] buffer = new byte[8192];
                        int length;
                        while ((length = in.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }
                    }
                    out.flush();
                    closeQuietly(out);
                    out = null;
                    loadLibrary(loader, tmpFile.getPath(), true);
                }
                catch (final UnsatisfiedLinkError e) {
                    try {
                        if (tmpFile != null && tmpFile.isFile() && tmpFile.canRead() && !canExecuteExecutable(tmpFile)) {
                            NativeLibraryLoader.logger.info("{} exists but cannot be executed even when execute permissions set; check volume for \"noexec\" flag; use -D{}=[path] to set native working directory separately.", tmpFile.getPath(), "io.netty.native.workdir");
                        }
                    }
                    catch (final Throwable t) {
                        suppressed.add(t);
                        NativeLibraryLoader.logger.debug("Error checking if {} is on a file store mounted with noexec", tmpFile, t);
                    }
                    ThrowableUtil.addSuppressedAndClear(e, suppressed);
                    throw e;
                }
                catch (final Exception e2) {
                    final UnsatisfiedLinkError ule = new UnsatisfiedLinkError("could not load a native library: " + name);
                    ule.initCause(e2);
                    ThrowableUtil.addSuppressedAndClear(ule, suppressed);
                    throw ule;
                }
                finally {
                    closeQuietly(in);
                    closeQuietly(out);
                    if (tmpFile != null && (!NativeLibraryLoader.DELETE_NATIVE_LIB_AFTER_LOADING || !tmpFile.delete())) {
                        tmpFile.deleteOnExit();
                    }
                }
            }
        }
    }
    
    static boolean patchShadedLibraryId(final InputStream in, final OutputStream out, final String originalName, final String name) throws IOException {
        final byte[] buffer = new byte[8192];
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(in.available());
        int length;
        while ((length = in.read(buffer)) > 0) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byteArrayOutputStream.flush();
        final byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        boolean patched;
        if (!patchShadedLibraryId(bytes, originalName, name)) {
            final String os = PlatformDependent.normalizedOs();
            final String arch = PlatformDependent.normalizedArch();
            final String osArch = "_" + os + "_" + arch;
            patched = (originalName.endsWith(osArch) && patchShadedLibraryId(bytes, originalName.substring(0, originalName.length() - osArch.length()), name));
        }
        else {
            patched = true;
        }
        out.write(bytes, 0, bytes.length);
        return patched;
    }
    
    private static boolean shouldShadedLibraryIdBePatched(final String packagePrefix) {
        return NativeLibraryLoader.TRY_TO_PATCH_SHADED_ID && PlatformDependent.isOsx() && !packagePrefix.isEmpty();
    }
    
    private static boolean patchShadedLibraryId(final byte[] bytes, final String originalName, final String name) {
        final byte[] nameBytes = originalName.getBytes(CharsetUtil.UTF_8);
        int idIdx = -1;
    Label_0085:
        for (int i = 0; i < bytes.length && bytes.length - i >= nameBytes.length; ++i) {
            int idx = i;
            int j = 0;
            while (j < nameBytes.length && bytes[idx++] == nameBytes[j++]) {
                if (j == nameBytes.length) {
                    idIdx = i;
                    break Label_0085;
                }
            }
        }
        if (idIdx == -1) {
            NativeLibraryLoader.logger.debug("Was not able to find the ID of the shaded native library {}, can't adjust it.", name);
            return false;
        }
        for (int i = 0; i < nameBytes.length; ++i) {
            bytes[idIdx + i] = NativeLibraryLoader.UNIQUE_ID_BYTES[PlatformDependent.threadLocalRandom().nextInt(NativeLibraryLoader.UNIQUE_ID_BYTES.length)];
        }
        if (NativeLibraryLoader.logger.isDebugEnabled()) {
            NativeLibraryLoader.logger.debug("Found the ID of the shaded native library {}. Replacing ID part {} with {}", name, originalName, new String(bytes, idIdx, nameBytes.length, CharsetUtil.UTF_8));
        }
        return true;
    }
    
    private static void loadLibrary(final ClassLoader loader, final String name, final boolean absolute) {
        Throwable suppressed = null;
        try {
            try {
                final Class<?> newHelper = tryToLoadClass(loader, NativeLibraryUtil.class);
                loadLibraryByHelper(newHelper, name, absolute);
                NativeLibraryLoader.logger.debug("Successfully loaded the library {}", name);
                return;
            }
            catch (final UnsatisfiedLinkError e) {
                suppressed = e;
            }
            catch (final Exception e2) {
                suppressed = e2;
            }
            NativeLibraryUtil.loadLibrary(name, absolute);
            NativeLibraryLoader.logger.debug("Successfully loaded the library {}", name);
        }
        catch (final NoSuchMethodError nsme) {
            if (suppressed != null) {
                ThrowableUtil.addSuppressed(nsme, suppressed);
            }
            rethrowWithMoreDetailsIfPossible(name, nsme);
        }
        catch (final UnsatisfiedLinkError ule) {
            if (suppressed != null) {
                ThrowableUtil.addSuppressed(ule, suppressed);
            }
            throw ule;
        }
    }
    
    @SuppressJava6Requirement(reason = "Guarded by version check")
    private static void rethrowWithMoreDetailsIfPossible(final String name, final NoSuchMethodError error) {
        if (PlatformDependent.javaVersion() >= 7) {
            throw new LinkageError("Possible multiple incompatible native libraries on the classpath for '" + name + "'?", error);
        }
        throw error;
    }
    
    private static void loadLibraryByHelper(final Class<?> helper, final String name, final boolean absolute) throws UnsatisfiedLinkError {
        final Object ret = AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    final Method method = helper.getMethod("loadLibrary", String.class, Boolean.TYPE);
                    method.setAccessible(true);
                    return method.invoke(null, name, absolute);
                }
                catch (final Exception e) {
                    return e;
                }
            }
        });
        if (!(ret instanceof Throwable)) {
            return;
        }
        final Throwable t = (Throwable)ret;
        assert !(t instanceof UnsatisfiedLinkError) : t + " should be a wrapper throwable";
        final Throwable cause = t.getCause();
        if (cause instanceof UnsatisfiedLinkError) {
            throw (UnsatisfiedLinkError)cause;
        }
        final UnsatisfiedLinkError ule = new UnsatisfiedLinkError(t.getMessage());
        ule.initCause(t);
        throw ule;
    }
    
    private static Class<?> tryToLoadClass(final ClassLoader loader, final Class<?> helper) throws ClassNotFoundException {
        try {
            return Class.forName(helper.getName(), false, loader);
        }
        catch (final ClassNotFoundException e1) {
            if (loader == null) {
                throw e1;
            }
            try {
                final byte[] classBinary = classToByteArray(helper);
                return AccessController.doPrivileged((PrivilegedAction<Class<?>>)new PrivilegedAction<Class<?>>() {
                    @Override
                    public Class<?> run() {
                        try {
                            final Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
                            defineClass.setAccessible(true);
                            return (Class)defineClass.invoke(loader, helper.getName(), classBinary, 0, classBinary.length);
                        }
                        catch (final Exception e) {
                            throw new IllegalStateException("Define class failed!", e);
                        }
                    }
                });
            }
            catch (final ClassNotFoundException e2) {
                ThrowableUtil.addSuppressed(e2, e1);
                throw e2;
            }
            catch (final RuntimeException e3) {
                ThrowableUtil.addSuppressed(e3, e1);
                throw e3;
            }
            catch (final Error e4) {
                ThrowableUtil.addSuppressed(e4, e1);
                throw e4;
            }
        }
    }
    
    private static byte[] classToByteArray(final Class<?> clazz) throws ClassNotFoundException {
        String fileName = clazz.getName();
        final int lastDot = fileName.lastIndexOf(46);
        if (lastDot > 0) {
            fileName = fileName.substring(lastDot + 1);
        }
        final URL classUrl = clazz.getResource(fileName + ".class");
        if (classUrl == null) {
            throw new ClassNotFoundException(clazz.getName());
        }
        final byte[] buf = new byte[1024];
        final ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        InputStream in = null;
        try {
            in = classUrl.openStream();
            int r;
            while ((r = in.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
            return out.toByteArray();
        }
        catch (final IOException ex) {
            throw new ClassNotFoundException(clazz.getName(), ex);
        }
        finally {
            closeQuietly(in);
            closeQuietly(out);
        }
    }
    
    private static void closeQuietly(final Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    private NativeLibraryLoader() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(NativeLibraryLoader.class);
        UNIQUE_ID_BYTES = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(CharsetUtil.US_ASCII);
        final String workdir = SystemPropertyUtil.get("io.netty.native.workdir");
        if (workdir != null) {
            File f = new File(workdir);
            f.mkdirs();
            try {
                f = f.getAbsoluteFile();
            }
            catch (final Exception ex) {}
            WORKDIR = f;
            NativeLibraryLoader.logger.debug("-Dio.netty.native.workdir: " + NativeLibraryLoader.WORKDIR);
        }
        else {
            WORKDIR = PlatformDependent.tmpdir();
            NativeLibraryLoader.logger.debug("-Dio.netty.native.workdir: " + NativeLibraryLoader.WORKDIR + " (io.netty.tmpdir)");
        }
        DELETE_NATIVE_LIB_AFTER_LOADING = SystemPropertyUtil.getBoolean("io.netty.native.deleteLibAfterLoading", true);
        NativeLibraryLoader.logger.debug("-Dio.netty.native.deleteLibAfterLoading: {}", (Object)NativeLibraryLoader.DELETE_NATIVE_LIB_AFTER_LOADING);
        TRY_TO_PATCH_SHADED_ID = SystemPropertyUtil.getBoolean("io.netty.native.tryPatchShadedId", true);
        NativeLibraryLoader.logger.debug("-Dio.netty.native.tryPatchShadedId: {}", (Object)NativeLibraryLoader.TRY_TO_PATCH_SHADED_ID);
    }
    
    private static final class NoexecVolumeDetector
    {
        @SuppressJava6Requirement(reason = "Usage guarded by java version check")
        private static boolean canExecuteExecutable(final File file) throws IOException {
            if (PlatformDependent.javaVersion() < 7) {
                return true;
            }
            if (file.canExecute()) {
                return true;
            }
            final Set<PosixFilePermission> existingFilePermissions = Files.getPosixFilePermissions(file.toPath(), new LinkOption[0]);
            final Set<PosixFilePermission> executePermissions = EnumSet.of(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE);
            if (existingFilePermissions.containsAll(executePermissions)) {
                return false;
            }
            final Set<PosixFilePermission> newPermissions = EnumSet.copyOf(existingFilePermissions);
            newPermissions.addAll(executePermissions);
            Files.setPosixFilePermissions(file.toPath(), newPermissions);
            return file.canExecute();
        }
    }
}
