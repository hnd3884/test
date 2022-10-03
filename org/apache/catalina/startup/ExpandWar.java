package org.apache.catalina.startup;

import org.apache.juli.logging.LogFactory;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.nio.file.Path;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.zip.ZipException;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.io.IOException;
import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import org.apache.catalina.Host;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class ExpandWar
{
    private static final Log log;
    protected static final StringManager sm;
    
    public static String expand(final Host host, final URL war, final String pathname) throws IOException {
        final JarURLConnection juc = (JarURLConnection)war.openConnection();
        juc.setUseCaches(false);
        final URL jarFileUrl = juc.getJarFileURL();
        final URLConnection jfuc = jarFileUrl.openConnection();
        boolean success = false;
        final File docBase = new File(host.getAppBaseFile(), pathname);
        final File warTracker = new File(host.getAppBaseFile(), pathname + "/META-INF/war-tracker");
        long warLastModified = -1L;
        try (final InputStream is = jfuc.getInputStream()) {
            warLastModified = jfuc.getLastModified();
        }
        if (docBase.exists()) {
            if (!warTracker.exists() || warTracker.lastModified() == warLastModified) {
                success = true;
                return docBase.getAbsolutePath();
            }
            ExpandWar.log.info((Object)ExpandWar.sm.getString("expandWar.deleteOld", new Object[] { docBase }));
            if (!delete(docBase)) {
                throw new IOException(ExpandWar.sm.getString("expandWar.deleteFailed", new Object[] { docBase }));
            }
        }
        if (!docBase.mkdir() && !docBase.isDirectory()) {
            throw new IOException(ExpandWar.sm.getString("expandWar.createFailed", new Object[] { docBase }));
        }
        final Path canonicalDocBasePath = docBase.getCanonicalFile().toPath();
        final File warTrackerParent = warTracker.getParentFile();
        if (!warTrackerParent.isDirectory() && !warTrackerParent.mkdirs()) {
            throw new IOException(ExpandWar.sm.getString("expandWar.createFailed", new Object[] { warTrackerParent.getAbsolutePath() }));
        }
        try (final JarFile jarFile = juc.getJarFile()) {
            final Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                final JarEntry jarEntry = jarEntries.nextElement();
                final String name = jarEntry.getName();
                final File expandedFile = new File(docBase, name);
                if (!expandedFile.getCanonicalFile().toPath().startsWith(canonicalDocBasePath)) {
                    throw new IllegalArgumentException(ExpandWar.sm.getString("expandWar.illegalPath", new Object[] { war, name, expandedFile.getCanonicalPath(), canonicalDocBasePath }));
                }
                final int last = name.lastIndexOf(47);
                if (last >= 0) {
                    final File parent = new File(docBase, name.substring(0, last));
                    if (!parent.mkdirs() && !parent.isDirectory()) {
                        throw new IOException(ExpandWar.sm.getString("expandWar.createFailed", new Object[] { parent }));
                    }
                }
                if (name.endsWith("/")) {
                    continue;
                }
                try (final InputStream input = jarFile.getInputStream(jarEntry)) {
                    if (null == input) {
                        throw new ZipException(ExpandWar.sm.getString("expandWar.missingJarEntry", new Object[] { jarEntry.getName() }));
                    }
                    expand(input, expandedFile);
                    final long lastModified = jarEntry.getTime();
                    if (lastModified != -1L && lastModified != 0L && !expandedFile.setLastModified(lastModified)) {
                        throw new IOException(ExpandWar.sm.getString("expandWar.lastModifiedFailed", new Object[] { expandedFile }));
                    }
                }
            }
            if (!warTracker.createNewFile()) {
                throw new IOException(ExpandWar.sm.getString("expandWar.createFileFailed", new Object[] { warTracker }));
            }
            if (!warTracker.setLastModified(warLastModified)) {
                throw new IOException(ExpandWar.sm.getString("expandWar.lastModifiedFailed", new Object[] { warTracker }));
            }
            success = true;
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            if (!success) {
                deleteDir(docBase);
            }
        }
        return docBase.getAbsolutePath();
    }
    
    public static void validate(final Host host, final URL war, final String pathname) throws IOException {
        final File docBase = new File(host.getAppBaseFile(), pathname);
        final Path canonicalDocBasePath = docBase.getCanonicalFile().toPath();
        final JarURLConnection juc = (JarURLConnection)war.openConnection();
        juc.setUseCaches(false);
        try (final JarFile jarFile = juc.getJarFile()) {
            final Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                final JarEntry jarEntry = jarEntries.nextElement();
                final String name = jarEntry.getName();
                final File expandedFile = new File(docBase, name);
                if (!expandedFile.getCanonicalFile().toPath().startsWith(canonicalDocBasePath)) {
                    throw new IllegalArgumentException(ExpandWar.sm.getString("expandWar.illegalPath", new Object[] { war, name, expandedFile.getCanonicalPath(), canonicalDocBasePath }));
                }
            }
        }
        catch (final IOException e) {
            throw e;
        }
    }
    
    public static boolean copy(final File src, final File dest) {
        boolean result = true;
        String[] files = null;
        if (src.isDirectory()) {
            files = src.list();
            result = dest.mkdir();
        }
        else {
            files = new String[] { "" };
        }
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length && result; ++i) {
            final File fileSrc = new File(src, files[i]);
            final File fileDest = new File(dest, files[i]);
            if (fileSrc.isDirectory()) {
                result = copy(fileSrc, fileDest);
            }
            else {
                try (final FileChannel ic = new FileInputStream(fileSrc).getChannel();
                     final FileChannel oc = new FileOutputStream(fileDest).getChannel()) {
                    ic.transferTo(0L, ic.size(), oc);
                }
                catch (final IOException e) {
                    ExpandWar.log.error((Object)ExpandWar.sm.getString("expandWar.copy", new Object[] { fileSrc, fileDest }), (Throwable)e);
                    result = false;
                }
            }
        }
        return result;
    }
    
    public static boolean delete(final File dir) {
        return delete(dir, true);
    }
    
    public static boolean delete(final File dir, final boolean logFailure) {
        boolean result;
        if (dir.isDirectory()) {
            result = deleteDir(dir, logFailure);
        }
        else {
            result = (!dir.exists() || dir.delete());
        }
        if (logFailure && !result) {
            ExpandWar.log.error((Object)ExpandWar.sm.getString("expandWar.deleteFailed", new Object[] { dir.getAbsolutePath() }));
        }
        return result;
    }
    
    public static boolean deleteDir(final File dir) {
        return deleteDir(dir, true);
    }
    
    public static boolean deleteDir(final File dir, final boolean logFailure) {
        String[] files = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (final String s : files) {
            final File file = new File(dir, s);
            if (file.isDirectory()) {
                deleteDir(file, logFailure);
            }
            else {
                file.delete();
            }
        }
        final boolean result = !dir.exists() || dir.delete();
        if (logFailure && !result) {
            ExpandWar.log.error((Object)ExpandWar.sm.getString("expandWar.deleteFailed", new Object[] { dir.getAbsolutePath() }));
        }
        return result;
    }
    
    private static void expand(final InputStream input, final File file) throws IOException {
        try (final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file))) {
            final byte[] buffer = new byte[2048];
            while (true) {
                final int n = input.read(buffer);
                if (n <= 0) {
                    break;
                }
                output.write(buffer, 0, n);
            }
        }
    }
    
    static {
        log = LogFactory.getLog((Class)ExpandWar.class);
        sm = StringManager.getManager("org.apache.catalina.startup");
    }
}
