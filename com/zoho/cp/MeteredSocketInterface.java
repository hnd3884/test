package com.zoho.cp;

import java.io.IOException;
import java.net.SocketException;
import java.net.Socket;
import java.util.Properties;

public interface MeteredSocketInterface
{
    Socket connect(final String p0, final int p1, final Properties p2) throws SocketException, IOException;
    
    Socket afterHandshake() throws SocketException, IOException;
    
    Socket beforeHandshake() throws SocketException, IOException;
    
    ThreadLocal<MeteredSocket> getSocketThreadLocal();
}
