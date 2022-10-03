package com.me.devicemanagement.onpremise.server.silentupdate.ondemand;

import java.util.Enumeration;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Set;
import java.util.Hashtable;
import java.util.Iterator;
import java.lang.instrument.Instrumentation;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.lang.instrument.ClassDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ClassReloader extends ClassLoader
{
    private static ClassReloader classReloader;
    private static Logger logger;
    private static String sourceClass;
    
    public static ClassReloader getInstance() {
        if (ClassReloader.classReloader == null) {
            ClassReloader.classReloader = new ClassReloader();
        }
        return ClassReloader.classReloader;
    }
    
    public boolean defineORRedefineClasses(final List<String> listOfUJars) {
        try {
            if (InstrumentationFactory.getInstance().isInstrumentationLoaded()) {
                final Instrumentation instrumentation = InstrumentationFactory.getInstance().getInstrumentation();
                final ArrayList<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>();
                if (instrumentation.isRedefineClassesSupported()) {
                    for (final String uJar : listOfUJars) {
                        SyMLogger.info(ClassReloader.logger, ClassReloader.sourceClass, "defineORRedefineClasses", "##########################  " + uJar + "#############################");
                        final Hashtable<String, byte[]> classContentsFromUJar = this.extractClassContentsFromJar(uJar);
                        SyMLogger.info(ClassReloader.logger, ClassReloader.sourceClass, "defineORRedefineClasses", "classContentsFromUJar : " + classContentsFromUJar);
                        final Set<String> classNames = classContentsFromUJar.keySet();
                        for (final String className : classNames) {
                            if (super.findLoadedClass(className) != null) {
                                SyMLogger.info(ClassReloader.logger, ClassReloader.sourceClass, "defineORRedefineClasses", "The class '" + className + "' is already loaded into JVM. Added to redefineClasses list.");
                                classDefinitions.add(new ClassDefinition(Class.forName(className), classContentsFromUJar.get(className)));
                            }
                            else {
                                SyMLogger.info(ClassReloader.logger, ClassReloader.sourceClass, "defineORRedefineClasses", "The class '" + className + "' is not loaded into JVM, Going to define the class!");
                                final byte[] content = classContentsFromUJar.get(className);
                                super.defineClass(className, content, 0, content.length);
                            }
                        }
                    }
                }
                else {
                    SyMLogger.info(ClassReloader.logger, ClassReloader.sourceClass, "defineORRedefineClasses", "RedefineClasses is not supported in this instrumentation.");
                }
                SyMLogger.info(ClassReloader.logger, ClassReloader.sourceClass, "defineORRedefineClasses", "Going to redefine the already loaded classes!!!");
                instrumentation.redefineClasses((ClassDefinition[])classDefinitions.toArray(new ClassDefinition[classDefinitions.size()]));
            }
            else {
                SyMLogger.info(ClassReloader.logger, ClassReloader.sourceClass, "defineORRedefineClasses", "Instrumentation is not loaded.");
            }
            return true;
        }
        catch (final Exception e) {
            SyMLogger.error(ClassReloader.logger, ClassReloader.sourceClass, "defineORRedefineClasses", "Exception occurred : ", (Throwable)e);
            return false;
        }
    }
    
    public boolean redefineClass(final String classFileUrl, final String className) {
        try {
            if (InstrumentationFactory.getInstance().isInstrumentationLoaded()) {
                SyMLogger.info(ClassReloader.logger, ClassReloader.sourceClass, "redefineClass", "Going to redefine the '" + className + "' classes!!!");
                final ClassDefinition classDefinition = new ClassDefinition(Class.forName(className), FileAccessUtil.getFileAsByteArray(classFileUrl));
                InstrumentationFactory.getInstance().getInstrumentation().redefineClasses(classDefinition);
                SyMLogger.info(ClassReloader.logger, ClassReloader.sourceClass, "redefineClass", "Class redefined");
                return true;
            }
            SyMLogger.info(ClassReloader.logger, ClassReloader.sourceClass, "redefineClass", "Instrumentation is not loaded.");
        }
        catch (final Exception e) {
            SyMLogger.error(ClassReloader.logger, ClassReloader.sourceClass, "redefineClass", "Exception occurred : ", (Throwable)e);
        }
        return false;
    }
    
    public Hashtable<String, byte[]> extractClassContentsFromJar(final String jarDir) {
        ZipFile zipFile = null;
        InputStream inpStream = null;
        final Hashtable<String, byte[]> hashtable = new Hashtable<String, byte[]>();
        try {
            zipFile = new ZipFile(jarDir);
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry zipEntry = (ZipEntry)entries.nextElement();
                if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(".class")) {
                    String className = zipEntry.getName().replace('/', '.');
                    className = className.substring(0, className.length() - ".class".length());
                    inpStream = zipFile.getInputStream(zipEntry);
                    final byte[] b = new byte[(int)zipEntry.getSize()];
                    int pos = 0;
                    while (true) {
                        final int length = inpStream.read(b, pos, b.length - pos);
                        if (length <= 0) {
                            break;
                        }
                        pos += length;
                    }
                    hashtable.put(className, b);
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(ClassReloader.logger, ClassReloader.sourceClass, "extractClassContentsFromJar", "Exception occurred : ", (Throwable)e);
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
                if (inpStream != null) {
                    inpStream.close();
                }
            }
            catch (final Exception e) {
                SyMLogger.error(ClassReloader.logger, ClassReloader.sourceClass, "extractClassContentsFromJar", "Exception occurred while closing stream : ", (Throwable)e);
            }
        }
        finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
                if (inpStream != null) {
                    inpStream.close();
                }
            }
            catch (final Exception e2) {
                SyMLogger.error(ClassReloader.logger, ClassReloader.sourceClass, "extractClassContentsFromJar", "Exception occurred while closing stream : ", (Throwable)e2);
            }
        }
        return hashtable;
    }
    
    static {
        ClassReloader.classReloader = null;
        ClassReloader.logger = Logger.getLogger("SilentUpdate");
        ClassReloader.sourceClass = "ClassReloader";
    }
}
