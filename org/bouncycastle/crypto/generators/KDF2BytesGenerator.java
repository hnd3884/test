package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.Digest;

public class KDF2BytesGenerator extends BaseKDFBytesGenerator
{
    public KDF2BytesGenerator(final Digest digest) {
        super(1, digest);
    }
}
