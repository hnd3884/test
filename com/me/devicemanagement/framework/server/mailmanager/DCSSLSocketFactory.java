package com.me.devicemanagement.framework.server.mailmanager;

import java.net.InetAddress;
import java.io.IOException;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class DCSSLSocketFactory extends SSLSocketFactory
{
    private SSLSocketFactory factory;
    
    public DCSSLSocketFactory() {
        try {
            this.factory = MailerUtils.getInstance().getSSLSocketFactory();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static SocketFactory getDefault() {
        return new DCSSLSocketFactory();
    }
    
    @Override
    public Socket createSocket() throws IOException {
        return this.factory.createSocket();
    }
    
    @Override
    public Socket createSocket(final Socket socket, final String s, final int i, final boolean flag) throws IOException {
        return this.factory.createSocket(socket, s, i, flag);
    }
    
    @Override
    public Socket createSocket(final InetAddress inaddr, final int i, final InetAddress inaddr1, final int j) throws IOException {
        return this.factory.createSocket(inaddr, i, inaddr1, j);
    }
    
    @Override
    public Socket createSocket(final InetAddress inaddr, final int i) throws IOException {
        return this.factory.createSocket(inaddr, i);
    }
    
    @Override
    public Socket createSocket(final String s, final int i, final InetAddress inaddr, final int j) throws IOException {
        return this.factory.createSocket(s, i, inaddr, j);
    }
    
    @Override
    public Socket createSocket(final String s, final int i) throws IOException {
        return this.factory.createSocket(s, i);
    }
    
    @Override
    public String[] getDefaultCipherSuites() {
        return this.factory.getDefaultCipherSuites();
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return this.factory.getSupportedCipherSuites();
    }
}
