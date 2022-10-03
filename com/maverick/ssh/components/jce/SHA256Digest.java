package com.maverick.ssh.components.jce;

import java.security.NoSuchAlgorithmException;

public class SHA256Digest extends AbstractDigest
{
    public SHA256Digest() throws NoSuchAlgorithmException {
        super("SHA-256");
    }
}
