package net.oauth;

import java.net.URISyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class OAuthAccessor implements Cloneable, Serializable
{
    private static final long serialVersionUID = 5590788443138352999L;
    public final OAuthConsumer consumer;
    public String requestToken;
    public String accessToken;
    public String tokenSecret;
    private final Map<String, Object> properties;
    
    public OAuthAccessor(final OAuthConsumer consumer) {
        this.properties = new HashMap<String, Object>();
        this.consumer = consumer;
        this.requestToken = null;
        this.accessToken = null;
        this.tokenSecret = null;
    }
    
    public OAuthAccessor clone() {
        try {
            return (OAuthAccessor)super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Object getProperty(final String name) {
        return this.properties.get(name);
    }
    
    public void setProperty(final String name, final Object value) {
        this.properties.put(name, value);
    }
    
    public OAuthMessage newRequestMessage(String method, final String url, final Collection<? extends Map.Entry> parameters, final InputStream body) throws OAuthException, IOException, URISyntaxException {
        if (method == null) {
            method = (String)this.getProperty("httpMethod");
            if (method == null) {
                method = (String)this.consumer.getProperty("httpMethod");
                if (method == null) {
                    method = "GET";
                }
            }
        }
        final OAuthMessage message = new OAuthMessage(method, url, parameters, body);
        message.addRequiredParameters(this);
        return message;
    }
    
    public OAuthMessage newRequestMessage(final String method, final String url, final Collection<? extends Map.Entry> parameters) throws OAuthException, IOException, URISyntaxException {
        return this.newRequestMessage(method, url, parameters, null);
    }
}
