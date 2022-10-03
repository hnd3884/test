package org.apache.commons.net.finger;

import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import org.apache.commons.net.util.Charsets;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.net.SocketClient;

public class FingerClient extends SocketClient
{
    public static final int DEFAULT_PORT = 79;
    private static final String __LONG_FLAG = "/W ";
    private transient char[] __buffer;
    
    public FingerClient() {
        this.__buffer = new char[1024];
        this.setDefaultPort(79);
    }
    
    public String query(final boolean longOutput, final String username) throws IOException {
        final StringBuilder result = new StringBuilder(this.__buffer.length);
        final BufferedReader input = new BufferedReader(new InputStreamReader(this.getInputStream(longOutput, username), this.getCharset()));
        try {
            while (true) {
                final int read = input.read(this.__buffer, 0, this.__buffer.length);
                if (read <= 0) {
                    break;
                }
                result.append(this.__buffer, 0, read);
            }
        }
        finally {
            input.close();
        }
        return result.toString();
    }
    
    public String query(final boolean longOutput) throws IOException {
        return this.query(longOutput, "");
    }
    
    public InputStream getInputStream(final boolean longOutput, final String username) throws IOException {
        return this.getInputStream(longOutput, username, null);
    }
    
    public InputStream getInputStream(final boolean longOutput, final String username, final String encoding) throws IOException {
        final StringBuilder buffer = new StringBuilder(64);
        if (longOutput) {
            buffer.append("/W ");
        }
        buffer.append(username);
        buffer.append("\r\n");
        final byte[] encodedQuery = buffer.toString().getBytes(Charsets.toCharset(encoding).name());
        final DataOutputStream output = new DataOutputStream(new BufferedOutputStream(this._output_, 1024));
        output.write(encodedQuery, 0, encodedQuery.length);
        output.flush();
        return this._input_;
    }
    
    public InputStream getInputStream(final boolean longOutput) throws IOException {
        return this.getInputStream(longOutput, "");
    }
}
