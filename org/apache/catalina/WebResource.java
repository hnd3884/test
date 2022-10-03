package org.apache.catalina;

import java.util.jar.Manifest;
import java.security.cert.Certificate;
import java.net.URL;
import java.io.InputStream;

public interface WebResource
{
    long getLastModified();
    
    String getLastModifiedHttp();
    
    boolean exists();
    
    boolean isVirtual();
    
    boolean isDirectory();
    
    boolean isFile();
    
    boolean delete();
    
    String getName();
    
    long getContentLength();
    
    String getCanonicalPath();
    
    boolean canRead();
    
    String getWebappPath();
    
    String getETag();
    
    void setMimeType(final String p0);
    
    String getMimeType();
    
    InputStream getInputStream();
    
    byte[] getContent();
    
    long getCreation();
    
    URL getURL();
    
    URL getCodeBase();
    
    WebResourceRoot getWebResourceRoot();
    
    Certificate[] getCertificates();
    
    Manifest getManifest();
}
