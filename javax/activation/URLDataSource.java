package javax.activation;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;

public class URLDataSource implements DataSource
{
    private URL url;
    private URLConnection url_conn;
    
    public URLDataSource(final URL url) {
        this.url = null;
        this.url_conn = null;
        this.url = url;
    }
    
    public String getContentType() {
        String contentType = null;
        try {
            if (this.url_conn == null) {
                this.url_conn = this.url.openConnection();
            }
        }
        catch (final IOException ex) {}
        if (this.url_conn != null) {
            contentType = this.url_conn.getContentType();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }
    
    public InputStream getInputStream() throws IOException {
        return this.url.openStream();
    }
    
    public String getName() {
        return this.url.getFile();
    }
    
    public OutputStream getOutputStream() throws IOException {
        this.url_conn = this.url.openConnection();
        if (this.url_conn != null) {
            this.url_conn.setDoOutput(true);
            return this.url_conn.getOutputStream();
        }
        return null;
    }
    
    public URL getURL() {
        return this.url;
    }
}
