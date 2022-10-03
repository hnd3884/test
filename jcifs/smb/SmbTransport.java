package jcifs.smb;

import jcifs.util.transport.Response;
import jcifs.util.transport.Request;
import java.net.NoRouteToHostException;
import java.net.ConnectException;
import jcifs.util.transport.TransportException;
import java.io.PrintStream;
import jcifs.util.Hexdump;
import jcifs.util.Encdec;
import jcifs.netbios.SessionServicePacket;
import jcifs.netbios.NbtException;
import jcifs.netbios.SessionRequestPacket;
import jcifs.netbios.NbtAddress;
import jcifs.netbios.Name;
import java.io.IOException;
import jcifs.Config;
import java.util.ListIterator;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import jcifs.UniAddress;
import java.net.InetAddress;
import jcifs.util.LogStream;
import jcifs.util.transport.Transport;

public class SmbTransport extends Transport implements SmbConstants
{
    static final byte[] BUF;
    final SmbComNegotiate NEGOTIATE_REQUEST;
    static LogStream log;
    InetAddress localAddr;
    int localPort;
    UniAddress address;
    Socket socket;
    int port;
    int mid;
    OutputStream out;
    InputStream in;
    byte[] sbuf;
    SmbComBlankResponse key;
    long sessionExpiration;
    LinkedList referrals;
    SigningDigest digest;
    LinkedList sessions;
    ServerData server;
    int flags2;
    int maxMpxCount;
    int snd_buf_size;
    int rcv_buf_size;
    int capabilities;
    int sessionKey;
    boolean useUnicode;
    String tconHostName;
    private SmbExtendedAuthenticator authenticator;
    
    static synchronized SmbTransport getSmbTransport(final UniAddress address, final int port) {
        return getSmbTransport(address, port, SmbConstants.LADDR, SmbConstants.LPORT);
    }
    
    static synchronized SmbTransport getSmbTransport(final UniAddress address, final int port, final InetAddress localAddr, final int localPort) {
        SmbTransport conn;
        synchronized (SmbConstants.CONNECTIONS) {
            final SmbComNegotiate negotiate = new SmbComNegotiate();
            if (SmbConstants.SSN_LIMIT != 1) {
                final ListIterator iter = SmbConstants.CONNECTIONS.listIterator();
                while (iter.hasNext()) {
                    conn = iter.next();
                    if (conn.matches(address, port, localAddr, localPort) && (SmbConstants.SSN_LIMIT == 0 || conn.sessions.size() < SmbConstants.SSN_LIMIT) && conn.NEGOTIATE_REQUEST.flags2 == negotiate.flags2) {
                        return conn;
                    }
                }
            }
            conn = new SmbTransport(negotiate, address, port, localAddr, localPort);
            SmbConstants.CONNECTIONS.add(0, conn);
        }
        return conn;
    }
    
    SmbTransport(final SmbComNegotiate nego, final UniAddress address, final int port, final InetAddress localAddr, final int localPort) {
        this.sbuf = new byte[255];
        this.key = new SmbComBlankResponse();
        this.sessionExpiration = System.currentTimeMillis() + SmbConstants.SO_TIMEOUT;
        this.referrals = new LinkedList();
        this.digest = null;
        this.sessions = new LinkedList();
        this.server = new ServerData();
        this.flags2 = SmbConstants.FLAGS2;
        this.maxMpxCount = SmbConstants.MAX_MPX_COUNT;
        this.snd_buf_size = SmbConstants.SND_BUF_SIZE;
        this.rcv_buf_size = SmbConstants.RCV_BUF_SIZE;
        this.capabilities = SmbConstants.CAPABILITIES;
        this.sessionKey = 0;
        this.useUnicode = SmbConstants.USE_UNICODE;
        this.authenticator = null;
        this.NEGOTIATE_REQUEST = nego;
        this.flags2 = this.NEGOTIATE_REQUEST.flags2;
        this.capabilities = Config.getInt("jcifs.smb.client.capabilities", SmbConstants.DEFAULT_CAPABILITIES);
        this.address = address;
        this.port = port;
        this.localAddr = localAddr;
        this.localPort = localPort;
    }
    
