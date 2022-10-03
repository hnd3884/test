package com.sshtools.net;

import java.io.OutputStream;
import java.io.InputStream;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import com.maverick.ssh.SshTransport;
import java.net.Socket;

public class SocksProxyTransport extends Socket implements SshTransport
{
    public static final int SOCKS4 = 4;
    public static final int SOCKS5 = 5;
    private static final String[] h;
    private static final String[] b;
    private String j;
    private int l;
    private String d;
    private int g;
    private String f;
    private int c;
    private String e;
    private String k;
    private boolean i;
    
    private SocksProxyTransport(final String d, final int g, final String j, final int l, final int c) throws IOException, UnknownHostException {
        super(j, l);
        this.j = j;
        this.l = l;
        this.d = d;
        this.g = g;
        this.c = c;
    }
    
    public static SocksProxyTransport connectViaSocks4Proxy(final String s, final int n, final String s2, final int n2, final String e) throws IOException, UnknownHostException {
        final SocksProxyTransport socksProxyTransport = new SocksProxyTransport(s, n, s2, n2, 4);
        socksProxyTransport.e = e;
        try {
            final InputStream inputStream = socksProxyTransport.getInputStream();
            final OutputStream outputStream = socksProxyTransport.getOutputStream();
            final InetAddress byName = InetAddress.getByName(s);
            outputStream.write(4);
            outputStream.write(1);
            outputStream.write(n >>> 8 & 0xFF);
            outputStream.write(n & 0xFF);
            outputStream.write(byName.getAddress());
            outputStream.write(e.getBytes());
            outputStream.write(0);
            outputStream.flush();
            final int read = inputStream.read();
            if (read == -1) {
                throw new IOException("SOCKS4 server " + s2 + ":" + n2 + " disconnected");
            }
            if (read != 0) {
                throw new IOException("Invalid response from SOCKS4 server (" + read + ") " + s2 + ":" + n2);
            }
            final int read2 = inputStream.read();
            if (read2 != 90) {
                if (read2 > 90 && read2 < 93) {
                    throw new IOException("SOCKS4 server unable to connect, reason: " + SocksProxyTransport.b[read2 - 91]);
                }
                throw new IOException("SOCKS4 server unable to connect, reason: " + read2);
            }
            else {
                final byte[] array = new byte[6];
                if (inputStream.read(array, 0, 6) != 6) {
                    throw new IOException("SOCKS4 error reading destination address/port");
                }
                socksProxyTransport.f = array[2] + "." + array[3] + "." + array[4] + "." + array[5] + ":" + (array[0] << 8 | array[1]);
            }
        }
        catch (final SocketException ex) {
            throw new SocketException("Error communicating with SOCKS4 server " + s2 + ":" + n2 + ", " + ex.getMessage());
        }
        return socksProxyTransport;
    }
    
