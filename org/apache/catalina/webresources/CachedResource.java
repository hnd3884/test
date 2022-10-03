package org.apache.catalina.webresources;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.JarURLConnection;
import java.security.Permission;
import java.io.IOException;
import java.net.URLConnection;
import org.apache.juli.logging.LogFactory;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Arrays;
import java.text.Collator;
import java.util.Locale;
import org.apache.catalina.WebResourceRoot;
import java.util.jar.Manifest;
import java.security.cert.Certificate;
import java.net.MalformedURLException;
import java.net.URLStreamHandler;
import java.net.URL;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.WebResource;

public class CachedResource implements WebResource
{
    private static final Log log;
    private static final StringManager sm;
    private static final long CACHE_ENTRY_SIZE = 500L;
    private final Cache cache;
    private final StandardRoot root;
    private final String webAppPath;
    private final long ttl;
    private final int objectMaxSizeBytes;
    private final boolean usesClassLoaderResources;
    private volatile WebResource webResource;
    private volatile WebResource[] webResources;
    private volatile long nextCheck;
    private volatile Long cachedLastModified;
    private volatile String cachedLastModifiedHttp;
    private volatile byte[] cachedContent;
    private volatile Boolean cachedIsFile;
    private volatile Boolean cachedIsDirectory;
    private volatile Boolean cachedExists;
    private volatile Boolean cachedIsVirtual;
    private volatile Long cachedContentLength;
    
    public CachedResource(final Cache cache, final StandardRoot root, final String path, final long ttl, final int objectMaxSizeBytes, final boolean usesClassLoaderResources) {
        this.cachedLastModified = null;
        this.cachedLastModifiedHttp = null;
        this.cachedContent = null;
        this.cachedIsFile = null;
        this.cachedIsDirectory = null;
        this.cachedExists = null;
        this.cachedIsVirtual = null;
        this.cachedContentLength = null;
        this.cache = cache;
        this.root = root;
        this.webAppPath = path;
        this.ttl = ttl;
        this.objectMaxSizeBytes = objectMaxSizeBytes;
        this.usesClassLoaderResources = usesClassLoaderResources;
    }
    
    protected boolean validateResource(final boolean useClassLoaderResources) {
        if (this.usesClassLoaderResources != useClassLoaderResources) {
            return false;
        }
        final long now = System.currentTimeMillis();
        if (this.webResource == null) {
            synchronized (this) {
                if (this.webResource == null) {
                    this.webResource = this.root.getResourceInternal(this.webAppPath, useClassLoaderResources);
                    this.getLastModified();
                    this.getContentLength();
                    this.nextCheck = this.ttl + now;
                    if (this.webResource instanceof EmptyResource) {
                        this.cachedExists = Boolean.FALSE;
                    }
                    else {
                        this.cachedExists = Boolean.TRUE;
                    }
                    return true;
                }
            }
        }
        if (now < this.nextCheck) {
            return true;
        }
        if (!this.root.isPackedWarFile()) {
            final WebResource webResourceInternal = this.root.getResourceInternal(this.webAppPath, useClassLoaderResources);
            if (!this.webResource.exists() && webResourceInternal.exists()) {
                return false;
            }
            if (this.webResource.getLastModified() != this.getLastModified() || this.webResource.getContentLength() != this.getContentLength()) {
                return false;
            }
            if (this.webResource.getLastModified() != webResourceInternal.getLastModified() || this.webResource.getContentLength() != webResourceInternal.getContentLength()) {
                return false;
            }
        }
        this.nextCheck = this.ttl + now;
        return true;
    }
    
    protected boolean validateResources(final boolean useClassLoaderResources) {
        final long now = System.currentTimeMillis();
        if (this.webResources == null) {
            synchronized (this) {
                if (this.webResources == null) {
                    this.webResources = this.root.getResourcesInternal(this.webAppPath, useClassLoaderResources);
                    this.nextCheck = this.ttl + now;
                    return true;
                }
            }
        }
        if (now < this.nextCheck) {
            return true;
        }
        if (this.root.isPackedWarFile()) {
            this.nextCheck = this.ttl + now;
            return true;
        }
        return false;
    }
    
    protected long getNextCheck() {
        return this.nextCheck;
    }
    
    @Override
    public long getLastModified() {
        Long cachedLastModified = this.cachedLastModified;
        if (cachedLastModified == null) {
            cachedLastModified = this.webResource.getLastModified();
            this.cachedLastModified = cachedLastModified;
        }
        return cachedLastModified;
    }
    
    @Override
    public String getLastModifiedHttp() {
        String cachedLastModifiedHttp = this.cachedLastModifiedHttp;
        if (cachedLastModifiedHttp == null) {
            cachedLastModifiedHttp = this.webResource.getLastModifiedHttp();
            this.cachedLastModifiedHttp = cachedLastModifiedHttp;
        }
        return cachedLastModifiedHttp;
    }
    
