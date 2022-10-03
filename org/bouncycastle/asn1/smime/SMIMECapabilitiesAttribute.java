package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.Attribute;

public class SMIMECapabilitiesAttribute extends Attribute
{
    public SMIMECapabilitiesAttribute(final SMIMECapabilityVector smimeCapabilityVector) {
        super(SMIMEAttributes.smimeCapabilities, new DERSet(new DERSequence(smimeCapabilityVector.toASN1EncodableVector())));
    }
}
