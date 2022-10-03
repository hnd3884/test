package cryptix.jce.provider;

import cryptix.jce.provider.random.DevRandom;
import java.security.Provider;

public final class CryptixRandom extends Provider
{
    private static final String NAME = "CryptixRandom";
    private static final String INFO = "Cryptix JCE Randomness Provider";
    private static final double VERSION = 1.2;
    
    public CryptixRandom() {
        super("CryptixRandom", 1.2, "Cryptix JCE Randomness Provider");
        if (DevRandom.isAvailable()) {
            this.put("SecureRandom.DevRandom", "cryptix.jce.provider.random.DevRandom");
        }
    }
}
