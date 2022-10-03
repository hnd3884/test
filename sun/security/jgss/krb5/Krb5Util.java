package sun.security.jgss.krb5;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import javax.security.auth.kerberos.KeyTab;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KerberosSecrets;
import javax.security.auth.kerberos.KerberosPrincipal;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.Credentials;
import javax.security.auth.login.LoginException;
import sun.security.jgss.GSSUtil;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosTicket;
import java.security.AccessControlContext;
import sun.security.jgss.GSSCaller;

public class Krb5Util
{
    static final boolean DEBUG;
    
    private Krb5Util() {
    }
    
    public static KerberosTicket getTicketFromSubjectAndTgs(final GSSCaller gssCaller, final String s, final String s2, final String s3, final AccessControlContext accessControlContext) throws LoginException, KrbException, IOException {
        final Subject subject = Subject.getSubject(accessControlContext);
        KerberosTicket credsToTicket = SubjectComber.find(subject, s2, s, KerberosTicket.class);
        if (credsToTicket != null) {
            return credsToTicket;
        }
        Subject login = null;
        if (!GSSUtil.useSubjectCredsOnly(gssCaller)) {
            try {
                login = GSSUtil.login(gssCaller, GSSUtil.GSS_KRB5_MECH_OID);
                credsToTicket = SubjectComber.find(login, s2, s, KerberosTicket.class);
                if (credsToTicket != null) {
                    return credsToTicket;
                }
            }
            catch (final LoginException ex) {}
        }
        KerberosTicket kerberosTicket = SubjectComber.find(subject, s3, s, KerberosTicket.class);
        boolean b;
        if (kerberosTicket == null && login != null) {
            kerberosTicket = SubjectComber.find(login, s3, s, KerberosTicket.class);
            b = false;
        }
        else {
            b = true;
        }
        if (kerberosTicket != null) {
            final Credentials acquireServiceCreds = Credentials.acquireServiceCreds(s2, ticketToCreds(kerberosTicket));
            if (acquireServiceCreds != null) {
                credsToTicket = credsToTicket(acquireServiceCreds);
                if (b && subject != null && !subject.isReadOnly()) {
                    subject.getPrivateCredentials().add(credsToTicket);
                }
            }
        }
        return credsToTicket;
    }
    
    static KerberosTicket getServiceTicket(final GSSCaller gssCaller, final String s, final String s2, final AccessControlContext accessControlContext) throws LoginException {
        return SubjectComber.find(Subject.getSubject(accessControlContext), s2, s, KerberosTicket.class);
    }
    
    static KerberosTicket getInitialTicket(final GSSCaller gssCaller, final String s, final AccessControlContext accessControlContext) throws LoginException {
        KerberosTicket kerberosTicket = SubjectComber.find(Subject.getSubject(accessControlContext), null, s, KerberosTicket.class);
        if (kerberosTicket == null && !GSSUtil.useSubjectCredsOnly(gssCaller)) {
            kerberosTicket = SubjectComber.find(GSSUtil.login(gssCaller, GSSUtil.GSS_KRB5_MECH_OID), null, s, KerberosTicket.class);
        }
        return kerberosTicket;
    }
    
    public static Subject getSubject(final GSSCaller gssCaller, final AccessControlContext accessControlContext) throws LoginException {
        Subject subject = Subject.getSubject(accessControlContext);
        if (subject == null && !GSSUtil.useSubjectCredsOnly(gssCaller)) {
            subject = GSSUtil.login(gssCaller, GSSUtil.GSS_KRB5_MECH_OID);
        }
        return subject;
    }
    
    public static ServiceCreds getServiceCreds(final GSSCaller gssCaller, final String s, final AccessControlContext accessControlContext) throws LoginException {
        final Subject subject = Subject.getSubject(accessControlContext);
        ServiceCreds serviceCreds = null;
        if (subject != null) {
            serviceCreds = ServiceCreds.getInstance(subject, s);
        }
        if (serviceCreds == null && !GSSUtil.useSubjectCredsOnly(gssCaller)) {
            serviceCreds = ServiceCreds.getInstance(GSSUtil.login(gssCaller, GSSUtil.GSS_KRB5_MECH_OID), s);
        }
        return serviceCreds;
    }
    
    public static KerberosTicket credsToTicket(final Credentials credentials) {
        final EncryptionKey sessionKey = credentials.getSessionKey();
        final KerberosTicket kerberosTicket = new KerberosTicket(credentials.getEncoded(), new KerberosPrincipal(credentials.getClient().getName()), new KerberosPrincipal(credentials.getServer().getName(), 2), sessionKey.getBytes(), sessionKey.getEType(), credentials.getFlags(), credentials.getAuthTime(), credentials.getStartTime(), credentials.getEndTime(), credentials.getRenewTill(), credentials.getClientAddresses());
        final PrincipalName clientAlias = credentials.getClientAlias();
        final PrincipalName serverAlias = credentials.getServerAlias();
        if (clientAlias != null) {
            KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketSetClientAlias(kerberosTicket, new KerberosPrincipal(clientAlias.getName(), clientAlias.getNameType()));
        }
        if (serverAlias != null) {
            KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketSetServerAlias(kerberosTicket, new KerberosPrincipal(serverAlias.getName(), serverAlias.getNameType()));
        }
        return kerberosTicket;
    }
    
    public static Credentials ticketToCreds(final KerberosTicket kerberosTicket) throws KrbException, IOException {
        final KerberosPrincipal kerberosTicketGetClientAlias = KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketGetClientAlias(kerberosTicket);
        final KerberosPrincipal kerberosTicketGetServerAlias = KerberosSecrets.getJavaxSecurityAuthKerberosAccess().kerberosTicketGetServerAlias(kerberosTicket);
        return new Credentials(kerberosTicket.getEncoded(), kerberosTicket.getClient().getName(), (kerberosTicketGetClientAlias != null) ? kerberosTicketGetClientAlias.getName() : null, kerberosTicket.getServer().getName(), (kerberosTicketGetServerAlias != null) ? kerberosTicketGetServerAlias.getName() : null, kerberosTicket.getSessionKey().getEncoded(), kerberosTicket.getSessionKeyType(), kerberosTicket.getFlags(), kerberosTicket.getAuthTime(), kerberosTicket.getStartTime(), kerberosTicket.getEndTime(), kerberosTicket.getRenewTill(), kerberosTicket.getClientAddresses());
    }
    
    public static sun.security.krb5.internal.ktab.KeyTab snapshotFromJavaxKeyTab(final KeyTab keyTab) {
        return KerberosSecrets.getJavaxSecurityAuthKerberosAccess().keyTabTakeSnapshot(keyTab);
    }
    
    public static EncryptionKey[] keysFromJavaxKeyTab(final KeyTab keyTab, final PrincipalName principalName) {
        return snapshotFromJavaxKeyTab(keyTab).readServiceKeys(principalName);
    }
    
    static {
        DEBUG = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.security.krb5.debug"));
    }
}
