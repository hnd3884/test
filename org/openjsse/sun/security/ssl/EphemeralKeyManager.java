package org.openjsse.sun.security.ssl;

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
    
    KeyPair getRSAKeyPair(final boolean export, final SecureRandom random) {
        int length;
        int index;
        if (export) {
            length = 512;
            index = 0;
        }
        else {
            length = 1024;
            index = 1;
        }
        synchronized (this.keys) {
            KeyPair kp = this.keys[index].getKeyPair();
            if (kp == null) {
                try {
                    final KeyPairGenerator kgen = JsseJce.getKeyPairGenerator("RSA");
                    kgen.initialize(length, random);
                    this.keys[index] = new EphemeralKeyPair(kgen.genKeyPair());
                    kp = this.keys[index].getKeyPair();
                }
                catch (final Exception ex) {}
            }
            return kp;
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
