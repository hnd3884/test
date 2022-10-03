package org.openjsse.legacy8ujsse.sun.security.ssl.krb5;

import java.security.Principal;
import java.security.PrivilegedActionException;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.jgss.GSSCaller;
import java.security.PrivilegedExceptionAction;
import java.security.Permission;
import javax.security.auth.kerberos.ServicePermission;
import java.net.UnknownHostException;
import org.openjsse.legacy8ujsse.sun.security.ssl.HandshakeMessage;
import java.net.InetAddress;
import java.io.PrintStream;
import org.openjsse.legacy8ujsse.sun.security.ssl.HandshakeOutStream;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.internal.EncTicketPart;
import sun.security.krb5.KrbException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.security.auth.kerberos.KerberosKey;
import org.openjsse.legacy8ujsse.sun.security.ssl.Krb5Helper;
import sun.security.jgss.krb5.ServiceCreds;
import sun.security.krb5.internal.Ticket;
import org.openjsse.legacy8ujsse.sun.security.ssl.Debug;
import org.openjsse.legacy8ujsse.sun.security.ssl.HandshakeInStream;
import java.io.IOException;
import javax.security.auth.kerberos.KerberosTicket;
import sun.security.krb5.EncryptionKey;
import java.security.SecureRandom;
import org.openjsse.legacy8ujsse.sun.security.ssl.ProtocolVersion;
import java.security.AccessControlContext;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.openjsse.legacy8ujsse.sun.security.ssl.KerberosClientKeyExchange;

public final class KerberosClientKeyExchangeImpl extends KerberosClientKeyExchange
{
    private KerberosPreMasterSecret preMaster;
    private byte[] encodedTicket;
    private KerberosPrincipal peerPrincipal;
    private KerberosPrincipal localPrincipal;
    
    @Override
    public void init(final String serverName, final AccessControlContext acc, final ProtocolVersion protocolVersion, final SecureRandom rand) throws IOException {
        final KerberosTicket ticket = getServiceTicket(serverName, acc);
        this.encodedTicket = ticket.getEncoded();
        this.peerPrincipal = ticket.getServer();
        this.localPrincipal = ticket.getClient();
        final EncryptionKey sessionKey = new EncryptionKey(ticket.getSessionKeyType(), ticket.getSessionKey().getEncoded());
        this.preMaster = new KerberosPreMasterSecret(protocolVersion, rand, sessionKey);
    }
    
    @Override
    public void init(final ProtocolVersion protocolVersion, final ProtocolVersion clientVersion, final SecureRandom rand, final HandshakeInStream input, final AccessControlContext acc, Object serviceCreds) throws IOException {
        this.encodedTicket = input.getBytes16();
        if (KerberosClientKeyExchangeImpl.debug != null && Debug.isOn("verbose")) {
            Debug.println(System.out, "encoded Kerberos service ticket", this.encodedTicket);
        }
        EncryptionKey sessionKey = null;
        try {
            final Ticket t = new Ticket(this.encodedTicket);
            final EncryptedData encPart = t.encPart;
            final PrincipalName ticketSname = t.sname;
            final ServiceCreds creds = (ServiceCreds)serviceCreds;
            final KerberosPrincipal princ = new KerberosPrincipal(ticketSname.toString());
            if (creds.getName() == null) {
                final SecurityManager sm = System.getSecurityManager();
                try {
                    if (sm != null) {
                        sm.checkPermission(Krb5Helper.getServicePermission(ticketSname.toString(), "accept"), acc);
                    }
                }
                catch (final SecurityException se) {
                    serviceCreds = null;
                    if (KerberosClientKeyExchangeImpl.debug != null && Debug.isOn("handshake")) {
                        System.out.println("Permission to access Kerberos secret key denied");
                    }
                    throw new IOException("Kerberos service not allowedy");
                }
            }
            final KerberosKey[] serverKeys = AccessController.doPrivileged((PrivilegedAction<KerberosKey[]>)new PrivilegedAction<KerberosKey[]>() {
                @Override
                public KerberosKey[] run() {
                    return creds.getKKeys(princ);
                }
            });
            if (serverKeys.length == 0) {
                throw new IOException("Found no key for " + princ + ((creds.getName() == null) ? "" : (", this keytab is for " + creds.getName() + " only")));
            }
            final int encPartKeyType = encPart.getEType();
            final Integer encPartKeyVersion = encPart.getKeyVersionNumber();
            KerberosKey dkey = null;
            try {
                dkey = findKey(encPartKeyType, encPartKeyVersion, serverKeys);
            }
            catch (final KrbException ke) {
                throw new IOException("Cannot find key matching version number", ke);
            }
            if (dkey == null) {
                throw new IOException("Cannot find key of appropriate type to decrypt ticket - need etype " + encPartKeyType);
            }
            final EncryptionKey secretKey = new EncryptionKey(encPartKeyType, dkey.getEncoded());
            final byte[] bytes = encPart.decrypt(secretKey, 2);
            final byte[] temp = encPart.reset(bytes);
            final EncTicketPart encTicketPart = new EncTicketPart(temp);
            this.peerPrincipal = new KerberosPrincipal(encTicketPart.cname.getName());
            this.localPrincipal = new KerberosPrincipal(ticketSname.getName());
            sessionKey = encTicketPart.key;
            if (KerberosClientKeyExchangeImpl.debug != null && Debug.isOn("handshake")) {
                System.out.println("server principal: " + ticketSname);
                System.out.println("cname: " + encTicketPart.cname.toString());
            }
        }
        catch (final IOException e) {
            throw e;
        }
        catch (final Exception e2) {
            if (KerberosClientKeyExchangeImpl.debug != null && Debug.isOn("handshake")) {
                System.out.println("KerberosWrapper error getting session key, generating random secret (" + e2.getMessage() + ")");
            }
            sessionKey = null;
        }
        input.getBytes16();
        if (sessionKey != null) {
            this.preMaster = new KerberosPreMasterSecret(protocolVersion, clientVersion, rand, input, sessionKey);
        }
        else {
            this.preMaster = new KerberosPreMasterSecret(clientVersion, rand);
        }
    }
    
