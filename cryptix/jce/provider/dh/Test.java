package cryptix.jce.provider.dh;

import java.security.KeyPair;
import java.math.BigInteger;
import java.security.Key;
import javax.crypto.KeyAgreement;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.Security;
import cryptix.jce.provider.CryptixCrypto;

public final class Test
{
    public static void main(final String[] argv) {
        while (true) {
            work();
        }
    }
    
    public static void work() {
        Security.addProvider(new CryptixCrypto());
        try {
            final KeyPairGenerator kpg1 = KeyPairGenerator.getInstance("DH");
            kpg1.initialize(1536);
            final KeyPair pair1 = kpg1.generateKeyPair();
            final KeyPairGenerator kpg2 = KeyPairGenerator.getInstance("DH");
            kpg2.initialize(1536);
            final KeyPair pair2 = kpg2.generateKeyPair();
            final KeyAgreement ka1 = KeyAgreement.getInstance("DH");
            ka1.init(pair1.getPrivate());
            ka1.doPhase(pair2.getPublic(), true);
            final byte[] master1 = ka1.generateSecret();
            final KeyAgreement ka2 = KeyAgreement.getInstance("DH");
            ka2.init(pair2.getPrivate());
            ka2.doPhase(pair1.getPublic(), true);
            final byte[] master2 = ka2.generateSecret();
            if (master1.length != master2.length) {
                throw new RuntimeException();
            }
            for (int i = 0; i < master1.length; ++i) {
                if (master1[i] != master2[i]) {
                    throw new RuntimeException();
                }
            }
            System.out.println();
            System.out.println("master1.length: " + master1.length);
            if (master1.length != 192) {
                throw new RuntimeException();
            }
            System.out.println("master1: " + new BigInteger(1, master1).toString(16));
            System.out.println("Done");
        }
        catch (final Throwable t) {
            t.printStackTrace();
        }
    }
}
