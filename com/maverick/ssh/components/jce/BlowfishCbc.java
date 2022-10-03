package com.maverick.ssh.components.jce;

import java.io.IOException;

public class BlowfishCbc extends AbstractJCECipher
{
    public BlowfishCbc() throws IOException {
        super("Blowfish/CBC/NoPadding", "Blowfish", 16, "blowfish-cbc");
    }
}
