package sun.security.jgss.spnego;

import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSNameSpi;
import java.security.Provider;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSException;
import sun.security.jgss.spi.GSSCredentialSpi;

public class SpNegoCredElement implements GSSCredentialSpi
{
    private GSSCredentialSpi cred;
    
    public SpNegoCredElement(final GSSCredentialSpi cred) throws GSSException {
        this.cred = null;
        this.cred = cred;
    }
    
    Oid getInternalMech() {
        return this.cred.getMechanism();
    }
    
    public GSSCredentialSpi getInternalCred() {
        return this.cred;
    }
    
    @Override
    public Provider getProvider() {
        return SpNegoMechFactory.PROVIDER;
    }
    
    @Override
    public void dispose() throws GSSException {
        this.cred.dispose();
    }
    
    @Override
    public GSSNameSpi getName() throws GSSException {
        return this.cred.getName();
    }
    
    @Override
    public int getInitLifetime() throws GSSException {
        return this.cred.getInitLifetime();
    }
    
    @Override
    public int getAcceptLifetime() throws GSSException {
        return this.cred.getAcceptLifetime();
    }
    
    @Override
    public boolean isInitiatorCredential() throws GSSException {
        return this.cred.isInitiatorCredential();
    }
    
    @Override
    public boolean isAcceptorCredential() throws GSSException {
        return this.cred.isAcceptorCredential();
    }
    
    @Override
    public Oid getMechanism() {
        return GSSUtil.GSS_SPNEGO_MECH_OID;
    }
    
    @Override
    public GSSCredentialSpi impersonate(final GSSNameSpi gssNameSpi) throws GSSException {
        return this.cred.impersonate(gssNameSpi);
    }
}
