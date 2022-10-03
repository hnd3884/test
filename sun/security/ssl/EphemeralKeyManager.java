package sun.security.ssl;

import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.KeyPair;

final class EphemeralKeyManager
{
    private static final int INDEX_RSA512 = 0;
    private static final int INDEX_RSA1024 = 1;
    private final EphemeralKeyPair[] keys;
    
    EphemeralKeyManager() {
        this.keys = new EphemeralKeyPair[] { new EphemeralKeyPair((KeyPair)null), new EphemeralKeyPair((KeyPair)null) };
    }
    
    KeyPair getRSAKeyPair(final boolean b, final SecureRandom secureRandom) {
        int n;
        int n2;
        if (b) {
            n = 512;
            n2 = 0;
        }
        else {
            n = 1024;
            n2 = 1;
        }
        synchronized (this.keys) {
            KeyPair keyPair = this.keys[n2].getKeyPair();
            if (keyPair == null) {
                try {
                    final KeyPairGenerator keyPairGenerator = JsseJce.getKeyPairGenerator("RSA");
                    keyPairGenerator.initialize(n, secureRandom);
                    this.keys[n2] = new EphemeralKeyPair(keyPairGenerator.genKeyPair());
                    keyPair = this.keys[n2].getKeyPair();
                }
                catch (final Exception ex) {}
            }
            return keyPair;
        }
    }
    
    private static class EphemeralKeyPair
    {
        private static final int MAX_USE = 200;
        private static final long USE_INTERVAL = 3600000L;
        private KeyPair keyPair;
        private int uses;
        private long expirationTime;
        
        private EphemeralKeyPair(final KeyPair keyPair) {
            this.keyPair = keyPair;
            this.expirationTime = System.currentTimeMillis() + 3600000L;
        }
        
        private boolean isValid() {
            return this.keyPair != null && this.uses < 200 && System.currentTimeMillis() < this.expirationTime;
        }
        
        private KeyPair getKeyPair() {
            if (!this.isValid()) {
                return this.keyPair = null;
            }
            ++this.uses;
            return this.keyPair;
        }
    }
}
