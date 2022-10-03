package org.bouncycastle.operator.bc;

import java.security.Key;
import org.bouncycastle.operator.GenericKey;

class OperatorUtils
{
    static byte[] getKeyBytes(final GenericKey genericKey) {
        if (genericKey.getRepresentation() instanceof Key) {
            return ((Key)genericKey.getRepresentation()).getEncoded();
        }
        if (genericKey.getRepresentation() instanceof byte[]) {
            return (byte[])genericKey.getRepresentation();
        }
        throw new IllegalArgumentException("unknown generic key type");
    }
}