    synchronized SmbSession getSmbSession(final NtlmPasswordAuthentication auth) {
        return this.getSmbSession(null, auth);
    }
    
    synchronized SmbSession getSmbSession(final SmbExtendedAuthenticator authenticator, final NtlmPasswordAuthentication auth) {
        ListIterator iter = this.sessions.listIterator();
        while (iter.hasNext()) {
            final SmbSession ssn = iter.next();
            if (ssn.matches(authenticator, auth)) {
                ssn.authenticator = authenticator;
                ssn.auth = auth;
                return ssn;
            }
        }
        final long now;
        if (SmbConstants.SO_TIMEOUT > 0 && this.sessionExpiration < (now = System.currentTimeMillis())) {
            this.sessionExpiration = now + SmbConstants.SO_TIMEOUT;
            iter = this.sessions.listIterator();
            while (iter.hasNext()) {
                final SmbSession ssn = iter.next();
                if (ssn.expiration < now) {
                    ssn.logoff(false);
                }
            }
        }
        final SmbSession ssn = new SmbSession(this.address, this.port, this.localAddr, this.localPort, authenticator, auth);
        ssn.transport = this;
        this.sessions.add(ssn);
        return ssn;
    }
    
    boolean matches(final UniAddress address, final int port, final InetAddress localAddr, final int localPort) {
        return address.equals(this.address) && (port == 0 || port == this.port || (port == 445 && this.port == 139)) && (localAddr == this.localAddr || (localAddr != null && localAddr.equals(this.localAddr))) && localPort == this.localPort;
    }
    
    boolean hasCapability(final int cap) throws SmbException {
        try {
            this.connect(SmbConstants.RESPONSE_TIMEOUT);
        }
        catch (final IOException ioe) {
            throw new SmbException("", ioe);
        }
        return (this.capabilities & cap) == cap;
    }
    
    boolean isSignatureSetupRequired(final NtlmPasswordAuthentication auth) {
        return (this.flags2 & 0x4) != 0x0 && this.digest == null && auth != NtlmPasswordAuthentication.NULL && !NtlmPasswordAuthentication.NULL.equals(auth);
    }
    
    void ssn139() throws IOException {
        final Name calledName = new Name(this.address.firstCalledName(), 32, null);
        String nextCalledName = null;
        do {
            if (this.localAddr == null) {
                this.socket = new Socket(this.address.getHostAddress(), 139);
            }
            else {
                this.socket = new Socket(this.address.getHostAddress(), 139, this.localAddr, this.localPort);
            }
            this.socket.setSoTimeout(SmbConstants.SO_TIMEOUT);
            this.out = this.socket.getOutputStream();
            this.in = this.socket.getInputStream();
            final SessionServicePacket ssp = new SessionRequestPacket(calledName, NbtAddress.getLocalName());
            this.out.write(this.sbuf, 0, ssp.writeWireFormat(this.sbuf, 0));
            if (Transport.readn(this.in, this.sbuf, 0, 4) < 4) {
                try {
                    this.socket.close();
                }
                catch (final IOException ex) {}
                throw new SmbException("EOF during NetBIOS session request");
            }
            switch (this.sbuf[0] & 0xFF) {
                case 130: {
                    if (LogStream.level >= 4) {
                        SmbTransport.log.println("session established ok with " + this.address);
                    }
                    return;
                }
                case 131: {
                    final int errorCode = this.in.read() & 0xFF;
                    switch (errorCode) {
                        case 128:
                        case 130: {
                            this.socket.close();
                            final Name name = calledName;
                            nextCalledName = this.address.nextCalledName();
                            name.name = nextCalledName;
                            continue;
                        }
                        default: {
                            this.disconnect(true);
                            throw new NbtException(2, errorCode);
                        }
                    }
                    break;
                }
                case -1: {
                    this.disconnect(true);
                    throw new NbtException(2, -1);
                }
                default: {
                    this.disconnect(true);
                    throw new NbtException(2, 0);
                }
            }
        } while (nextCalledName != null);
        throw new IOException("Failed to establish session with " + this.address);
    }
    
