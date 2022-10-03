package org.bouncycastle.cms.jcajce;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.io.MacOutputStream;
import java.io.OutputStream;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.operator.GenericKey;
import javax.crypto.Mac;
import java.security.Key;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class JcePasswordAuthenticatedRecipient extends JcePasswordRecipient
{
    public JcePasswordAuthenticatedRecipient(final char[] array) {
        super(array);
    }
    
    public RecipientOperator getRecipientOperator(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array, final byte[] array2) throws CMSException {
        final Key secretKey = this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, array, array2);
        return new RecipientOperator(new MacCalculator() {
            final /* synthetic */ Mac val$dataMac = JcePasswordAuthenticatedRecipient.this.helper.createContentMac(secretKey, algorithmIdentifier2);
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }
            
            public GenericKey getKey() {
                return new JceGenericKey(algorithmIdentifier2, secretKey);
            }
            
            public OutputStream getOutputStream() {
                return (OutputStream)new MacOutputStream(this.val$dataMac);
            }
            
            public byte[] getMac() {
                return this.val$dataMac.doFinal();
            }
        });
    }
}
