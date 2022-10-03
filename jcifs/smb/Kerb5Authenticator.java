package jcifs.smb;

import java.security.Key;
import org.ietf.jgss.GSSException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.security.auth.Subject;

public class Kerb5Authenticator implements SmbExtendedAuthenticator
{
    public static final String FLAGS2 = "55301";
    public static final String CAPABILITIES = "-2147483436";
    private static final String SERVICE = "cifs";
    private Subject subject;
    private String user;
    private String service;
    private int userLifetime;
    private int contextLifetime;
    
    public Kerb5Authenticator(final Subject subject) {
        this.subject = null;
        this.user = null;
        this.service = "cifs";
        this.userLifetime = 0;
        this.contextLifetime = 0;
        this.subject = subject;
    }
    
    public void setUser(final String name) {
        this.user = name;
    }
    
    public Subject getSubject() {
        return this.subject;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public void setService(final String name) {
        this.service = name;
    }
    
    public String getService() {
        return this.service;
    }
    
    public int getUserLifeTime() {
        return this.userLifetime;
    }
    
    public void setUserLifeTime(final int time) {
        this.userLifetime = time;
    }
    
    public int getLifeTime() {
        return this.contextLifetime;
    }
    
    public void setLifeTime(final int time) {
        this.contextLifetime = time;
    }
    
    public void sessionSetup(final SmbSession session, final ServerMessageBlock andx, final ServerMessageBlock andxResponse) throws SmbException {
        try {
            Subject.doAs(this.subject, (PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws Exception {
                    Kerb5Authenticator.this.setup(session, andx, andxResponse);
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException e) {
            if (e.getException() instanceof SmbException) {
                throw (SmbException)e.getException();
            }
            throw new SmbException(e.getMessage(), e.getException());
        }
    }
    
    private void setup(final SmbSession session, final ServerMessageBlock andx, final ServerMessageBlock andxResponse) throws SmbAuthException, SmbException {
        Kerb5Context context = null;
        SpnegoContext spnego = null;
        try {
            String host = session.transport.address.getHostAddress();
            try {
                host = session.transport.address.getHostName();
            }
            catch (final Throwable t) {}
            context = this.createContext(host);
            spnego = new SpnegoContext(context.getGSSContext());
            byte[] token = new byte[0];
            Kerb5SessionSetupAndX request = null;
            Kerb5SessionSetupAndXResponse response = null;
            while (!spnego.isEstablished()) {
                token = spnego.initSecContext(token, 0, token.length);
                if (token != null) {
                    request = new Kerb5SessionSetupAndX(session, null);
                    request.getSecurityBlob().set(token);
                    response = new Kerb5SessionSetupAndXResponse(andxResponse);
                    if (session.transport.digest == null && (session.transport.server.signaturesRequired || (session.transport.server.signaturesEnabled && SmbConstants.SIGNPREF))) {
                        final Key key = context.searchSessionKey(this.subject);
                        if (key == null) {
                            throw new SmbException("Not found the session key.");
                        }
                        request.digest = new SigningDigest(key.getEncoded());
                    }
                    session.transport.send(request, response);
                    session.transport.digest = request.digest;
                    token = response.getSecurityBlob().get();
                }
            }
            session.setUid(response.uid);
            session.setSessionSetup(true);
        }
        catch (final GSSException e) {
            e.printStackTrace();
            throw new SmbException(e.getMessage());
        }
        finally {
            if (context != null) {
                try {
                    context.dispose();
                }
                catch (final GSSException ex) {}
            }
        }
    }
    
    private Kerb5Context createContext(final String host) throws GSSException {
        final Kerb5Context kerb5Context = new Kerb5Context(host, this.service, this.user, this.userLifetime, this.contextLifetime);
        kerb5Context.getGSSContext().requestAnonymity(false);
        kerb5Context.getGSSContext().requestSequenceDet(false);
        kerb5Context.getGSSContext().requestMutualAuth(false);
        kerb5Context.getGSSContext().requestConf(false);
        kerb5Context.getGSSContext().requestInteg(false);
        kerb5Context.getGSSContext().requestReplayDet(false);
        return kerb5Context;
    }
    
    public boolean equals(final Object arg0) {
        return false;
    }
}
