package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.Digest;

class Utils
{
    static Digest getDigest(final String s) {
        if (s.equals("SHA-1")) {
            return new SHA1Digest();
        }
        if (s.equals("SHA-224")) {
            return new SHA224Digest();
        }
        if (s.equals("SHA-256")) {
            return new SHA256Digest();
        }
        if (s.equals("SHA-384")) {
            return new SHA384Digest();
        }
        if (s.equals("SHA-512")) {
            return new SHA512Digest();
        }
        throw new IllegalArgumentException("unrecognised digest algorithm: " + s);
    }
}
