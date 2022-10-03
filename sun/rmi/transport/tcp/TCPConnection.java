package sun.rmi.transport.tcp;

import sun.rmi.runtime.Log;
import sun.rmi.transport.proxy.RMISocketInfo;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import sun.rmi.transport.Channel;
import java.net.Socket;
import sun.rmi.transport.Connection;

public class TCPConnection implements Connection
{
    private Socket socket;
    private Channel channel;
    private InputStream in;
    private OutputStream out;
    private long expiration;
    private long lastuse;
    private long roundtrip;
    
    TCPConnection(final TCPChannel channel, final Socket socket, final InputStream in, final OutputStream out) {
        this.in = null;
        this.out = null;
        this.expiration = Long.MAX_VALUE;
        this.lastuse = Long.MIN_VALUE;
        this.roundtrip = 5L;
        this.socket = socket;
        this.channel = channel;
        this.in = in;
        this.out = out;
    }
    
    TCPConnection(final TCPChannel tcpChannel, final InputStream inputStream, final OutputStream outputStream) {
        this(tcpChannel, null, inputStream, outputStream);
    }
    
    TCPConnection(final TCPChannel tcpChannel, final Socket socket) {
        this(tcpChannel, socket, null, null);
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.out == null) {
            this.out = new BufferedOutputStream(this.socket.getOutputStream());
        }
        return this.out;
    }
    
    @Override
    public void releaseOutputStream() throws IOException {
        if (this.out != null) {
            this.out.flush();
        }
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.in == null) {
            this.in = new BufferedInputStream(this.socket.getInputStream());
        }
        return this.in;
    }
    
    @Override
    public void releaseInputStream() {
    }
    
    @Override
    public boolean isReusable() {
        return this.socket == null || !(this.socket instanceof RMISocketInfo) || ((RMISocketInfo)this.socket).isReusable();
    }
    
    void setExpiration(final long expiration) {
        this.expiration = expiration;
    }
    
    void setLastUseTime(final long lastuse) {
        this.lastuse = lastuse;
    }
    
    boolean expired(final long n) {
        return this.expiration <= n;
    }
    
    public boolean isDead() {
        final long currentTimeMillis = System.currentTimeMillis();
        if (this.roundtrip > 0L && currentTimeMillis < this.lastuse + this.roundtrip) {
            return false;
        }
        InputStream inputStream;
        OutputStream outputStream;
        try {
            inputStream = this.getInputStream();
            outputStream = this.getOutputStream();
        }
        catch (final IOException ex) {
            return true;
        }
        int read;
        try {
            outputStream.write(82);
            outputStream.flush();
            read = inputStream.read();
        }
        catch (final IOException ex2) {
            TCPTransport.tcpLog.log(Log.VERBOSE, "exception: ", ex2);
            TCPTransport.tcpLog.log(Log.BRIEF, "server ping failed");
            return true;
        }
        if (read == 83) {
            this.roundtrip = (System.currentTimeMillis() - currentTimeMillis) * 2L;
            return false;
        }
        if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
            TCPTransport.tcpLog.log(Log.BRIEF, (read == -1) ? "server has been deactivated" : ("server protocol error: ping response = " + read));
        }
        return true;
    }
    
    @Override
    public void close() throws IOException {
        TCPTransport.tcpLog.log(Log.BRIEF, "close connection");
        if (this.socket != null) {
            this.socket.close();
        }
        else {
            this.in.close();
            this.out.close();
        }
    }
    
    @Override
    public Channel getChannel() {
        return this.channel;
    }
}
