package com.maverick.ssh.components.jce;

import java.security.NoSuchAlgorithmException;

public class SHA1Digest extends AbstractDigest
{
    public SHA1Digest() throws NoSuchAlgorithmException {
        super("SHA-1");
    }
}
