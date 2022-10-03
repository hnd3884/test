package sun.security.krb5;

import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.CredentialsUtil;
import sun.security.krb5.internal.crypto.EType;
import java.util.Locale;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.KDCOptions;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import sun.security.krb5.internal.ccache.CredentialsCache;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.TicketFlags;
import sun.security.krb5.internal.Ticket;

public class Credentials
{
    Ticket ticket;
    PrincipalName client;
    PrincipalName clientAlias;
    PrincipalName server;
    PrincipalName serverAlias;
    EncryptionKey key;
    TicketFlags flags;
    KerberosTime authTime;
    KerberosTime startTime;
    KerberosTime endTime;
    KerberosTime renewTill;
    HostAddresses cAddr;
    AuthorizationData authzData;
    private static boolean DEBUG;
    private static CredentialsCache cache;
    static boolean alreadyLoaded;
    private static boolean alreadyTried;
    private Credentials proxy;
    
    public Credentials getProxy() {
        return this.proxy;
    }
    
    public Credentials setProxy(final Credentials proxy) {
        this.proxy = proxy;
        return this;
    }
    
    private static native Credentials acquireDefaultNativeCreds(final int[] p0);
    
    public Credentials(final Ticket ticket, final PrincipalName principalName, final PrincipalName principalName2, final PrincipalName principalName3, final PrincipalName principalName4, final EncryptionKey encryptionKey, final TicketFlags ticketFlags, final KerberosTime kerberosTime, final KerberosTime kerberosTime2, final KerberosTime kerberosTime3, final KerberosTime kerberosTime4, final HostAddresses hostAddresses, final AuthorizationData authzData) {
        this(ticket, principalName, principalName2, principalName3, principalName4, encryptionKey, ticketFlags, kerberosTime, kerberosTime2, kerberosTime3, kerberosTime4, hostAddresses);
        this.authzData = authzData;
    }
    
    public Credentials(final Ticket ticket, final PrincipalName client, final PrincipalName clientAlias, final PrincipalName server, final PrincipalName serverAlias, final EncryptionKey key, final TicketFlags flags, final KerberosTime authTime, final KerberosTime startTime, final KerberosTime endTime, final KerberosTime renewTill, final HostAddresses cAddr) {
        this.proxy = null;
        this.ticket = ticket;
        this.client = client;
        this.clientAlias = clientAlias;
        this.server = server;
        this.serverAlias = serverAlias;
        this.key = key;
        this.flags = flags;
        this.authTime = authTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.renewTill = renewTill;
        this.cAddr = cAddr;
    }
    
    public Credentials(final byte[] array, final String s, final String s2, final String s3, final String s4, final byte[] array2, final int n, final boolean[] array3, final Date date, final Date date2, final Date date3, final Date date4, final InetAddress[] array4) throws KrbException, IOException {
        this(new Ticket(array), new PrincipalName(s, 1), (s2 == null) ? null : new PrincipalName(s2, 1), new PrincipalName(s3, 2), (s4 == null) ? null : new PrincipalName(s4, 2), new EncryptionKey(n, array2), (array3 == null) ? null : new TicketFlags(array3), (date == null) ? null : new KerberosTime(date), (date2 == null) ? null : new KerberosTime(date2), (date3 == null) ? null : new KerberosTime(date3), (date4 == null) ? null : new KerberosTime(date4), null);
    }
    
    public final PrincipalName getClient() {
        return this.client;
    }
    
    public final PrincipalName getClientAlias() {
        return this.clientAlias;
    }
    
    public final PrincipalName getServer() {
        return this.server;
    }
    
    public final PrincipalName getServerAlias() {
        return this.serverAlias;
    }
    
    public final EncryptionKey getSessionKey() {
        return this.key;
    }
    
    public final Date getAuthTime() {
        if (this.authTime != null) {
            return this.authTime.toDate();
        }
        return null;
    }
    
    public final Date getStartTime() {
        if (this.startTime != null) {
            return this.startTime.toDate();
        }
        return null;
    }
    
    public final Date getEndTime() {
        if (this.endTime != null) {
            return this.endTime.toDate();
        }
        return null;
    }
    
    public final Date getRenewTill() {
        if (this.renewTill != null) {
            return this.renewTill.toDate();
        }
        return null;
    }
    
