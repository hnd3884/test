package org.bouncycastle.util.test;

import org.bouncycastle.util.encoders.Hex;

public class TestRandomData extends FixedSecureRandom
{
    public TestRandomData(final String s) {
        super(new Source[] { new Data(Hex.decode(s)) });
    }
    
    public TestRandomData(final byte[] array) {
        super(new Source[] { new Data(array) });
    }
}
