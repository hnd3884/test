package org.bouncycastle.openssl;

import org.bouncycastle.operator.OperatorCreationException;
import java.io.IOException;

public class PEMEncryptedKeyPair
{
    private final String dekAlgName;
    private final byte[] iv;
    private final byte[] keyBytes;
    private final PEMKeyPairParser parser;
    
    PEMEncryptedKeyPair(final String dekAlgName, final byte[] iv, final byte[] keyBytes, final PEMKeyPairParser parser) {
        this.dekAlgName = dekAlgName;
        this.iv = iv;
        this.keyBytes = keyBytes;
        this.parser = parser;
    }
    
    public PEMKeyPair decryptKeyPair(final PEMDecryptorProvider pemDecryptorProvider) throws IOException {
        try {
            return this.parser.parse(pemDecryptorProvider.get(this.dekAlgName).decrypt(this.keyBytes, this.iv));
        }
        catch (final IOException ex) {
            throw ex;
        }
        catch (final OperatorCreationException ex2) {
            throw new PEMException("cannot create extraction operator: " + ex2.getMessage(), ex2);
        }
        catch (final Exception ex3) {
            throw new PEMException("exception processing key pair: " + ex3.getMessage(), ex3);
        }
    }
}
