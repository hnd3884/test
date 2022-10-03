package sun.net.www.protocol.jar;

import java.io.FilterInputStream;
import java.util.List;
import java.util.Map;
import java.io.BufferedInputStream;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.net.URLConnection;
import java.security.Permission;
import java.net.URL;

public class JarURLConnection extends java.net.JarURLConnection
{
    private static final boolean debug = false;
    private static final JarFileFactory factory;
    private URL jarFileURL;
    private Permission permission;
    private URLConnection jarFileURLConnection;
    private String entryName;
    private JarEntry jarEntry;
    private JarFile jarFile;
    private String contentType;
    
    public JarURLConnection(final URL url, final Handler handler) throws MalformedURLException, IOException {
        super(url);
        this.jarFileURL = this.getJarFileURL();
        this.jarFileURLConnection = this.jarFileURL.openConnection();
        this.entryName = this.getEntryName();
    }
    
    @Override
    public JarFile getJarFile() throws IOException {
        this.connect();
        return this.jarFile;
    }
    
    @Override
    public JarEntry getJarEntry() throws IOException {
        this.connect();
        return this.jarEntry;
    }
    
    @Override
    public Permission getPermission() throws IOException {
        return this.jarFileURLConnection.getPermission();
    }
    
    @Override
    public void connect() throws IOException {
        if (!this.connected) {
            this.jarFile = JarURLConnection.factory.get(this.getJarFileURL(), this.getUseCaches());
            if (this.getUseCaches()) {
                (this.jarFileURLConnection = JarURLConnection.factory.getConnection(this.jarFile)).setUseCaches(this.jarFileURLConnection.getUseCaches());
            }
            if (this.entryName != null) {
                this.jarEntry = (JarEntry)this.jarFile.getEntry(this.entryName);
                if (this.jarEntry == null) {
                    try {
                        if (!this.getUseCaches()) {
                            this.jarFile.close();
                        }
                    }
                    catch (final Exception ex) {}
                    throw new FileNotFoundException("JAR entry " + this.entryName + " not found in " + this.jarFile.getName());
                }
            }
            this.connected = true;
        }
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        this.connect();
        if (this.entryName == null) {
            throw new IOException("no entry name specified");
        }
        if (this.jarEntry == null) {
            throw new FileNotFoundException("JAR entry " + this.entryName + " not found in " + this.jarFile.getName());
        }
        return new JarURLInputStream(this.jarFile.getInputStream(this.jarEntry));
    }
    
    @Override
    public int getContentLength() {
        final long contentLengthLong = this.getContentLengthLong();
        if (contentLengthLong > 2147483647L) {
            return -1;
        }
        return (int)contentLengthLong;
    }
    
    @Override
    public long getContentLengthLong() {
        long n = -1L;
        try {
            this.connect();
            if (this.jarEntry == null) {
                n = this.jarFileURLConnection.getContentLengthLong();
            }
            else {
                n = this.getJarEntry().getSize();
            }
        }
        catch (final IOException ex) {}
        return n;
    }
    
    @Override
    public Object getContent() throws IOException {
        this.connect();
        Object o;
        if (this.entryName == null) {
            o = this.jarFile;
        }
        else {
            o = super.getContent();
        }
        return o;
    }
    
    @Override
    public String getContentType() {
        if (this.contentType == null) {
            if (this.entryName == null) {
                this.contentType = "x-java/jar";
            }
            else {
                try {
                    this.connect();
                    final InputStream inputStream = this.jarFile.getInputStream(this.jarEntry);
                    this.contentType = URLConnection.guessContentTypeFromStream(new BufferedInputStream(inputStream));
                    inputStream.close();
                }
                catch (final IOException ex) {}
            }
            if (this.contentType == null) {
                this.contentType = URLConnection.guessContentTypeFromName(this.entryName);
            }
            if (this.contentType == null) {
                this.contentType = "content/unknown";
            }
        }
        return this.contentType;
    }
    
    @Override
    public String getHeaderField(final String s) {
        return this.jarFileURLConnection.getHeaderField(s);
    }
    
    @Override
    public void setRequestProperty(final String s, final String s2) {
        this.jarFileURLConnection.setRequestProperty(s, s2);
    }
    
    @Override
    public String getRequestProperty(final String s) {
        return this.jarFileURLConnection.getRequestProperty(s);
    }
    
    @Override
    public void addRequestProperty(final String s, final String s2) {
        this.jarFileURLConnection.addRequestProperty(s, s2);
    }
    
    @Override
    public Map<String, List<String>> getRequestProperties() {
        return this.jarFileURLConnection.getRequestProperties();
    }
    
    @Override
    public void setAllowUserInteraction(final boolean allowUserInteraction) {
        this.jarFileURLConnection.setAllowUserInteraction(allowUserInteraction);
    }
    
    @Override
    public boolean getAllowUserInteraction() {
        return this.jarFileURLConnection.getAllowUserInteraction();
    }
    
    @Override
    public void setUseCaches(final boolean useCaches) {
        this.jarFileURLConnection.setUseCaches(useCaches);
    }
    
    @Override
    public boolean getUseCaches() {
        return this.jarFileURLConnection.getUseCaches();
    }
    
    @Override
    public void setIfModifiedSince(final long ifModifiedSince) {
        this.jarFileURLConnection.setIfModifiedSince(ifModifiedSince);
    }
    
    @Override
    public void setDefaultUseCaches(final boolean defaultUseCaches) {
        this.jarFileURLConnection.setDefaultUseCaches(defaultUseCaches);
    }
    
    @Override
    public boolean getDefaultUseCaches() {
        return this.jarFileURLConnection.getDefaultUseCaches();
    }
    
    static {
        factory = JarFileFactory.getInstance();
    }
    
    class JarURLInputStream extends FilterInputStream
    {
        JarURLInputStream(final InputStream inputStream) {
            super(inputStream);
        }
        
        @Override
        public void close() throws IOException {
            try {
                super.close();
            }
            finally {
                if (!JarURLConnection.this.getUseCaches()) {
                    JarURLConnection.this.jarFile.close();
                }
            }
        }
    }
}