    public final boolean[] getFlags() {
        if (this.flags == null) {
            return null;
        }
        return this.flags.toBooleanArray();
    }
    
    public final InetAddress[] getClientAddresses() {
        if (this.cAddr == null) {
            return null;
        }
        return this.cAddr.getInetAddresses();
    }
    
    public final byte[] getEncoded() {
        byte[] asn1Encode = null;
        try {
            asn1Encode = this.ticket.asn1Encode();
        }
        catch (final Asn1Exception ex) {
            if (Credentials.DEBUG) {
                System.out.println(ex);
            }
        }
        catch (final IOException ex2) {
            if (Credentials.DEBUG) {
                System.out.println(ex2);
            }
        }
        return asn1Encode;
    }
    
    public boolean isForwardable() {
        return this.flags.get(1);
    }
    
    public boolean isRenewable() {
        return this.flags.get(8);
    }
    
    public Ticket getTicket() {
        return this.ticket;
    }
    
    public TicketFlags getTicketFlags() {
        return this.flags;
    }
    
    public AuthorizationData getAuthzData() {
        return this.authzData;
    }
    
    public boolean checkDelegate() {
        return this.flags.get(13);
    }
    
    public void resetDelegate() {
        this.flags.set(13, false);
    }
    
    public Credentials renew() throws KrbException, IOException {
        final KDCOptions kdcOptions = new KDCOptions();
        kdcOptions.set(30, true);
        kdcOptions.set(8, true);
        return new KrbTgsReq(kdcOptions, this, this.server, this.serverAlias, null, null, null, null, this.cAddr, null, null, null).sendAndGetCreds();
    }
    
    public static Credentials acquireTGTFromCache(final PrincipalName principalName, final String s) throws KrbException, IOException {
        if (s == null) {
            final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.name"));
            if (s2.toUpperCase(Locale.ENGLISH).startsWith("WINDOWS") || s2.toUpperCase(Locale.ENGLISH).contains("OS X")) {
                final Credentials acquireDefaultCreds = acquireDefaultCreds();
                if (acquireDefaultCreds == null) {
                    if (Credentials.DEBUG) {
                        System.out.println(">>> Found no TGT's in LSA");
                    }
                    return null;
                }
                if (principalName == null) {
                    if (Credentials.DEBUG) {
                        System.out.println(">>> Obtained TGT from LSA: " + acquireDefaultCreds);
                    }
                    return acquireDefaultCreds;
                }
                if (acquireDefaultCreds.getClient().equals(principalName)) {
                    if (Credentials.DEBUG) {
                        System.out.println(">>> Obtained TGT from LSA: " + acquireDefaultCreds);
                    }
                    return acquireDefaultCreds;
                }
                if (Credentials.DEBUG) {
                    System.out.println(">>> LSA contains TGT for " + acquireDefaultCreds.getClient() + " not " + principalName);
                }
                return null;
            }
        }
        final CredentialsCache instance = CredentialsCache.getInstance(principalName, s);
        if (instance == null) {
            return null;
        }
        final Credentials initialCreds = instance.getInitialCreds();
        if (initialCreds == null) {
            return null;
        }
        if (EType.isSupported(initialCreds.key.getEType())) {
            return initialCreds;
        }
        if (Credentials.DEBUG) {
            System.out.println(">>> unsupported key type found the default TGT: " + initialCreds.key.getEType());
        }
        return null;
    }
    
    public static synchronized Credentials acquireDefaultCreds() {
        Credentials acquireDefaultNativeCreds = null;
        if (Credentials.cache == null) {
            Credentials.cache = CredentialsCache.getInstance();
        }
        if (Credentials.cache != null) {
            final Credentials initialCreds = Credentials.cache.getInitialCreds();
            if (initialCreds != null) {
                if (Credentials.DEBUG) {
                    System.out.println(">>> KrbCreds found the default ticket granting ticket in credential cache.");
                }
                if (EType.isSupported(initialCreds.key.getEType())) {
                    acquireDefaultNativeCreds = initialCreds;
                }
                else if (Credentials.DEBUG) {
                    System.out.println(">>> unsupported key type found the default TGT: " + initialCreds.key.getEType());
                }
            }
        }
        if (acquireDefaultNativeCreds == null) {
            if (!Credentials.alreadyTried) {
                try {
                    ensureLoaded();
                }
                catch (final Exception ex) {
                    if (Credentials.DEBUG) {
                        System.out.println("Can not load credentials cache");
                        ex.printStackTrace();
                    }
                    Credentials.alreadyTried = true;
                }
            }
            if (Credentials.alreadyLoaded) {
                if (Credentials.DEBUG) {
                    System.out.println(">> Acquire default native Credentials");
                }
                try {
                    acquireDefaultNativeCreds = acquireDefaultNativeCreds(EType.getDefaults("default_tkt_enctypes"));
                }
                catch (final KrbException ex2) {}
            }
        }
        return acquireDefaultNativeCreds;
    }
    
