package sun.security.jgss;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Enumeration;
import java.lang.reflect.InvocationTargetException;
import sun.security.jgss.wrapper.NativeGSSFactory;
import java.util.Iterator;
import org.ietf.jgss.GSSException;
import java.security.Security;
import sun.security.jgss.wrapper.SunNativeProvider;
import java.security.Provider;
import java.util.HashSet;
import sun.security.jgss.spi.MechanismFactory;
import java.util.HashMap;
import java.util.ArrayList;
import org.ietf.jgss.Oid;

public final class ProviderList
{
    private static final String PROV_PROP_PREFIX = "GssApiMechanism.";
    private static final int PROV_PROP_PREFIX_LEN;
    private static final String SPI_MECH_FACTORY_TYPE = "sun.security.jgss.spi.MechanismFactory";
    private static final String DEFAULT_MECH_PROP = "sun.security.jgss.mechanism";
    public static final Oid DEFAULT_MECH_OID;
    private ArrayList<PreferencesEntry> preferences;
    private HashMap<PreferencesEntry, MechanismFactory> factories;
    private HashSet<Oid> mechs;
    private final GSSCaller caller;
    
    public ProviderList(final GSSCaller caller, final boolean b) {
        this.preferences = new ArrayList<PreferencesEntry>(5);
        this.factories = new HashMap<PreferencesEntry, MechanismFactory>(5);
        this.mechs = new HashSet<Oid>(5);
        this.caller = caller;
        Provider[] providers;
        if (b) {
            providers = new Provider[] { new SunNativeProvider() };
        }
        else {
            providers = Security.getProviders();
        }
        for (int i = 0; i < providers.length; ++i) {
            final Provider provider = providers[i];
            try {
                this.addProviderAtEnd(provider, null);
            }
            catch (final GSSException ex) {
                GSSUtil.debug("Error in adding provider " + provider.getName() + ": " + ex);
            }
        }
    }
    
    private boolean isMechFactoryProperty(final String s) {
        return s.startsWith("GssApiMechanism.") || s.regionMatches(true, 0, "GssApiMechanism.", 0, ProviderList.PROV_PROP_PREFIX_LEN);
    }
    
    private Oid getOidFromMechFactoryProperty(final String s) throws GSSException {
        return new Oid(s.substring(ProviderList.PROV_PROP_PREFIX_LEN));
    }
    
    public synchronized MechanismFactory getMechFactory(Oid default_MECH_OID) throws GSSException {
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        return this.getMechFactory(default_MECH_OID, null);
    }
    
    public synchronized MechanismFactory getMechFactory(Oid default_MECH_OID, final Provider provider) throws GSSException {
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        if (provider == null) {
            for (final PreferencesEntry preferencesEntry : this.preferences) {
                if (preferencesEntry.impliesMechanism(default_MECH_OID)) {
                    final MechanismFactory mechFactory = this.getMechFactory(preferencesEntry, default_MECH_OID);
                    if (mechFactory != null) {
                        return mechFactory;
                    }
                    continue;
                }
            }
            throw new GSSExceptionImpl(2, default_MECH_OID);
        }
        return this.getMechFactory(new PreferencesEntry(provider, default_MECH_OID), default_MECH_OID);
    }
    
    private MechanismFactory getMechFactory(final PreferencesEntry preferencesEntry, final Oid oid) throws GSSException {
        final Provider provider = preferencesEntry.getProvider();
        final PreferencesEntry preferencesEntry2 = new PreferencesEntry(provider, oid);
        MechanismFactory mechFactoryImpl = this.factories.get(preferencesEntry2);
        if (mechFactoryImpl == null) {
            final String property = provider.getProperty("GssApiMechanism." + oid.toString());
            if (property != null) {
                mechFactoryImpl = getMechFactoryImpl(provider, property, oid, this.caller);
                this.factories.put(preferencesEntry2, mechFactoryImpl);
            }
            else if (preferencesEntry.getOid() != null) {
                throw new GSSExceptionImpl(2, "Provider " + provider.getName() + " does not support mechanism " + oid);
            }
        }
        return mechFactoryImpl;
    }
    
    private static MechanismFactory getMechFactoryImpl(final Provider provider, final String s, final Oid mech, final GSSCaller gssCaller) throws GSSException {
        try {
            final Class<?> forName = Class.forName("sun.security.jgss.spi.MechanismFactory");
            final ClassLoader classLoader = provider.getClass().getClassLoader();
            Class<?> clazz;
            if (classLoader != null) {
                clazz = classLoader.loadClass(s);
            }
            else {
                clazz = Class.forName(s);
            }
            if (forName.isAssignableFrom(clazz)) {
                final MechanismFactory mechanismFactory = (MechanismFactory)clazz.getConstructor(GSSCaller.class).newInstance(gssCaller);
                if (mechanismFactory instanceof NativeGSSFactory) {
                    ((NativeGSSFactory)mechanismFactory).setMech(mech);
                }
                return mechanismFactory;
            }
            throw createGSSException(provider, s, "is not a sun.security.jgss.spi.MechanismFactory", null);
        }
        catch (final ClassNotFoundException ex) {
            throw createGSSException(provider, s, "cannot be created", ex);
        }
        catch (final NoSuchMethodException ex2) {
            throw createGSSException(provider, s, "cannot be created", ex2);
        }
        catch (final InvocationTargetException ex3) {
            throw createGSSException(provider, s, "cannot be created", ex3);
        }
        catch (final InstantiationException ex4) {
            throw createGSSException(provider, s, "cannot be created", ex4);
        }
        catch (final IllegalAccessException ex5) {
            throw createGSSException(provider, s, "cannot be created", ex5);
        }
        catch (final SecurityException ex6) {
            throw createGSSException(provider, s, "cannot be created", ex6);
        }
    }
    
