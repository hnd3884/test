package org.bouncycastle.tsp.cms;

import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.ASN1String;
import java.io.IOException;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.cms.MetaData;

class MetaDataUtil
{
    private final MetaData metaData;
    
    MetaDataUtil(final MetaData metaData) {
        this.metaData = metaData;
    }
    
    void initialiseMessageImprintDigestCalculator(final DigestCalculator digestCalculator) throws CMSException {
        if (this.metaData != null && this.metaData.isHashProtected()) {
            try {
                digestCalculator.getOutputStream().write(this.metaData.getEncoded("DER"));
            }
            catch (final IOException ex) {
                throw new CMSException("unable to initialise calculator from metaData: " + ex.getMessage(), ex);
            }
        }
    }
    
    String getFileName() {
        if (this.metaData != null) {
            return this.convertString((ASN1String)this.metaData.getFileName());
        }
        return null;
    }
    
    String getMediaType() {
        if (this.metaData != null) {
            return this.convertString((ASN1String)this.metaData.getMediaType());
        }
        return null;
    }
    
    Attributes getOtherMetaData() {
        if (this.metaData != null) {
            return this.metaData.getOtherMetaData();
        }
        return null;
    }
    
    private String convertString(final ASN1String asn1String) {
        if (asn1String != null) {
            return asn1String.toString();
        }
        return null;
    }
}
