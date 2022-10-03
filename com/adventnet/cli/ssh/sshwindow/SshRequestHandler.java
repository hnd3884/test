package com.adventnet.cli.ssh.sshwindow;

import java.io.IOException;
import com.adventnet.cli.transport.ssh.SshTransportProviderImpl;

public class SshRequestHandler
{
    protected SshTransportProviderImpl stpi;
    protected String host;
    protected int port;
    
    SshRequestHandler() {
        this.port = 22;
        this.stpi = new SshTransportProviderImpl();
    }
    
    public void connect(final String s, final int n) throws IOException {
        try {
            this.stpi.connect(s, n);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void connect(final String s, final int n, final String s2) throws IOException {
        try {
            this.stpi.connect(s, n, s2);
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public byte[] readAsByteArray() throws IOException {
        final byte[] array = new byte[256];
        final int read = this.stpi.read(array);
        if (read > 0) {
            final byte[] array2 = new byte[read];
            System.arraycopy(array, 0, array2, 0, read);
            return array2;
        }
        if (read < 0) {
            throw new IOException("cannot read socket");
        }
        return null;
    }
    
    public int read(final byte[] array) throws IOException {
        final int read = this.stpi.read(array);
        if (read > 0) {
            return read;
        }
        return 0;
    }
    
    public void disconnect() throws IOException {
        try {
            this.stpi.close();
            this.stpi = null;
        }
        catch (final Exception ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    public void write(final byte b) throws IOException {
        this.stpi.write(new byte[] { b });
    }
    
    public void write(byte[] bytes) throws IOException {
        final String s = "\n";
        final String s2 = new String(bytes);
        final String s3 = "\r";
        if (s2.equals(s)) {
            bytes = s3.getBytes();
        }
        this.stpi.write(bytes);
    }
    
    public void setSocketTimeout(final int socketTimeout) throws IOException {
        this.stpi.setSocketTimeout(socketTimeout);
    }
    
    public String login(final String s, final String s2) throws IOException {
        return this.stpi.login(s, s2, null);
    }
}
