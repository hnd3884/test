package sun.security.ssl.krb5;

import java.security.Principal;
import java.security.PrivilegedActionException;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.jgss.GSSCaller;
import java.security.PrivilegedExceptionAction;
import java.security.Permission;
import javax.security.auth.kerberos.ServicePermission;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Arrays;
import javax.net.ssl.SSLKeyException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.internal.EncTicketPart;
import sun.security.krb5.KrbException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.security.auth.kerberos.KerberosKey;
import sun.security.ssl.SSLLogger;
import sun.security.ssl.Krb5Helper;
import sun.security.jgss.krb5.ServiceCreds;
import sun.security.krb5.internal.Ticket;
import java.io.IOException;
import javax.security.auth.kerberos.KerberosTicket;
import sun.security.krb5.EncryptionKey;
import java.security.AccessControlContext;
import javax.security.auth.kerberos.KerberosPrincipal;
import sun.security.ssl.KrbClientKeyExchangeHelper;

public final class KrbClientKeyExchangeHelperImpl implements KrbClientKeyExchangeHelper
{
    private byte[] preMaster;
    private byte[] preMasterEnc;
    private byte[] encodedTicket;
    private KerberosPrincipal peerPrincipal;
    private KerberosPrincipal localPrincipal;
    
    @Override
    public void init(final byte[] preMaster, final String s, final AccessControlContext accessControlContext) throws IOException {
        this.preMaster = preMaster;
        final KerberosTicket serviceTicket = getServiceTicket(s, accessControlContext);
        this.encodedTicket = serviceTicket.getEncoded();
        this.peerPrincipal = serviceTicket.getServer();
        this.localPrincipal = serviceTicket.getClient();
        this.encryptPremasterSecret(new EncryptionKey(serviceTicket.getSessionKeyType(), serviceTicket.getSessionKey().getEncoded()));
    }
    
