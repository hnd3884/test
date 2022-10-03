package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.IOException;
import javax.net.ssl.SSLProtocolException;

final class ExtendedMasterSecretExtension extends HelloExtension
{
    ExtendedMasterSecretExtension() {
        super(ExtensionType.EXT_EXTENDED_MASTER_SECRET);
    }
    
    ExtendedMasterSecretExtension(final HandshakeInStream s, final int len) throws IOException {
        super(ExtensionType.EXT_EXTENDED_MASTER_SECRET);
        if (len != 0) {
            throw new SSLProtocolException("Invalid " + this.type + " extension");
        }
    }
    
    @Override
    int length() {
        return 4;
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        s.putInt16(this.type.id);
        s.putInt16(0);
    }
    
    @Override
    public String toString() {
        return "Extension " + this.type;
    }
}
