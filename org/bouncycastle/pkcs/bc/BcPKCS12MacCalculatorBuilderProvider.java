package org.bouncycastle.pkcs.bc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilder;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.bc.BcDigestProvider;
import org.bouncycastle.pkcs.PKCS12MacCalculatorBuilderProvider;

public class BcPKCS12MacCalculatorBuilderProvider implements PKCS12MacCalculatorBuilderProvider
{
    private BcDigestProvider digestProvider;
    
    public BcPKCS12MacCalculatorBuilderProvider(final BcDigestProvider digestProvider) {
        this.digestProvider = digestProvider;
    }
    
    public PKCS12MacCalculatorBuilder get(final AlgorithmIdentifier algorithmIdentifier) {
        return new PKCS12MacCalculatorBuilder() {
            public MacCalculator build(final char[] array) throws OperatorCreationException {
                return PKCS12PBEUtils.createMacCalculator(algorithmIdentifier.getAlgorithm(), BcPKCS12MacCalculatorBuilderProvider.this.digestProvider.get(algorithmIdentifier), PKCS12PBEParams.getInstance((Object)algorithmIdentifier.getParameters()), array);
            }
            
            public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
                return new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), (ASN1Encodable)DERNull.INSTANCE);
            }
        };
    }
}
