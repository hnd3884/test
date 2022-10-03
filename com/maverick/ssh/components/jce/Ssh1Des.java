package com.maverick.ssh.components.jce;

import java.io.IOException;

public class Ssh1Des extends AbstractJCECipher
{
    public Ssh1Des() throws IOException {
        super("DES/CBC/NoPadding", "DES", 8, "ssh1DES");
    }
}
