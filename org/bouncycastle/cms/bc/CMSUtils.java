package org.bouncycastle.cms.bc;

import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.operator.GenericKey;

class CMSUtils
{
    static CipherParameters getBcKey(final GenericKey genericKey) {
        if (genericKey.getRepresentation() instanceof CipherParameters) {
            return (CipherParameters)genericKey.getRepresentation();
        }
        if (genericKey.getRepresentation() instanceof byte[]) {
            return (CipherParameters)new KeyParameter((byte[])genericKey.getRepresentation());
        }
        throw new IllegalArgumentException("unknown generic key type");
    }
}
