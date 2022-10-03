package net.oauth;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class OAuthConsumer implements Serializable
{
    private static final long serialVersionUID = -2258581186977818580L;
    public final String callbackURL;
    public final String consumerKey;
    public final String consumerSecret;
    public final OAuthServiceProvider serviceProvider;
    private final Map<String, Object> properties;
    public static final String ACCEPT_ENCODING = "HTTP.header.Accept-Encoding";
    public static final String ACCESSOR_SECRET = "oauth_accessor_secret";
    
    public OAuthConsumer(final String callbackURL, final String consumerKey, final String consumerSecret, final OAuthServiceProvider serviceProvider) {
        this.properties = new HashMap<String, Object>();
        this.callbackURL = callbackURL;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.serviceProvider = serviceProvider;
    }
    
    public Object getProperty(final String name) {
        return this.properties.get(name);
    }
    
    public void setProperty(final String name, final Object value) {
        this.properties.put(name, value);
    }
}
