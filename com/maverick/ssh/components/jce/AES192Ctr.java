package com.maverick.ssh.components.jce;

import java.io.IOException;

public class AES192Ctr extends AbstractJCECipher
{
    public AES192Ctr() throws IOException {
        super("AES/CTR/NoPadding", "AES", 24, "aes192-ctr");
    }
}