    private void negotiate(int port, final ServerMessageBlock resp) throws IOException {
        synchronized (this.sbuf) {
            if (SmbConstants.NETBIOS_HOSTNAME != null && !SmbConstants.NETBIOS_HOSTNAME.equals("")) {
                port = 139;
            }
            if (port == 139) {
                this.ssn139();
            }
            else {
                if (port == 0) {
                    port = 445;
                }
                if (this.localAddr == null) {
                    this.socket = new Socket(this.address.getHostAddress(), port);
                }
                else {
                    this.socket = new Socket(this.address.getHostAddress(), port, this.localAddr, this.localPort);
                }
                this.socket.setSoTimeout(SmbConstants.SO_TIMEOUT);
                this.out = this.socket.getOutputStream();
                this.in = this.socket.getInputStream();
            }
            if (++this.mid == 32000) {
                this.mid = 1;
            }
            this.NEGOTIATE_REQUEST.mid = this.mid;
            final int n = this.NEGOTIATE_REQUEST.encode(this.sbuf, 4);
            Encdec.enc_uint32be(n & 0xFFFF, this.sbuf, 0);
            if (LogStream.level >= 4) {
                SmbTransport.log.println(this.NEGOTIATE_REQUEST);
                if (LogStream.level >= 6) {
                    Hexdump.hexdump(SmbTransport.log, this.sbuf, 4, n);
                }
            }
            this.out.write(this.sbuf, 0, 4 + n);
            this.out.flush();
            if (this.peekKey() == null) {
                throw new IOException("transport closed in negotiate");
            }
            final int size = Encdec.dec_uint16be(this.sbuf, 2) & 0xFFFF;
            if (size < 33 || 4 + size > this.sbuf.length) {
                throw new IOException("Invalid payload size: " + size);
            }
            Transport.readn(this.in, this.sbuf, 36, size - 32);
            resp.decode(this.sbuf, 4);
            if (LogStream.level >= 4) {
                SmbTransport.log.println(resp);
                if (LogStream.level >= 6) {
                    Hexdump.hexdump(SmbTransport.log, this.sbuf, 4, n);
                }
            }
        }
    }
    
    public void connect() throws SmbException {
        try {
            super.connect(SmbConstants.RESPONSE_TIMEOUT);
        }
        catch (final TransportException te) {
            throw new SmbException("", te);
        }
    }
    
    protected void doConnect() throws IOException {
        final SmbComNegotiateResponse resp = new SmbComNegotiateResponse(this.server);
        try {
            this.negotiate(this.port, resp);
        }
        catch (final ConnectException ce) {
            this.negotiate(this.port = ((this.port == 0 || this.port == 445) ? 139 : 445), resp);
        }
        catch (final NoRouteToHostException nr) {
            this.negotiate(this.port = ((this.port == 0 || this.port == 445) ? 139 : 445), resp);
        }
        if (resp.dialectIndex > 10) {
            throw new SmbException("This client does not support the negotiated dialect.");
        }
        this.tconHostName = this.address.getHostName();
        if (this.server.signaturesRequired || (this.server.signaturesEnabled && SmbConstants.SIGNPREF)) {
            this.flags2 |= 0x4;
        }
        else {
            this.flags2 &= 0xFFFB;
        }
        this.maxMpxCount = Math.min(this.maxMpxCount, this.server.maxMpxCount);
        if (this.maxMpxCount < 1) {
            this.maxMpxCount = 1;
        }
        this.snd_buf_size = Math.min(this.snd_buf_size, this.server.maxBufferSize);
        this.capabilities &= this.server.capabilities;
        if ((this.capabilities & 0x4) == 0x0) {
            if (SmbConstants.FORCE_UNICODE) {
                this.capabilities |= 0x4;
            }
            else {
                this.useUnicode = false;
                this.flags2 &= 0x7FFF;
            }
        }
    }
    
