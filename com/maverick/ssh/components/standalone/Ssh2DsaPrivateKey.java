package com.maverick.ssh.components.standalone;

import com.maverick.crypto.publickey.Dsa;
import com.maverick.ssh.components.SshDsaPublicKey;
import java.math.BigInteger;
import com.maverick.ssh.components.SshDsaPrivateKey;
import com.maverick.crypto.publickey.DsaPrivateKey;

public class Ssh2DsaPrivateKey extends DsaPrivateKey implements SshDsaPrivateKey
{
    public Ssh2DsaPrivateKey(final DsaPrivateKey dsaPrivateKey) {
        super(dsaPrivateKey.getP(), dsaPrivateKey.getQ(), dsaPrivateKey.getG(), dsaPrivateKey.getX());
    }
    
    public Ssh2DsaPrivateKey(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) {
        super(bigInteger, bigInteger2, bigInteger3, bigInteger4);
    }
    
    public SshDsaPublicKey getPublicKey() {
        return new Ssh2DsaPublicKey(super.p, super.q, super.g, Dsa.generatePublicKey(super.g, super.p, super.x));
    }
    
    public String getAlgorithm() {
        return "ssh-dss";
    }
}
