package com.adventnet.telnet.telnetwindow;

import java.io.IOException;

public interface TelnetInterface
{
    void write(final byte[] p0) throws IOException;
    
    void connect(final String p0, final int p1) throws IOException;
    
    void disconnect() throws IOException;
    
    int read(final byte[] p0) throws IOException;
    
    void setSocketTimeout(final int p0) throws IOException;
    
    void connect(final String p0, final int p1, final String p2) throws IOException;
    
    String login(final String p0, final String p1) throws IOException;
}
