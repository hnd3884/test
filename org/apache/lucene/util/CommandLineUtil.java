package org.apache.lucene.util;

import java.lang.reflect.Constructor;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.FSLockFactory;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Path;

public final class CommandLineUtil
{
    private CommandLineUtil() {
    }
    
    public static FSDirectory newFSDirectory(final String clazzName, final Path path) {
        return newFSDirectory(clazzName, path, FSLockFactory.getDefault());
    }
    
    public static FSDirectory newFSDirectory(final String clazzName, final Path path, final LockFactory lf) {
        try {
            final Class<? extends FSDirectory> clazz = loadFSDirectoryClass(clazzName);
            return newFSDirectory(clazz, path, lf);
        }
        catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException(FSDirectory.class.getSimpleName() + " implementation not found: " + clazzName, e);
        }
        catch (final ClassCastException e2) {
            throw new IllegalArgumentException(clazzName + " is not a " + FSDirectory.class.getSimpleName() + " implementation", e2);
        }
        catch (final NoSuchMethodException e3) {
            throw new IllegalArgumentException(clazzName + " constructor with " + Path.class.getSimpleName() + " as parameter not found", e3);
        }
        catch (final Exception e4) {
            throw new IllegalArgumentException("Error creating " + clazzName + " instance", e4);
        }
    }
    
    public static Class<? extends Directory> loadDirectoryClass(final String clazzName) throws ClassNotFoundException {
        return Class.forName(adjustDirectoryClassName(clazzName)).asSubclass(Directory.class);
    }
    
    public static Class<? extends FSDirectory> loadFSDirectoryClass(final String clazzName) throws ClassNotFoundException {
        return Class.forName(adjustDirectoryClassName(clazzName)).asSubclass(FSDirectory.class);
    }
    
    private static String adjustDirectoryClassName(String clazzName) {
        if (clazzName == null || clazzName.trim().length() == 0) {
            throw new IllegalArgumentException("The " + FSDirectory.class.getSimpleName() + " implementation cannot be null or empty");
        }
        if (clazzName.indexOf(".") == -1) {
            clazzName = Directory.class.getPackage().getName() + "." + clazzName;
        }
        return clazzName;
    }
    
    public static FSDirectory newFSDirectory(final Class<? extends FSDirectory> clazz, final Path path) throws ReflectiveOperationException {
        return newFSDirectory(clazz, path, FSLockFactory.getDefault());
    }
    
    public static FSDirectory newFSDirectory(final Class<? extends FSDirectory> clazz, final Path path, final LockFactory lf) throws ReflectiveOperationException {
        final Constructor<? extends FSDirectory> ctor = clazz.getConstructor(Path.class, LockFactory.class);
        return (FSDirectory)ctor.newInstance(path, lf);
    }
}
