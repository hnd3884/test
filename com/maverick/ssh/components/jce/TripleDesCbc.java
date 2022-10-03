package com.maverick.ssh.components.jce;

import java.io.IOException;

public class TripleDesCbc extends AbstractJCECipher
{
    public TripleDesCbc() throws IOException {
        super("DESede/CBC/NoPadding", "DESede", 24, "3des-cbc");
    }
}
