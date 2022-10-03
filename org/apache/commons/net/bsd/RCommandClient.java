package org.apache.commons.net.bsd;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import org.apache.commons.net.io.SocketInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.SocketException;
import java.io.InputStream;

public class RCommandClient extends RExecClient
{
    public static final int DEFAULT_PORT = 514;
    public static final int MIN_CLIENT_PORT = 512;
    public static final int MAX_CLIENT_PORT = 1023;
    
    @Override
    InputStream _createErrorStream() throws IOException {
        int localPort = 1023;
        ServerSocket server = null;
        localPort = 1023;
        while (localPort >= 512) {
            try {
                server = this._serverSocketFactory_.createServerSocket(localPort, 1, this.getLocalAddress());
            }
            catch (final SocketException e) {
                --localPort;
                continue;
            }
            break;
        }
        if (server == null) {
            throw new BindException("All ports in use.");
        }
        this._output_.write(Integer.toString(server.getLocalPort()).getBytes("UTF-8"));
        this._output_.write(0);
        this._output_.flush();
        final Socket socket = server.accept();
        server.close();
        if (this.isRemoteVerificationEnabled() && !this.verifyRemote(socket)) {
            socket.close();
            throw new IOException("Security violation: unexpected connection attempt by " + socket.getInetAddress().getHostAddress());
        }
        return new SocketInputStream(socket, socket.getInputStream());
    }
    
    public RCommandClient() {
        this.setDefaultPort(514);
    }
    
    public void connect(final InetAddress host, final int port, final InetAddress localAddr) throws SocketException, BindException, IOException {
        int localPort;
        for (localPort = 1023, localPort = 1023; localPort >= 512; --localPort) {
            try {
                this._socket_ = this._socketFactory_.createSocket(host, port, localAddr, localPort);
                break;
            }
            catch (final BindException be) {}
            catch (final SocketException e) {}
        }
        if (localPort < 512) {
            throw new BindException("All ports in use or insufficient permssion.");
        }
        this._connectAction_();
    }
    
    @Override
    public void connect(final InetAddress host, final int port) throws SocketException, IOException {
        this.connect(host, port, InetAddress.getLocalHost());
    }
    
    @Override
    public void connect(final String hostname, final int port) throws SocketException, IOException, UnknownHostException {
        this.connect(InetAddress.getByName(hostname), port, InetAddress.getLocalHost());
    }
    
    public void connect(final String hostname, final int port, final InetAddress localAddr) throws SocketException, IOException {
        this.connect(InetAddress.getByName(hostname), port, localAddr);
    }
    
    @Override
    public void connect(final InetAddress host, final int port, final InetAddress localAddr, final int localPort) throws SocketException, IOException, IllegalArgumentException {
        if (localPort < 512 || localPort > 1023) {
            throw new IllegalArgumentException("Invalid port number " + localPort);
        }
        super.connect(host, port, localAddr, localPort);
    }
    
    @Override
    public void connect(final String hostname, final int port, final InetAddress localAddr, final int localPort) throws SocketException, IOException, IllegalArgumentException, UnknownHostException {
        if (localPort < 512 || localPort > 1023) {
            throw new IllegalArgumentException("Invalid port number " + localPort);
        }
        super.connect(hostname, port, localAddr, localPort);
    }
    
    public void rcommand(final String localUsername, final String remoteUsername, final String command, final boolean separateErrorStream) throws IOException {
        this.rexec(localUsername, remoteUsername, command, separateErrorStream);
    }
    
    public void rcommand(final String localUsername, final String remoteUsername, final String command) throws IOException {
        this.rcommand(localUsername, remoteUsername, command, false);
    }
}
