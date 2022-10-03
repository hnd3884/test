package com.microsoft.sqlserver.jdbc;

import javax.security.auth.login.Configuration;
import java.security.PrivilegedExceptionAction;
import java.security.AccessControlContext;
import org.ietf.jgss.GSSName;
import java.security.PrivilegedActionException;
import org.ietf.jgss.GSSException;
import javax.security.auth.login.LoginException;
import java.text.MessageFormat;
import java.util.logging.Level;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import java.security.AccessController;
import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import javax.security.auth.login.LoginContext;
import org.ietf.jgss.GSSManager;
import java.util.logging.Logger;

final class KerbAuthentication extends SSPIAuthentication
{
    private static final Logger authLogger;
    private final SQLServerConnection con;
    private final String spn;
    private final GSSManager manager;
    private LoginContext lc;
    private boolean isUserCreatedCredential;
    private GSSCredential peerCredentials;
    private GSSContext peerContext;
    
    private void intAuthInit() throws SQLServerException {
        try {
            final Oid kerberos = new Oid("1.2.840.113554.1.2.2");
            final GSSName remotePeerName = this.manager.createName(this.spn, null);
            if (null != this.peerCredentials) {
                (this.peerContext = this.manager.createContext(remotePeerName, kerberos, this.peerCredentials, 0)).requestCredDeleg(false);
                this.peerContext.requestMutualAuth(true);
                this.peerContext.requestInteg(true);
            }
            else {
                final String configName = this.con.activeConnectionProperties.getProperty(SQLServerDriverStringProperty.JAAS_CONFIG_NAME.toString(), SQLServerDriverStringProperty.JAAS_CONFIG_NAME.getDefaultValue());
                final KerbCallback callback = new KerbCallback(this.con);
                Subject currentSubject;
                try {
                    final AccessControlContext context = AccessController.getContext();
                    currentSubject = Subject.getSubject(context);
                    if (null == currentSubject) {
                        (this.lc = new LoginContext(configName, callback)).login();
                        currentSubject = this.lc.getSubject();
                    }
                }
                catch (final LoginException le) {
                    if (KerbAuthentication.authLogger.isLoggable(Level.FINE)) {
                        KerbAuthentication.authLogger.fine(this.toString() + "Failed to login using Kerberos due to " + le.getClass().getName() + ":" + le.getMessage());
                    }
                    try {
                        this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"), le);
                    }
                    catch (final SQLServerException alwaysTriggered) {
                        String message = MessageFormat.format(SQLServerException.getErrString("R_kerberosLoginFailed"), alwaysTriggered.getMessage(), le.getClass().getName(), le.getMessage());
                        if (callback.getUsernameRequested() != null) {
                            message = MessageFormat.format(SQLServerException.getErrString("R_kerberosLoginFailedForUsername"), callback.getUsernameRequested(), message);
                        }
                        throw new SQLServerException(message, alwaysTriggered.getSQLState(), 18456, le);
                    }
                    return;
                }
                if (KerbAuthentication.authLogger.isLoggable(Level.FINER)) {
                    KerbAuthentication.authLogger.finer(this.toString() + " Getting client credentials");
                }
                this.peerCredentials = getClientCredential(currentSubject, this.manager, kerberos);
                if (KerbAuthentication.authLogger.isLoggable(Level.FINER)) {
                    KerbAuthentication.authLogger.finer(this.toString() + " creating security context");
                }
                (this.peerContext = this.manager.createContext(remotePeerName, kerberos, this.peerCredentials, 0)).requestCredDeleg(true);
                this.peerContext.requestMutualAuth(true);
                this.peerContext.requestInteg(true);
            }
        }
        catch (final GSSException ge) {
            if (KerbAuthentication.authLogger.isLoggable(Level.FINER)) {
                KerbAuthentication.authLogger.finer(this.toString() + "initAuthInit failed GSSException:-" + ge);
            }
            this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"), ge);
        }
        catch (final PrivilegedActionException ge2) {
            if (KerbAuthentication.authLogger.isLoggable(Level.FINER)) {
                KerbAuthentication.authLogger.finer(this.toString() + "initAuthInit failed privileged exception:-" + ge2);
            }
            this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"), ge2);
        }
    }
    
