package com.me.mdm.server.nsclient;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.util.logging.Level;
import java.io.IOException;
import java.net.UnknownHostException;
import com.me.mdm.server.nsserver.NSUtil;
import java.util.logging.Logger;
import java.net.SocketAddress;
import java.net.Socket;

public class NSClient
{
    public static NSClient client;
    Socket clientsocket;
    SocketAddress remotesocketaddress;
    int nsserverport;
    String nsservername;
    private static Logger logger;
    
    public NSClient() {
        this.clientsocket = null;
        this.remotesocketaddress = null;
        this.nsserverport = 0;
        this.nsservername = "localhost";
    }
    
    public static NSClient getInstance() {
        if (NSClient.client == null) {
            NSClient.client = new NSClient();
        }
        return NSClient.client;
    }
    
    public void setPort(final int nsserverport) {
        this.nsserverport = nsserverport;
    }
    
    public void setServerName(final String nsservername) {
        this.nsservername = nsservername;
    }
    
    public void connect() throws UnknownHostException, IOException {
        this.nsserverport = NSUtil.getInstance().getNSPort();
        this.clientsocket = new Socket(this.nsservername, this.nsserverport);
    }
    
    public void close() throws IOException {
        this.clientsocket.close();
    }
    
    public NSResponse sendRequest(final NSRequest nsrequest) throws Exception {
        return this.sendRequestToNS(nsrequest);
    }
    
    private NSResponse sendRequestToNS(final NSRequest nsrequest) throws Exception {
        NSResponse nsresponse = null;
        DataInputStream in = null;
        try {
            nsresponse = new NSResponse();
            int available = 0;
            byte[] requestBytes = null;
            byte[] responseBytes = null;
            final String emptyStr = "";
            if (this.clientsocket == null) {
                this.connect();
            }
            if (this.clientsocket.isClosed() || !this.clientsocket.isConnected()) {
                this.connect();
            }
            requestBytes = nsrequest.getRequestBytes();
            NSClient.logger.log(Level.INFO, "NSClient Request Buffer : {0} ", new String(requestBytes, "UTF-8"));
            int loopCounter = 0;
            OutputStream out = null;
            try {
                out = this.clientsocket.getOutputStream();
                out.write(emptyStr.getBytes());
                out.write(requestBytes);
                out.flush();
                NSClient.logger.log(Level.INFO, "NSClient Request Buffer has been successfully written");
            }
            catch (final IOException exp) {
                NSClient.logger.log(Level.INFO, "NSClient Request Buffer IOException has been occurred {0}. Hence performing the reconnection", exp.getMessage());
                this.connect();
                out = this.clientsocket.getOutputStream();
                out.write(requestBytes);
                out.flush();
                NSClient.logger.log(Level.INFO, "NSClient Request Buffer has been successfully written");
            }
            in = new DataInputStream(new BufferedInputStream(this.clientsocket.getInputStream()));
            if (in != null) {
                while (in.available() < 1 && loopCounter < 40) {
                    ++loopCounter;
                    Thread.sleep(500L);
                }
            }
            available = in.available();
            responseBytes = new byte[available];
            if (available > 0) {
                in.readFully(responseBytes);
            }
            nsresponse.parseResponse(responseBytes);
        }
        catch (final Exception exp2) {
            NSClient.logger.log(Level.INFO, "NSClient Exception has been occurred {0}", exp2.getMessage());
            throw new Exception(exp2.getMessage());
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
        return nsresponse;
    }
    
    static {
        NSClient.client = null;
        NSClient.logger = Logger.getLogger("NSClientLogger");
    }
}
