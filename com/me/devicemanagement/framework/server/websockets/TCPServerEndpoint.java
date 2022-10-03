package com.me.devicemanagement.framework.server.websockets;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.util.StringTokenizer;
import java.net.Socket;
import java.io.IOException;
import java.util.logging.Level;
import java.net.ServerSocket;
import java.util.logging.Logger;

public class TCPServerEndpoint
{
    private static final String[] SSL_ENABLED_CIPHERS;
    private static final String[] SSL_ENABLED_PROTOCOLS;
    private static Logger wsFrameworkLogger;
    private static TCPServerEndpoint tcpServerEndpoint;
    private int sslPort;
    private int nonSslPort;
    private boolean run;
    private ServerSocket sslServerSocket;
    private ServerSocket nonSslServerSocket;
    private TCPSession tcpSession;
    
    private TCPServerEndpoint() {
        TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "Initializing TCP Server Endpoint");
        this.run = false;
    }
    
    public static TCPServerEndpoint getInstance() {
        if (TCPServerEndpoint.tcpServerEndpoint == null) {
            TCPServerEndpoint.tcpServerEndpoint = new TCPServerEndpoint();
        }
        return TCPServerEndpoint.tcpServerEndpoint;
    }
    
    int getSslPort() {
        return this.sslPort;
    }
    
    int getNonSslPort() {
        return this.nonSslPort;
    }
    
    public void setSSLPort(final int sslPort) {
        this.sslPort = sslPort;
    }
    
    public void setNonSslPort(final int nonSslPort) {
        this.nonSslPort = nonSslPort;
    }
    
    private void serviceIncomingConnections(final ServerSocket serverSock, final int port) throws Exception {
        while (this.run) {
            Socket clientSocket = null;
            try {
                TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "Waiting For Socket Connection on Port" + port);
                clientSocket = serverSock.accept();
                this.tcpSession = new TCPSession(clientSocket);
                this.readAndProcessClientRequest();
                TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "New Socket Connection received on port - " + port + ". Client IP - " + clientSocket.getInetAddress());
            }
            catch (final IOException iex) {
                TCPServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, "IOException while Servicing Incoming Connection.", iex);
                if (clientSocket == null) {
                    continue;
                }
                try {
                    TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "serviceIncomingConnections: Closing Client Socket");
                    clientSocket.close();
                }
                catch (final Exception ex) {
                    TCPServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, "serviceIncomingConnections: Exception while closing Socket", ex);
                }
            }
            catch (final RuntimeException re) {
                TCPServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, "serviceIncomingConnections: Runtime Exception", re);
            }
        }
    }
    
    private void readAndProcessClientRequest() {
        final String sourceMethod = "TCPServerEndpoint::readAndProcessClientRequest";
        final byte[] readBuffer = new byte[100];
        try {
            this.tcpSession.getInputStream().read(readBuffer);
            final ClientDetails tcpClient = this.getClientDetailsObject(new String(readBuffer));
            if (tcpClient != null) {
                final ClientManager clientMgr = ClientRequestMapper.getInstance().createClientManager(tcpClient);
                if (clientMgr != null) {
                    ClientAccessLogger.logClientAccess(String.valueOf(tcpClient.clientId), tcpClient.clientName, tcpClient.clientType, "CONNECTED", Constants.ClientConnentionMode.BLOCKING_READ.ordinal(), Constants.ClientSocketType.TCP.ordinal());
                    clientMgr.setClientStatus(Constants.ClientStatus.VERIFYING.ordinal());
                    clientMgr.setLastContactTime(System.currentTimeMillis());
                    if (!ConnectionPoolHandler.getInstance().addClientRequestToPool(clientMgr)) {
                        TCPServerEndpoint.wsFrameworkLogger.log(Level.WARNING, tcpClient.clientType + " -> Adding TCP client request to thread pool failed - " + tcpClient.clientType + ":" + clientMgr.getClientId());
                        this.tcpSession.closeSocket();
                    }
                }
                else {
                    TCPServerEndpoint.wsFrameworkLogger.log(Level.WARNING, sourceMethod + " - Unable to create ClientManager object");
                    this.tcpSession.closeSocket();
                }
            }
            else {
                TCPServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, sourceMethod + " - Unable to form ClientDetails object");
                this.tcpSession.closeSocket();
            }
        }
        catch (final Exception ex) {
            TCPServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, sourceMethod + " - Exception while processing TCP client request", ex);
            try {
                this.tcpSession.closeSocket();
            }
            catch (final Exception exp) {
                TCPServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, sourceMethod + " - Exception while closing Socket", exp);
            }
        }
    }
    
    private ClientDetails getClientDetailsObject(final String initCommand) {
        ClientDetails clientDet = null;
        final StringTokenizer strTok = new StringTokenizer(initCommand.trim());
        if (strTok.countTokens() == 5 && strTok.nextToken().equalsIgnoreCase("CONN_DETAILS")) {
            final String strClientId = strTok.nextToken();
            final String clientName = strTok.nextToken();
            final String clientType = strTok.nextToken();
            final String sessionId = strTok.nextToken();
            final long clientId = Long.parseLong(strClientId);
            clientDet = new ClientDetails(clientId, clientName, clientType, sessionId, this.tcpSession);
        }
        else {
            TCPServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, "getClientDetailsObject: Connection params Command mismatch");
        }
        return clientDet;
    }
    
    private void startSSLPortListener() {
        try {
            TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "startSSLPortListener: Starting SSL Port Listener!");
            final ServerSocketFactory sslServerSockFact = SSLServerSocketFactory.getDefault();
            this.sslServerSocket = sslServerSockFact.createServerSocket(this.sslPort);
            ((SSLServerSocket)this.sslServerSocket).setEnabledCipherSuites(TCPServerEndpoint.SSL_ENABLED_CIPHERS);
            ((SSLServerSocket)this.sslServerSocket).setEnabledProtocols(TCPServerEndpoint.SSL_ENABLED_PROTOCOLS);
            TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "startSSLPortListener: SSL Server Socket created Successfully!");
            this.serviceIncomingConnections(this.sslServerSocket, this.sslPort);
            this.sslServerSocket.close();
        }
        catch (final Exception ex) {
            TCPServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, "startSSLPortListener: Exception while starting SSL Port Listener", ex);
        }
    }
    
    private void startNonSSLPortListener() {
        try {
            TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "startNonSSLPortListener: Starting Non SSL Port Listener!");
            this.nonSslServerSocket = new ServerSocket(this.nonSslPort);
            TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "startNonSSLPortListener: Non SSL Server Socket created Successfully!");
            this.serviceIncomingConnections(this.nonSslServerSocket, this.nonSslPort);
            this.nonSslServerSocket.close();
        }
        catch (final Exception ex) {
            TCPServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, "startSSLPortListener: Exception while starting Non SSL Port Listener", ex);
        }
    }
    
    public void start(final boolean isSecure) throws Exception {
        TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "start: Starting TCP Server Endpoint.");
        this.run = true;
        if (isSecure) {
            final Thread sslPortListener = new Thread(new Runnable() {
                @Override
                public void run() {
                    TCPServerEndpoint.this.startSSLPortListener();
                }
            }, "TCP SSL LISTENER - " + this.sslPort);
            sslPortListener.start();
        }
        else {
            final Thread nonSslPortListener = new Thread(new Runnable() {
                @Override
                public void run() {
                    TCPServerEndpoint.this.startNonSSLPortListener();
                }
            }, "TCP NON SSL LISTENER - " + this.nonSslPort);
            nonSslPortListener.start();
        }
    }
    
    public void stop() throws Exception {
        try {
            if (this.run) {
                this.run = false;
                if (this.sslServerSocket != null) {
                    TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "stop: Closing Secure TCP Server Socket.");
                    this.sslServerSocket.close();
                }
                if (this.nonSslServerSocket != null) {
                    TCPServerEndpoint.wsFrameworkLogger.log(Level.INFO, "stop: Closing Non Secure TCP Server Socket.");
                    this.nonSslServerSocket.close();
                }
            }
        }
        catch (final Exception ex) {
            TCPServerEndpoint.wsFrameworkLogger.log(Level.SEVERE, "stop: Exception while stopping TCPServerEndpoint.");
        }
    }
    
    static {
        SSL_ENABLED_CIPHERS = new String[] { "TLS_DH_anon_WITH_AES_128_CBC_SHA" };
        SSL_ENABLED_PROTOCOLS = new String[] { "TLSv1" };
        TCPServerEndpoint.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}
