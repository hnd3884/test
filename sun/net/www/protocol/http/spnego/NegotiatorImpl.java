package sun.net.www.protocol.http.spnego;

import sun.security.action.GetBooleanAction;
import java.io.IOException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import com.sun.security.jgss.ExtendedGSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSName;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.GSSManagerImpl;
import sun.security.jgss.HttpCaller;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.jgss.GSSUtil;
import sun.net.www.protocol.http.HttpCallerInfo;
import org.ietf.jgss.GSSContext;
import sun.net.www.protocol.http.Negotiator;

public class NegotiatorImpl extends Negotiator
{
    private static final boolean DEBUG;
    private GSSContext context;
    private byte[] oneToken;
    
    private void init(final HttpCallerInfo httpCallerInfo) throws GSSException {
        Oid oid;
        if (httpCallerInfo.scheme.equalsIgnoreCase("Kerberos")) {
            oid = GSSUtil.GSS_KRB5_MECH_OID;
        }
        else if (AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("http.auth.preference", "spnego");
            }
        }).equalsIgnoreCase("kerberos")) {
            oid = GSSUtil.GSS_KRB5_MECH_OID;
        }
        else {
            oid = GSSUtil.GSS_SPNEGO_MECH_OID;
        }
        final GSSManagerImpl gssManagerImpl = new GSSManagerImpl(new HttpCaller(httpCallerInfo));
        this.context = gssManagerImpl.createContext(gssManagerImpl.createName("HTTP@" + httpCallerInfo.host.toLowerCase(), GSSName.NT_HOSTBASED_SERVICE), oid, null, 0);
        if (this.context instanceof ExtendedGSSContext) {
            ((ExtendedGSSContext)this.context).requestDelegPolicy(true);
        }
        this.oneToken = this.context.initSecContext(new byte[0], 0, 0);
    }
    
    public NegotiatorImpl(final HttpCallerInfo httpCallerInfo) throws IOException {
        try {
            this.init(httpCallerInfo);
        }
        catch (final GSSException ex) {
            if (NegotiatorImpl.DEBUG) {
                System.out.println("Negotiate support not initiated, will fallback to other scheme if allowed. Reason:");
                ex.printStackTrace();
            }
            final IOException ex2 = new IOException("Negotiate support not initiated");
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    public byte[] firstToken() {
        return this.oneToken;
    }
    
    @Override
    public byte[] nextToken(final byte[] array) throws IOException {
        try {
            return this.context.initSecContext(array, 0, array.length);
        }
        catch (final GSSException ex) {
            if (NegotiatorImpl.DEBUG) {
                System.out.println("Negotiate support cannot continue. Reason:");
                ex.printStackTrace();
            }
            final IOException ex2 = new IOException("Negotiate support cannot continue");
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    static {
        DEBUG = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.security.krb5.debug"));
    }
}
