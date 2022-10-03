package org.bouncycastle.dvcs;

import java.io.IOException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;

public class SignedDVCSMessageGenerator
{
    private final CMSSignedDataGenerator signedDataGen;
    
    public SignedDVCSMessageGenerator(final CMSSignedDataGenerator signedDataGen) {
        this.signedDataGen = signedDataGen;
    }
    
    public CMSSignedData build(final DVCSMessage dvcsMessage) throws DVCSException {
        try {
            return this.signedDataGen.generate(new CMSProcessableByteArray(dvcsMessage.getContentType(), dvcsMessage.getContent().toASN1Primitive().getEncoded("DER")), true);
        }
        catch (final CMSException ex) {
            throw new DVCSException("Could not sign DVCS request", ex);
        }
        catch (final IOException ex2) {
            throw new DVCSException("Could not encode DVCS request", ex2);
        }
    }
}
