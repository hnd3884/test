package org.apache.catalina.webresources;

import org.apache.juli.logging.LogFactory;
import java.util.jar.JarFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import org.apache.tomcat.util.buf.UriUtil;
import java.util.jar.JarEntry;
import org.apache.juli.logging.Log;

public class JarWarResource extends AbstractArchiveResource
{
    private static final Log log;
    private final String archivePath;
    
    public JarWarResource(final AbstractArchiveResourceSet archiveResourceSet, final String webAppPath, final String baseUrl, final JarEntry jarEntry, final String archivePath) {
        super(archiveResourceSet, webAppPath, "jar:war:" + baseUrl + UriUtil.getWarSeparator() + archivePath + "!/", jarEntry, "war:" + baseUrl + UriUtil.getWarSeparator() + archivePath);
        this.archivePath = archivePath;
    }
    
    @Override
    protected JarInputStreamWrapper getJarInputStreamWrapper() {
        JarFile warFile = null;
        JarInputStream jarIs = null;
        JarEntry entry = null;
        try {
            warFile = this.getArchiveResourceSet().openJarFile();
            final JarEntry jarFileInWar = warFile.getJarEntry(this.archivePath);
            final InputStream isInWar = warFile.getInputStream(jarFileInWar);
            for (jarIs = new JarInputStream(isInWar), entry = jarIs.getNextJarEntry(); entry != null && !entry.getName().equals(this.getResource().getName()); entry = jarIs.getNextJarEntry()) {}
            if (entry == null) {
                return null;
            }
            return new JarInputStreamWrapper(entry, jarIs);
        }
        catch (final IOException e) {
            if (JarWarResource.log.isDebugEnabled()) {
                JarWarResource.log.debug((Object)JarWarResource.sm.getString("jarResource.getInputStreamFail", new Object[] { this.getResource().getName(), this.getBaseUrl() }), (Throwable)e);
            }
            entry = null;
            return null;
        }
        finally {
            if (entry == null) {
                if (jarIs != null) {
                    try {
                        jarIs.close();
                    }
                    catch (final IOException ex) {}
                }
                if (warFile != null) {
                    this.getArchiveResourceSet().closeJarFile();
                }
            }
        }
    }
    
    @Override
    protected Log getLog() {
        return JarWarResource.log;
    }
    
    static {
        log = LogFactory.getLog((Class)JarWarResource.class);
    }
}
