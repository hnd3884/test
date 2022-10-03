package com.maverick.ssh.components.jce;

import java.io.IOException;

public class TripleDesCtr extends AbstractJCECipher
{
    public TripleDesCtr() throws IOException {
        super("DESede/CTR/NoPadding", "DESede", 24, "3des-ctr");
    }
}
