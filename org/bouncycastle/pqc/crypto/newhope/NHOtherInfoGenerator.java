package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.pqc.crypto.ExchangePair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import java.io.IOException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.SecureRandom;
import org.bouncycastle.crypto.util.DEROtherInfo;

public class NHOtherInfoGenerator
{
    protected final DEROtherInfo.Builder otherInfoBuilder;
    protected final SecureRandom random;
    
    public NHOtherInfoGenerator(final AlgorithmIdentifier algorithmIdentifier, final byte[] array, final byte[] array2, final SecureRandom random) {
        this.otherInfoBuilder = new DEROtherInfo.Builder(algorithmIdentifier, array, array2);
        this.random = random;
    }
    
    private static byte[] getEncoded(final NHPublicKeyParameters nhPublicKeyParameters) {
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PQCObjectIdentifiers.newHope), nhPublicKeyParameters.getPubData()).getEncoded();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    private static NHPublicKeyParameters getPublicKey(final byte[] array) {
        return new NHPublicKeyParameters(SubjectPublicKeyInfo.getInstance(array).getPublicKeyData().getOctets());
    }
    
    public static class PartyU extends NHOtherInfoGenerator
    {
        private AsymmetricCipherKeyPair aKp;
        private NHAgreement agreement;
        
        public PartyU(final AlgorithmIdentifier algorithmIdentifier, final byte[] array, final byte[] array2, final SecureRandom secureRandom) {
            super(algorithmIdentifier, array, array2, secureRandom);
            this.agreement = new NHAgreement();
            final NHKeyPairGenerator nhKeyPairGenerator = new NHKeyPairGenerator();
            nhKeyPairGenerator.init(new KeyGenerationParameters(secureRandom, 2048));
            this.aKp = nhKeyPairGenerator.generateKeyPair();
            this.agreement.init(this.aKp.getPrivate());
        }
        
        public NHOtherInfoGenerator withSuppPubInfo(final byte[] array) {
            this.otherInfoBuilder.withSuppPubInfo(array);
            return this;
        }
        
        public byte[] getSuppPrivInfoPartA() {
            return getEncoded((NHPublicKeyParameters)this.aKp.getPublic());
        }
        
        public DEROtherInfo generate(final byte[] array) {
            this.otherInfoBuilder.withSuppPrivInfo(this.agreement.calculateAgreement(getPublicKey(array)));
            return this.otherInfoBuilder.build();
        }
    }
    
    public static class PartyV extends NHOtherInfoGenerator
    {
        public PartyV(final AlgorithmIdentifier algorithmIdentifier, final byte[] array, final byte[] array2, final SecureRandom secureRandom) {
            super(algorithmIdentifier, array, array2, secureRandom);
        }
        
        public NHOtherInfoGenerator withSuppPubInfo(final byte[] array) {
            this.otherInfoBuilder.withSuppPubInfo(array);
            return this;
        }
        
        public byte[] getSuppPrivInfoPartB(final byte[] array) {
            final ExchangePair generateExchange = new NHExchangePairGenerator(this.random).generateExchange(getPublicKey(array));
            this.otherInfoBuilder.withSuppPrivInfo(generateExchange.getSharedValue());
            return getEncoded((NHPublicKeyParameters)generateExchange.getPublicKey());
        }
        
        public DEROtherInfo generate() {
            return this.otherInfoBuilder.build();
        }
    }
}
