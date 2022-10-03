package com.maverick.ssh.components.standalone;

import com.maverick.crypto.publickey.Rsa;
import java.math.BigInteger;
import com.maverick.ssh.components.SshRsaPrivateCrtKey;
import com.maverick.crypto.publickey.RsaPrivateCrtKey;

public class Ssh2RsaPrivateCrtKey extends RsaPrivateCrtKey implements SshRsaPrivateCrtKey
{
    public Ssh2RsaPrivateCrtKey(final RsaPrivateCrtKey rsaPrivateCrtKey) {
        super(rsaPrivateCrtKey.getModulus(), rsaPrivateCrtKey.getPublicExponent(), rsaPrivateCrtKey.getPrivateExponent(), rsaPrivateCrtKey.getPrimeP(), rsaPrivateCrtKey.getPrimeQ(), rsaPrivateCrtKey.getPrimeExponentP(), rsaPrivateCrtKey.getPrimeExponentQ(), rsaPrivateCrtKey.getCrtCoefficient());
    }
    
    public Ssh2RsaPrivateCrtKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6) {
        this(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, Rsa.getPrimeExponent(bigInteger3, bigInteger4), Rsa.getPrimeExponent(bigInteger3, bigInteger5), bigInteger6);
    }
    
    public Ssh2RsaPrivateCrtKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6, final BigInteger bigInteger7, final BigInteger bigInteger8) {
        super(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6, bigInteger7, bigInteger8);
    }
    
    public String getAlgorithm() {
        return "ssh-rsa";
    }
    
    public BigInteger doPrivate(BigInteger doPrivate) {
        doPrivate = Rsa.doPrivate(doPrivate, this.getModulus(), this.getPrivateExponent());
        return Rsa.removePKCS1(doPrivate, 2);
    }
}
