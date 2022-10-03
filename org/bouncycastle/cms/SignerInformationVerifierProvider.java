package org.bouncycastle.cms;

import org.bouncycastle.operator.OperatorCreationException;

public interface SignerInformationVerifierProvider
{
    SignerInformationVerifier get(final SignerId p0) throws OperatorCreationException;
}
