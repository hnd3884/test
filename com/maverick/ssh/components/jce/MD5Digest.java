package com.maverick.ssh.components.jce;

import java.security.NoSuchAlgorithmException;

public class MD5Digest extends AbstractDigest
{
    public MD5Digest() throws NoSuchAlgorithmException {
        super("MD5");
    }
}
