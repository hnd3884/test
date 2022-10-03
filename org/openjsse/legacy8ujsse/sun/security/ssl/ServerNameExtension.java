package org.openjsse.legacy8ujsse.sun.security.ssl;

import javax.net.ssl.SNIMatcher;
import java.util.Collection;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLProtocolException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.io.IOException;
import java.util.Collections;
import javax.net.ssl.SNIServerName;
import java.util.Map;

final class ServerNameExtension extends HelloExtension
{
    static final int NAME_HEADER_LENGTH = 3;
    private Map<Integer, SNIServerName> sniMap;
    private int listLength;
    
    ServerNameExtension() throws IOException {
        super(ExtensionType.EXT_SERVER_NAME);
        this.listLength = 0;
        this.sniMap = Collections.emptyMap();
    }
    
    ServerNameExtension(final List<SNIServerName> serverNames) throws IOException {
        super(ExtensionType.EXT_SERVER_NAME);
        this.listLength = 0;
        this.sniMap = new LinkedHashMap<Integer, SNIServerName>();
        for (final SNIServerName serverName : serverNames) {
            if (this.sniMap.put(serverName.getType(), serverName) != null) {
                throw new RuntimeException("Duplicated server name of type " + serverName.getType());
            }
            this.listLength += serverName.getEncoded().length + 3;
        }
        if (this.listLength == 0) {
            throw new RuntimeException("The ServerNameList cannot be empty");
        }
    }
    
    ServerNameExtension(final HandshakeInStream s, final int len) throws IOException {
        super(ExtensionType.EXT_SERVER_NAME);
        int remains = len;
        if (len >= 2) {
            this.listLength = s.getInt16();
            if (this.listLength == 0 || this.listLength + 2 != len) {
                throw new SSLProtocolException("Invalid " + this.type + " extension");
            }
            remains -= 2;
            this.sniMap = new LinkedHashMap<Integer, SNIServerName>();
            while (remains > 0) {
                final int code = s.getInt8();
                final byte[] encoded = s.getBytes16();
                SNIServerName serverName = null;
                Label_0300: {
                    switch (code) {
                        case 0: {
                            if (encoded.length == 0) {
                                throw new SSLProtocolException("Empty HostName in server name indication");
                            }
                            try {
                                serverName = new SNIHostName(encoded);
                                break Label_0300;
                            }
                            catch (final IllegalArgumentException iae) {
                                final SSLProtocolException spe = new SSLProtocolException("Illegal server name, type=host_name(" + code + "), name=" + new String(encoded, StandardCharsets.UTF_8) + ", value=" + Debug.toString(encoded));
                                spe.initCause(iae);
                                throw spe;
                            }
                            break;
                        }
                    }
                    try {
                        serverName = new UnknownServerName(code, encoded);
                    }
                    catch (final IllegalArgumentException iae) {
                        final SSLProtocolException spe = new SSLProtocolException("Illegal server name, type=(" + code + "), value=" + Debug.toString(encoded));
                        spe.initCause(iae);
                        throw spe;
                    }
                }
                if (this.sniMap.put(serverName.getType(), serverName) != null) {
                    throw new SSLProtocolException("Duplicated server name of type " + serverName.getType());
                }
                remains -= encoded.length + 3;
            }
        }
        else if (len == 0) {
            this.listLength = 0;
            this.sniMap = Collections.emptyMap();
        }
        if (remains != 0) {
            throw new SSLProtocolException("Invalid server_name extension");
        }
    }
    
    List<SNIServerName> getServerNames() {
        if (this.sniMap != null && !this.sniMap.isEmpty()) {
            return Collections.unmodifiableList((List<? extends SNIServerName>)new ArrayList<SNIServerName>(this.sniMap.values()));
        }
        return Collections.emptyList();
    }
    
    boolean isMatched(final Collection<SNIMatcher> matchers) {
        if (this.sniMap != null && !this.sniMap.isEmpty()) {
            for (final SNIMatcher matcher : matchers) {
                final SNIServerName sniName = this.sniMap.get(matcher.getType());
                if (sniName != null && !matcher.matches(sniName)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    boolean isIdentical(final List<SNIServerName> other) {
        if (other.size() == this.sniMap.size()) {
            for (final SNIServerName sniInOther : other) {
                final SNIServerName sniName = this.sniMap.get(sniInOther.getType());
                if (sniName == null || !sniInOther.equals(sniName)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    int length() {
        return (this.listLength == 0) ? 4 : (6 + this.listLength);
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        s.putInt16(this.type.id);
        if (this.listLength == 0) {
            s.putInt16(this.listLength);
        }
        else {
            s.putInt16(this.listLength + 2);
            s.putInt16(this.listLength);
            for (final SNIServerName sniName : this.sniMap.values()) {
                s.putInt8(sniName.getType());
                s.putBytes16(sniName.getEncoded());
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        for (final SNIServerName sniName : this.sniMap.values()) {
            buffer.append("[" + sniName + "]");
        }
        return "Extension " + this.type + ", server_name: " + (Object)buffer;
    }
    
    private static class UnknownServerName extends SNIServerName
    {
        UnknownServerName(final int code, final byte[] encoded) {
            super(code, encoded);
        }
    }
}
