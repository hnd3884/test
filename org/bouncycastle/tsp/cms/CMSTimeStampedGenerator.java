package org.bouncycastle.tsp.cms;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.cms.Attributes;
import java.net.URI;
import org.bouncycastle.asn1.cms.MetaData;

public class CMSTimeStampedGenerator
{
    protected MetaData metaData;
    protected URI dataUri;
    
    public void setDataUri(final URI dataUri) {
        this.dataUri = dataUri;
    }
    
    public void setMetaData(final boolean b, final String s, final String s2) {
        this.setMetaData(b, s, s2, null);
    }
    
    public void setMetaData(final boolean b, final String s, final String s2, final Attributes attributes) {
        DERUTF8String derutf8String = null;
        if (s != null) {
            derutf8String = new DERUTF8String(s);
        }
        DERIA5String deria5String = null;
        if (s2 != null) {
            deria5String = new DERIA5String(s2);
        }
        this.setMetaData(b, derutf8String, deria5String, attributes);
    }
    
    private void setMetaData(final boolean b, final DERUTF8String derutf8String, final DERIA5String deria5String, final Attributes attributes) {
        this.metaData = new MetaData(ASN1Boolean.getInstance(b), derutf8String, deria5String, attributes);
    }
    
    public void initialiseMessageImprintDigestCalculator(final DigestCalculator digestCalculator) throws CMSException {
        new MetaDataUtil(this.metaData).initialiseMessageImprintDigestCalculator(digestCalculator);
    }
}
