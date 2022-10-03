package javax.ws.rs.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import javax.net.ssl.HostnameVerifier;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.Configuration;
import java.net.URL;
import javax.ws.rs.core.Configurable;

public abstract class ClientBuilder implements Configurable<ClientBuilder>
{
    public static final String JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY = "javax.ws.rs.client.ClientBuilder";
    private static final String JAXRS_DEFAULT_CLIENT_BUILDER = "org.glassfish.jersey.client.JerseyClientBuilder";
    
    protected ClientBuilder() {
    }
    
    public static ClientBuilder newBuilder() {
        try {
            final Object delegate = FactoryFinder.find("javax.ws.rs.client.ClientBuilder", "org.glassfish.jersey.client.JerseyClientBuilder", ClientBuilder.class);
            if (!(delegate instanceof ClientBuilder)) {
                final Class pClass = ClientBuilder.class;
                final String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
                ClassLoader loader = pClass.getClassLoader();
                if (loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }
                final URL targetTypeURL = loader.getResource(classnameAsResource);
                throw new LinkageError("ClassCastException: attempting to cast" + delegate.getClass().getClassLoader().getResource(classnameAsResource) + " to " + targetTypeURL);
            }
            return (ClientBuilder)delegate;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static Client newClient() {
        return newBuilder().build();
    }
    
    public static Client newClient(final Configuration configuration) {
        return newBuilder().withConfig(configuration).build();
    }
    
    public abstract ClientBuilder withConfig(final Configuration p0);
    
    public abstract ClientBuilder sslContext(final SSLContext p0);
    
    public abstract ClientBuilder keyStore(final KeyStore p0, final char[] p1);
    
    public ClientBuilder keyStore(final KeyStore keyStore, final String password) {
        return this.keyStore(keyStore, password.toCharArray());
    }
    
    public abstract ClientBuilder trustStore(final KeyStore p0);
    
    public abstract ClientBuilder hostnameVerifier(final HostnameVerifier p0);
    
    public abstract ClientBuilder executorService(final ExecutorService p0);
    
    public abstract ClientBuilder scheduledExecutorService(final ScheduledExecutorService p0);
    
    public abstract ClientBuilder connectTimeout(final long p0, final TimeUnit p1);
    
    public abstract ClientBuilder readTimeout(final long p0, final TimeUnit p1);
    
    public abstract Client build();
}
