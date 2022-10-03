package org.bouncycastle.cms.jcajce;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

interface KeyMaterialGenerator
{
    byte[] generateKDFMaterial(final AlgorithmIdentifier p0, final int p1, final byte[] p2);
}