    @Override
    public void init(final byte[] encodedTicket, final byte[] preMasterEnc, final Object o, final AccessControlContext accessControlContext) throws IOException {
        this.encodedTicket = encodedTicket;
        this.preMasterEnc = preMasterEnc;
        EncryptionKey key2;
        try {
            final Ticket ticket = new Ticket(encodedTicket);
            final EncryptedData encPart = ticket.encPart;
            final PrincipalName sname = ticket.sname;
            final ServiceCreds serviceCreds = (ServiceCreds)o;
            final KerberosPrincipal kerberosPrincipal = new KerberosPrincipal(sname.toString());
            if (serviceCreds.getName() == null) {
                final SecurityManager securityManager = System.getSecurityManager();
                try {
                    if (securityManager != null) {
                        securityManager.checkPermission(Krb5Helper.getServicePermission(sname.toString(), "accept"), accessControlContext);
                    }
                }
                catch (final SecurityException ex) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                        SSLLogger.fine("Permission to access Kerberos secret key denied", new Object[0]);
                    }
                    throw new IOException("Kerberos service not allowed");
                }
            }
            final KerberosKey[] array = AccessController.doPrivileged((PrivilegedAction<KerberosKey[]>)new PrivilegedAction<KerberosKey[]>() {
                @Override
                public KerberosKey[] run() {
                    return serviceCreds.getKKeys(kerberosPrincipal);
                }
            });
            if (array.length == 0) {
                throw new IOException("Found no key for " + kerberosPrincipal + ((serviceCreds.getName() == null) ? "" : (", this keytab is for " + serviceCreds.getName() + " only")));
            }
            final int eType = encPart.getEType();
            final Integer keyVersionNumber = encPart.getKeyVersionNumber();
            KerberosKey key;
            try {
                key = findKey(eType, keyVersionNumber, array);
            }
            catch (final KrbException ex2) {
                throw new IOException("Cannot find key matching version number", ex2);
            }
            if (key == null) {
                throw new IOException("Cannot find key of appropriate type to decrypt ticket - need etype " + eType);
            }
            final EncTicketPart encTicketPart = new EncTicketPart(encPart.reset(encPart.decrypt(new EncryptionKey(eType, key.getEncoded()), 2)));
            this.peerPrincipal = new KerberosPrincipal(encTicketPart.cname.getName());
            this.localPrincipal = new KerberosPrincipal(sname.getName());
            key2 = encTicketPart.key;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("server principal: " + sname, new Object[0]);
                SSLLogger.fine("cname: " + encTicketPart.cname.toString(), new Object[0]);
            }
        }
        catch (final Exception ex3) {
            key2 = null;
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Error getting the Kerberos session key to decrypt the pre-master secret", new Object[0]);
            }
        }
        if (key2 != null) {
            this.decryptPremasterSecret(key2);
        }
    }
    
    @Override
    public byte[] getEncodedTicket() {
        return this.encodedTicket;
    }
    
    @Override
    public byte[] getEncryptedPreMasterSecret() {
        return this.preMasterEnc;
    }
    
    @Override
    public byte[] getPlainPreMasterSecret() {
        return this.preMaster;
    }
    
    @Override
    public KerberosPrincipal getPeerPrincipal() {
        return this.peerPrincipal;
    }
    
    @Override
    public KerberosPrincipal getLocalPrincipal() {
        return this.localPrincipal;
    }
    
    private void encryptPremasterSecret(final EncryptionKey encryptionKey) throws IOException {
        if (encryptionKey.getEType() == 16) {
            throw new IOException("session keys with des3-cbc-hmac-sha1-kd encryption type are not supported for TLS Kerberos cipher suites");
        }
        try {
            this.preMasterEnc = new EncryptedData(encryptionKey, this.preMaster, 0).getBytes();
        }
        catch (final KrbException ex) {
            throw (IOException)new SSLKeyException("Kerberos pre-master secret error").initCause(ex);
        }
    }
    
    private void decryptPremasterSecret(final EncryptionKey encryptionKey) throws IOException {
        if (encryptionKey.getEType() == 16) {
            throw new IOException("session keys with des3-cbc-hmac-sha1-kd encryption type are not supported for TLS Kerberos cipher suites");
        }
        try {
            final EncryptedData encryptedData = new EncryptedData(encryptionKey.getEType(), null, this.preMasterEnc);
            byte[] preMaster = encryptedData.decrypt(encryptionKey, 0);
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake") && this.preMasterEnc != null) {
                SSLLogger.fine("decrypted premaster secret", preMaster);
            }
            if (preMaster.length == 52 && encryptedData.getEType() == 1) {
                if (paddingByteIs(preMaster, 52, (byte)4) || paddingByteIs(preMaster, 52, (byte)0)) {
                    preMaster = Arrays.copyOf(preMaster, 48);
                }
            }
            else if (preMaster.length == 56 && encryptedData.getEType() == 3 && paddingByteIs(preMaster, 56, (byte)8)) {
                preMaster = Arrays.copyOf(preMaster, 48);
            }
            this.preMaster = preMaster;
        }
        catch (final Exception ex) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Error decrypting the pre-master secret", new Object[0]);
            }
        }
    }
    
    private static boolean paddingByteIs(final byte[] array, final int n, final byte b) {
        for (int i = 48; i < n; ++i) {
            if (array[i] != b) {
                return false;
            }
        }
        return true;
    }
    
    private static KerberosTicket getServiceTicket(String s, final AccessControlContext accessControlContext) throws IOException {
        if ("localhost".equals(s) || "localhost.localdomain".equals(s)) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                SSLLogger.fine("Get the local hostname", new Object[0]);
            }
            final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    try {
                        return InetAddress.getLocalHost().getHostName();
                    }
                    catch (final UnknownHostException ex) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,handshake")) {
                            SSLLogger.fine("Warning, cannot get the local hostname: " + ex.getMessage(), new Object[0]);
                        }
                        return null;
                    }
                }
            });
            if (s2 != null) {
                s = s2;
            }
        }
        final String string = "host/" + s;
        PrincipalName principalName;
        try {
            principalName = new PrincipalName(string, 3);
        }
        catch (final SecurityException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            final IOException ex3 = new IOException("Invalid service principal name: " + string);
            ex3.initCause(ex2);
            throw ex3;
        }
        final String realmAsString = principalName.getRealmAsString();
        final String string2 = principalName.toString();
        final String string3 = "krbtgt/" + realmAsString + "@" + realmAsString;
        final String s3 = null;
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new ServicePermission(string2, "initiate"), accessControlContext);
        }
        try {
            final KerberosTicket kerberosTicket = AccessController.doPrivileged((PrivilegedExceptionAction<KerberosTicket>)new PrivilegedExceptionAction<KerberosTicket>() {
                @Override
                public KerberosTicket run() throws Exception {
                    return Krb5Util.getTicketFromSubjectAndTgs(GSSCaller.CALLER_SSL_CLIENT, s3, string2, string3, accessControlContext);
                }
            });
            if (kerberosTicket == null) {
                throw new IOException("Failed to find any kerberos service ticket for " + string2);
            }
            return kerberosTicket;
        }
        catch (final PrivilegedActionException ex4) {
            final IOException ex5 = new IOException("Attempt to obtain kerberos service ticket for " + string2 + " failed!");
            ex5.initCause(ex4);
            throw ex5;
        }
    }
    
    private static boolean versionMatches(final Integer n, final int n2) {
        return n == null || n == 0 || n2 == 0 || n.equals(n2);
    }
    
    private static KerberosKey findKey(final int n, final Integer n2, final KerberosKey[] array) throws KrbException {
        boolean b = false;
        int n3 = 0;
        KerberosKey kerberosKey = null;
        for (int i = 0; i < array.length; ++i) {
            if (n == array[i].getKeyType()) {
                final int versionNumber = array[i].getVersionNumber();
                b = true;
                if (versionMatches(n2, versionNumber)) {
                    return array[i];
                }
                if (versionNumber > n3) {
                    kerberosKey = array[i];
                    n3 = versionNumber;
                }
            }
        }
        if (n == 1 || n == 3) {
            for (int j = 0; j < array.length; ++j) {
                final int keyType = array[j].getKeyType();
                if (keyType == 1 || keyType == 3) {
                    final int versionNumber2 = array[j].getVersionNumber();
                    b = true;
                    if (versionMatches(n2, versionNumber2)) {
                        return new KerberosKey(array[j].getPrincipal(), array[j].getEncoded(), n, versionNumber2);
                    }
                    if (versionNumber2 > n3) {
                        kerberosKey = new KerberosKey(array[j].getPrincipal(), array[j].getEncoded(), n, versionNumber2);
                        n3 = versionNumber2;
                    }
                }
            }
        }
        if (b) {
            return kerberosKey;
        }
        return null;
    }
}
