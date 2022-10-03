package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERIA5String;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class X509DefaultEntryConverter extends X509NameEntryConverter
{
    @Override
    public ASN1Primitive getConvertedValue(final ASN1ObjectIdentifier asn1ObjectIdentifier, String substring) {
        if (substring.length() != 0 && substring.charAt(0) == '#') {
            try {
                return this.convertHexEncoded(substring, 1);
            }
            catch (final IOException ex) {
                throw new RuntimeException("can't recode value for oid " + asn1ObjectIdentifier.getId());
            }
        }
        if (substring.length() != 0 && substring.charAt(0) == '\\') {
            substring = substring.substring(1);
        }
        if (asn1ObjectIdentifier.equals(X509Name.EmailAddress) || asn1ObjectIdentifier.equals(X509Name.DC)) {
            return new DERIA5String(substring);
        }
        if (asn1ObjectIdentifier.equals(X509Name.DATE_OF_BIRTH)) {
            return new DERGeneralizedTime(substring);
        }
        if (asn1ObjectIdentifier.equals(X509Name.C) || asn1ObjectIdentifier.equals(X509Name.SN) || asn1ObjectIdentifier.equals(X509Name.DN_QUALIFIER) || asn1ObjectIdentifier.equals(X509Name.TELEPHONE_NUMBER)) {
            return new DERPrintableString(substring);
        }
        return new DERUTF8String(substring);
    }
}
