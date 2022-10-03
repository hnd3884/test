package com.maverick.ssh.components.jce;

import java.io.IOException;

public class AES256Cbc extends AbstractJCECipher
{
    public AES256Cbc() throws IOException {
        super("AES/CBC/NoPadding", "AES", 32, "aes256-cbc");
    }
}