    public static Credentials acquireServiceCreds(final String s, final Credentials credentials) throws KrbException, IOException {
        return CredentialsUtil.acquireServiceCreds(s, credentials);
    }
    
    public static Credentials acquireS4U2selfCreds(final PrincipalName principalName, final Credentials credentials) throws KrbException, IOException {
        return CredentialsUtil.acquireS4U2selfCreds(principalName, credentials);
    }
    
    public static Credentials acquireS4U2proxyCreds(final String s, final Ticket ticket, final PrincipalName principalName, final Credentials credentials) throws KrbException, IOException {
        return CredentialsUtil.acquireS4U2proxyCreds(s, ticket, principalName, credentials);
    }
    
    public CredentialsCache getCache() {
        return Credentials.cache;
    }
    
    public static void printDebug(final Credentials credentials) {
        System.out.println(">>> DEBUG: ----Credentials----");
        System.out.println("\tclient: " + credentials.client.toString());
        if (credentials.clientAlias != null) {
            System.out.println("\tclient alias: " + credentials.clientAlias.toString());
        }
        System.out.println("\tserver: " + credentials.server.toString());
        if (credentials.serverAlias != null) {
            System.out.println("\tserver alias: " + credentials.serverAlias.toString());
        }
        System.out.println("\tticket: sname: " + credentials.ticket.sname.toString());
        if (credentials.startTime != null) {
            System.out.println("\tstartTime: " + credentials.startTime.getTime());
        }
        System.out.println("\tendTime: " + credentials.endTime.getTime());
        System.out.println("        ----Credentials end----");
    }
    
    static void ensureLoaded() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                if (System.getProperty("os.name").contains("OS X")) {
                    System.loadLibrary("osxkrb5");
                }
                else {
                    System.loadLibrary("w2k_lsa_auth");
                }
                return null;
            }
        });
        Credentials.alreadyLoaded = true;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Credentials:");
        sb.append("\n      client=").append(this.client);
        if (this.clientAlias != null) {
            sb.append("\n      clientAlias=").append(this.clientAlias);
        }
        sb.append("\n      server=").append(this.server);
        if (this.serverAlias != null) {
            sb.append("\n      serverAlias=").append(this.serverAlias);
        }
        if (this.authTime != null) {
            sb.append("\n    authTime=").append(this.authTime);
        }
        if (this.startTime != null) {
            sb.append("\n   startTime=").append(this.startTime);
        }
        sb.append("\n     endTime=").append(this.endTime);
        sb.append("\n   renewTill=").append(this.renewTill);
        sb.append("\n       flags=").append(this.flags);
        sb.append("\nEType (skey)=").append(this.key.getEType());
        sb.append("\n   (tkt key)=").append(this.ticket.encPart.eType);
        return sb.toString();
    }
    
    public sun.security.krb5.internal.ccache.Credentials toCCacheCreds() {
        return new sun.security.krb5.internal.ccache.Credentials(this.getClient(), this.getServer(), this.getSessionKey(), date2kt(this.getAuthTime()), date2kt(this.getStartTime()), date2kt(this.getEndTime()), date2kt(this.getRenewTill()), false, this.flags, new HostAddresses(this.getClientAddresses()), this.getAuthzData(), this.getTicket(), null);
    }
    
    private static KerberosTime date2kt(final Date date) {
        return (date == null) ? null : new KerberosTime(date);
    }
    
    static {
        Credentials.DEBUG = Krb5.DEBUG;
        Credentials.alreadyLoaded = false;
        Credentials.alreadyTried = false;
    }
}
