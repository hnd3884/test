package org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2;

public final class DigestFactory
{
    public static Digest createSHA256() {
        return new SHA256Digest();
    }
}
