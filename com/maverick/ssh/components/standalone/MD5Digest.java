package com.maverick.ssh.components.standalone;

import com.maverick.ssh.components.Digest;
import com.maverick.crypto.digests.Hash;

public class MD5Digest extends Hash implements Digest
{
    public MD5Digest() {
        super("MD5");
    }
}
