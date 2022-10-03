package com.maverick.ssh.components.standalone;

import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.crypto.digests.HMac;
import com.maverick.crypto.digests.Digest;
import com.maverick.crypto.digests.GeneralHMac;
import com.maverick.crypto.digests.SHA1Digest;

public class SHA1HMac extends AbstractHmac
{
    public SHA1HMac() {
        super("hmac-sha1", new GeneralHMac(new SHA1Digest()));
    }
    
    public void init(final byte[] array) throws SshException {
        final byte[] array2 = new byte[System.getProperty("miscomputes.ssh2.hmac.keys", "false").equalsIgnoreCase("true") ? 16 : this.getMacLength()];
        System.arraycopy(array, 0, array2, 0, array2.length);
        try {
            super.c.init(array2);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
}
