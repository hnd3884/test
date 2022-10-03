package sun.security.jgss;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import sun.security.jgss.spi.GSSNameSpi;
import java.util.Vector;
import org.ietf.jgss.GSSCredential;
import java.util.Enumeration;
import sun.security.jgss.spnego.SpNegoCredElement;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSCredentialSpi;
import java.util.Hashtable;
import com.sun.security.jgss.ExtendedGSSCredential;

public class GSSCredentialImpl implements ExtendedGSSCredential
{
    private GSSManagerImpl gssManager;
    private boolean destroyed;
    private Hashtable<SearchKey, GSSCredentialSpi> hashtable;
    private GSSCredentialSpi tempCred;
    
    GSSCredentialImpl(final GSSManagerImpl gssManagerImpl, final int n) throws GSSException {
        this(gssManagerImpl, null, 0, (Oid[])null, n);
    }
    
    GSSCredentialImpl(final GSSManagerImpl gssManagerImpl, final GSSName gssName, final int n, Oid default_MECH_OID, final int n2) throws GSSException {
        this.gssManager = null;
        this.destroyed = false;
        this.hashtable = null;
        this.tempCred = null;
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        this.init(gssManagerImpl);
        this.add(gssName, n, n, default_MECH_OID, n2);
    }
    
    GSSCredentialImpl(final GSSManagerImpl gssManagerImpl, final GSSName gssName, final int n, Oid[] mechs, final int n2) throws GSSException {
        this.gssManager = null;
        this.destroyed = false;
        this.hashtable = null;
        this.tempCred = null;
        this.init(gssManagerImpl);
        boolean b = false;
        if (mechs == null) {
            mechs = gssManagerImpl.getMechs();
            b = true;
        }
        for (int i = 0; i < mechs.length; ++i) {
            try {
                this.add(gssName, n, n, mechs[i], n2);
            }
            catch (final GSSException ex) {
                if (!b) {
                    throw ex;
                }
                GSSUtil.debug("Ignore " + ex + " while acquring cred for " + mechs[i]);
            }
        }
        if (this.hashtable.size() == 0 || n2 != this.getUsage()) {
            throw new GSSException(13);
        }
    }
    
    public GSSCredentialImpl(final GSSManagerImpl gssManagerImpl, final GSSCredentialSpi tempCred) throws GSSException {
        this.gssManager = null;
        this.destroyed = false;
        this.hashtable = null;
        this.tempCred = null;
        this.init(gssManagerImpl);
        int n = 2;
        if (tempCred.isInitiatorCredential()) {
            if (tempCred.isAcceptorCredential()) {
                n = 0;
            }
            else {
                n = 1;
            }
        }
        final SearchKey searchKey = new SearchKey(tempCred.getMechanism(), n);
        this.tempCred = tempCred;
        this.hashtable.put(searchKey, this.tempCred);
        if (!GSSUtil.isSpNegoMech(tempCred.getMechanism())) {
            this.hashtable.put(new SearchKey(GSSUtil.GSS_SPNEGO_MECH_OID, n), new SpNegoCredElement(tempCred));
        }
    }
    
    void init(final GSSManagerImpl gssManager) {
        this.gssManager = gssManager;
        this.hashtable = new Hashtable<SearchKey, GSSCredentialSpi>(gssManager.getMechs().length);
    }
    
    @Override
    public void dispose() throws GSSException {
        if (!this.destroyed) {
            final Enumeration<GSSCredentialSpi> elements = this.hashtable.elements();
            while (elements.hasMoreElements()) {
                elements.nextElement().dispose();
            }
            this.destroyed = true;
        }
    }
    
