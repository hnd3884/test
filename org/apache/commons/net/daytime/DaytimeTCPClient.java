package org.apache.commons.net.daytime;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.net.SocketClient;

public final class DaytimeTCPClient extends SocketClient
{
    public static final int DEFAULT_PORT = 13;
    private final char[] __buffer;
    
    public DaytimeTCPClient() {
        this.__buffer = new char[64];
        this.setDefaultPort(13);
    }
    
    public String getTime() throws IOException {
        final StringBuilder result = new StringBuilder(this.__buffer.length);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(this._input_, this.getCharset()));
        while (true) {
            final int read = reader.read(this.__buffer, 0, this.__buffer.length);
            if (read <= 0) {
                break;
            }
            result.append(this.__buffer, 0, read);
        }
        return result.toString();
    }
}
