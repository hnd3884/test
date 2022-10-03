package org.apache.catalina.authenticator.jaspic;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Constructor;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.RegistrationListener;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.juli.logging.LogFactory;
import java.util.Map;
import java.io.File;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import javax.security.auth.message.config.AuthConfigFactory;

public class AuthConfigFactoryImpl extends AuthConfigFactory
{
    private final Log log;
    private static final StringManager sm;
    private static final String CONFIG_PATH = "conf/jaspic-providers.xml";
    private static final File CONFIG_FILE;
    private static final Object CONFIG_FILE_LOCK;
    private static final String[] EMPTY_STRING_ARRAY;
    private static String DEFAULT_REGISTRATION_ID;
    private final Map<String, RegistrationContextImpl> layerAppContextRegistrations;
    private final Map<String, RegistrationContextImpl> appContextRegistrations;
    private final Map<String, RegistrationContextImpl> layerRegistrations;
    private final Map<String, RegistrationContextImpl> defaultRegistration;
    
    public AuthConfigFactoryImpl() {
        this.log = LogFactory.getLog((Class)AuthConfigFactoryImpl.class);
        this.layerAppContextRegistrations = new ConcurrentHashMap<String, RegistrationContextImpl>();
        this.appContextRegistrations = new ConcurrentHashMap<String, RegistrationContextImpl>();
        this.layerRegistrations = new ConcurrentHashMap<String, RegistrationContextImpl>();
        this.defaultRegistration = new ConcurrentHashMap<String, RegistrationContextImpl>(1);
        this.loadPersistentRegistrations();
    }
    
    public AuthConfigProvider getConfigProvider(final String layer, final String appContext, final RegistrationListener listener) {
        final RegistrationContextImpl registrationContext = this.findRegistrationContextImpl(layer, appContext);
        if (registrationContext != null) {
            if (listener != null) {
                final RegistrationListenerWrapper wrapper = new RegistrationListenerWrapper(layer, appContext, listener);
                registrationContext.addListener(wrapper);
            }
            return registrationContext.getProvider();
        }
        return null;
    }
    
    public String registerConfigProvider(final String className, final Map properties, final String layer, final String appContext, final String description) {
        final String registrationID = this.doRegisterConfigProvider(className, properties, layer, appContext, description);
        this.savePersistentRegistrations();
        return registrationID;
    }
    