    public static SocksProxyTransport connectViaSocks5Proxy(final String s, final int n, final String s2, final int n2, final boolean i, final String e, final String k) throws IOException, UnknownHostException {
        final SocksProxyTransport socksProxyTransport = new SocksProxyTransport(s, n, s2, n2, 5);
        socksProxyTransport.e = e;
        socksProxyTransport.k = k;
        socksProxyTransport.i = i;
        try {
            final InputStream inputStream = socksProxyTransport.getInputStream();
            final OutputStream outputStream = socksProxyTransport.getOutputStream();
            outputStream.write(new byte[] { 5, 2, 0, 2 });
            outputStream.flush();
            final int read = inputStream.read();
            if (read == -1) {
                throw new IOException("SOCKS5 server " + s2 + ":" + n2 + " disconnected");
            }
            if (read != 5) {
                throw new IOException("Invalid response from SOCKS5 server (" + read + ") " + s2 + ":" + n2);
            }
            switch (inputStream.read()) {
                case 0: {
                    break;
                }
                case 2: {
                    b(inputStream, outputStream, e, k, s2, n2);
                    break;
                }
                default: {
                    throw new IOException("SOCKS5 server does not support our authentication methods");
                }
            }
            if (i) {
                InetAddress byName;
                try {
                    byName = InetAddress.getByName(s);
                }
                catch (final UnknownHostException ex) {
                    throw new IOException("Can't do local lookup on: " + s + ", try socks5 without local lookup");
                }
                outputStream.write(new byte[] { 5, 1, 0, 1 });
                outputStream.write(byName.getAddress());
            }
            else {
                outputStream.write(new byte[] { 5, 1, 0, 3 });
                outputStream.write(s.length());
                outputStream.write(s.getBytes());
            }
            outputStream.write(n >>> 8 & 0xFF);
            outputStream.write(n & 0xFF);
            outputStream.flush();
            final int read2 = inputStream.read();
            if (read2 != 5) {
                throw new IOException("Invalid response from SOCKS5 server (" + read2 + ") " + s2 + ":" + n2);
            }
            final int read3 = inputStream.read();
            if (read3 != 0) {
                if (read3 > 0 && read3 < 9) {
                    throw new IOException("SOCKS5 server unable to connect, reason: " + SocksProxyTransport.h[read3]);
                }
                throw new IOException("SOCKS5 server unable to connect, reason: " + read3);
            }
            else {
                inputStream.read();
                final int read4 = inputStream.read();
                final byte[] array = new byte[255];
                switch (read4) {
                    case 1: {
                        if (inputStream.read(array, 0, 4) != 4) {
                            throw new IOException("SOCKS5 error reading address");
                        }
                        socksProxyTransport.f = array[0] + "." + array[1] + "." + array[2] + "." + array[3];
                        break;
                    }
                    case 3: {
                        final int read5 = inputStream.read();
                        if (inputStream.read(array, 0, read5) != read5) {
                            throw new IOException("SOCKS5 error reading address");
                        }
                        socksProxyTransport.f = new String(array);
                        break;
                    }
                    default: {
                        throw new IOException("SOCKS5 gave unsupported address type: " + read4);
                    }
                }
                if (inputStream.read(array, 0, 2) != 2) {
                    throw new IOException("SOCKS5 error reading port");
                }
                final StringBuffer sb = new StringBuffer();
                final SocksProxyTransport socksProxyTransport2 = socksProxyTransport;
                socksProxyTransport2.f = sb.append(socksProxyTransport2.f).append(":").append(array[0] << 8 | array[1]).toString();
            }
        }
        catch (final SocketException ex2) {
            throw new SocketException("Error communicating with SOCKS5 server " + s2 + ":" + n2 + ", " + ex2.getMessage());
        }
        return socksProxyTransport;
    }
    
    private static void b(final InputStream inputStream, final OutputStream outputStream, final String s, final String s2, final String s3, final int n) throws IOException {
        outputStream.write(1);
        outputStream.write(s.length());
        outputStream.write(s.getBytes());
        outputStream.write(s2.length());
        outputStream.write(s2.getBytes());
        final int read = inputStream.read();
        if (read != 1 && read != 5) {
            throw new IOException("Invalid response from SOCKS5 server (" + read + ") " + s3 + ":" + n);
        }
        if (inputStream.read() != 0) {
            throw new IOException("Invalid username/password for SOCKS5 server");
        }
    }
    
    public String toString() {
        return "SocksProxySocket[addr=" + this.getInetAddress() + ",port=" + this.getPort() + ",localport=" + this.getLocalPort() + "]";
    }
    
    public static SocksProxyTransport connectViaSocks5Proxy(final String s, final int n, final String s2, final int n2, final String s3, final String s4) throws IOException, UnknownHostException {
        return connectViaSocks5Proxy(s, n, s2, n2, false, s3, s4);
    }
    
    public String getHost() {
        return this.d;
    }
    
    public SshTransport duplicate() throws IOException {
        switch (this.c) {
            case 4: {
                return connectViaSocks4Proxy(this.d, this.g, this.j, this.l, this.e);
            }
            default: {
                return connectViaSocks5Proxy(this.d, this.g, this.j, this.l, this.i, this.e, this.k);
            }
        }
    }
    
    static {
        h = new String[] { "Success", "General SOCKS server failure", "Connection not allowed by ruleset", "Network unreachable", "Host unreachable", "Connection refused", "TTL expired", "Command not supported", "Address type not supported" };
        b = new String[] { "Request rejected or failed", "SOCKS server cannot connect to identd on the client", "The client program and identd report different user-ids" };
    }
}
