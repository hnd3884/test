package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface PasswordRecipient extends Recipient
{
    public static final int PKCS5_SCHEME2 = 0;
    public static final int PKCS5_SCHEME2_UTF8 = 1;
    
    byte[] calculateDerivedKey(final int p0, final AlgorithmIdentifier p1, final int p2) throws CMSException;
    
    RecipientOperator getRecipientOperator(final AlgorithmIdentifier p0, final AlgorithmIdentifier p1, final byte[] p2, final byte[] p3) throws CMSException;
    
    int getPasswordConversionScheme();
    
    char[] getPassword();
    
    public static final class PRF
    {
        public static final PRF HMacSHA1;
        public static final PRF HMacSHA224;
        public static final PRF HMacSHA256;
        public static final PRF HMacSHA384;
        public static final PRF HMacSHA512;
        private final String hmac;
        final AlgorithmIdentifier prfAlgID;
        
        private PRF(final String hmac, final AlgorithmIdentifier prfAlgID) {
            this.hmac = hmac;
            this.prfAlgID = prfAlgID;
        }
        
        public String getName() {
            return this.hmac;
        }
        
        public AlgorithmIdentifier getAlgorithmID() {
            return this.prfAlgID;
        }
        
        static {
            HMacSHA1 = new PRF("HMacSHA1", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, (ASN1Encodable)DERNull.INSTANCE));
            HMacSHA224 = new PRF("HMacSHA224", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, (ASN1Encodable)DERNull.INSTANCE));
            HMacSHA256 = new PRF("HMacSHA256", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, (ASN1Encodable)DERNull.INSTANCE));
            HMacSHA384 = new PRF("HMacSHA384", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, (ASN1Encodable)DERNull.INSTANCE));
            HMacSHA512 = new PRF("HMacSHA512", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, (ASN1Encodable)DERNull.INSTANCE));
        }
    }
}
