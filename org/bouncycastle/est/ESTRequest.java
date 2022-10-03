package org.bouncycastle.est;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.net.URL;

public class ESTRequest
{
    final String method;
    final URL url;
    HttpUtil.Headers headers;
    final byte[] data;
    final ESTHijacker hijacker;
    final ESTClient estClient;
    final ESTSourceConnectionListener listener;
    
    ESTRequest(final String method, final URL url, final byte[] data, final ESTHijacker hijacker, final ESTSourceConnectionListener listener, final HttpUtil.Headers headers, final ESTClient estClient) {
        this.headers = new HttpUtil.Headers();
        this.method = method;
        this.url = url;
        this.data = data;
        this.hijacker = hijacker;
        this.listener = listener;
        this.headers = headers;
        this.estClient = estClient;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public URL getURL() {
        return this.url;
    }
    
    public Map<String, String[]> getHeaders() {
        return (Map)this.headers.clone();
    }
    
    public ESTHijacker getHijacker() {
        return this.hijacker;
    }
    
    public ESTClient getClient() {
        return this.estClient;
    }
    
    public ESTSourceConnectionListener getListener() {
        return this.listener;
    }
    
    public void writeData(final OutputStream outputStream) throws IOException {
        if (this.data != null) {
            outputStream.write(this.data);
        }
    }
}
