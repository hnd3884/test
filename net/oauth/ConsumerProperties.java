package net.oauth;

import java.util.Iterator;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public class ConsumerProperties
{
    private final Properties consumerProperties;
    private final Map<String, OAuthConsumer> pool;
    
    public static URL getResource(final String name, final ClassLoader loader) throws IOException {
        final URL resource = loader.getResource(name);
        if (resource == null) {
            throw new IOException("resource not found: " + name);
        }
        return resource;
    }
    
    public static Properties getProperties(final URL source) throws IOException {
        final InputStream input = source.openStream();
        try {
            final Properties p = new Properties();
            p.load(input);
            return p;
        }
        finally {
            input.close();
        }
    }
    
    public ConsumerProperties(final String resourceName, final ClassLoader loader) throws IOException {
        this(getProperties(getResource(resourceName, loader)));
    }
    
    public ConsumerProperties(final Properties consumerProperties) {
        this.pool = new HashMap<String, OAuthConsumer>();
        this.consumerProperties = consumerProperties;
    }
    
    public OAuthConsumer getConsumer(final String name) throws MalformedURLException {
        OAuthConsumer consumer;
        synchronized (this.pool) {
            consumer = this.pool.get(name);
        }
        if (consumer == null) {
            consumer = this.newConsumer(name);
        }
        synchronized (this.pool) {
            final OAuthConsumer first = this.pool.get(name);
            if (first == null) {
                this.pool.put(name, consumer);
            }
            else {
                consumer = first;
            }
        }
        return consumer;
    }
    
    protected OAuthConsumer newConsumer(final String name) throws MalformedURLException {
        final String base = this.consumerProperties.getProperty(name + ".serviceProvider.baseURL");
        final URL baseURL = (base == null) ? null : new URL(base);
        final OAuthServiceProvider serviceProvider = new OAuthServiceProvider(this.getURL(baseURL, name + ".serviceProvider.requestTokenURL"), this.getURL(baseURL, name + ".serviceProvider.userAuthorizationURL"), this.getURL(baseURL, name + ".serviceProvider.accessTokenURL"));
        final OAuthConsumer consumer = new OAuthConsumer(this.consumerProperties.getProperty(name + ".callbackURL"), this.consumerProperties.getProperty(name + ".consumerKey"), this.consumerProperties.getProperty(name + ".consumerSecret"), serviceProvider);
        consumer.setProperty("name", name);
        if (baseURL != null) {
            consumer.setProperty("serviceProvider.baseURL", baseURL);
        }
        for (final Map.Entry prop : this.consumerProperties.entrySet()) {
            final String propName = prop.getKey();
            if (propName.startsWith(name + ".consumer.")) {
                final String c = propName.substring(name.length() + 10);
                consumer.setProperty(c, prop.getValue());
            }
        }
        return consumer;
    }
    
    private String getURL(final URL base, final String name) throws MalformedURLException {
        String url = this.consumerProperties.getProperty(name);
        if (base != null) {
            url = new URL(base, url).toExternalForm();
        }
        return url;
    }
}
