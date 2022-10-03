package com.maverick.ssh.components.standalone;

import com.maverick.crypto.digests.HMac;
import com.maverick.crypto.digests.Digest;
import com.maverick.crypto.digests.GeneralHMac;
import com.maverick.crypto.digests.MD5Digest;

public class MD5HMac96 extends AbstractHmac
{
    public MD5HMac96() {
        super("hmac-md5-96", new GeneralHMac(new MD5Digest(), 12));
    }
}
