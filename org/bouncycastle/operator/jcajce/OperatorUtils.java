package org.bouncycastle.operator.jcajce;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import org.bouncycastle.operator.GenericKey;

class OperatorUtils
{
    static Key getJceKey(final GenericKey genericKey) {
        if (genericKey.getRepresentation() instanceof Key) {
            return (Key)genericKey.getRepresentation();
        }
        if (genericKey.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[])genericKey.getRepresentation(), "ENC");
        }
        throw new IllegalArgumentException("unknown generic key type");
    }
}
