package HTTPClient;

import java.net.UnknownHostException;
import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class SocksClient
{
    private String socks_host;
    private int socks_port;
    private int socks_version;
    private static final byte CONNECT = 1;
    private static final byte BIND = 2;
    private static final byte UDP_ASS = 3;
    private static final byte NO_AUTH = 0;
    private static final byte GSSAPI = 1;
    private static final byte USERPWD = 2;
    private static final byte NO_ACC = -1;
    private static final byte IP_V4 = 1;
    private static final byte DMNAME = 3;
    private static final byte IP_V6 = 4;
    private boolean v4A;
    private byte[] user;
    
    SocksClient(final String host, final int port) {
        this.v4A = false;
        this.socks_host = host;
        this.socks_port = port;
        this.socks_version = -1;
    }
    
    SocksClient(final String host, final int port, final int version) throws SocksException {
        this.v4A = false;
        this.socks_host = host;
        this.socks_port = port;
        if (version != 4 && version != 5) {
            throw new SocksException("SOCKS Version not supported: " + version);
        }
        this.socks_version = version;
    }
    
    Socket getSocket(final String host, final int port) throws IOException {
        return this.getSocket(host, port, null, -1);
    }
    
    Socket getSocket(final String host, final int port, final InetAddress localAddr, final int localPort) throws IOException {
        Socket sock = null;
        try {
            Log.write(64, "Socks: contacting server on " + this.socks_host + ":" + this.socks_port);
            sock = connect(this.socks_host, this.socks_port, localAddr, localPort);
            InputStream inp = sock.getInputStream();
            OutputStream out = sock.getOutputStream();
            switch (this.socks_version) {
                case 4: {
                    this.v4ProtExchg(inp, out, host, port);
                    break;
                }
                case 5: {
                    this.v5ProtExchg(inp, out, host, port);
                    break;
                }
                case -1: {
                    try {
                        this.v4ProtExchg(inp, out, host, port);
                        this.socks_version = 4;
                    }
                    catch (final SocksException se) {
                        Log.write(64, "Socks: V4 request failed: " + se.getMessage());
                        sock.close();
                        sock = connect(this.socks_host, this.socks_port, localAddr, localPort);
                        inp = sock.getInputStream();
                        out = sock.getOutputStream();
                        this.v5ProtExchg(inp, out, host, port);
                        this.socks_version = 5;
                    }
                    break;
                }
                default: {
                    throw new Error("SocksClient internal error: unknown version " + this.socks_version);
                }
            }
            Log.write(64, "Socks: connection established.");
            return sock;
        }
        catch (final IOException ioe) {
            if (sock != null) {
                try {
                    sock.close();
                }
                catch (final IOException ex) {}
            }
            throw ioe;
        }
    }
    
    private static final Socket connect(final String host, final int port, final InetAddress localAddr, final int localPort) throws IOException {
        final InetAddress[] addr_list = InetAddress.getAllByName(host);
        int idx = 0;
        while (idx < addr_list.length) {
            try {
                if (localAddr == null) {
                    return new Socket(addr_list[idx], port);
                }
                return new Socket(addr_list[idx], port, localAddr, localPort);
            }
            catch (final SocketException se) {
                if (idx >= addr_list.length - 1) {
                    throw se;
                }
                ++idx;
            }
        }
        return null;
    }
    
    private void v4ProtExchg(final InputStream inp, final OutputStream out, final String host, final int port) throws SocksException, IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream(100);
        Log.write(64, "Socks: Beginning V4 Protocol Exchange for host " + host + ":" + port);
        byte[] addr = { 0, 0, 0, 42 };
        if (!this.v4A) {
            try {
                addr = InetAddress.getByName(host).getAddress();
            }
            catch (final UnknownHostException ex) {
                this.v4A = true;
            }
            catch (final SecurityException ex2) {
                this.v4A = true;
            }
            if (this.v4A) {
                Log.write(64, "Socks: Switching to version 4A");
            }
        }
        if (this.user == null) {
            String user_str;
            try {
                user_str = System.getProperty("user.name", "");
            }
            catch (final SecurityException ex3) {
                user_str = "";
            }
            final byte[] tmp = user_str.getBytes();
            System.arraycopy(tmp, 0, this.user = new byte[tmp.length + 1], 0, tmp.length);
            this.user[user_str.length()] = 0;
        }
        Log.write(64, "Socks: Sending connect request for user " + new String(this.user, 0, this.user.length - 1));
        buffer.reset();
        buffer.write(4);
        buffer.write(1);
        buffer.write(port >> 8 & 0xFF);
        buffer.write(port & 0xFF);
        buffer.write(addr);
        buffer.write(this.user);
        if (this.v4A) {
            buffer.write(host.getBytes("8859_1"));
            buffer.write(0);
        }
        buffer.writeTo(out);
        final int version = inp.read();
        if (version == -1) {
            throw new SocksException("Connection refused by server");
        }
        if (version == 4) {
            Log.write(64, "Socks: Warning: received version 4 instead of 0");
        }
        else if (version != 0) {
            throw new SocksException("Received invalid version: " + version + "; expected: 0");
        }
        final int sts = inp.read();
        Log.write(64, "Socks: Received response; version: " + version + "; status: " + sts);
        switch (sts) {
            case 91: {
                throw new SocksException("Connection request rejected");
            }
            case 92: {
                throw new SocksException("Connection request rejected: can't connect to identd");
            }
            case 93: {
                throw new SocksException("Connection request rejected: identd reports different user-id from " + new String(this.user, 0, this.user.length - 1));
            }
            default: {
                throw new SocksException("Connection request rejected: unknown error " + sts);
            }
            case 90: {
                final byte[] skip = new byte[6];
                for (int rcvd = 0, tot = 0; tot < skip.length && (rcvd = inp.read(skip, 0, skip.length - tot)) != -1; tot += rcvd) {}
            }
        }
    }
    
    private void v5ProtExchg(final InputStream inp, final OutputStream out, final String host, final int port) throws SocksException, IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream(100);
        Log.write(64, "Socks: Beginning V5 Protocol Exchange for host " + host + ":" + port);
        Log.write(64, "Socks: Sending authentication request; methods No-Authentication, Username/Password");
        buffer.reset();
        buffer.write(5);
        buffer.write(2);
        buffer.write(0);
        buffer.write(2);
        buffer.writeTo(out);
        int version = inp.read();
        if (version == -1) {
            throw new SocksException("Connection refused by server");
        }
        if (version != 5) {
            throw new SocksException("Received invalid version: " + version + "; expected: 5");
        }
        final int method = inp.read();
        Log.write(64, "Socks: Received response; version: " + version + "; method: " + method);
        while (true) {
            switch (method) {
                case -1: {
                    throw new SocksException("Server unwilling to accept any standard authentication methods");
                }
                default: {
                    throw new SocksException("Cannot handle authentication method " + method);
                }
                case 0: {
                    Log.write(64, "Socks: Sending connect request");
                    buffer.reset();
                    buffer.write(5);
                    buffer.write(1);
                    buffer.write(0);
                    buffer.write(3);
                    buffer.write(host.length() & 0xFF);
                    buffer.write(host.getBytes("8859_1"));
                    buffer.write(port >> 8 & 0xFF);
                    buffer.write(port & 0xFF);
                    buffer.writeTo(out);
                    version = inp.read();
                    if (version != 5) {
                        throw new SocksException("Received invalid version: " + version + "; expected: 5");
                    }
                    final int sts = inp.read();
                    Log.write(64, "Socks: Received response; version: " + version + "; status: " + sts);
                    switch (sts) {
                        case 1: {
                            throw new SocksException("General SOCKS server failure");
                        }
                        case 2: {
                            throw new SocksException("Connection not allowed");
                        }
                        case 3: {
                            throw new SocksException("Network unreachable");
                        }
                        case 4: {
                            throw new SocksException("Host unreachable");
                        }
                        case 5: {
                            throw new SocksException("Connection refused");
                        }
                        case 6: {
                            throw new SocksException("TTL expired");
                        }
                        case 7: {
                            throw new SocksException("Command not supported");
                        }
                        case 8: {
                            throw new SocksException("Address type not supported");
                        }
                        default: {
                            throw new SocksException("Unknown reply received from server: " + sts);
                        }
                        case 0: {
                            inp.read();
                            final int atype = inp.read();
                            int alen = 0;
                            switch (atype) {
                                case 4: {
                                    alen = 16;
                                    break;
                                }
                                case 1: {
                                    alen = 4;
                                    break;
                                }
                                case 3: {
                                    alen = inp.read();
                                    break;
                                }
                                default: {
                                    throw new SocksException("Invalid address type received from server: " + atype);
                                }
                            }
                            final byte[] skip = new byte[alen + 2];
                            for (int rcvd = 0, tot = 0; tot < skip.length && (rcvd = inp.read(skip, 0, skip.length - tot)) != -1; tot += rcvd) {}
                            return;
                        }
                    }
                    break;
                }
                case 1: {
                    this.negotiate_gssapi(inp, out);
                    continue;
                }
                case 2: {
                    this.negotiate_userpwd(inp, out);
                    continue;
                }
            }
            break;
        }
    }
    
    private void negotiate_gssapi(final InputStream inp, final OutputStream out) throws SocksException, IOException {
        throw new SocksException("GSSAPI authentication protocol not implemented");
    }
    
    private void negotiate_userpwd(final InputStream inp, final OutputStream out) throws SocksException, IOException {
        Log.write(64, "Socks: Entering authorization subnegotiation; method: Username/Password");
        AuthorizationInfo auth_info;
        try {
            auth_info = AuthorizationInfo.getAuthorization(this.socks_host, this.socks_port, "SOCKS5", "USER/PASS", null, null, true);
        }
        catch (final AuthSchemeNotImplException ex) {
            auth_info = null;
        }
        if (auth_info == null) {
            throw new SocksException("No Authorization info for SOCKS found (server requested username/password).");
        }
        final NVPair[] unpw = auth_info.getParams();
        if (unpw == null || unpw.length == 0) {
            throw new SocksException("No Username/Password found in authorization info for SOCKS.");
        }
        final String user_str = unpw[0].getName();
        final String pass_str = unpw[0].getValue();
        Log.write(64, "Socks: Sending authorization request for user " + user_str);
        final byte[] utmp = user_str.getBytes();
        final byte[] ptmp = pass_str.getBytes();
        final byte[] buffer = new byte[2 + utmp.length + 1 + ptmp.length];
        buffer[0] = 1;
        buffer[1] = (byte)utmp.length;
        System.arraycopy(utmp, 0, buffer, 2, utmp.length);
        buffer[2 + buffer[1]] = (byte)ptmp.length;
        System.arraycopy(ptmp, 0, buffer, 2 + buffer[1] + 1, ptmp.length);
        out.write(buffer);
        final int version = inp.read();
        if (version != 1) {
            throw new SocksException("Wrong version received in username/password subnegotiation response: " + version + "; expected: 1");
        }
        final int sts = inp.read();
        if (sts != 0) {
            throw new SocksException("Username/Password authentication failed; status: " + sts);
        }
        Log.write(64, "Socks: Received response; version: " + version + "; status: " + sts);
    }
    
    public String toString() {
        return String.valueOf(this.getClass().getName()) + "[" + this.socks_host + ":" + this.socks_port + "]";
    }
}
