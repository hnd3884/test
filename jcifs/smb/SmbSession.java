package jcifs.smb;

import jcifs.Config;
import java.util.Enumeration;
import java.net.UnknownHostException;
import java.io.PrintStream;
import jcifs.util.LogStream;
import java.net.InetAddress;
import jcifs.UniAddress;
import java.util.Vector;
import jcifs.netbios.NbtAddress;

public final class SmbSession
{
    private static final String LOGON_SHARE;
    private static final int LOOKUP_RESP_LIMIT;
    private static final String DOMAIN;
    private static final String USERNAME;
    private static final int CACHE_POLICY;
    static NbtAddress[] dc_list;
    static long dc_list_expiration;
    static int dc_list_counter;
    private int uid;
    private Vector trees;
    private boolean sessionSetup;
    private UniAddress address;
    private int port;
    private int localPort;
    private InetAddress localAddr;
    SmbTransport transport;
    NtlmPasswordAuthentication auth;
    long expiration;
    int x;
    SmbExtendedAuthenticator authenticator;
    
    private static NtlmChallenge interrogate(final NbtAddress addr) throws SmbException {
        final UniAddress dc = new UniAddress(addr);
        final SmbTransport trans = SmbTransport.getSmbTransport(dc, 0);
        if (SmbSession.USERNAME == null) {
            trans.connect();
            final LogStream log = SmbTransport.log;
            if (LogStream.level >= 3) {
                SmbTransport.log.println("Default credentials (jcifs.smb.client.username/password) not specified. SMB signing may not work propertly.  Skipping DC interrogation.");
            }
        }
        else {
            final SmbSession ssn = trans.getSmbSession(NtlmPasswordAuthentication.DEFAULT);
            ssn.getSmbTree(SmbSession.LOGON_SHARE, null).treeConnect(null, null);
        }
        return new NtlmChallenge(trans.server.encryptionKey, dc);
    }
    
    public static NtlmChallenge getChallengeForDomain() throws SmbException, UnknownHostException {
        if (SmbSession.DOMAIN == null) {
            throw new SmbException("A domain was not specified");
        }
        synchronized (SmbSession.DOMAIN) {
            final long now = System.currentTimeMillis();
            int retry = 1;
            do {
                if (SmbSession.dc_list_expiration < now) {
                    SmbSession.dc_list_expiration = now + SmbSession.CACHE_POLICY * 1000L;
                    final NbtAddress[] list = NbtAddress.getAllByName(SmbSession.DOMAIN, 28, null, null);
                    if (list != null && list.length > 0) {
                        SmbSession.dc_list = list;
                    }
                    else {
                        SmbSession.dc_list_expiration = now + 900000L;
                        final LogStream log = SmbTransport.log;
                        if (LogStream.level >= 2) {
                            SmbTransport.log.println("Failed to retrieve DC list from WINS");
                        }
                    }
                }
                for (int max = Math.min(SmbSession.dc_list.length, SmbSession.LOOKUP_RESP_LIMIT), j = 0; j < max; ++j) {
                    final int i = SmbSession.dc_list_counter++ % max;
                    if (SmbSession.dc_list[i] != null) {
                        try {
                            return interrogate(SmbSession.dc_list[i]);
                        }
                        catch (final SmbException se) {
                            final LogStream log2 = SmbTransport.log;
                            if (LogStream.level >= 2) {
                                SmbTransport.log.println("Failed validate DC: " + SmbSession.dc_list[i]);
                                final LogStream log3 = SmbTransport.log;
                                if (LogStream.level > 2) {
                                    se.printStackTrace(SmbTransport.log);
                                }
                            }
                            SmbSession.dc_list[i] = null;
                        }
                    }
                }
                SmbSession.dc_list_expiration = 0L;
            } while (retry-- > 0);
            SmbSession.dc_list_expiration = now + 900000L;
        }
        throw new UnknownHostException("Failed to negotiate with a suitable domain controller for " + SmbSession.DOMAIN);
    }
    
    public static byte[] getChallenge(final UniAddress dc) throws SmbException, UnknownHostException {
        return getChallenge(dc, 0);
    }
    
    public static byte[] getChallenge(final UniAddress dc, final int port) throws SmbException, UnknownHostException {
        final SmbTransport trans = SmbTransport.getSmbTransport(dc, port);
        trans.connect();
        return trans.server.encryptionKey;
    }
    
    public static void logon(final UniAddress dc, final NtlmPasswordAuthentication auth) throws SmbException {
        logon(dc, 0, auth);
    }
    
    public static void logon(final UniAddress dc, final int port, final NtlmPasswordAuthentication auth) throws SmbException {
        final SmbTree tree = SmbTransport.getSmbTransport(dc, port).getSmbSession(auth).getSmbTree(SmbSession.LOGON_SHARE, null);
        if (SmbSession.LOGON_SHARE == null) {
            tree.treeConnect(null, null);
        }
        else {
            final Trans2FindFirst2 req = new Trans2FindFirst2("\\", "*", 16);
            final Trans2FindFirst2Response resp = new Trans2FindFirst2Response();
            tree.send(req, resp);
        }
    }
    