    private static GSSException createGSSException(final Provider provider, final String s, final String s2, final Exception ex) {
        return new GSSExceptionImpl(2, s + " configured by " + provider.getName() + " for GSS-API Mechanism Factory " + s2, ex);
    }
    
    public Oid[] getMechs() {
        return this.mechs.toArray(new Oid[0]);
    }
    
    public synchronized void addProviderAtFront(final Provider provider, final Oid oid) throws GSSException {
        final PreferencesEntry preferencesEntry = new PreferencesEntry(provider, oid);
        final Iterator<PreferencesEntry> iterator = this.preferences.iterator();
        while (iterator.hasNext()) {
            if (preferencesEntry.implies(iterator.next())) {
                iterator.remove();
            }
        }
        boolean addAllMechsFromProvider;
        if (oid == null) {
            addAllMechsFromProvider = this.addAllMechsFromProvider(provider);
        }
        else {
            final String string = oid.toString();
            if (provider.getProperty("GssApiMechanism." + string) == null) {
                throw new GSSExceptionImpl(2, "Provider " + provider.getName() + " does not support " + string);
            }
            this.mechs.add(oid);
            addAllMechsFromProvider = true;
        }
        if (addAllMechsFromProvider) {
            this.preferences.add(0, preferencesEntry);
        }
    }
    
    public synchronized void addProviderAtEnd(final Provider provider, final Oid oid) throws GSSException {
        final PreferencesEntry preferencesEntry = new PreferencesEntry(provider, oid);
        final Iterator<PreferencesEntry> iterator = this.preferences.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().implies(preferencesEntry)) {
                return;
            }
        }
        boolean addAllMechsFromProvider;
        if (oid == null) {
            addAllMechsFromProvider = this.addAllMechsFromProvider(provider);
        }
        else {
            final String string = oid.toString();
            if (provider.getProperty("GssApiMechanism." + string) == null) {
                throw new GSSExceptionImpl(2, "Provider " + provider.getName() + " does not support " + string);
            }
            this.mechs.add(oid);
            addAllMechsFromProvider = true;
        }
        if (addAllMechsFromProvider) {
            this.preferences.add(preferencesEntry);
        }
    }
    
    private boolean addAllMechsFromProvider(final Provider provider) {
        boolean b = false;
        final Enumeration<Object> keys = provider.keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            if (this.isMechFactoryProperty(s)) {
                try {
                    this.mechs.add(this.getOidFromMechFactoryProperty(s));
                    b = true;
                }
                catch (final GSSException ex) {
                    GSSUtil.debug("Ignore the invalid property " + s + " from provider " + provider.getName());
                }
            }
        }
        return b;
    }
    
    static {
        PROV_PROP_PREFIX_LEN = "GssApiMechanism.".length();
        Oid oid = null;
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.security.jgss.mechanism"));
        if (s != null) {
            oid = GSSUtil.createOid(s);
        }
        DEFAULT_MECH_OID = ((oid == null) ? GSSUtil.GSS_KRB5_MECH_OID : oid);
    }
    
    private static final class PreferencesEntry
    {
        private Provider p;
        private Oid oid;
        
        PreferencesEntry(final Provider p2, final Oid oid) {
            this.p = p2;
            this.oid = oid;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PreferencesEntry)) {
                return false;
            }
            final PreferencesEntry preferencesEntry = (PreferencesEntry)o;
            if (!this.p.getName().equals(preferencesEntry.p.getName())) {
                return false;
            }
            if (this.oid != null && preferencesEntry.oid != null) {
                return this.oid.equals(preferencesEntry.oid);
            }
            return this.oid == null && preferencesEntry.oid == null;
        }
        
        @Override
        public int hashCode() {
            int n = 37 * 17 + this.p.getName().hashCode();
            if (this.oid != null) {
                n = 37 * n + this.oid.hashCode();
            }
            return n;
        }
        
        boolean implies(final Object o) {
            if (o instanceof PreferencesEntry) {
                final PreferencesEntry preferencesEntry = (PreferencesEntry)o;
                return this.equals(preferencesEntry) || (this.p.getName().equals(preferencesEntry.p.getName()) && this.oid == null);
            }
            return false;
        }
        
        Provider getProvider() {
            return this.p;
        }
        
        Oid getOid() {
            return this.oid;
        }
        
        boolean impliesMechanism(final Oid oid) {
            return this.oid == null || this.oid.equals(oid);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("<");
            sb.append(this.p.getName());
            sb.append(", ");
            sb.append(this.oid);
            sb.append(">");
            return sb.toString();
        }
    }
}
