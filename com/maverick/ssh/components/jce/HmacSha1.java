package com.maverick.ssh.components.jce;

import com.maverick.ssh.SshException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;

public class HmacSha1 extends AbstractHmac
{
    public HmacSha1() {
        super("HmacSha1", 20);
    }
    
    public String getAlgorithm() {
        return "hmac-sha1";
    }
    
    public void init(final byte[] array) throws SshException {
        try {
            super.mac = ((JCEProvider.getProviderForAlgorithm(super.jceAlgorithm) == null) ? Mac.getInstance(super.jceAlgorithm) : Mac.getInstance(super.jceAlgorithm, JCEProvider.getProviderForAlgorithm(super.jceAlgorithm)));
            final byte[] array2 = new byte[System.getProperty("miscomputes.ssh2.hmac.keys", "false").equalsIgnoreCase("true") ? 16 : 20];
            System.arraycopy(array, 0, array2, 0, array2.length);
            super.mac.init(new SecretKeySpec(array2, super.jceAlgorithm));
        }
        catch (final Throwable t) {
            throw new SshException(t);
        }
    }
}
