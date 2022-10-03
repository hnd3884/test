package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.util.logging.Logger;

class TempFiles
{
    private static final Logger LOGGER;
    private static final Class<?> CLASS_FILES;
    private static final Class<?> CLASS_PATH;
    private static final Class<?> CLASS_FILE_ATTRIBUTE;
    private static final Class<?> CLASS_FILE_ATTRIBUTES;
    private static final Method METHOD_FILE_TO_PATH;
    private static final Method METHOD_FILES_CREATE_TEMP_FILE;
    private static final Method METHOD_FILES_CREATE_TEMP_FILE_WITHPATH;
    private static final Method METHOD_PATH_TO_FILE;
    private static boolean useJdk6API;
    
    private static boolean isJdk6() {
        final String javaVersion = System.getProperty("java.version");
        TempFiles.LOGGER.log(Level.FINEST, "Detected java version = {0}", javaVersion);
        return javaVersion.startsWith("1.6.");
    }
    
    private static Class<?> safeGetClass(final String className) {
        if (TempFiles.useJdk6API) {
            return null;
        }
        try {
            return Class.forName(className);
        }
        catch (final ClassNotFoundException e) {
            TempFiles.LOGGER.log(Level.SEVERE, "Exception cought", e);
            TempFiles.LOGGER.log(Level.WARNING, "Class {0} not found. Temp files will be created using old java.io API.", className);
            TempFiles.useJdk6API = true;
            return null;
        }
    }
    
    private static Method safeGetMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
        if (TempFiles.useJdk6API) {
            return null;
        }
        try {
            return clazz.getMethod(methodName, parameterTypes);
        }
        catch (final NoSuchMethodException e) {
            TempFiles.LOGGER.log(Level.SEVERE, "Exception cought", e);
            TempFiles.LOGGER.log(Level.WARNING, "Method {0} not found. Temp files will be created using old java.io API.", methodName);
            TempFiles.useJdk6API = true;
            return null;
        }
    }
    
    static Object toPath(final File f) throws InvocationTargetException, IllegalAccessException {
        return TempFiles.METHOD_FILE_TO_PATH.invoke(f, new Object[0]);
    }
    
    static File toFile(final Object path) throws InvocationTargetException, IllegalAccessException {
        return (File)TempFiles.METHOD_PATH_TO_FILE.invoke(path, new Object[0]);
    }
    
    static File createTempFile(final String prefix, final String suffix, final File dir) throws IOException {
        if (TempFiles.useJdk6API) {
            TempFiles.LOGGER.log(Level.FINEST, "Jdk6 detected, temp file (prefix:{0}, suffix:{1}) being created using old java.io API.", new Object[] { prefix, suffix });
            return File.createTempFile(prefix, suffix, dir);
        }
        try {
            if (dir != null) {
                final Object path = toPath(dir);
                TempFiles.LOGGER.log(Level.FINEST, "Temp file (path: {0}, prefix:{1}, suffix:{2}) being created using NIO API.", new Object[] { dir.getAbsolutePath(), prefix, suffix });
                return toFile(TempFiles.METHOD_FILES_CREATE_TEMP_FILE_WITHPATH.invoke(null, path, prefix, suffix, Array.newInstance(TempFiles.CLASS_FILE_ATTRIBUTE, 0)));
            }
            TempFiles.LOGGER.log(Level.FINEST, "Temp file (prefix:{0}, suffix:{1}) being created using NIO API.", new Object[] { prefix, suffix });
            return toFile(TempFiles.METHOD_FILES_CREATE_TEMP_FILE.invoke(null, prefix, suffix, Array.newInstance(TempFiles.CLASS_FILE_ATTRIBUTE, 0)));
        }
        catch (final IllegalAccessException e) {
            TempFiles.LOGGER.log(Level.SEVERE, "Exception caught", e);
            TempFiles.LOGGER.log(Level.WARNING, "Error invoking java.nio API, temp file (path: {0}, prefix:{1}, suffix:{2}) being created using old java.io API.", new Object[] { (dir != null) ? dir.getAbsolutePath() : null, prefix, suffix });
            return File.createTempFile(prefix, suffix, dir);
        }
        catch (final InvocationTargetException e2) {
            TempFiles.LOGGER.log(Level.SEVERE, "Exception caught", e2);
            TempFiles.LOGGER.log(Level.WARNING, "Error invoking java.nio API, temp file (path: {0}, prefix:{1}, suffix:{2}) being created using old java.io API.", new Object[] { (dir != null) ? dir.getAbsolutePath() : null, prefix, suffix });
            return File.createTempFile(prefix, suffix, dir);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(TempFiles.class.getName());
        TempFiles.useJdk6API = isJdk6();
        CLASS_FILES = safeGetClass("java.nio.file.Files");
        CLASS_PATH = safeGetClass("java.nio.file.Path");
        CLASS_FILE_ATTRIBUTE = safeGetClass("java.nio.file.attribute.FileAttribute");
        CLASS_FILE_ATTRIBUTES = safeGetClass("[Ljava.nio.file.attribute.FileAttribute;");
        METHOD_FILE_TO_PATH = safeGetMethod(File.class, "toPath", (Class<?>[])new Class[0]);
        METHOD_FILES_CREATE_TEMP_FILE = safeGetMethod(TempFiles.CLASS_FILES, "createTempFile", String.class, String.class, TempFiles.CLASS_FILE_ATTRIBUTES);
        METHOD_FILES_CREATE_TEMP_FILE_WITHPATH = safeGetMethod(TempFiles.CLASS_FILES, "createTempFile", TempFiles.CLASS_PATH, String.class, String.class, TempFiles.CLASS_FILE_ATTRIBUTES);
        METHOD_PATH_TO_FILE = safeGetMethod(TempFiles.CLASS_PATH, "toFile", (Class<?>[])new Class[0]);
    }
}
