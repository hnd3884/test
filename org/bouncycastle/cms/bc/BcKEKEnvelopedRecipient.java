package org.bouncycastle.cms.bc;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.BufferedBlockCipher;
import java.io.InputStream;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.bc.BcSymmetricKeyUnwrapper;

public class BcKEKEnvelopedRecipient extends BcKEKRecipient
{
    public BcKEKEnvelopedRecipient(final BcSymmetricKeyUnwrapper bcSymmetricKeyUnwrapper) {
        super(bcSymmetricKeyUnwrapper);
    }
    
    public RecipientOperator getRecipientOperator(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CMSException {
        return new RecipientOperator(new InputDecryptor() {
            final /* synthetic */ Object val$dataCipher = EnvelopedDataHelper.createContentCipher(false, this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, array), algorithmIdentifier2);
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }
            
            public InputStream getInputStream(final InputStream inputStream) {
                if (this.val$dataCipher instanceof BufferedBlockCipher) {
                    return (InputStream)new CipherInputStream(inputStream, (BufferedBlockCipher)this.val$dataCipher);
                }
                return (InputStream)new CipherInputStream(inputStream, (StreamCipher)this.val$dataCipher);
            }
        });
    }
}
