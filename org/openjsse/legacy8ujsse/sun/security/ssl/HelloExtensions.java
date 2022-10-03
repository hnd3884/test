package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.PrintStream;
import java.util.Iterator;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class HelloExtensions
{
    private List<HelloExtension> extensions;
    private int encodedLength;
    
    HelloExtensions() {
        this.extensions = Collections.emptyList();
    }
    
    HelloExtensions(final HandshakeInStream s) throws IOException {
        int len = s.getInt16();
        this.extensions = new ArrayList<HelloExtension>();
        this.encodedLength = len + 2;
        while (len > 0) {
            final int type = s.getInt16();
            final int extlen = s.getInt16();
            final ExtensionType extType = ExtensionType.get(type);
            HelloExtension extension;
            if (extType == ExtensionType.EXT_SERVER_NAME) {
                extension = new ServerNameExtension(s, extlen);
            }
            else if (extType == ExtensionType.EXT_SIGNATURE_ALGORITHMS) {
                extension = new SignatureAlgorithmsExtension(s, extlen);
            }
            else if (extType == ExtensionType.EXT_ELLIPTIC_CURVES) {
                extension = new EllipticCurvesExtension(s, extlen);
            }
            else if (extType == ExtensionType.EXT_EC_POINT_FORMATS) {
                extension = new EllipticPointFormatsExtension(s, extlen);
            }
            else if (extType == ExtensionType.EXT_RENEGOTIATION_INFO) {
                extension = new RenegotiationInfoExtension(s, extlen);
            }
            else if (extType == ExtensionType.EXT_EXTENDED_MASTER_SECRET) {
                extension = new ExtendedMasterSecretExtension(s, extlen);
            }
            else if (extType == ExtensionType.EXT_ALPN) {
                extension = new ALPNExtension(s, extlen);
            }
            else {
                extension = new UnknownExtension(s, extlen, extType);
            }
            this.extensions.add(extension);
            len -= extlen + 4;
        }
        if (len != 0) {
            throw new SSLProtocolException("Error parsing extensions: extra data");
        }
    }
    
    List<HelloExtension> list() {
        return this.extensions;
    }
    
    void add(final HelloExtension ext) {
        if (this.extensions.isEmpty()) {
            this.extensions = new ArrayList<HelloExtension>();
        }
        this.extensions.add(ext);
        this.encodedLength = -1;
    }
    
    HelloExtension get(final ExtensionType type) {
        for (final HelloExtension ext : this.extensions) {
            if (ext.type == type) {
                return ext;
            }
        }
        return null;
    }
    
    int length() {
        if (this.encodedLength >= 0) {
            return this.encodedLength;
        }
        if (this.extensions.isEmpty()) {
            this.encodedLength = 0;
        }
        else {
            this.encodedLength = 2;
            for (final HelloExtension ext : this.extensions) {
                this.encodedLength += ext.length();
            }
        }
        return this.encodedLength;
    }
    
    void send(final HandshakeOutStream s) throws IOException {
        final int length = this.length();
        if (length == 0) {
            return;
        }
        s.putInt16(length - 2);
        for (final HelloExtension ext : this.extensions) {
            ext.send(s);
        }
    }
    
    void print(final PrintStream s) throws IOException {
        for (final HelloExtension ext : this.extensions) {
            s.println(ext.toString());
        }
    }
}
