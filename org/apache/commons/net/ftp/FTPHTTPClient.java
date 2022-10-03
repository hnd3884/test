package org.apache.commons.net.ftp;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.net.SocketException;
import java.io.Reader;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Inet6Address;
import java.io.IOException;
import java.net.Socket;
import org.apache.commons.net.util.Base64;

public class FTPHTTPClient extends FTPClient
{
    private final String proxyHost;
    private final int proxyPort;
    private final String proxyUsername;
    private final String proxyPassword;
    private static final byte[] CRLF;
    private final Base64 base64;
    private String tunnelHost;
    
    public FTPHTTPClient(final String proxyHost, final int proxyPort, final String proxyUser, final String proxyPass) {
        this.base64 = new Base64();
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyUsername = proxyUser;
        this.proxyPassword = proxyPass;
        this.tunnelHost = null;
    }
    
    public FTPHTTPClient(final String proxyHost, final int proxyPort) {
        this(proxyHost, proxyPort, null, null);
    }
    
    @Deprecated
    @Override
    protected Socket _openDataConnection_(final int command, final String arg) throws IOException {
        return super._openDataConnection_(command, arg);
    }
    
    @Override
    protected Socket _openDataConnection_(final String command, final String arg) throws IOException {
        if (this.getDataConnectionMode() != 2) {
            throw new IllegalStateException("Only passive connection mode supported");
        }
        final boolean isInet6Address = this.getRemoteAddress() instanceof Inet6Address;
        String passiveHost = null;
        final boolean attemptEPSV = this.isUseEPSVwithIPv4() || isInet6Address;
        if (attemptEPSV && this.epsv() == 229) {
            this._parseExtendedPassiveModeReply(this._replyLines.get(0));
            passiveHost = this.tunnelHost;
        }
        else {
            if (isInet6Address) {
                return null;
            }
            if (this.pasv() != 227) {
                return null;
            }
            this._parsePassiveModeReply(this._replyLines.get(0));
            passiveHost = this.getPassiveHost();
        }
        final Socket socket = this._socketFactory_.createSocket(this.proxyHost, this.proxyPort);
        final InputStream is = socket.getInputStream();
        final OutputStream os = socket.getOutputStream();
        this.tunnelHandshake(passiveHost, this.getPassivePort(), is, os);
        if (this.getRestartOffset() > 0L && !this.restart(this.getRestartOffset())) {
            socket.close();
            return null;
        }
        if (!FTPReply.isPositivePreliminary(this.sendCommand(command, arg))) {
            socket.close();
            return null;
        }
        return socket;
    }
    
    @Override
    public void connect(final String host, final int port) throws SocketException, IOException {
        this._socket_ = this._socketFactory_.createSocket(this.proxyHost, this.proxyPort);
        this._input_ = this._socket_.getInputStream();
        this._output_ = this._socket_.getOutputStream();
        Reader socketIsReader;
        try {
            socketIsReader = this.tunnelHandshake(host, port, this._input_, this._output_);
        }
        catch (final Exception e) {
            final IOException ioe = new IOException("Could not connect to " + host + " using port " + port);
            ioe.initCause(e);
            throw ioe;
        }
        super._connectAction_(socketIsReader);
    }
    
    private BufferedReader tunnelHandshake(final String host, final int port, final InputStream input, final OutputStream output) throws IOException, UnsupportedEncodingException {
        final String connectString = "CONNECT " + host + ":" + port + " HTTP/1.1";
        final String hostString = "Host: " + host + ":" + port;
        this.tunnelHost = host;
        output.write(connectString.getBytes("UTF-8"));
        output.write(FTPHTTPClient.CRLF);
        output.write(hostString.getBytes("UTF-8"));
        output.write(FTPHTTPClient.CRLF);
        if (this.proxyUsername != null && this.proxyPassword != null) {
            final String auth = this.proxyUsername + ":" + this.proxyPassword;
            final String header = "Proxy-Authorization: Basic " + this.base64.encodeToString(auth.getBytes("UTF-8"));
            output.write(header.getBytes("UTF-8"));
        }
        output.write(FTPHTTPClient.CRLF);
        final List<String> response = new ArrayList<String>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input, this.getCharset()));
        for (String line = reader.readLine(); line != null && line.length() > 0; line = reader.readLine()) {
            response.add(line);
        }
        final int size = response.size();
        if (size == 0) {
            throw new IOException("No response from proxy");
        }
        String code = null;
        final String resp = response.get(0);
        if (!resp.startsWith("HTTP/") || resp.length() < 12) {
            throw new IOException("Invalid response from proxy: " + resp);
        }
        code = resp.substring(9, 12);
        if (!"200".equals(code)) {
            final StringBuilder msg = new StringBuilder();
            msg.append("HTTPTunnelConnector: connection failed\r\n");
            msg.append("Response received from the proxy:\r\n");
            for (final String line2 : response) {
                msg.append(line2);
                msg.append("\r\n");
            }
            throw new IOException(msg.toString());
        }
        return reader;
    }
    
    static {
        CRLF = new byte[] { 13, 10 };
    }
}
