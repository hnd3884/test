package javapns.communication;

import java.io.IOException;
import java.security.KeyStore;
import java.io.InputStream;

class WrappedKeystore extends InputStream
{
    private final KeyStore keystore;
    
    WrappedKeystore(final KeyStore keystore) {
        this.keystore = keystore;
    }
    
    public KeyStore getKeystore() {
        return this.keystore;
    }
    
    @Override
    public int read() throws IOException {
        return 0;
    }
}
