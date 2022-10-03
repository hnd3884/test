package com.maverick.ssh.components.standalone;

import com.maverick.crypto.digests.HMac;
import com.maverick.crypto.digests.Digest;
import com.maverick.crypto.digests.GeneralHMac;
import com.maverick.crypto.digests.MD5Digest;

public class MD5HMac extends AbstractHmac
{
    public MD5HMac() {
        super("hmac-md5", new GeneralHMac(new MD5Digest()));
    }
}
