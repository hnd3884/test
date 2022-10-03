package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.Attribute;

public class SMIMEEncryptionKeyPreferenceAttribute extends Attribute
{
    public SMIMEEncryptionKeyPreferenceAttribute(final IssuerAndSerialNumber issuerAndSerialNumber) {
        super(SMIMEAttributes.encrypKeyPref, new DERSet(new DERTaggedObject(false, 0, issuerAndSerialNumber)));
    }
    
    public SMIMEEncryptionKeyPreferenceAttribute(final RecipientKeyIdentifier recipientKeyIdentifier) {
        super(SMIMEAttributes.encrypKeyPref, new DERSet(new DERTaggedObject(false, 1, recipientKeyIdentifier)));
    }
    
    public SMIMEEncryptionKeyPreferenceAttribute(final ASN1OctetString asn1OctetString) {
        super(SMIMEAttributes.encrypKeyPref, new DERSet(new DERTaggedObject(false, 2, asn1OctetString)));
    }
}