    @Override
    public boolean exists() {
        Boolean cachedExists = this.cachedExists;
        if (cachedExists == null) {
            cachedExists = this.webResource.exists();
            this.cachedExists = cachedExists;
        }
        return cachedExists;
    }
    
    @Override
    public boolean isVirtual() {
        Boolean cachedIsVirtual = this.cachedIsVirtual;
        if (cachedIsVirtual == null) {
            cachedIsVirtual = this.webResource.isVirtual();
            this.cachedIsVirtual = cachedIsVirtual;
        }
        return cachedIsVirtual;
    }
    
    @Override
    public boolean isDirectory() {
        Boolean cachedIsDirectory = this.cachedIsDirectory;
        if (cachedIsDirectory == null) {
            cachedIsDirectory = this.webResource.isDirectory();
            this.cachedIsDirectory = cachedIsDirectory;
        }
        return cachedIsDirectory;
    }
    
    @Override
    public boolean isFile() {
        Boolean cachedIsFile = this.cachedIsFile;
        if (cachedIsFile == null) {
            cachedIsFile = this.webResource.isFile();
            this.cachedIsFile = cachedIsFile;
        }
        return cachedIsFile;
    }
    
    @Override
    public boolean delete() {
        final boolean deleteResult = this.webResource.delete();
        if (deleteResult) {
            this.cache.removeCacheEntry(this.webAppPath);
        }
        return deleteResult;
    }
    
    @Override
    public String getName() {
        return this.webResource.getName();
    }
    
    @Override
    public long getContentLength() {
        Long cachedContentLength = this.cachedContentLength;
        if (cachedContentLength == null) {
            long result = 0L;
            if (this.webResource != null) {
                result = this.webResource.getContentLength();
                cachedContentLength = result;
                this.cachedContentLength = cachedContentLength;
            }
            return result;
        }
        return cachedContentLength;
    }
    
    @Override
    public String getCanonicalPath() {
        return this.webResource.getCanonicalPath();
    }
    
    @Override
    public boolean canRead() {
        return this.webResource.canRead();
    }
    
    @Override
    public String getWebappPath() {
        return this.webAppPath;
    }
    
    @Override
    public String getETag() {
        return this.webResource.getETag();
    }
    
    @Override
    public void setMimeType(final String mimeType) {
        this.webResource.setMimeType(mimeType);
    }
    
    @Override
    public String getMimeType() {
        return this.webResource.getMimeType();
    }
    
    @Override
    public InputStream getInputStream() {
        final byte[] content = this.getContent();
        if (content == null) {
            return this.webResource.getInputStream();
        }
        return new ByteArrayInputStream(content);
    }
    
    @Override
    public byte[] getContent() {
        byte[] cachedContent = this.cachedContent;
        if (cachedContent == null) {
            if (this.getContentLength() > this.objectMaxSizeBytes) {
                return null;
            }
            cachedContent = this.webResource.getContent();
            this.cachedContent = cachedContent;
        }
        return cachedContent;
    }
    
    @Override
    public long getCreation() {
        return this.webResource.getCreation();
    }
    
    @Override
    public URL getURL() {
        final URL resourceURL = this.webResource.getURL();
        if (resourceURL == null) {
            return null;
        }
        try {
            final CachedResourceURLStreamHandler handler = new CachedResourceURLStreamHandler(resourceURL, this.root, this.webAppPath, this.usesClassLoaderResources);
            final URL result = new URL(null, resourceURL.toExternalForm(), handler);
            handler.setAssociatedURL(result);
            return result;
        }
        catch (final MalformedURLException e) {
            CachedResource.log.error((Object)CachedResource.sm.getString("cachedResource.invalidURL", new Object[] { resourceURL.toExternalForm() }), (Throwable)e);
            return null;
        }
    }
    
    @Override
    public URL getCodeBase() {
        return this.webResource.getCodeBase();
    }
    
    @Override
    public Certificate[] getCertificates() {
        return this.webResource.getCertificates();
    }
    
    @Override
    public Manifest getManifest() {
        return this.webResource.getManifest();
    }
    
    @Override
    public WebResourceRoot getWebResourceRoot() {
        return this.webResource.getWebResourceRoot();
    }
    
    WebResource getWebResource() {
        return this.webResource;
    }
    
    WebResource[] getWebResources() {
        return this.webResources;
    }
    
    boolean usesClassLoaderResources() {
        return this.usesClassLoaderResources;
    }
    
    long getSize() {
        long result = 500L;
        result += this.getWebappPath().length() * 2;
        if (this.getContentLength() <= this.objectMaxSizeBytes) {
            result += this.getContentLength();
        }
        return result;
    }
    
