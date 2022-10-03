package org.apache.xmlbeans.impl.common;

import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.io.FileOutputStream;
import java.io.File;

public class JarHelper
{
    private static final int BUFFER_SIZE = 2156;
    private byte[] mBuffer;
    private int mByteCount;
    private boolean mVerbose;
    private String mDestJarName;
    private static final char SEP = '/';
    
    public JarHelper() {
        this.mBuffer = new byte[2156];
        this.mByteCount = 0;
        this.mVerbose = false;
        this.mDestJarName = "";
    }
    
    public void jarDir(final File dirOrFile2Jar, final File destJar) throws IOException {
        if (dirOrFile2Jar == null || destJar == null) {
            throw new IllegalArgumentException();
        }
        this.mDestJarName = destJar.getCanonicalPath();
        final FileOutputStream fout = new FileOutputStream(destJar);
        final JarOutputStream jout = new JarOutputStream(fout);
        try {
            this.jarDir(dirOrFile2Jar, jout, null);
        }
        catch (final IOException ioe) {
            throw ioe;
        }
        finally {
            jout.close();
            fout.close();
        }
    }
    
    public void unjarDir(final File jarFile, final File destDir) throws IOException {
        final BufferedOutputStream dest = null;
        final FileInputStream fis = new FileInputStream(jarFile);
        this.unjar(fis, destDir);
    }
    
    public void unjar(final InputStream in, final File destDir) throws IOException {
        BufferedOutputStream dest = null;
        final JarInputStream jis = new JarInputStream(in);
        JarEntry entry;
        while ((entry = jis.getNextJarEntry()) != null) {
            if (entry.isDirectory()) {
                final File dir = new File(destDir, entry.getName());
                dir.mkdir();
                if (entry.getTime() == -1L) {
                    continue;
                }
                dir.setLastModified(entry.getTime());
            }
            else {
                final byte[] data = new byte[2156];
                final File destFile = new File(destDir, entry.getName());
                if (this.mVerbose) {
                    System.out.println("unjarring " + destFile + " from " + entry.getName());
                }
                final FileOutputStream fos = new FileOutputStream(destFile);
                dest = new BufferedOutputStream(fos, 2156);
                int count;
                while ((count = jis.read(data, 0, 2156)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
                if (entry.getTime() == -1L) {
                    continue;
                }
                destFile.setLastModified(entry.getTime());
            }
        }
        jis.close();
    }
    
    public void setVerbose(final boolean b) {
        this.mVerbose = b;
    }
    
    private void jarDir(final File dirOrFile2jar, final JarOutputStream jos, final String path) throws IOException {
        if (this.mVerbose) {
            System.out.println("checking " + dirOrFile2jar);
        }
        if (dirOrFile2jar.isDirectory()) {
            final String[] dirList = dirOrFile2jar.list();
            final String subPath = (path == null) ? "" : (path + dirOrFile2jar.getName() + '/');
            if (path != null) {
                final JarEntry je = new JarEntry(subPath);
                je.setTime(dirOrFile2jar.lastModified());
                jos.putNextEntry(je);
                jos.flush();
                jos.closeEntry();
            }
            for (int i = 0; i < dirList.length; ++i) {
                final File f = new File(dirOrFile2jar, dirList[i]);
                this.jarDir(f, jos, subPath);
            }
        }
        else {
            if (dirOrFile2jar.getCanonicalPath().equals(this.mDestJarName)) {
                if (this.mVerbose) {
                    System.out.println("skipping " + dirOrFile2jar.getPath());
                }
                return;
            }
            if (this.mVerbose) {
                System.out.println("adding " + dirOrFile2jar.getPath());
            }
            final FileInputStream fis = new FileInputStream(dirOrFile2jar);
            try {
                final JarEntry entry = new JarEntry(path + dirOrFile2jar.getName());
                entry.setTime(dirOrFile2jar.lastModified());
                jos.putNextEntry(entry);
                while ((this.mByteCount = fis.read(this.mBuffer)) != -1) {
                    jos.write(this.mBuffer, 0, this.mByteCount);
                    if (this.mVerbose) {
                        System.out.println("wrote " + this.mByteCount + " bytes");
                    }
                }
                jos.flush();
                jos.closeEntry();
            }
            catch (final IOException ioe) {
                throw ioe;
            }
            finally {
                fis.close();
            }
        }
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: JarHelper jarname.jar directory");
            return;
        }
        final JarHelper jarHelper = new JarHelper();
        jarHelper.mVerbose = true;
        final File destJar = new File(args[0]);
        final File dirOrFile2Jar = new File(args[1]);
        jarHelper.jarDir(dirOrFile2Jar, destJar);
    }
}