    SmbSession(final UniAddress address, final int port, final InetAddress localAddr, final int localPort, final NtlmPasswordAuthentication auth) {
        this.transport = null;
        this.x = 0;
        this.authenticator = null;
        this.address = address;
        this.port = port;
        this.localAddr = localAddr;
        this.localPort = localPort;
        this.auth = auth;
        this.trees = new Vector();
    }
    
    synchronized SmbTree getSmbTree(String share, final String service) {
        if (share == null) {
            share = "IPC$";
        }
        final Enumeration e = this.trees.elements();
        while (e.hasMoreElements()) {
            final SmbTree t = e.nextElement();
            if (t.matches(share, service)) {
                return t;
            }
        }
        final SmbTree t = new SmbTree(this, share, service);
        this.trees.addElement(t);
        return t;
    }
    
    boolean matches(final SmbExtendedAuthenticator authenticator, final NtlmPasswordAuthentication auth) {
        return this.matcheObject(this.authenticator, authenticator) && this.matcheObject(this.auth, auth);
    }
    
    private boolean matcheObject(final Object obj1, final Object obj2) {
        boolean ret = false;
        if (obj1 == null) {
            if (obj2 == null) {
                ret = true;
            }
        }
        else {
            ret = obj1.equals(obj2);
        }
        return ret;
    }
    
    synchronized SmbTransport transport() {
        if (this.transport == null) {
            this.transport = SmbTransport.getSmbTransport(this.address, this.port, this.localAddr, this.localPort);
        }
        return this.transport;
    }
    
    void send(final ServerMessageBlock request, final ServerMessageBlock response) throws SmbException {
        if (response != null) {
            response.received = false;
        }
        synchronized (this.transport.setupDiscoLock) {
            this.expiration = System.currentTimeMillis() + SmbConstants.SO_TIMEOUT;
            this.sessionSetup(request, response);
            if (response != null && response.received) {
                return;
            }
            request.uid = this.uid;
            request.auth = this.auth;
            request.authenticator = this.authenticator;
            this.transport.send(request, response);
        }
    }
    
    void sessionSetup(final ServerMessageBlock andx, final ServerMessageBlock andxResponse) throws SmbException {
        synchronized (this.transport()) {
            if (this.sessionSetup) {
                return;
            }
            this.transport.connect();
            if (this.authenticator != null) {
                this.authenticator.sessionSetup(this, andx, andxResponse);
            }
            else {
                if (LogStream.level >= 4) {
                    SmbTransport.log.println("sessionSetup: accountName=" + this.auth.username + ",primaryDomain=" + this.auth.domain);
                }
                final SmbComSessionSetupAndX request = new SmbComSessionSetupAndX(this, andx);
                final SmbComSessionSetupAndXResponse response = new SmbComSessionSetupAndXResponse(andxResponse);
                if (this.transport.isSignatureSetupRequired(this.auth)) {
                    if (this.auth.hashesExternal && NtlmPasswordAuthentication.DEFAULT_PASSWORD != "") {
                        this.transport.getSmbSession(NtlmPasswordAuthentication.DEFAULT).getSmbTree(SmbSession.LOGON_SHARE, null).treeConnect(null, null);
                    }
                    else {
                        request.digest = new SigningDigest(this.transport, this.auth);
                    }
                }
                request.auth = this.auth;
                this.transport.send(request, response);
                if (response.isLoggedInAsGuest && !"GUEST".equalsIgnoreCase(this.auth.username)) {
                    throw new SmbAuthException(-1073741715);
                }
                this.uid = response.uid;
                this.sessionSetup = true;
                if (request.digest != null) {
                    this.transport.digest = request.digest;
                }
            }
        }
    }
    
    void logoff(final boolean inError) {
        synchronized (this.transport()) {
            if (!this.sessionSetup) {
                return;
            }
            final Enumeration e = this.trees.elements();
            while (e.hasMoreElements()) {
                final SmbTree t = e.nextElement();
                t.treeDisconnect(inError);
            }
            if (!inError && this.transport.server.security != 0) {
                final SmbComLogoffAndX request = new SmbComLogoffAndX(null);
                request.uid = this.uid;
                try {
                    this.transport.send(request, null);
                }
                catch (final SmbException ex) {}
            }
            this.sessionSetup = false;
        }
    }
    
    public String toString() {
        return "SmbSession[accountName=" + this.auth.username + ",primaryDomain=" + this.auth.domain + ",uid=" + this.uid + ",sessionSetup=" + this.sessionSetup + "]";
    }
    
    SmbSession(final UniAddress address, final int port, final InetAddress localAddr, final int localPort, final SmbExtendedAuthenticator authenticator, final NtlmPasswordAuthentication auth) {
        this(address, port, localAddr, localPort, auth);
        this.authenticator = authenticator;
    }
    
    void setUid(final int uid) {
        this.uid = uid;
    }
    
    void setSessionSetup(final boolean b) {
        this.sessionSetup = true;
    }
    
    static {
        LOGON_SHARE = Config.getProperty("jcifs.smb.client.logonShare", null);
        LOOKUP_RESP_LIMIT = Config.getInt("jcifs.netbios.lookupRespLimit", 3);
        DOMAIN = Config.getProperty("jcifs.smb.client.domain", null);
        USERNAME = Config.getProperty("jcifs.smb.client.username", null);
        CACHE_POLICY = Config.getInt("jcifs.netbios.cachePolicy", 600) * 60;
        SmbSession.dc_list = null;
    }
}
