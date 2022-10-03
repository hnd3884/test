package com.sun.jndi.dns;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;

class Tcp
{
    private Socket sock;
    InputStream in;
    OutputStream out;
    
    Tcp(final InetAddress inetAddress, final int n) throws IOException {
        (this.sock = new Socket(inetAddress, n)).setTcpNoDelay(true);
        this.out = new BufferedOutputStream(this.sock.getOutputStream());
        this.in = new BufferedInputStream(this.sock.getInputStream());
    }
    
    void close() throws IOException {
        this.sock.close();
    }
}
