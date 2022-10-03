package sun.security.jgss.spnego;

import sun.security.jgss.ProviderList;
import org.ietf.jgss.GSSName;
import sun.security.jgss.SunProvider;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.GSSCaller;
import org.ietf.jgss.GSSException;
import java.util.Vector;
import sun.security.jgss.krb5.Krb5AcceptCredential;
import sun.security.jgss.krb5.Krb5MechFactory;
import sun.security.jgss.krb5.Krb5InitCredential;
import sun.security.jgss.krb5.Krb5NameElement;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.GSSManagerImpl;
import org.ietf.jgss.Oid;
import java.security.Provider;
import sun.security.jgss.spi.MechanismFactory;

public final class SpNegoMechFactory implements MechanismFactory
{
    static final Provider PROVIDER;
    static final Oid GSS_SPNEGO_MECH_OID;
    private static Oid[] nameTypes;
    private static final Oid DEFAULT_SPNEGO_MECH_OID;
    final GSSManagerImpl manager;
    final Oid[] availableMechs;
    
    private static SpNegoCredElement getCredFromSubject(final GSSNameSpi gssNameSpi, final boolean b) throws GSSException {
        final Vector<GSSCredentialSpi> searchSubject = GSSUtil.searchSubject(gssNameSpi, SpNegoMechFactory.GSS_SPNEGO_MECH_OID, b, (Class<? extends GSSCredentialSpi>)SpNegoCredElement.class);
        final SpNegoCredElement spNegoCredElement = (searchSubject == null || searchSubject.isEmpty()) ? null : searchSubject.firstElement();
        if (spNegoCredElement != null) {
            final GSSCredentialSpi internalCred = spNegoCredElement.getInternalCred();
            if (GSSUtil.isKerberosMech(internalCred.getMechanism())) {
                if (b) {
                    Krb5MechFactory.checkInitCredPermission((Krb5NameElement)((Krb5InitCredential)internalCred).getName());
                }
                else {
                    Krb5MechFactory.checkAcceptCredPermission((Krb5NameElement)((Krb5AcceptCredential)internalCred).getName(), gssNameSpi);
                }
            }
        }
        return spNegoCredElement;
    }
    
    public SpNegoMechFactory(final GSSCaller gssCaller) {
        this.manager = new GSSManagerImpl(gssCaller, false);
        final Oid[] mechs = this.manager.getMechs();
        this.availableMechs = new Oid[mechs.length - 1];
        int i = 0;
        int n = 0;
        while (i < mechs.length) {
            if (!mechs[i].equals(SpNegoMechFactory.GSS_SPNEGO_MECH_OID)) {
                this.availableMechs[n++] = mechs[i];
            }
            ++i;
        }
        int j = 0;
        while (j < this.availableMechs.length) {
            if (this.availableMechs[j].equals(SpNegoMechFactory.DEFAULT_SPNEGO_MECH_OID)) {
                if (j != 0) {
                    this.availableMechs[j] = this.availableMechs[0];
                    this.availableMechs[0] = SpNegoMechFactory.DEFAULT_SPNEGO_MECH_OID;
                    break;
                }
                break;
            }
            else {
                ++j;
            }
        }
    }
    
    @Override
    public GSSNameSpi getNameElement(final String s, final Oid oid) throws GSSException {
        return this.manager.getNameElement(s, oid, SpNegoMechFactory.DEFAULT_SPNEGO_MECH_OID);
    }
    
    @Override
    public GSSNameSpi getNameElement(final byte[] array, final Oid oid) throws GSSException {
        return this.manager.getNameElement(array, oid, SpNegoMechFactory.DEFAULT_SPNEGO_MECH_OID);
    }
    
    @Override
    public GSSCredentialSpi getCredentialElement(final GSSNameSpi gssNameSpi, final int n, final int n2, final int n3) throws GSSException {
        SpNegoCredElement credFromSubject = getCredFromSubject(gssNameSpi, n3 != 2);
        if (credFromSubject == null) {
            credFromSubject = new SpNegoCredElement(this.manager.getCredentialElement(gssNameSpi, n, n2, null, n3));
        }
        return credFromSubject;
    }
    
    @Override
    public GSSContextSpi getMechanismContext(final GSSNameSpi gssNameSpi, GSSCredentialSpi credFromSubject, final int n) throws GSSException {
        if (credFromSubject == null) {
            credFromSubject = getCredFromSubject(null, true);
        }
        else if (!(credFromSubject instanceof SpNegoCredElement)) {
            return new SpNegoContext(this, gssNameSpi, new SpNegoCredElement(credFromSubject), n);
        }
        return new SpNegoContext(this, gssNameSpi, credFromSubject, n);
    }
    
    @Override
    public GSSContextSpi getMechanismContext(GSSCredentialSpi credFromSubject) throws GSSException {
        if (credFromSubject == null) {
            credFromSubject = getCredFromSubject(null, false);
        }
        else if (!(credFromSubject instanceof SpNegoCredElement)) {
            return new SpNegoContext(this, new SpNegoCredElement(credFromSubject));
        }
        return new SpNegoContext(this, credFromSubject);
    }
    
    @Override
    public GSSContextSpi getMechanismContext(final byte[] array) throws GSSException {
        return new SpNegoContext(this, array);
    }
    
    @Override
    public final Oid getMechanismOid() {
        return SpNegoMechFactory.GSS_SPNEGO_MECH_OID;
    }
    
    @Override
    public Provider getProvider() {
        return SpNegoMechFactory.PROVIDER;
    }
    
    @Override
    public Oid[] getNameTypes() {
        return SpNegoMechFactory.nameTypes;
    }
    
    static {
        PROVIDER = new SunProvider();
        GSS_SPNEGO_MECH_OID = GSSUtil.createOid("1.3.6.1.5.5.2");
        SpNegoMechFactory.nameTypes = new Oid[] { GSSName.NT_USER_NAME, GSSName.NT_HOSTBASED_SERVICE, GSSName.NT_EXPORT_NAME };
        DEFAULT_SPNEGO_MECH_OID = (ProviderList.DEFAULT_MECH_OID.equals(SpNegoMechFactory.GSS_SPNEGO_MECH_OID) ? GSSUtil.GSS_KRB5_MECH_OID : ProviderList.DEFAULT_MECH_OID);
    }
}
