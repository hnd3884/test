package javax.security.sasl;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Enumeration;
import java.security.Provider;
import java.security.Security;
import java.util.logging.Level;
import javax.security.auth.callback.CallbackHandler;
import java.util.Map;
import java.util.logging.Logger;
import java.util.List;

public class Sasl
{
    private static List<String> disabledMechanisms;
    private static final String SASL_LOGGER_NAME = "javax.security.sasl";
    private static final Logger logger;
    public static final String QOP = "javax.security.sasl.qop";
    public static final String STRENGTH = "javax.security.sasl.strength";
    public static final String SERVER_AUTH = "javax.security.sasl.server.authentication";
    public static final String BOUND_SERVER_NAME = "javax.security.sasl.bound.server.name";
    public static final String MAX_BUFFER = "javax.security.sasl.maxbuffer";
    public static final String RAW_SEND_SIZE = "javax.security.sasl.rawsendsize";
    public static final String REUSE = "javax.security.sasl.reuse";
    public static final String POLICY_NOPLAINTEXT = "javax.security.sasl.policy.noplaintext";
    public static final String POLICY_NOACTIVE = "javax.security.sasl.policy.noactive";
    public static final String POLICY_NODICTIONARY = "javax.security.sasl.policy.nodictionary";
    public static final String POLICY_NOANONYMOUS = "javax.security.sasl.policy.noanonymous";
    public static final String POLICY_FORWARD_SECRECY = "javax.security.sasl.policy.forward";
    public static final String POLICY_PASS_CREDENTIALS = "javax.security.sasl.policy.credentials";
    public static final String CREDENTIALS = "javax.security.sasl.credentials";
    
    private Sasl() {
    }
    
    public static SaslClient createSaslClient(final String[] array, final String s, final String s2, final String s3, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        for (int i = 0; i < array.length; ++i) {
            final String s4;
            if ((s4 = array[i]) == null) {
                throw new NullPointerException("Mechanism name cannot be null");
            }
            if (s4.length() != 0) {
                if (isDisabled(s4)) {
                    Sasl.logger.log(Level.FINE, "Disabled " + s4 + " mechanism ignored");
                }
                else {
                    final String string = "SaslClientFactory." + s4;
                    final Provider[] providers = Security.getProviders(string);
                    for (int n = 0; providers != null && n < providers.length; ++n) {
                        final String property = providers[n].getProperty(string);
                        if (property != null) {
                            final SaslClientFactory saslClientFactory = (SaslClientFactory)loadFactory(providers[n], property);
                            if (saslClientFactory != null) {
                                final SaslClient saslClient = saslClientFactory.createSaslClient(new String[] { array[i] }, s, s2, s3, map, callbackHandler);
                                if (saslClient != null) {
                                    return saslClient;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private static Object loadFactory(final Provider provider, final String s) throws SaslException {
        try {
            return Class.forName(s, true, provider.getClass().getClassLoader()).newInstance();
        }
        catch (final ClassNotFoundException ex) {
            throw new SaslException("Cannot load class " + s, ex);
        }
        catch (final InstantiationException ex2) {
            throw new SaslException("Cannot instantiate class " + s, ex2);
        }
        catch (final IllegalAccessException ex3) {
            throw new SaslException("Cannot access class " + s, ex3);
        }
        catch (final SecurityException ex4) {
            throw new SaslException("Cannot access class " + s, ex4);
        }
    }
    
    public static SaslServer createSaslServer(final String s, final String s2, final String s3, final Map<String, ?> map, final CallbackHandler callbackHandler) throws SaslException {
        if (s == null) {
            throw new NullPointerException("Mechanism name cannot be null");
        }
        if (s.length() == 0) {
            return null;
        }
        if (isDisabled(s)) {
            Sasl.logger.log(Level.FINE, "Disabled " + s + " mechanism ignored");
            return null;
        }
        final String string = "SaslServerFactory." + s;
        final Provider[] providers = Security.getProviders(string);
        for (int n = 0; providers != null && n < providers.length; ++n) {
            final String property = providers[n].getProperty(string);
            if (property == null) {
                throw new SaslException("Provider does not support " + string);
            }
            final SaslServerFactory saslServerFactory = (SaslServerFactory)loadFactory(providers[n], property);
            if (saslServerFactory != null) {
                final SaslServer saslServer = saslServerFactory.createSaslServer(s, s2, s3, map, callbackHandler);
                if (saslServer != null) {
                    return saslServer;
                }
            }
        }
        return null;
    }
    
    public static Enumeration<SaslClientFactory> getSaslClientFactories() {
        return new Enumeration<SaslClientFactory>() {
            final /* synthetic */ Iterator val$iter = getFactories("SaslClientFactory").iterator();
            
            @Override
            public boolean hasMoreElements() {
                return this.val$iter.hasNext();
            }
            
            @Override
            public SaslClientFactory nextElement() {
                return this.val$iter.next();
            }
        };
    }
    
    public static Enumeration<SaslServerFactory> getSaslServerFactories() {
        return new Enumeration<SaslServerFactory>() {
            final /* synthetic */ Iterator val$iter = getFactories("SaslServerFactory").iterator();
            
            @Override
            public boolean hasMoreElements() {
                return this.val$iter.hasNext();
            }
            
            @Override
            public SaslServerFactory nextElement() {
                return this.val$iter.next();
            }
        };
    }
    
    private static Set<Object> getFactories(final String s) {
        final HashSet set = new HashSet();
        if (s == null || s.length() == 0 || s.endsWith(".")) {
            return set;
        }
        final Provider[] providers = Security.getProviders();
        final HashSet set2 = new HashSet();
        for (int i = 0; i < providers.length; ++i) {
            set2.clear();
            final Enumeration<Object> keys = providers[i].keys();
            while (keys.hasMoreElements()) {
                final String s2 = keys.nextElement();
                if (s2.startsWith(s) && s2.indexOf(" ") < 0) {
                    final String property = providers[i].getProperty(s2);
                    if (set2.contains(property)) {
                        continue;
                    }
                    set2.add(property);
                    try {
                        final Object loadFactory = loadFactory(providers[i], property);
                        if (loadFactory == null) {
                            continue;
                        }
                        set.add(loadFactory);
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        return Collections.unmodifiableSet((Set<?>)set);
    }
    
    private static boolean isDisabled(final String s) {
        return Sasl.disabledMechanisms.contains(s);
    }
    
    static {
        Sasl.disabledMechanisms = new ArrayList<String>();
        final String s = AccessController.doPrivileged(() -> Security.getProperty("jdk.sasl.disabledMechanisms"));
        if (s != null) {
            for (final String s2 : s.split("\\s*,\\s*")) {
                if (!s2.isEmpty()) {
                    Sasl.disabledMechanisms.add(s2);
                }
            }
        }
        logger = Logger.getLogger("javax.security.sasl");
    }
}