    protected void doDisconnect(final boolean hard) throws IOException {
        final ListIterator iter = this.sessions.listIterator();
        while (iter.hasNext()) {
            final SmbSession ssn = iter.next();
            ssn.logoff(hard);
        }
        this.socket.shutdownOutput();
        this.out.close();
        this.in.close();
        this.socket.close();
        this.digest = null;
        synchronized (SmbConstants.CONNECTIONS) {
            SmbConstants.CONNECTIONS.remove(this);
        }
    }
    
    protected void makeKey(final Request request) throws IOException {
        if (++this.mid == 32000) {
            this.mid = 1;
        }
        ((ServerMessageBlock)request).mid = this.mid;
    }
    
    protected Request peekKey() throws IOException {
        int n;
        while ((n = Transport.readn(this.in, this.sbuf, 0, 4)) >= 4) {
            if (this.sbuf[0] != -123) {
                if ((n = Transport.readn(this.in, this.sbuf, 4, 32)) < 32) {
                    return null;
                }
                if (LogStream.level >= 4) {
                    SmbTransport.log.println("New data read: " + this);
                    Hexdump.hexdump(SmbTransport.log, this.sbuf, 4, 32);
                }
                while (this.sbuf[0] != 0 || this.sbuf[1] != 0 || this.sbuf[4] != -1 || this.sbuf[5] != 83 || this.sbuf[6] != 77 || this.sbuf[7] != 66) {
                    for (int i = 0; i < 35; ++i) {
                        this.sbuf[i] = this.sbuf[i + 1];
                    }
                    final int b;
                    if ((b = this.in.read()) == -1) {
                        return null;
                    }
                    this.sbuf[35] = (byte)b;
                }
                this.key.mid = (Encdec.dec_uint16le(this.sbuf, 34) & 0xFFFF);
                return this.key;
            }
        }
        return null;
    }
    
    protected void doSend(final Request request) throws IOException {
        synchronized (SmbTransport.BUF) {
            ServerMessageBlock smb = (ServerMessageBlock)request;
            final int n = smb.encode(SmbTransport.BUF, 4);
            Encdec.enc_uint32be(n & 0xFFFF, SmbTransport.BUF, 0);
            if (LogStream.level >= 4) {
                do {
                    SmbTransport.log.println(smb);
                } while (smb instanceof AndXServerMessageBlock && (smb = ((AndXServerMessageBlock)smb).andx) != null);
                if (LogStream.level >= 6) {
                    Hexdump.hexdump(SmbTransport.log, SmbTransport.BUF, 4, n);
                }
            }
            this.out.write(SmbTransport.BUF, 0, 4 + n);
        }
    }
    
    protected void doSend0(final Request request) throws IOException {
        try {
            this.doSend(request);
        }
        catch (final IOException ioe) {
            if (LogStream.level > 2) {
                ioe.printStackTrace(SmbTransport.log);
            }
            try {
                this.disconnect(true);
            }
            catch (final IOException ioe2) {
                ioe2.printStackTrace(SmbTransport.log);
            }
            throw ioe;
        }
    }
    
