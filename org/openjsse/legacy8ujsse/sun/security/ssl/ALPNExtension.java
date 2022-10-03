package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Iterator;
import java.io.IOException;
import java.util.ArrayList;
import javax.net.ssl.SSLProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.net.ssl.SSLException;
import java.util.List;

final class ALPNExtension extends HelloExtension
{
    static final int ALPN_HEADER_LENGTH = 1;
    static final int MAX_APPLICATION_PROTOCOL_LENGTH = 255;
    static final int MAX_APPLICATION_PROTOCOL_LIST_LENGTH = 65535;
    private int listLength;
    private List<String> protocolNames;
    
    ALPNExtension(final String protocolName) throws SSLException {
        this(new String[] { protocolName });
    }
    
    ALPNExtension(final String[] protocolNames) throws SSLException {
        super(ExtensionType.EXT_ALPN);
        this.listLength = 0;
        this.protocolNames = null;
        if (protocolNames.length == 0) {
            throw new IllegalArgumentException("The list of application protocols cannot be empty");
        }
        this.protocolNames = Arrays.asList(protocolNames);
        for (final String p : protocolNames) {
            final int length = p.getBytes(StandardCharsets.UTF_8).length;
            if (length == 0) {
                throw new SSLProtocolException("Application protocol name is empty");
            }
            if (length > 255) {
                throw new SSLProtocolException("Application protocol name is too long: " + p);
            }
            this.listLength += length + 1;
            if (this.listLength > 65535) {
                throw new SSLProtocolException("Application protocol name list is too long");
            }
        }
    }
    
    ALPNExtension(final HandshakeInStream s, final int len) throws IOException {
        super(ExtensionType.EXT_ALPN);
        this.listLength = 0;
        this.protocolNames = null;
        if (len < 2) {
            throw new SSLProtocolException("Invalid " + this.type + " extension: insufficient data (length=" + len + ")");
        }
        this.listLength = s.getInt16();
        if (this.listLength < 2 || this.listLength + 2 != len) {
            throw new SSLProtocolException("Invalid " + this.type + " extension: incorrect list length (length=" + this.listLength + ")");
        }
        int remaining = this.listLength;
        this.protocolNames = new ArrayList<String>();
        while (remaining > 0) {
            final byte[] bytes = s.getBytes8();
            if (bytes.length == 0) {
                throw new SSLProtocolException("Invalid " + this.type + " extension: empty application protocol name");
            }
            final String p = new String(bytes, StandardCharsets.UTF_8);
            this.protocolNames.add(p);
            remaining -= bytes.length + 1;
        }
        if (remaining != 0) {
            throw new SSLProtocolException("Invalid " + this.type + " extension: extra data (length=" + remaining + ")");
        }
    }
    
    List<String> getPeerAPs() {
        return this.protocolNames;
    }
    
    @Override
    int length() {
        return 6 + this.listLength;
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        s.putInt16(this.type.id);
        s.putInt16(this.listLength + 2);
        s.putInt16(this.listLength);
        for (final String p : this.protocolNames) {
            s.putBytes8(p.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.protocolNames == null || this.protocolNames.isEmpty()) {
            sb.append("<empty>");
        }
        else {
            for (final String protocolName : this.protocolNames) {
                sb.append("[" + protocolName + "]");
            }
        }
        return "Extension " + this.type + ", protocol names: " + (Object)sb;
    }
}
