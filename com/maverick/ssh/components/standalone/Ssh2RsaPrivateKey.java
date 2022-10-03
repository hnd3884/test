package com.maverick.ssh.components.standalone;

import java.math.BigInteger;
import com.maverick.ssh.components.SshRsaPrivateKey;
import com.maverick.crypto.publickey.RsaPrivateKey;

public class Ssh2RsaPrivateKey extends RsaPrivateKey implements SshRsaPrivateKey
{
    public Ssh2RsaPrivateKey(final RsaPrivateKey rsaPrivateKey) {
        super(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
    }
    
    public Ssh2RsaPrivateKey(final BigInteger bigInteger, final BigInteger bigInteger2) {
        super(bigInteger, bigInteger2);
    }
    
    public String getAlgorithm() {
        return "ssh-rsa";
    }
}
