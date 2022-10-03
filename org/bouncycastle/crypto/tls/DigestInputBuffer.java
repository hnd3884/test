package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Digest;
import java.io.ByteArrayOutputStream;

class DigestInputBuffer extends ByteArrayOutputStream
{
    void updateDigest(final Digest digest) {
        digest.update(this.buf, 0, this.count);
    }
}
