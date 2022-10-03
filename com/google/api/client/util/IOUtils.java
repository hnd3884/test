package com.google.api.client.util;

import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class IOUtils
{
    @Deprecated
    public static void copy(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        copy(inputStream, outputStream, true);
    }
    
    @Deprecated
    public static void copy(final InputStream inputStream, final OutputStream outputStream, final boolean closeInputStream) throws IOException {
        try {
            ByteStreams.copy(inputStream, outputStream);
        }
        finally {
            if (closeInputStream) {
                inputStream.close();
            }
        }
    }
    
    public static long computeLength(final StreamingContent content) throws IOException {
        final ByteCountingOutputStream countingStream = new ByteCountingOutputStream();
        try {
            content.writeTo(countingStream);
        }
        finally {
            countingStream.close();
        }
        return countingStream.count;
    }
    
    public static byte[] serialize(final Object value) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        serialize(value, out);
        return out.toByteArray();
    }
    
    public static void serialize(final Object value, final OutputStream outputStream) throws IOException {
        try {
            new ObjectOutputStream(outputStream).writeObject(value);
        }
        finally {
            outputStream.close();
        }
    }
    
    public static <S extends Serializable> S deserialize(final byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }
        return deserialize(new ByteArrayInputStream(bytes));
    }
    
    public static <S extends Serializable> S deserialize(final InputStream inputStream) throws IOException {
        try {
            return (S)new ObjectInputStream(inputStream).readObject();
        }
        catch (final ClassNotFoundException exception) {
            final IOException ioe = new IOException("Failed to deserialize object");
            ioe.initCause(exception);
            throw ioe;
        }
        finally {
            inputStream.close();
        }
    }
    
    @Deprecated
    public static boolean isSymbolicLink(final File file) throws IOException {
        try {
            final Class<?> filesClass = Class.forName("java.nio.file.Files");
            final Class<?> pathClass = Class.forName("java.nio.file.Path");
            final Object path = File.class.getMethod("toPath", (Class<?>[])new Class[0]).invoke(file, new Object[0]);
            return (boolean)filesClass.getMethod("isSymbolicLink", pathClass).invoke(null, path);
        }
        catch (final InvocationTargetException exception) {
            final Throwable cause = exception.getCause();
            Throwables.propagateIfPossible(cause, IOException.class);
            throw new RuntimeException(cause);
        }
        catch (final ClassNotFoundException ex) {}
        catch (final IllegalArgumentException ex2) {}
        catch (final SecurityException ex3) {}
        catch (final IllegalAccessException ex4) {}
        catch (final NoSuchMethodException ex5) {}
        if (File.separatorChar == '\\') {
            return false;
        }
        File canonical = file;
        if (file.getParent() != null) {
            canonical = new File(file.getParentFile().getCanonicalFile(), file.getName());
        }
        return !canonical.getCanonicalFile().equals(canonical.getAbsoluteFile());
    }
}
