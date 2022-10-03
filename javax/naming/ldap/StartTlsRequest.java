package javax.naming.ldap;

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.naming.NamingException;
import java.util.Iterator;
import com.sun.naming.internal.VersionHelper;
import java.util.ServiceLoader;
import javax.naming.ConfigurationException;

public class StartTlsRequest implements ExtendedRequest
{
    public static final String OID = "1.3.6.1.4.1.1466.20037";
    private static final long serialVersionUID = 4441679576360753397L;
    
    @Override
    public String getID() {
        return "1.3.6.1.4.1.1466.20037";
    }
    
    @Override
    public byte[] getEncodedValue() {
        return null;
    }
    
    @Override
    public ExtendedResponse createExtendedResponse(final String s, final byte[] array, final int n, final int n2) throws NamingException {
        if (s != null && !s.equals("1.3.6.1.4.1.1466.20037")) {
            throw new ConfigurationException("Start TLS received the following response instead of 1.3.6.1.4.1.1466.20037: " + s);
        }
        ExtendedResponse extendedResponse = null;
        for (Iterator<StartTlsResponse> iterator = ServiceLoader.load(StartTlsResponse.class, this.getContextClassLoader()).iterator(); extendedResponse == null && privilegedHasNext(iterator); extendedResponse = iterator.next()) {}
        if (extendedResponse != null) {
            return extendedResponse;
        }
        StartTlsResponse startTlsResponse;
        try {
            startTlsResponse = (StartTlsResponse)VersionHelper.getVersionHelper().loadClass("com.sun.jndi.ldap.ext.StartTlsResponseImpl").newInstance();
        }
        catch (final IllegalAccessException ex) {
            throw this.wrapException(ex);
        }
        catch (final InstantiationException ex2) {
            throw this.wrapException(ex2);
        }
        catch (final ClassNotFoundException ex3) {
            throw this.wrapException(ex3);
        }
        return startTlsResponse;
    }
    
    private ConfigurationException wrapException(final Exception rootCause) {
        final ConfigurationException ex = new ConfigurationException("Cannot load implementation of javax.naming.ldap.StartTlsResponse");
        ex.setRootCause(rootCause);
        return ex;
    }
    
    private final ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
    
    private static final boolean privilegedHasNext(final Iterator<StartTlsResponse> iterator) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return iterator.hasNext();
            }
        });
    }
}
