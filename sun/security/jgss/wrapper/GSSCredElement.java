package sun.security.jgss.wrapper;

import sun.security.jgss.spi.GSSNameSpi;
import java.security.Provider;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSException;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSCredentialSpi;

public class GSSCredElement implements GSSCredentialSpi
{
    private int usage;
    long pCred;
    private GSSNameElement name;
    private GSSLibStub cStub;
    
    void doServicePermCheck() throws GSSException {
        if (GSSUtil.isKerberosMech(this.cStub.getMech()) && System.getSecurityManager() != null) {
            if (this.isInitiatorCredential()) {
                Krb5Util.checkServicePermission(Krb5Util.getTGSName(this.name), "initiate");
            }
            if (this.isAcceptorCredential() && this.name != GSSNameElement.DEF_ACCEPTOR) {
                Krb5Util.checkServicePermission(this.name.getKrbName(), "accept");
            }
        }
    }
    
    GSSCredElement(final long pCred, final GSSNameElement name, final Oid oid) throws GSSException {
        this.name = null;
        this.pCred = pCred;
        this.cStub = GSSLibStub.getInstance(oid);
        this.usage = 1;
        this.name = name;
    }
    
    GSSCredElement(final GSSNameElement name, final int n, final int usage, final GSSLibStub cStub) throws GSSException {
        this.name = null;
        this.cStub = cStub;
        this.usage = usage;
        if (name != null) {
            this.name = name;
            this.doServicePermCheck();
            this.pCred = this.cStub.acquireCred(this.name.pName, n, usage);
        }
        else {
            this.pCred = this.cStub.acquireCred(0L, n, usage);
            this.name = new GSSNameElement(this.cStub.getCredName(this.pCred), this.cStub);
            this.doServicePermCheck();
        }
    }
    
    @Override
    public Provider getProvider() {
        return SunNativeProvider.INSTANCE;
    }
    
    @Override
    public void dispose() throws GSSException {
        this.name = null;
        if (this.pCred != 0L) {
            this.pCred = this.cStub.releaseCred(this.pCred);
        }
    }
    
    @Override
    public GSSNameElement getName() throws GSSException {
        return (this.name == GSSNameElement.DEF_ACCEPTOR) ? null : this.name;
    }
    
    @Override
    public int getInitLifetime() throws GSSException {
        if (this.isInitiatorCredential()) {
            return this.cStub.getCredTime(this.pCred);
        }
        return 0;
    }
    
    @Override
    public int getAcceptLifetime() throws GSSException {
        if (this.isAcceptorCredential()) {
            return this.cStub.getCredTime(this.pCred);
        }
        return 0;
    }
    
    @Override
    public boolean isInitiatorCredential() {
        return this.usage != 2;
    }
    
    @Override
    public boolean isAcceptorCredential() {
        return this.usage != 1;
    }
    
    @Override
    public Oid getMechanism() {
        return this.cStub.getMech();
    }
    
    @Override
    public String toString() {
        return "N/A";
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.dispose();
    }
    
    @Override
    public GSSCredentialSpi impersonate(final GSSNameSpi gssNameSpi) throws GSSException {
        throw new GSSException(11, -1, "Not supported yet");
    }
}
