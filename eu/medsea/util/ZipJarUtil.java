package eu.medsea.util;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.ArrayList;
import java.io.File;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.Collection;
import java.io.IOException;
import java.net.JarURLConnection;
import java.io.InputStream;
import java.net.URL;

public class ZipJarUtil
{
    public static InputStream getInputStreamForURL(final URL url) throws IOException {
        final JarURLConnection conn = (JarURLConnection)url.openConnection();
        return conn.getInputStream();
    }
    
    public static Collection getEntries(final String fileName) throws ZipException, IOException {
        return getEntries(new ZipFile(fileName));
    }
    
    public static Collection getEntries(final File file) throws ZipException, IOException {
        return getEntries(new ZipFile(file));
    }
    
    public static Collection getEntries(final URL url) throws ZipException, IOException {
        final JarURLConnection conn = (JarURLConnection)url.openConnection();
        return getEntries(conn.getJarFile());
    }
    
    public static Collection getEntries(final ZipFile zipFile) throws ZipException, IOException {
        final Collection entries = new ArrayList();
        final Enumeration e = zipFile.entries();
        while (e.hasMoreElements()) {
            final ZipEntry ze = e.nextElement();
            if (!ze.isDirectory()) {
                entries.add(ze.getName());
            }
        }
        return entries;
    }
    
    public static void main(final String[] args) throws Exception {
        System.out.println(getEntries("src/test/resources/a.zip"));
        System.out.println(getEntries(new File("src/test/resources/a.zip")));
        System.out.println(getEntries(new URL("jar:file:src/test/resources/a.zip!/")));
        System.out.println(getEntries(new URL("jar:file:src/test/resources/a.zip!/resources/eu/medsea/mimeutil/magic.mime")));
    }
}
