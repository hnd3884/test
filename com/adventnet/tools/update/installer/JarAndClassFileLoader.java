package com.adventnet.tools.update.installer;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.io.RandomAccessFile;
import java.util.zip.ZipFile;
import java.io.File;
import java.util.Vector;

public class JarAndClassFileLoader extends ClassLoader
{
    Vector pathFileVec;
    
    public JarAndClassFileLoader() {
        this.pathFileVec = new Vector();
    }
    
    public void addPathFile(final File fileArg) {
        this.pathFileVec.addElement(fileArg);
    }
    
    public void addPathFile(final ZipFile fileArg) {
        this.pathFileVec.addElement(fileArg);
    }
    
    protected final Class findClassFromDirectories(File localResourceDirectory, final String nameArg) throws ClassNotFoundException {
        final String fileName = nameArg.replace('.', '/') + ".class";
        if (!localResourceDirectory.isDirectory()) {
            localResourceDirectory = localResourceDirectory.getParentFile();
        }
        final File file = new File(localResourceDirectory.getPath(), fileName);
        if (file.canRead()) {
            try {
                final RandomAccessFile rda = new RandomAccessFile(file, "r");
                final byte[] b = new byte[(int)rda.length()];
                rda.readFully(b);
                return this.defineClass(nameArg, b, 0, b.length);
            }
            catch (final Throwable ex) {
                throw new ClassNotFoundException(PureUtils.getCorrectErrorMsg("Exception occurred when trying to define class from file \"" + file.getAbsolutePath() + '\"', ex));
            }
        }
        return null;
    }
    
    protected final Class findClassFromJars(final ZipFile zipFile, final String nameArg) throws ClassNotFoundException {
        final String fileName = nameArg.replace('.', '/') + ".class";
        final ZipEntry zipEntry = zipFile.getEntry(fileName);
        if (zipEntry != null) {
            try {
                final InputStream inpStream = zipFile.getInputStream(zipEntry);
                final byte[] b = new byte[(int)zipEntry.getSize()];
                int pos = 0;
                while (true) {
                    final int length = inpStream.read(b, pos, b.length - pos);
                    if (length <= 0) {
                        break;
                    }
                    pos += length;
                }
                return this.defineClass(nameArg, b, 0, pos);
            }
            catch (final Throwable ex) {
                throw new ClassNotFoundException("Exception occurred when trying to define class \"" + nameArg + "\"." + ex.getMessage());
            }
        }
        return null;
    }
    
    protected final Class findClassFromFiles(final String nameArg) throws ClassNotFoundException {
        for (int i = this.pathFileVec.size() - 1; i >= 0; --i) {
            final Object element = this.pathFileVec.elementAt(i);
            if (element instanceof ZipFile) {
                final Class cls = this.findClassFromJars((ZipFile)element, nameArg);
                if (cls != null) {
                    return cls;
                }
            }
            else if (element instanceof File) {
                final Class cls = this.findClassFromDirectories((File)element, nameArg);
                if (cls != null) {
                    return cls;
                }
            }
        }
        return null;
    }
    
    @Override
    public Class loadClass(final String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }
    
    public Class loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        Class c = this.findLoadedClass(name);
        if (c == null) {
            c = this.findClassFromFiles(name);
        }
        if (c == null) {
            c = this.findSystemClass(name);
        }
        if (resolve) {
            this.resolveClass(c);
        }
        return c;
    }
    
    public static void main(final String[] args) throws Throwable {
        if (args.length < 2) {
            ConsoleOut.println(" Usage : JarAndClassFileLoader [<jarfiles>]* <className>");
        }
        final JarAndClassFileLoader jfl = new JarAndClassFileLoader();
        for (int i = 0; i < args.length - 1; ++i) {
            if (new File(args[i]).exists()) {
                if (args[i].endsWith(".jar") || args[i].endsWith(".zip")) {
                    jfl.addPathFile(new ZipFile(args[i]));
                }
                else {
                    jfl.addPathFile(new File(args[i]));
                }
            }
            else {
                ConsoleOut.println(" File " + args[0] + " does not exist");
            }
        }
        final Class c = jfl.loadClass(args[args.length - 1]);
        if (c != null) {
            ConsoleOut.println(" Successfully Loaded " + c.getName());
        }
        else {
            ConsoleOut.println(" Failed loading the class " + args[args.length - 1]);
        }
    }
}
