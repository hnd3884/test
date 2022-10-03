package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.IOException;

final class UnknownExtension extends HelloExtension
{
    private final byte[] data;
    
    UnknownExtension(final HandshakeInStream s, final int len, final ExtensionType type) throws IOException {
        super(type);
        this.data = new byte[len];
        if (len != 0) {
            s.read(this.data);
        }
    }
    
    @Override
    int length() {
        return 4 + this.data.length;
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        s.putInt16(this.type.id);
        s.putBytes16(this.data);
    }
    
    @Override
    public String toString() {
        return "Unsupported extension " + this.type + ", data: " + Debug.toString(this.data);
    }
}
