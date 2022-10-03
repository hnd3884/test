package org.bouncycastle.openssl;

import org.bouncycastle.operator.OperatorCreationException;

public interface PEMDecryptorProvider
{
    PEMDecryptor get(final String p0) throws OperatorCreationException;
}