    private String doRegisterConfigProvider(final String className, final Map properties, final String layer, final String appContext, final String description) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)AuthConfigFactoryImpl.sm.getString("authConfigFactoryImpl.registerClass", new Object[] { className, layer, appContext }));
        }
        AuthConfigProvider provider = null;
        if (className != null) {
            provider = this.createAuthConfigProvider(className, properties);
        }
        final String registrationID = getRegistrationID(layer, appContext);
        final RegistrationContextImpl registrationContextImpl = new RegistrationContextImpl(layer, appContext, description, true, provider, properties);
        this.addRegistrationContextImpl(layer, appContext, registrationID, registrationContextImpl);
        return registrationID;
    }
    
    private AuthConfigProvider createAuthConfigProvider(final String className, final Map properties) throws SecurityException {
        Class<?> clazz = null;
        AuthConfigProvider provider = null;
        try {
            clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        }
        catch (final ClassNotFoundException ex) {}
        try {
            if (clazz == null) {
                clazz = Class.forName(className);
            }
            final Constructor<?> constructor = clazz.getConstructor(Map.class, AuthConfigFactory.class);
            provider = (AuthConfigProvider)constructor.newInstance(properties, null);
        }
        catch (final ReflectiveOperationException | IllegalArgumentException e) {
            throw new SecurityException(e);
        }
        return provider;
    }
    
    public String registerConfigProvider(final AuthConfigProvider provider, final String layer, final String appContext, final String description) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)AuthConfigFactoryImpl.sm.getString("authConfigFactoryImpl.registerInstance", new Object[] { provider.getClass().getName(), layer, appContext }));
        }
        final String registrationID = getRegistrationID(layer, appContext);
        final RegistrationContextImpl registrationContextImpl = new RegistrationContextImpl(layer, appContext, description, false, provider, (Map)null);
        this.addRegistrationContextImpl(layer, appContext, registrationID, registrationContextImpl);
        return registrationID;
    }
    
    private void addRegistrationContextImpl(final String layer, final String appContext, final String registrationID, final RegistrationContextImpl registrationContextImpl) {
        RegistrationContextImpl previous = null;
        if (layer != null && appContext != null) {
            previous = this.layerAppContextRegistrations.put(registrationID, registrationContextImpl);
        }
        else if (layer == null && appContext != null) {
            previous = this.appContextRegistrations.put(registrationID, registrationContextImpl);
        }
        else if (layer != null && appContext == null) {
            previous = this.layerRegistrations.put(registrationID, registrationContextImpl);
        }
        else {
            previous = this.defaultRegistration.put(registrationID, registrationContextImpl);
        }
        if (previous == null) {
            if (layer != null && appContext != null) {
                final RegistrationContextImpl registration = this.appContextRegistrations.get(getRegistrationID(null, appContext));
                if (registration != null) {
                    for (final RegistrationListenerWrapper wrapper : registration.listeners) {
                        if (layer.equals(wrapper.getMessageLayer()) && appContext.equals(wrapper.getAppContext())) {
                            registration.listeners.remove(wrapper);
                            wrapper.listener.notify(wrapper.messageLayer, wrapper.appContext);
                        }
                    }
                }
            }
            if (appContext != null) {
                for (final RegistrationContextImpl registration2 : this.layerRegistrations.values()) {
                    for (final RegistrationListenerWrapper wrapper2 : registration2.listeners) {
                        if (appContext.equals(wrapper2.getAppContext())) {
                            registration2.listeners.remove(wrapper2);
                            wrapper2.listener.notify(wrapper2.messageLayer, wrapper2.appContext);
                        }
                    }
                }
            }
            if (layer != null || appContext != null) {
                for (final RegistrationContextImpl registration2 : this.defaultRegistration.values()) {
                    for (final RegistrationListenerWrapper wrapper2 : registration2.listeners) {
                        if ((appContext != null && appContext.equals(wrapper2.getAppContext())) || (layer != null && layer.equals(wrapper2.getMessageLayer()))) {
                            registration2.listeners.remove(wrapper2);
                            wrapper2.listener.notify(wrapper2.messageLayer, wrapper2.appContext);
                        }
                    }
                }
            }
        }
        else {
            for (final RegistrationListenerWrapper wrapper3 : previous.listeners) {
                previous.listeners.remove(wrapper3);
                wrapper3.listener.notify(wrapper3.messageLayer, wrapper3.appContext);
            }
        }
    }
    
    public boolean removeRegistration(final String registrationID) {
        RegistrationContextImpl registration = null;
        if (AuthConfigFactoryImpl.DEFAULT_REGISTRATION_ID.equals(registrationID)) {
            registration = this.defaultRegistration.remove(registrationID);
        }
        if (registration == null) {
            registration = this.layerAppContextRegistrations.remove(registrationID);
        }
        if (registration == null) {
            registration = this.appContextRegistrations.remove(registrationID);
        }
        if (registration == null) {
            registration = this.layerRegistrations.remove(registrationID);
        }
        if (registration == null) {
            return false;
        }
        for (final RegistrationListenerWrapper wrapper : registration.listeners) {
            wrapper.getListener().notify(wrapper.getMessageLayer(), wrapper.getAppContext());
        }
        if (registration.isPersistent()) {
            this.savePersistentRegistrations();
        }
        return true;
    }
    
    public String[] detachListener(final RegistrationListener listener, final String layer, final String appContext) {
        final String registrationID = getRegistrationID(layer, appContext);
        final RegistrationContextImpl registrationContext = this.findRegistrationContextImpl(layer, appContext);
        if (registrationContext != null && registrationContext.removeListener(listener)) {
            return new String[] { registrationID };
        }
        return AuthConfigFactoryImpl.EMPTY_STRING_ARRAY;
    }
    
    public String[] getRegistrationIDs(final AuthConfigProvider provider) {
        final List<String> result = new ArrayList<String>();
        if (provider == null) {
            result.addAll(this.layerAppContextRegistrations.keySet());
            result.addAll(this.appContextRegistrations.keySet());
            result.addAll(this.layerRegistrations.keySet());
            if (!this.defaultRegistration.isEmpty()) {
                result.add(AuthConfigFactoryImpl.DEFAULT_REGISTRATION_ID);
            }
        }
        else {
            this.findProvider(provider, this.layerAppContextRegistrations, result);
            this.findProvider(provider, this.appContextRegistrations, result);
            this.findProvider(provider, this.layerRegistrations, result);
            this.findProvider(provider, this.defaultRegistration, result);
        }
        return result.toArray(AuthConfigFactoryImpl.EMPTY_STRING_ARRAY);
    }
    
    private void findProvider(final AuthConfigProvider provider, final Map<String, RegistrationContextImpl> registrations, final List<String> result) {
        for (final Map.Entry<String, RegistrationContextImpl> entry : registrations.entrySet()) {
            if (provider.equals(entry.getValue().getProvider())) {
                result.add(entry.getKey());
            }
        }
    }
    
    public AuthConfigFactory.RegistrationContext getRegistrationContext(final String registrationID) {
        AuthConfigFactory.RegistrationContext result = (AuthConfigFactory.RegistrationContext)this.defaultRegistration.get(registrationID);
        if (result == null) {
            result = (AuthConfigFactory.RegistrationContext)this.layerAppContextRegistrations.get(registrationID);
        }
        if (result == null) {
            result = (AuthConfigFactory.RegistrationContext)this.appContextRegistrations.get(registrationID);
        }
        if (result == null) {
            result = (AuthConfigFactory.RegistrationContext)this.layerRegistrations.get(registrationID);
        }
        return result;
    }
    
    public void refresh() {
        this.loadPersistentRegistrations();
    }
    
    private static String getRegistrationID(final String layer, final String appContext) {
        if (layer != null && layer.length() == 0) {
            throw new IllegalArgumentException(AuthConfigFactoryImpl.sm.getString("authConfigFactoryImpl.zeroLengthMessageLayer"));
        }
        if (appContext != null && appContext.length() == 0) {
            throw new IllegalArgumentException(AuthConfigFactoryImpl.sm.getString("authConfigFactoryImpl.zeroLengthAppContext"));
        }
        return ((layer == null) ? "" : layer) + ":" + ((appContext == null) ? "" : appContext);
    }
    
    private void loadPersistentRegistrations() {
        synchronized (AuthConfigFactoryImpl.CONFIG_FILE_LOCK) {
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)AuthConfigFactoryImpl.sm.getString("authConfigFactoryImpl.load", new Object[] { AuthConfigFactoryImpl.CONFIG_FILE.getAbsolutePath() }));
            }
            if (!AuthConfigFactoryImpl.CONFIG_FILE.isFile()) {
                return;
            }
            final PersistentProviderRegistrations.Providers providers = PersistentProviderRegistrations.loadProviders(AuthConfigFactoryImpl.CONFIG_FILE);
            for (final PersistentProviderRegistrations.Provider provider : providers.getProviders()) {
                this.doRegisterConfigProvider(provider.getClassName(), provider.getProperties(), provider.getLayer(), provider.getAppContext(), provider.getDescription());
            }
        }
    }
    
    private void savePersistentRegistrations() {
        synchronized (AuthConfigFactoryImpl.CONFIG_FILE_LOCK) {
            final PersistentProviderRegistrations.Providers providers = new PersistentProviderRegistrations.Providers();
            this.savePersistentProviders(providers, this.layerAppContextRegistrations);
            this.savePersistentProviders(providers, this.appContextRegistrations);
            this.savePersistentProviders(providers, this.layerRegistrations);
            this.savePersistentProviders(providers, this.defaultRegistration);
            PersistentProviderRegistrations.writeProviders(providers, AuthConfigFactoryImpl.CONFIG_FILE);
        }
    }
    
    private void savePersistentProviders(final PersistentProviderRegistrations.Providers providers, final Map<String, RegistrationContextImpl> registrations) {
        for (final Map.Entry<String, RegistrationContextImpl> entry : registrations.entrySet()) {
            this.savePersistentProvider(providers, entry.getValue());
        }
    }
    
    private void savePersistentProvider(final PersistentProviderRegistrations.Providers providers, final RegistrationContextImpl registrationContextImpl) {
        if (registrationContextImpl != null && registrationContextImpl.isPersistent()) {
            final PersistentProviderRegistrations.Provider provider = new PersistentProviderRegistrations.Provider();
            provider.setAppContext(registrationContextImpl.getAppContext());
            if (registrationContextImpl.getProvider() != null) {
                provider.setClassName(registrationContextImpl.getProvider().getClass().getName());
            }
            provider.setDescription(registrationContextImpl.getDescription());
            provider.setLayer(registrationContextImpl.getMessageLayer());
            for (final Map.Entry<String, String> property : registrationContextImpl.getProperties().entrySet()) {
                provider.addProperty(property.getKey(), property.getValue());
            }
            providers.addProvider(provider);
        }
    }
    
    private RegistrationContextImpl findRegistrationContextImpl(final String layer, final String appContext) {
        RegistrationContextImpl result = this.layerAppContextRegistrations.get(getRegistrationID(layer, appContext));
        if (result == null) {
            result = this.appContextRegistrations.get(getRegistrationID(null, appContext));
        }
        if (result == null) {
            result = this.layerRegistrations.get(getRegistrationID(layer, null));
        }
        if (result == null) {
            result = this.defaultRegistration.get(AuthConfigFactoryImpl.DEFAULT_REGISTRATION_ID);
        }
        return result;
    }
    
    static {
        sm = StringManager.getManager((Class)AuthConfigFactoryImpl.class);
        CONFIG_FILE = new File(System.getProperty("catalina.base"), "conf/jaspic-providers.xml");
        CONFIG_FILE_LOCK = new Object();
        EMPTY_STRING_ARRAY = new String[0];
        AuthConfigFactoryImpl.DEFAULT_REGISTRATION_ID = getRegistrationID(null, null);
    }
    
    private static class RegistrationContextImpl implements AuthConfigFactory.RegistrationContext
    {
        private final String messageLayer;
        private final String appContext;
        private final String description;
        private final boolean persistent;
        private final AuthConfigProvider provider;
        private final Map<String, String> properties;
        private final List<RegistrationListenerWrapper> listeners;
        
        private RegistrationContextImpl(final String messageLayer, final String appContext, final String description, final boolean persistent, final AuthConfigProvider provider, final Map<String, String> properties) {
            this.listeners = new CopyOnWriteArrayList<RegistrationListenerWrapper>();
            this.messageLayer = messageLayer;
            this.appContext = appContext;
            this.description = description;
            this.persistent = persistent;
            this.provider = provider;
            final Map<String, String> propertiesCopy = new HashMap<String, String>();
            if (properties != null) {
                propertiesCopy.putAll(properties);
            }
            this.properties = Collections.unmodifiableMap((Map<? extends String, ? extends String>)propertiesCopy);
        }
        
        public String getMessageLayer() {
            return this.messageLayer;
        }
        
        public String getAppContext() {
            return this.appContext;
        }
        
        public String getDescription() {
            return this.description;
        }
        
        public boolean isPersistent() {
            return this.persistent;
        }
        
        private AuthConfigProvider getProvider() {
            return this.provider;
        }
        
        private void addListener(final RegistrationListenerWrapper listener) {
            if (listener != null) {
                this.listeners.add(listener);
            }
        }
        
        private Map<String, String> getProperties() {
            return this.properties;
        }
        
        private boolean removeListener(final RegistrationListener listener) {
            boolean result = false;
            for (final RegistrationListenerWrapper wrapper : this.listeners) {
                if (wrapper.getListener().equals(listener)) {
                    this.listeners.remove(wrapper);
                    result = true;
                }
            }
            return result;
        }
    }
    
    private static class RegistrationListenerWrapper
    {
        private final String messageLayer;
        private final String appContext;
        private final RegistrationListener listener;
        
        public RegistrationListenerWrapper(final String messageLayer, final String appContext, final RegistrationListener listener) {
            this.messageLayer = messageLayer;
            this.appContext = appContext;
            this.listener = listener;
        }
        
        public String getMessageLayer() {
            return this.messageLayer;
        }
        
        public String getAppContext() {
            return this.appContext;
        }
        
        public RegistrationListener getListener() {
            return this.listener;
        }
    }
}
