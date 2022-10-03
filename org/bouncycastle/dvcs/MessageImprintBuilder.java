package org.bouncycastle.dvcs;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.DigestCalculator;

public class MessageImprintBuilder
{
    private final DigestCalculator digestCalculator;
    
    public MessageImprintBuilder(final DigestCalculator digestCalculator) {
        this.digestCalculator = digestCalculator;
    }
    
    public MessageImprint build(final byte[] array) throws DVCSException {
        try {
            final OutputStream outputStream = this.digestCalculator.getOutputStream();
            outputStream.write(array);
            outputStream.close();
            return new MessageImprint(new DigestInfo(this.digestCalculator.getAlgorithmIdentifier(), this.digestCalculator.getDigest()));
        }
        catch (final Exception ex) {
            throw new DVCSException("unable to build MessageImprint: " + ex.getMessage(), ex);
        }
    }
}
