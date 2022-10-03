package com.zoho.cp;

import java.io.IOException;
import java.net.SocketException;
import java.net.Socket;
import java.util.Properties;

public class MeteredSocketFactory implements MeteredSocketInterface
{
    public static ThreadLocal<MeteredSocket> socketThreadLocal;
    private MeteredSocket meteredSocket;
    
    @Override
    public Socket connect(final String host, final int portNumber, final Properties props) throws SocketException, IOException {
        return null;
    }
    
    @Override
    public Socket afterHandshake() throws SocketException, IOException {
        return this.meteredSocket;
    }
    
    @Override
    public Socket beforeHandshake() throws SocketException, IOException {
        return this.meteredSocket;
    }
    
    @Override
    public ThreadLocal<MeteredSocket> getSocketThreadLocal() {
        return MeteredSocketFactory.socketThreadLocal;
    }
    
    static {
        MeteredSocketFactory.socketThreadLocal = new ThreadLocal<MeteredSocket>();
    }
}