    protected void doRecv(final Response response) throws IOException {
        final ServerMessageBlock resp = (ServerMessageBlock)response;
        resp.useUnicode = this.useUnicode;
        synchronized (SmbTransport.BUF) {
            System.arraycopy(this.sbuf, 0, SmbTransport.BUF, 0, 36);
            final int size = Encdec.dec_uint16be(SmbTransport.BUF, 2) & 0xFFFF;
            if (size < 33 || 4 + size > this.rcv_buf_size) {
                throw new IOException("Invalid payload size: " + size);
            }
            if (resp.command == 46) {
                final SmbComReadAndXResponse r = (SmbComReadAndXResponse)resp;
                int off = 32;
                Transport.readn(this.in, SmbTransport.BUF, 4 + off, 27);
                off += 27;
                resp.decode(SmbTransport.BUF, 4);
                if (r.dataLength > 0) {
                    Transport.readn(this.in, SmbTransport.BUF, 4 + off, r.dataOffset - off);
                    Transport.readn(this.in, r.b, r.off, r.dataLength);
                }
            }
            else {
                Transport.readn(this.in, SmbTransport.BUF, 36, size - 32);
                resp.decode(SmbTransport.BUF, 4);
                if (resp instanceof SmbComTransactionResponse) {
                    ((SmbComTransactionResponse)resp).nextElement();
                }
            }
            if (this.digest != null && resp.errorCode == 0) {
                this.digest.verify(SmbTransport.BUF, 4, resp);
            }
            if (LogStream.level >= 4) {
                SmbTransport.log.println(response);
                if (LogStream.level >= 6) {
                    Hexdump.hexdump(SmbTransport.log, SmbTransport.BUF, 4, size);
                }
            }
        }
    }
    
    protected void doSkip() throws IOException {
        final int size = Encdec.dec_uint16be(this.sbuf, 2) & 0xFFFF;
        if (size < 33 || 4 + size > this.rcv_buf_size) {
            this.in.skip(this.in.available());
        }
        else {
            this.in.skip(size - 32);
        }
    }
    
    void checkStatus(final ServerMessageBlock req, final ServerMessageBlock resp) throws SmbException {
        switch (resp.errorCode = SmbException.getStatusByCode(resp.errorCode)) {
            case 0: {
                break;
            }
            case -1073741790:
            case -1073741718:
            case -1073741715:
            case -1073741714:
            case -1073741713:
            case -1073741712:
            case -1073741711:
            case -1073741710:
            case -1073741428:
            case -1073741260: {
                throw new SmbAuthException(resp.errorCode);
            }
            case -1073741225: {
                if (req.auth == null && req.authenticator == null) {
                    throw new SmbException(resp.errorCode, null);
                }
                this.authenticator = req.authenticator;
                final DfsReferral dr = this.getDfsReferral(req.auth, req.path);
                this.referrals.add(dr);
                throw dr;
            }
            case -2147483643: {
                break;
            }
            default: {
                throw new SmbException(resp.errorCode, null);
            }
        }
        if (resp.verifyFailed) {
            throw new SmbException("Signature verification failed.");
        }
    }
    
    void send(final ServerMessageBlock request, final ServerMessageBlock response) throws SmbException {
        this.connect();
        request.flags2 |= this.flags2;
        request.useUnicode = this.useUnicode;
        request.response = response;
        if (request.digest == null) {
            request.digest = this.digest;
        }
        try {
            if (response == null) {
                this.doSend0(request);
                return;
            }
            if (request instanceof SmbComTransaction) {
                response.command = request.command;
                final SmbComTransaction req = (SmbComTransaction)request;
                final SmbComTransactionResponse resp = (SmbComTransactionResponse)response;
                req.maxBufferSize = this.snd_buf_size;
                resp.reset();
                try {
                    BufferCache.getBuffers(req, resp);
                    req.nextElement();
                    if (req.hasMoreElements()) {
                        final SmbComBlankResponse interim = new SmbComBlankResponse();
                        super.sendrecv(req, interim, SmbConstants.RESPONSE_TIMEOUT);
                        if (interim.errorCode != 0) {
                            this.checkStatus(req, interim);
                        }
                        req.nextElement();
                    }
                    else {
                        this.makeKey(req);
                    }
                    synchronized (this.response_map) {
                        response.received = false;
                        resp.isReceived = false;
                        try {
                            this.response_map.put(req, resp);
                            do {
                                this.doSend0(req);
                            } while (req.hasMoreElements() && req.nextElement() != null);
                            long timeout = SmbConstants.RESPONSE_TIMEOUT;
                            resp.expiration = System.currentTimeMillis() + timeout;
                            while (resp.hasMoreElements()) {
                                this.response_map.wait(timeout);
                                timeout = resp.expiration - System.currentTimeMillis();
                                if (timeout <= 0L) {
                                    throw new TransportException(this + " timedout waiting for response to " + req);
                                }
                            }
                            if (response.errorCode != 0) {
                                this.checkStatus(req, resp);
                            }
                        }
                        catch (final InterruptedException ie) {
                            throw new TransportException(ie);
                        }
                        finally {
                            this.response_map.remove(req);
                        }
                    }
                }
                finally {
                    BufferCache.releaseBuffer(req.txn_buf);
                    BufferCache.releaseBuffer(resp.txn_buf);
                }
            }
            else {
                response.command = request.command;
                super.sendrecv(request, response, SmbConstants.RESPONSE_TIMEOUT);
            }
        }
        catch (final SmbException se) {
            throw se;
        }
        catch (final IOException ioe) {
            throw new SmbException("", ioe);
        }
        this.checkStatus(request, response);
    }
    
