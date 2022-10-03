package com.maverick.ssh.components.jce;

import java.io.IOException;

public class AES192Cbc extends AbstractJCECipher
{
    public AES192Cbc() throws IOException {
        super("AES/CBC/NoPadding", "AES", 24, "aes192-cbc");
    }
}
