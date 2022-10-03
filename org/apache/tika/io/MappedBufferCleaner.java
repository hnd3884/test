package org.apache.tika.io;

import java.security.AccessController;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.invoke.MethodHandle;
import java.util.Objects;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MappedBufferCleaner
{
    public static final boolean UNMAP_SUPPORTED;
    public static final String UNMAP_NOT_SUPPORTED_REASON;
    private static final BufferCleaner CLEANER;
    
    public static void freeBuffer(final ByteBuffer b) throws IOException {
        if (MappedBufferCleaner.CLEANER != null && b != null) {
            MappedBufferCleaner.CLEANER.freeBuffer("", b);
        }
    }
    
    private static Object unmapHackImpl() {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            try {
                final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                final MethodHandle unmapper = lookup.findVirtual(unsafeClass, "invokeCleaner", MethodType.methodType(Void.TYPE, ByteBuffer.class));
                final Field f = unsafeClass.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                final Object theUnsafe = f.get(null);
                return newBufferCleaner(ByteBuffer.class, unmapper.bindTo(theUnsafe));
            }
            catch (final SecurityException se) {
                throw se;
            }
            catch (final ReflectiveOperationException | RuntimeException e) {
                final Class<?> directBufferClass = Class.forName("java.nio.DirectByteBuffer");
                final Method m = directBufferClass.getMethod("cleaner", (Class<?>[])new Class[0]);
                m.setAccessible(true);
                final MethodHandle directBufferCleanerMethod = lookup.unreflect(m);
                final Class<?> cleanerClass = directBufferCleanerMethod.type().returnType();
                final MethodHandle cleanMethod = lookup.findVirtual(cleanerClass, "clean", MethodType.methodType(Void.TYPE));
                final MethodHandle nonNullTest = lookup.findStatic(Objects.class, "nonNull", MethodType.methodType(Boolean.TYPE, Object.class)).asType(MethodType.methodType(Boolean.TYPE, cleanerClass));
                final MethodHandle noop = MethodHandles.dropArguments(MethodHandles.constant(Void.class, null).asType(MethodType.methodType(Void.TYPE)), 0, cleanerClass);
                final MethodHandle unmapper2 = MethodHandles.filterReturnValue(directBufferCleanerMethod, MethodHandles.guardWithTest(nonNullTest, cleanMethod, noop)).asType(MethodType.methodType(Void.TYPE, ByteBuffer.class));
                return newBufferCleaner(directBufferClass, unmapper2);
            }
        }
        catch (final SecurityException se) {
            return "Unmapping is not supported, because not all required permissions are given to  the Tika JAR file: " + se + " [Please grant at least the following permissions:  RuntimePermission(\"accessClassInPackage.sun.misc\")  and ReflectPermission(\"suppressAccessChecks\")]";
        }
        catch (final ReflectiveOperationException | RuntimeException e) {
            return "Unmapping is not supported on this platform, because internal Java APIs are not compatible with this Lucene version: " + e;
        }
    }
    
    private static BufferCleaner newBufferCleaner(final Class<?> unmappableBufferClass, final MethodHandle unmapper) {
        assert Objects.equals(MethodType.methodType(Void.TYPE, ByteBuffer.class), unmapper.type());
        return (resourceDescription, buffer) -> {
            if (!buffer.isDirect()) {
                throw new IllegalArgumentException("unmapping only works with direct buffers");
            }
            else if (!unmappableBufferClass.isInstance(buffer)) {
                new IllegalArgumentException("buffer is not an instance of " + unmappableBufferClass.getName());
                throw;
            }
            else {
                final Throwable error = AccessController.doPrivileged(() -> {
                    try {
                        unmapper.invokeExact(buffer);
                        return null;
                    }
                    catch (final Throwable t) {
                        return t;
                    }
                });
                if (error != null) {
                    new IOException("Unable to unmap the mapped buffer: " + resourceDescription, error);
                    throw;
                }
            }
        };
    }
    
    static {
        final Object hack = AccessController.doPrivileged(MappedBufferCleaner::unmapHackImpl);
        if (hack instanceof BufferCleaner) {
            CLEANER = (BufferCleaner)hack;
            UNMAP_SUPPORTED = true;
            UNMAP_NOT_SUPPORTED_REASON = null;
        }
        else {
            CLEANER = null;
            UNMAP_SUPPORTED = false;
            UNMAP_NOT_SUPPORTED_REASON = hack.toString();
        }
    }
    
    private interface BufferCleaner
    {
        void freeBuffer(final String p0, final ByteBuffer p1) throws IOException;
    }
}
