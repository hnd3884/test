package sun.security.jgss;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.jgss.spi.MechanismFactory;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import java.security.Provider;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSManager;

public class GSSManagerImpl extends GSSManager
{
    private static final String USE_NATIVE_PROP = "sun.security.jgss.native";
    private static final Boolean USE_NATIVE;
    private ProviderList list;
    
    public GSSManagerImpl(final GSSCaller gssCaller, final boolean b) {
        this.list = new ProviderList(gssCaller, b);
    }
    
    public GSSManagerImpl(final GSSCaller gssCaller) {
        this.list = new ProviderList(gssCaller, GSSManagerImpl.USE_NATIVE);
    }
    
    public GSSManagerImpl() {
        this.list = new ProviderList(GSSCaller.CALLER_UNKNOWN, GSSManagerImpl.USE_NATIVE);
    }
    
    @Override
    public Oid[] getMechs() {
        return this.list.getMechs();
    }
    
    @Override
    public Oid[] getNamesForMech(final Oid oid) throws GSSException {
        return this.list.getMechFactory(oid).getNameTypes().clone();
    }
    
    @Override
    public Oid[] getMechsForName(Oid nt_HOSTBASED_SERVICE) {
        final Oid[] mechs = this.list.getMechs();
        Oid[] array = new Oid[mechs.length];
        int n = 0;
        if (nt_HOSTBASED_SERVICE.equals(GSSNameImpl.oldHostbasedServiceName)) {
            nt_HOSTBASED_SERVICE = GSSName.NT_HOSTBASED_SERVICE;
        }
        for (int i = 0; i < mechs.length; ++i) {
            final Oid oid = mechs[i];
            try {
                if (nt_HOSTBASED_SERVICE.containedIn(this.getNamesForMech(oid))) {
                    array[n++] = oid;
                }
            }
            catch (final GSSException ex) {
                GSSUtil.debug("Skip " + oid + ": error retrieving supported name types");
            }
        }
        if (n < array.length) {
            final Oid[] array2 = new Oid[n];
            for (int j = 0; j < n; ++j) {
                array2[j] = array[j];
            }
            array = array2;
        }
        return array;
    }
    
    @Override
    public GSSName createName(final String s, final Oid oid) throws GSSException {
        return new GSSNameImpl(this, s, oid);
    }
    
    @Override
    public GSSName createName(final byte[] array, final Oid oid) throws GSSException {
        return new GSSNameImpl(this, array, oid);
    }
    
    @Override
    public GSSName createName(final String s, final Oid oid, final Oid oid2) throws GSSException {
        return new GSSNameImpl(this, s, oid, oid2);
    }
    
    @Override
    public GSSName createName(final byte[] array, final Oid oid, final Oid oid2) throws GSSException {
        return new GSSNameImpl(this, array, oid, oid2);
    }
    
    @Override
    public GSSCredential createCredential(final int n) throws GSSException {
        return new GSSCredentialImpl(this, n);
    }
    
    @Override
    public GSSCredential createCredential(final GSSName gssName, final int n, final Oid oid, final int n2) throws GSSException {
        return new GSSCredentialImpl(this, gssName, n, oid, n2);
    }
    
    @Override
    public GSSCredential createCredential(final GSSName gssName, final int n, final Oid[] array, final int n2) throws GSSException {
        return new GSSCredentialImpl(this, gssName, n, array, n2);
    }
    
    @Override
    public GSSContext createContext(final GSSName gssName, final Oid oid, final GSSCredential gssCredential, final int n) throws GSSException {
        return new GSSContextImpl(this, gssName, oid, gssCredential, n);
    }
    
    @Override
    public GSSContext createContext(final GSSCredential gssCredential) throws GSSException {
        return new GSSContextImpl(this, gssCredential);
    }
    
    @Override
    public GSSContext createContext(final byte[] array) throws GSSException {
        return new GSSContextImpl(this, array);
    }
    
    @Override
    public void addProviderAtFront(final Provider provider, final Oid oid) throws GSSException {
        this.list.addProviderAtFront(provider, oid);
    }
    
    @Override
    public void addProviderAtEnd(final Provider provider, final Oid oid) throws GSSException {
        this.list.addProviderAtEnd(provider, oid);
    }
    
    public GSSCredentialSpi getCredentialElement(final GSSNameSpi gssNameSpi, final int n, final int n2, final Oid oid, final int n3) throws GSSException {
        return this.list.getMechFactory(oid).getCredentialElement(gssNameSpi, n, n2, n3);
    }
    
    public GSSNameSpi getNameElement(final String s, final Oid oid, final Oid oid2) throws GSSException {
        return this.list.getMechFactory(oid2).getNameElement(s, oid);
    }
    
    public GSSNameSpi getNameElement(final byte[] array, final Oid oid, final Oid oid2) throws GSSException {
        return this.list.getMechFactory(oid2).getNameElement(array, oid);
    }
    
    GSSContextSpi getMechanismContext(final GSSNameSpi gssNameSpi, final GSSCredentialSpi gssCredentialSpi, final int n, final Oid oid) throws GSSException {
        Provider provider = null;
        if (gssCredentialSpi != null) {
            provider = gssCredentialSpi.getProvider();
        }
        return this.list.getMechFactory(oid, provider).getMechanismContext(gssNameSpi, gssCredentialSpi, n);
    }
    
    GSSContextSpi getMechanismContext(final GSSCredentialSpi gssCredentialSpi, final Oid oid) throws GSSException {
        Provider provider = null;
        if (gssCredentialSpi != null) {
            provider = gssCredentialSpi.getProvider();
        }
        return this.list.getMechFactory(oid, provider).getMechanismContext(gssCredentialSpi);
    }
    
    GSSContextSpi getMechanismContext(final byte[] array) throws GSSException {
        if (array == null || array.length == 0) {
            throw new GSSException(12);
        }
        GSSContextSpi mechanismContext = null;
        final Oid[] mechs = this.list.getMechs();
        for (int i = 0; i < mechs.length; ++i) {
            final MechanismFactory mechFactory = this.list.getMechFactory(mechs[i]);
            if (mechFactory.getProvider().getName().equals("SunNativeGSS")) {
                mechanismContext = mechFactory.getMechanismContext(array);
                if (mechanismContext != null) {
                    break;
                }
            }
        }
        if (mechanismContext == null) {
            throw new GSSException(16);
        }
        return mechanismContext;
    }
    
    static {
        USE_NATIVE = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String property = System.getProperty("os.name");
                if (property.startsWith("SunOS") || property.contains("OS X") || property.startsWith("Linux")) {
                    return new Boolean(System.getProperty("sun.security.jgss.native"));
                }
                return Boolean.FALSE;
            }
        });
    }
}