    private static InputStream buildInputStream(final String[] files) {
        Arrays.sort(files, Collator.getInstance(Locale.getDefault()));
        final StringBuilder result = new StringBuilder();
        for (final String file : files) {
            result.append(file);
            result.append('\n');
        }
        return new ByteArrayInputStream(result.toString().getBytes(Charset.defaultCharset()));
    }
    
    static {
        log = LogFactory.getLog((Class)CachedResource.class);
        sm = StringManager.getManager((Class)CachedResource.class);
    }
    
    private static class CachedResourceURLStreamHandler extends URLStreamHandler
    {
        private final URL resourceURL;
        private final StandardRoot root;
        private final String webAppPath;
        private final boolean usesClassLoaderResources;
        private URL associatedURL;
        
        public CachedResourceURLStreamHandler(final URL resourceURL, final StandardRoot root, final String webAppPath, final boolean usesClassLoaderResources) {
            this.associatedURL = null;
            this.resourceURL = resourceURL;
            this.root = root;
            this.webAppPath = webAppPath;
            this.usesClassLoaderResources = usesClassLoaderResources;
        }
        
        protected void setAssociatedURL(final URL associatedURL) {
            this.associatedURL = associatedURL;
        }
        
        @Override
        protected URLConnection openConnection(final URL u) throws IOException {
            if (this.associatedURL == null || u != this.associatedURL) {
                final URL constructedURL = new URL(u.toExternalForm());
                return constructedURL.openConnection();
            }
            if ("jar".equals(this.associatedURL.getProtocol())) {
                return new CachedResourceJarURLConnection(this.resourceURL, this.root, this.webAppPath, this.usesClassLoaderResources);
            }
            return new CachedResourceURLConnection(this.resourceURL, this.root, this.webAppPath, this.usesClassLoaderResources);
        }
    }
    
    private static class CachedResourceURLConnection extends URLConnection
    {
        private final StandardRoot root;
        private final String webAppPath;
        private final boolean usesClassLoaderResources;
        private final URL resourceURL;
        
        protected CachedResourceURLConnection(final URL resourceURL, final StandardRoot root, final String webAppPath, final boolean usesClassLoaderResources) {
            super(resourceURL);
            this.root = root;
            this.webAppPath = webAppPath;
            this.usesClassLoaderResources = usesClassLoaderResources;
            this.resourceURL = resourceURL;
        }
        
        @Override
        public void connect() throws IOException {
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            final WebResource resource = this.getResource();
            if (resource.isDirectory()) {
                return buildInputStream(resource.getWebResourceRoot().list(this.webAppPath));
            }
            return this.getResource().getInputStream();
        }
        
        @Override
        public Permission getPermission() throws IOException {
            return this.resourceURL.openConnection().getPermission();
        }
        
        @Override
        public long getLastModified() {
            return this.getResource().getLastModified();
        }
        
        @Override
        public long getContentLengthLong() {
            return this.getResource().getContentLength();
        }
        
        private WebResource getResource() {
            return this.root.getResource(this.webAppPath, false, this.usesClassLoaderResources);
        }
    }
    
    private static class CachedResourceJarURLConnection extends JarURLConnection
    {
        private final StandardRoot root;
        private final String webAppPath;
        private final boolean usesClassLoaderResources;
        private final URL resourceURL;
        
        protected CachedResourceJarURLConnection(final URL resourceURL, final StandardRoot root, final String webAppPath, final boolean usesClassLoaderResources) throws IOException {
            super(resourceURL);
            this.root = root;
            this.webAppPath = webAppPath;
            this.usesClassLoaderResources = usesClassLoaderResources;
            this.resourceURL = resourceURL;
        }
        
        @Override
        public void connect() throws IOException {
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            final WebResource resource = this.getResource();
            if (resource.isDirectory()) {
                return buildInputStream(resource.getWebResourceRoot().list(this.webAppPath));
            }
            return this.getResource().getInputStream();
        }
        
        @Override
        public Permission getPermission() throws IOException {
            return this.resourceURL.openConnection().getPermission();
        }
        
        @Override
        public long getLastModified() {
            return this.getResource().getLastModified();
        }
        
        @Override
        public long getContentLengthLong() {
            return this.getResource().getContentLength();
        }
        
        private WebResource getResource() {
            return this.root.getResource(this.webAppPath, false, this.usesClassLoaderResources);
        }
        
        @Override
        public JarFile getJarFile() throws IOException {
            return ((JarURLConnection)this.resourceURL.openConnection()).getJarFile();
        }
        
        @Override
        public JarEntry getJarEntry() throws IOException {
            if (this.getEntryName() == null) {
                return null;
            }
            return super.getJarEntry();
        }
    }
}
