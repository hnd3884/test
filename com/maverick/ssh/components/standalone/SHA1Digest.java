package com.maverick.ssh.components.standalone;

import com.maverick.ssh.components.Digest;
import com.maverick.crypto.digests.Hash;

public class SHA1Digest extends Hash implements Digest
{
    public SHA1Digest() {
        super("SHA-1");
    }
}
