package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;

final class EllipticPointFormatsExtension extends HelloExtension
{
    static final int FMT_UNCOMPRESSED = 0;
    static final int FMT_ANSIX962_COMPRESSED_PRIME = 1;
    static final int FMT_ANSIX962_COMPRESSED_CHAR2 = 2;
    static final HelloExtension DEFAULT;
    private final byte[] formats;
    
    private EllipticPointFormatsExtension(final byte[] formats) {
        super(ExtensionType.EXT_EC_POINT_FORMATS);
        this.formats = formats;
    }
    
    EllipticPointFormatsExtension(final HandshakeInStream s, final int len) throws IOException {
        super(ExtensionType.EXT_EC_POINT_FORMATS);
        this.formats = s.getBytes8();
        boolean uncompressed = false;
        for (final int format : this.formats) {
            if (format == 0) {
                uncompressed = true;
                break;
            }
        }
        if (!uncompressed) {
            throw new SSLProtocolException("Peer does not support uncompressed points");
        }
    }
    
    @Override
    int length() {
        return 5 + this.formats.length;
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        s.putInt16(this.type.id);
        s.putInt16(this.formats.length + 1);
        s.putBytes8(this.formats);
    }
    
    private static String toString(final byte format) {
        final int f = format & 0xFF;
        switch (f) {
            case 0: {
                return "uncompressed";
            }
            case 1: {
                return "ansiX962_compressed_prime";
            }
            case 2: {
                return "ansiX962_compressed_char2";
            }
            default: {
                return "unknown-" + f;
            }
        }
    }
    
    @Override
    public String toString() {
        final List<String> list = new ArrayList<String>();
        for (final byte format : this.formats) {
            list.add(toString(format));
        }
        return "Extension " + this.type + ", formats: " + list;
    }
    
    static {
        DEFAULT = new EllipticPointFormatsExtension(new byte[] { 0 });
    }
}