    @Override
    public GSSCredential impersonate(final GSSName gssName) throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        final Oid mechanism = this.tempCred.getMechanism();
        final GSSCredentialSpi impersonate = this.tempCred.impersonate((gssName == null) ? null : ((GSSNameImpl)gssName).getElement(mechanism));
        return (impersonate == null) ? null : new GSSCredentialImpl(this.gssManager, impersonate);
    }
    
    @Override
    public GSSName getName() throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        return GSSNameImpl.wrapElement(this.gssManager, this.tempCred.getName());
    }
    
    @Override
    public GSSName getName(Oid default_MECH_OID) throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        GSSCredentialSpi gssCredentialSpi = this.hashtable.get(new SearchKey(default_MECH_OID, 1));
        if (gssCredentialSpi == null) {
            gssCredentialSpi = this.hashtable.get(new SearchKey(default_MECH_OID, 2));
        }
        if (gssCredentialSpi == null) {
            gssCredentialSpi = this.hashtable.get(new SearchKey(default_MECH_OID, 0));
        }
        if (gssCredentialSpi == null) {
            throw new GSSExceptionImpl(2, default_MECH_OID);
        }
        return GSSNameImpl.wrapElement(this.gssManager, gssCredentialSpi.getName());
    }
    
    @Override
    public int getRemainingLifetime() throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        int n = Integer.MAX_VALUE;
        final Enumeration<SearchKey> keys = this.hashtable.keys();
        while (keys.hasMoreElements()) {
            final SearchKey searchKey = keys.nextElement();
            final GSSCredentialSpi gssCredentialSpi = this.hashtable.get(searchKey);
            int n2;
            if (searchKey.getUsage() == 1) {
                n2 = gssCredentialSpi.getInitLifetime();
            }
            else if (searchKey.getUsage() == 2) {
                n2 = gssCredentialSpi.getAcceptLifetime();
            }
            else {
                final int initLifetime = gssCredentialSpi.getInitLifetime();
                final int acceptLifetime = gssCredentialSpi.getAcceptLifetime();
                n2 = ((initLifetime < acceptLifetime) ? initLifetime : acceptLifetime);
            }
            if (n > n2) {
                n = n2;
            }
        }
        return n;
    }
    
    @Override
    public int getRemainingInitLifetime(Oid default_MECH_OID) throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        boolean b = false;
        int n = 0;
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        final GSSCredentialSpi gssCredentialSpi = this.hashtable.get(new SearchKey(default_MECH_OID, 1));
        if (gssCredentialSpi != null) {
            b = true;
            if (n < gssCredentialSpi.getInitLifetime()) {
                n = gssCredentialSpi.getInitLifetime();
            }
        }
        final GSSCredentialSpi gssCredentialSpi2 = this.hashtable.get(new SearchKey(default_MECH_OID, 0));
        if (gssCredentialSpi2 != null) {
            b = true;
            if (n < gssCredentialSpi2.getInitLifetime()) {
                n = gssCredentialSpi2.getInitLifetime();
            }
        }
        if (!b) {
            throw new GSSExceptionImpl(2, default_MECH_OID);
        }
        return n;
    }
    
    @Override
    public int getRemainingAcceptLifetime(Oid default_MECH_OID) throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        boolean b = false;
        int n = 0;
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        final GSSCredentialSpi gssCredentialSpi = this.hashtable.get(new SearchKey(default_MECH_OID, 2));
        if (gssCredentialSpi != null) {
            b = true;
            if (n < gssCredentialSpi.getAcceptLifetime()) {
                n = gssCredentialSpi.getAcceptLifetime();
            }
        }
        final GSSCredentialSpi gssCredentialSpi2 = this.hashtable.get(new SearchKey(default_MECH_OID, 0));
        if (gssCredentialSpi2 != null) {
            b = true;
            if (n < gssCredentialSpi2.getAcceptLifetime()) {
                n = gssCredentialSpi2.getAcceptLifetime();
            }
        }
        if (!b) {
            throw new GSSExceptionImpl(2, default_MECH_OID);
        }
        return n;
    }
    
    @Override
    public int getUsage() throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        boolean b = false;
        boolean b2 = false;
        final Enumeration<SearchKey> keys = this.hashtable.keys();
        while (keys.hasMoreElements()) {
            final SearchKey searchKey = keys.nextElement();
            if (searchKey.getUsage() == 1) {
                b = true;
            }
            else {
                if (searchKey.getUsage() != 2) {
                    return 0;
                }
                b2 = true;
            }
        }
        if (!b) {
            return 2;
        }
        if (b2) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public int getUsage(Oid default_MECH_OID) throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        boolean b = false;
        boolean b2 = false;
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        if (this.hashtable.get(new SearchKey(default_MECH_OID, 1)) != null) {
            b = true;
        }
        if (this.hashtable.get(new SearchKey(default_MECH_OID, 2)) != null) {
            b2 = true;
        }
        if (this.hashtable.get(new SearchKey(default_MECH_OID, 0)) != null) {
            b = true;
            b2 = true;
        }
        if (b && b2) {
            return 0;
        }
        if (b) {
            return 1;
        }
        if (b2) {
            return 2;
        }
        throw new GSSExceptionImpl(2, default_MECH_OID);
    }
    
    @Override
    public Oid[] getMechs() throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        final Vector vector = new Vector(this.hashtable.size());
        final Enumeration<SearchKey> keys = this.hashtable.keys();
        while (keys.hasMoreElements()) {
            vector.addElement(keys.nextElement().getMech());
        }
        return vector.toArray(new Oid[0]);
    }
    
    @Override
    public void add(final GSSName gssName, final int n, final int n2, Oid default_MECH_OID, final int n3) throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
        }
        final SearchKey searchKey = new SearchKey(default_MECH_OID, n3);
        if (this.hashtable.containsKey(searchKey)) {
            throw new GSSExceptionImpl(17, "Duplicate element found: " + getElementStr(default_MECH_OID, n3));
        }
        final GSSNameSpi gssNameSpi = (gssName == null) ? null : ((GSSNameImpl)gssName).getElement(default_MECH_OID);
        this.tempCred = this.gssManager.getCredentialElement(gssNameSpi, n, n2, default_MECH_OID, n3);
        if (this.tempCred != null) {
            if (n3 == 0 && (!this.tempCred.isAcceptorCredential() || !this.tempCred.isInitiatorCredential())) {
                int n4;
                int n5;
                if (!this.tempCred.isInitiatorCredential()) {
                    n4 = 2;
                    n5 = 1;
                }
                else {
                    n4 = 1;
                    n5 = 2;
                }
                this.hashtable.put(new SearchKey(default_MECH_OID, n4), this.tempCred);
                this.tempCred = this.gssManager.getCredentialElement(gssNameSpi, n, n2, default_MECH_OID, n5);
                this.hashtable.put(new SearchKey(default_MECH_OID, n5), this.tempCred);
            }
            else {
                this.hashtable.put(searchKey, this.tempCred);
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        return this == o || (!(o instanceof GSSCredentialImpl) && false);
    }
    
    @Override
    public int hashCode() {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        return 1;
    }
    
    public GSSCredentialSpi getElement(Oid default_MECH_OID, final boolean b) throws GSSException {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        GSSCredentialSpi gssCredentialSpi;
        if (default_MECH_OID == null) {
            default_MECH_OID = ProviderList.DEFAULT_MECH_OID;
            gssCredentialSpi = this.hashtable.get(new SearchKey(default_MECH_OID, b ? 1 : 2));
            if (gssCredentialSpi == null) {
                gssCredentialSpi = this.hashtable.get(new SearchKey(default_MECH_OID, 0));
                if (gssCredentialSpi == null) {
                    final Object[] array = this.hashtable.entrySet().toArray();
                    for (int i = 0; i < array.length; ++i) {
                        gssCredentialSpi = (GSSCredentialSpi)((Map.Entry)array[i]).getValue();
                        if (gssCredentialSpi.isInitiatorCredential() == b) {
                            break;
                        }
                    }
                }
            }
        }
        else {
            SearchKey searchKey;
            if (b) {
                searchKey = new SearchKey(default_MECH_OID, 1);
            }
            else {
                searchKey = new SearchKey(default_MECH_OID, 2);
            }
            gssCredentialSpi = this.hashtable.get(searchKey);
            if (gssCredentialSpi == null) {
                gssCredentialSpi = this.hashtable.get(new SearchKey(default_MECH_OID, 0));
            }
        }
        if (gssCredentialSpi == null) {
            throw new GSSExceptionImpl(13, "No credential found for: " + getElementStr(default_MECH_OID, b ? 1 : 2));
        }
        return gssCredentialSpi;
    }
    
    Set<GSSCredentialSpi> getElements() {
        final HashSet set = new HashSet(this.hashtable.size());
        final Enumeration<GSSCredentialSpi> elements = this.hashtable.elements();
        while (elements.hasMoreElements()) {
            set.add(elements.nextElement());
        }
        return set;
    }
    
    private static String getElementStr(final Oid oid, final int n) {
        final String string = oid.toString();
        String s;
        if (n == 1) {
            s = string.concat(" usage: Initiate");
        }
        else if (n == 2) {
            s = string.concat(" usage: Accept");
        }
        else {
            s = string.concat(" usage: Initiate and Accept");
        }
        return s;
    }
    
    @Override
    public String toString() {
        if (this.destroyed) {
            throw new IllegalStateException("This credential is no longer valid");
        }
        final StringBuffer sb = new StringBuffer("[GSSCredential: ");
        final Object[] array = this.hashtable.entrySet().toArray();
        for (int i = 0; i < array.length; ++i) {
            try {
                sb.append('\n');
                final GSSCredentialSpi gssCredentialSpi = ((Map.Entry)array[i]).getValue();
                sb.append(gssCredentialSpi.getName());
                sb.append(' ');
                sb.append(gssCredentialSpi.getMechanism());
                sb.append(gssCredentialSpi.isInitiatorCredential() ? " Initiate" : "");
                sb.append(gssCredentialSpi.isAcceptorCredential() ? " Accept" : "");
                sb.append(" [");
                sb.append(gssCredentialSpi.getClass());
                sb.append(']');
            }
            catch (final GSSException ex) {}
        }
        sb.append(']');
        return sb.toString();
    }
    
    static class SearchKey
    {
        private Oid mechOid;
        private int usage;
        
        public SearchKey(final Oid mechOid, final int usage) {
            this.mechOid = null;
            this.usage = 0;
            this.mechOid = mechOid;
            this.usage = usage;
        }
        
        public Oid getMech() {
            return this.mechOid;
        }
        
        public int getUsage() {
            return this.usage;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof SearchKey)) {
                return false;
            }
            final SearchKey searchKey = (SearchKey)o;
            return this.mechOid.equals(searchKey.mechOid) && this.usage == searchKey.usage;
        }
        
        @Override
        public int hashCode() {
            return this.mechOid.hashCode();
        }
    }
}