    private static GSSCredential getClientCredential(final Subject subject, final GSSManager gssManager, final Oid kerboid) throws PrivilegedActionException {
        final PrivilegedExceptionAction<GSSCredential> action = new PrivilegedExceptionAction<GSSCredential>() {
            @Override
            public GSSCredential run() throws GSSException {
                return gssManager.createCredential(null, 0, kerboid, 1);
            }
        };
        final Object credential = Subject.doAs(subject, action);
        return (GSSCredential)credential;
    }
    
    private byte[] intAuthHandShake(final byte[] pin, final boolean[] done) throws SQLServerException {
        try {
            if (KerbAuthentication.authLogger.isLoggable(Level.FINER)) {
                KerbAuthentication.authLogger.finer(this.toString() + " Sending token to server over secure context");
            }
            final byte[] byteToken = this.peerContext.initSecContext(pin, 0, pin.length);
            if (this.peerContext.isEstablished()) {
                done[0] = true;
                if (KerbAuthentication.authLogger.isLoggable(Level.FINER)) {
                    KerbAuthentication.authLogger.finer(this.toString() + "Authentication done.");
                }
            }
            else if (null == byteToken) {
                if (KerbAuthentication.authLogger.isLoggable(Level.INFO)) {
                    KerbAuthentication.authLogger.info(this.toString() + "byteToken is null in initSecContext.");
                }
                this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"));
            }
            return byteToken;
        }
        catch (final GSSException ge) {
            if (KerbAuthentication.authLogger.isLoggable(Level.FINER)) {
                KerbAuthentication.authLogger.finer(this.toString() + "initSecContext Failed :-" + ge);
            }
            this.con.terminate(0, SQLServerException.getErrString("R_integratedAuthenticationFailed"), ge);
            return null;
        }
    }
    
    KerbAuthentication(final SQLServerConnection con, final String address, final int port) {
        this.manager = GSSManager.getInstance();
        this.lc = null;
        this.isUserCreatedCredential = false;
        this.peerCredentials = null;
        this.peerContext = null;
        this.con = con;
        this.spn = ((null != con) ? this.getSpn(con) : null);
    }
    
    KerbAuthentication(final SQLServerConnection con, final String address, final int port, final GSSCredential impersonatedUserCred, final boolean isUserCreated) {
        this(con, address, port);
        this.peerCredentials = impersonatedUserCred;
        this.isUserCreatedCredential = isUserCreated;
    }
    
    @Override
    byte[] generateClientContext(final byte[] pin, final boolean[] done) throws SQLServerException {
        if (null == this.peerContext) {
            this.intAuthInit();
        }
        return this.intAuthHandShake(pin, done);
    }
    
    @Override
    void releaseClientContext() {
        try {
            if (null != this.peerCredentials && !this.isUserCreatedCredential) {
                this.peerCredentials.dispose();
            }
            else if (null != this.peerCredentials && this.isUserCreatedCredential) {
                this.peerCredentials = null;
            }
            if (null != this.peerContext) {
                this.peerContext.dispose();
            }
            if (null != this.lc) {
                this.lc.logout();
            }
        }
        catch (final LoginException e) {
            if (KerbAuthentication.authLogger.isLoggable(Level.FINE)) {
                KerbAuthentication.authLogger.fine(this.toString() + " Release of the credentials failed LoginException: " + e);
            }
        }
        catch (final GSSException e2) {
            if (KerbAuthentication.authLogger.isLoggable(Level.FINE)) {
                KerbAuthentication.authLogger.fine(this.toString() + " Release of the credentials failed GSSException: " + e2);
            }
        }
    }
    
    static {
        authLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.KerbAuthentication");
        Configuration.setConfiguration(new JaasConfiguration(Configuration.getConfiguration()));
    }
}