    @Override
    public int messageLength() {
        return 6 + this.encodedTicket.length + this.preMaster.getEncrypted().length;
    }
    
    @Override
    public void send(final HandshakeOutStream s) throws IOException {
        s.putBytes16(this.encodedTicket);
        s.putBytes16(null);
        s.putBytes16(this.preMaster.getEncrypted());
    }
    
    @Override
    public void print(final PrintStream s) throws IOException {
        s.println("*** ClientKeyExchange, Kerberos");
        if (KerberosClientKeyExchangeImpl.debug != null && Debug.isOn("verbose")) {
            Debug.println(s, "Kerberos service ticket", this.encodedTicket);
            Debug.println(s, "Random Secret", this.preMaster.getUnencrypted());
            Debug.println(s, "Encrypted random Secret", this.preMaster.getEncrypted());
        }
    }
    
    private static KerberosTicket getServiceTicket(String serverName, final AccessControlContext acc) throws IOException {
        if ("localhost".equals(serverName) || "localhost.localdomain".equals(serverName)) {
            if (KerberosClientKeyExchangeImpl.debug != null && Debug.isOn("handshake")) {
                System.out.println("Get the local hostname");
            }
            final String localHost = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    try {
                        return InetAddress.getLocalHost().getHostName();
                    }
                    catch (final UnknownHostException e) {
                        if (HandshakeMessage.debug != null && Debug.isOn("handshake")) {
                            System.out.println("Warning, cannot get the local hostname: " + e.getMessage());
                        }
                        return null;
                    }
                }
            });
            if (localHost != null) {
                serverName = localHost;
            }
        }
        final String serviceName = "host/" + serverName;
        PrincipalName principal;
        try {
            principal = new PrincipalName(serviceName, 3);
        }
        catch (final SecurityException se) {
            throw se;
        }
        catch (final Exception e) {
            final IOException ioe = new IOException("Invalid service principal name: " + serviceName);
            ioe.initCause(e);
            throw ioe;
        }
        final String realm = principal.getRealmAsString();
        final String serverPrincipal = principal.toString();
        final String tgsPrincipal = "krbtgt/" + realm + "@" + realm;
        final String clientPrincipal = null;
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new ServicePermission(serverPrincipal, "initiate"), acc);
        }
        try {
            final KerberosTicket ticket = AccessController.doPrivileged((PrivilegedExceptionAction<KerberosTicket>)new PrivilegedExceptionAction<KerberosTicket>() {
                @Override
                public KerberosTicket run() throws Exception {
                    return Krb5Util.getTicketFromSubjectAndTgs(GSSCaller.CALLER_SSL_CLIENT, clientPrincipal, serverPrincipal, tgsPrincipal, acc);
                }
            });
            if (ticket == null) {
                throw new IOException("Failed to find any kerberos service ticket for " + serverPrincipal);
            }
            return ticket;
        }
        catch (final PrivilegedActionException e2) {
            final IOException ioe2 = new IOException("Attempt to obtain kerberos service ticket for " + serverPrincipal + " failed!");
            ioe2.initCause(e2);
            throw ioe2;
        }
    }
    
    @Override
    public byte[] getUnencryptedPreMasterSecret() {
        return this.preMaster.getUnencrypted();
    }
    
    @Override
    public KerberosPrincipal getPeerPrincipal() {
        return this.peerPrincipal;
    }
    
    @Override
    public KerberosPrincipal getLocalPrincipal() {
        return this.localPrincipal;
    }
    
    private static boolean versionMatches(final Integer v1, final int v2) {
        return v1 == null || v1 == 0 || v2 == 0 || v1.equals(v2);
    }
    
    private static KerberosKey findKey(final int etype, final Integer version, final KerberosKey[] keys) throws KrbException {
        boolean etypeFound = false;
        int kvno_found = 0;
        KerberosKey key_found = null;
        for (int i = 0; i < keys.length; ++i) {
            final int ktype = keys[i].getKeyType();
            if (etype == ktype) {
                final int kv = keys[i].getVersionNumber();
                etypeFound = true;
                if (versionMatches(version, kv)) {
                    return keys[i];
                }
                if (kv > kvno_found) {
                    key_found = keys[i];
                    kvno_found = kv;
                }
            }
        }
        if (etype == 1 || etype == 3) {
            for (int i = 0; i < keys.length; ++i) {
                final int ktype = keys[i].getKeyType();
                if (ktype == 1 || ktype == 3) {
                    final int kv = keys[i].getVersionNumber();
                    etypeFound = true;
                    if (versionMatches(version, kv)) {
                        return new KerberosKey(keys[i].getPrincipal(), keys[i].getEncoded(), etype, kv);
                    }
                    if (kv > kvno_found) {
                        key_found = new KerberosKey(keys[i].getPrincipal(), keys[i].getEncoded(), etype, kv);
                        kvno_found = kv;
                    }
                }
            }
        }
        if (etypeFound) {
            return key_found;
        }
        return null;
    }
}
