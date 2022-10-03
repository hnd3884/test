package sun.net.www.protocol.jar;

import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.io.InputStream;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import sun.net.www.ParseUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.jar.JarFile;

public class URLJarFile extends JarFile
{
    private static URLJarFileCallBack callback;
    private URLJarFileCloseController closeController;
    private static int BUF_SIZE;
    private Manifest superMan;
    private Attributes superAttr;
    private Map<String, Attributes> superEntries;
    
    static JarFile getJarFile(final URL url) throws IOException {
        return getJarFile(url, null);
    }
    
    static JarFile getJarFile(final URL url, final URLJarFileCloseController urlJarFileCloseController) throws IOException {
        if (isFileURL(url)) {
            return new URLJarFile(url, urlJarFileCloseController);
        }
        return retrieve(url, urlJarFileCloseController);
    }
    
    public URLJarFile(final File file) throws IOException {
        this(file, (URLJarFileCloseController)null);
    }
    
    public URLJarFile(final File file, final URLJarFileCloseController closeController) throws IOException {
        super(file, true, 5);
        this.closeController = null;
        this.closeController = closeController;
    }
    
    private URLJarFile(final URL url, final URLJarFileCloseController closeController) throws IOException {
        super(ParseUtil.decode(url.getFile()));
        this.closeController = null;
        this.closeController = closeController;
    }
    
    private static boolean isFileURL(final URL url) {
        if (url.getProtocol().equalsIgnoreCase("file")) {
            final String host = url.getHost();
            if (host == null || host.equals("") || host.equals("~") || host.equalsIgnoreCase("localhost")) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void finalize() throws IOException {
        this.close();
    }
    
    @Override
    public ZipEntry getEntry(final String s) {
        final ZipEntry entry = super.getEntry(s);
        if (entry == null) {
            return null;
        }
        if (entry instanceof JarEntry) {
            return new URLJarFileEntry((JarEntry)entry);
        }
        throw new InternalError(this.getClass() + " returned unexpected entry type " + ((JarEntry)entry).getClass());
    }
    
    @Override
    public Manifest getManifest() throws IOException {
        if (!this.isSuperMan()) {
            return null;
        }
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().putAll((Map<?, ?>)this.superAttr.clone());
        if (this.superEntries != null) {
            final Map<String, Attributes> entries = manifest.getEntries();
            for (final String s : this.superEntries.keySet()) {
                entries.put(s, (Attributes)this.superEntries.get(s).clone());
            }
        }
        return manifest;
    }
    
    @Override
    public void close() throws IOException {
        if (this.closeController != null) {
            this.closeController.close(this);
        }
        super.close();
    }
    
    private synchronized boolean isSuperMan() throws IOException {
        if (this.superMan == null) {
            this.superMan = super.getManifest();
        }
        if (this.superMan != null) {
            this.superAttr = this.superMan.getMainAttributes();
            this.superEntries = this.superMan.getEntries();
            return true;
        }
        return false;
    }
    
    private static JarFile retrieve(final URL url) throws IOException {
        return retrieve(url, null);
    }
    
    private static JarFile retrieve(final URL url, final URLJarFileCloseController urlJarFileCloseController) throws IOException {
        if (URLJarFile.callback != null) {
            return URLJarFile.callback.retrieve(url);
        }
        JarFile jarFile = null;
        try (final InputStream inputStream = url.openConnection().getInputStream()) {
            jarFile = AccessController.doPrivileged((PrivilegedExceptionAction<JarFile>)new PrivilegedExceptionAction<JarFile>() {
                @Override
                public JarFile run() throws IOException {
                    final Path tempFile = Files.createTempFile("jar_cache", null, (FileAttribute<?>[])new FileAttribute[0]);
                    try {
                        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                        final URLJarFile urlJarFile = new URLJarFile(tempFile.toFile(), urlJarFileCloseController);
                        tempFile.toFile().deleteOnExit();
                        return urlJarFile;
                    }
                    catch (final Throwable t) {
                        try {
                            Files.delete(tempFile);
                        }
                        catch (final IOException ex) {
                            t.addSuppressed(ex);
                        }
                        throw t;
                    }
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
        return jarFile;
    }
    
    public static void setCallBack(final URLJarFileCallBack callback) {
        URLJarFile.callback = callback;
    }
    
    static {
        URLJarFile.callback = null;
        URLJarFile.BUF_SIZE = 2048;
    }
    
    private class URLJarFileEntry extends JarEntry
    {
        private JarEntry je;
        
        URLJarFileEntry(final JarEntry je) {
            super(je);
            this.je = je;
        }
        
        @Override
        public Attributes getAttributes() throws IOException {
            if (URLJarFile.this.isSuperMan()) {
                final Map access$100 = URLJarFile.this.superEntries;
                if (access$100 != null) {
                    final Attributes attributes = access$100.get(this.getName());
                    if (attributes != null) {
                        return (Attributes)attributes.clone();
                    }
                }
            }
            return null;
        }
        
        @Override
        public Certificate[] getCertificates() {
            final Certificate[] certificates = this.je.getCertificates();
            return (Certificate[])((certificates == null) ? null : ((Certificate[])certificates.clone()));
        }
        
        @Override
        public CodeSigner[] getCodeSigners() {
            final CodeSigner[] codeSigners = this.je.getCodeSigners();
            return (CodeSigner[])((codeSigners == null) ? null : ((CodeSigner[])codeSigners.clone()));
        }
    }
    
    public interface URLJarFileCloseController
    {
        void close(final JarFile p0);
    }
}
