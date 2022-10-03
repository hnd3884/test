package org.bouncycastle.est;

import org.bouncycastle.util.Arrays;
import java.net.URL;

public class ESTRequestBuilder
{
    private final String method;
    private URL url;
    private HttpUtil.Headers headers;
    ESTHijacker hijacker;
    ESTSourceConnectionListener listener;
    ESTClient client;
    private byte[] data;
    
    public ESTRequestBuilder(final ESTRequest estRequest) {
        this.method = estRequest.method;
        this.url = estRequest.url;
        this.listener = estRequest.listener;
        this.data = estRequest.data;
        this.hijacker = estRequest.hijacker;
        this.headers = (HttpUtil.Headers)estRequest.headers.clone();
        this.client = estRequest.getClient();
    }
    
    public ESTRequestBuilder(final String method, final URL url) {
        this.method = method;
        this.url = url;
        this.headers = new HttpUtil.Headers();
    }
    
    public ESTRequestBuilder withConnectionListener(final ESTSourceConnectionListener listener) {
        this.listener = listener;
        return this;
    }
    
    public ESTRequestBuilder withHijacker(final ESTHijacker hijacker) {
        this.hijacker = hijacker;
        return this;
    }
    
    public ESTRequestBuilder withURL(final URL url) {
        this.url = url;
        return this;
    }
    
    public ESTRequestBuilder withData(final byte[] array) {
        this.data = Arrays.clone(array);
        return this;
    }
    
    public ESTRequestBuilder addHeader(final String s, final String s2) {
        this.headers.add(s, s2);
        return this;
    }
    
    public ESTRequestBuilder setHeader(final String s, final String s2) {
        this.headers.set(s, s2);
        return this;
    }
    
    public ESTRequestBuilder withClient(final ESTClient client) {
        this.client = client;
        return this;
    }
    
    public ESTRequest build() {
        return new ESTRequest(this.method, this.url, this.data, this.hijacker, this.listener, this.headers, this.client);
    }
}
