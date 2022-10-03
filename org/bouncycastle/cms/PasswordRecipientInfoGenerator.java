package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.util.Arrays;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public abstract class PasswordRecipientInfoGenerator implements RecipientInfoGenerator
{
    protected char[] password;
    private AlgorithmIdentifier keyDerivationAlgorithm;
    private ASN1ObjectIdentifier kekAlgorithm;
    private SecureRandom random;
    private int schemeID;
    private int keySize;
    private int blockSize;
    private PasswordRecipient.PRF prf;
    private byte[] salt;
    private int iterationCount;
    
    protected PasswordRecipientInfoGenerator(final ASN1ObjectIdentifier asn1ObjectIdentifier, final char[] array) {
        this(asn1ObjectIdentifier, array, getKeySize(asn1ObjectIdentifier), PasswordRecipientInformation.BLOCKSIZES.get(asn1ObjectIdentifier));
    }
    
    protected PasswordRecipientInfoGenerator(final ASN1ObjectIdentifier kekAlgorithm, final char[] password, final int keySize, final int blockSize) {
        this.password = password;
        this.schemeID = 1;
        this.kekAlgorithm = kekAlgorithm;
        this.keySize = keySize;
        this.blockSize = blockSize;
        this.prf = PasswordRecipient.PRF.HMacSHA1;
        this.iterationCount = 1024;
    }
    
    private static int getKeySize(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final Integer n = PasswordRecipientInformation.KEYSIZES.get(asn1ObjectIdentifier);
        if (n == null) {
            throw new IllegalArgumentException("cannot find key size for algorithm: " + asn1ObjectIdentifier);
        }
        return n;
    }
    
    public PasswordRecipientInfoGenerator setPasswordConversionScheme(final int schemeID) {
        this.schemeID = schemeID;
        return this;
    }
    
    public PasswordRecipientInfoGenerator setPRF(final PasswordRecipient.PRF prf) {
        this.prf = prf;
        return this;
    }
    
    public PasswordRecipientInfoGenerator setSaltAndIterationCount(final byte[] array, final int iterationCount) {
        this.salt = Arrays.clone(array);
        this.iterationCount = iterationCount;
        return this;
    }
    
    public PasswordRecipientInfoGenerator setSecureRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }
    
    public RecipientInfo generate(final GenericKey genericKey) throws CMSException {
        final byte[] array = new byte[this.blockSize];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(array);
        if (this.salt == null) {
            this.salt = new byte[20];
            this.random.nextBytes(this.salt);
        }
        this.keyDerivationAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(this.salt, this.iterationCount, this.prf.prfAlgID));
        final DEROctetString derOctetString = new DEROctetString(this.generateEncryptedBytes(new AlgorithmIdentifier(this.kekAlgorithm, (ASN1Encodable)new DEROctetString(array)), this.calculateDerivedKey(this.schemeID, this.keyDerivationAlgorithm, this.keySize), genericKey));
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add((ASN1Encodable)this.kekAlgorithm);
        asn1EncodableVector.add((ASN1Encodable)new DEROctetString(array));
        return new RecipientInfo(new PasswordRecipientInfo(this.keyDerivationAlgorithm, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_PWRI_KEK, (ASN1Encodable)new DERSequence(asn1EncodableVector)), (ASN1OctetString)derOctetString));
    }
    
    protected abstract byte[] calculateDerivedKey(final int p0, final AlgorithmIdentifier p1, final int p2) throws CMSException;
    
    protected abstract byte[] generateEncryptedBytes(final AlgorithmIdentifier p0, final byte[] p1, final GenericKey p2) throws CMSException;
}
