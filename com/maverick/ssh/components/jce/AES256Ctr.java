package com.maverick.ssh.components.jce;

import java.io.IOException;

public class AES256Ctr extends AbstractJCECipher
{
    public AES256Ctr() throws IOException {
        super("AES/CTR/NoPadding", "AES", 32, "aes256-ctr");
    }
}
