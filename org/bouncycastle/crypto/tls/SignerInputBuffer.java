package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Signer;
import java.io.ByteArrayOutputStream;

class SignerInputBuffer extends ByteArrayOutputStream
{
    void updateSigner(final Signer signer) {
        signer.update(this.buf, 0, this.count);
    }
}