    public String toString() {
        return super.toString() + "[" + this.address + ":" + this.port + "]";
    }
    
    DfsReferral getDfsReferral(final NtlmPasswordAuthentication auth, final String path) throws SmbException {
        final DfsReferral dr = new DfsReferral();
        final SmbTree ipc = this.getSmbSession(this.authenticator, auth).getSmbTree("IPC$", null);
        final Trans2GetDfsReferralResponse resp = new Trans2GetDfsReferralResponse();
        final Trans2GetDfsReferral trans2GetDfsReferral = new Trans2GetDfsReferral(path);
        ipc.send(trans2GetDfsReferral, resp);
        final String subpath = path.substring(0, resp.pathConsumed);
        final String node = resp.referral.node;
        final int i;
        final int p;
        final int s;
        if (subpath.charAt(0) != '\\' || (i = subpath.indexOf(92, 1)) < 2 || (p = subpath.indexOf(92, i + 1)) < i + 2 || node.charAt(0) != '\\' || (s = node.indexOf(92, 1)) < 2) {
            throw new SmbException("Invalid DFS path: " + path);
        }
        int n;
        if ((n = node.indexOf(92, s + 1)) == -1) {
            n = node.length();
        }
        dr.path = subpath.substring(p);
        dr.node = node.substring(0, n);
        dr.nodepath = node.substring(n);
        dr.server = node.substring(1, s);
        dr.share = node.substring(s + 1, n);
        dr.resolveHashes = auth.hashesExternal;
        return dr;
    }
    
    DfsReferral lookupReferral(final String unc) {
        synchronized (this.referrals) {
            final ListIterator iter = this.referrals.listIterator();
            while (iter.hasNext()) {
                DfsReferral dr;
                int len;
                int i;
                for (dr = iter.next(), len = dr.path.length(), i = 0; i < len && i < unc.length() && dr.path.charAt(i) == unc.charAt(i); ++i) {}
                if (i == len && (len == unc.length() || unc.charAt(len) == '\\')) {
                    return dr;
                }
            }
        }
        return null;
    }
    
    static {
        BUF = new byte[65535];
        SmbTransport.log = LogStream.getInstance();
    }
    
    class ServerData
    {
        byte flags;
        int flags2;
        int maxMpxCount;
        int maxBufferSize;
        int sessionKey;
        int capabilities;
        String oemDomainName;
        int securityMode;
        int security;
        boolean encryptedPasswords;
        boolean signaturesEnabled;
        boolean signaturesRequired;
        int maxNumberVcs;
        int maxRawSize;
        long serverTime;
        int serverTimeZone;
        int encryptionKeyLength;
        byte[] encryptionKey;
    }
}
