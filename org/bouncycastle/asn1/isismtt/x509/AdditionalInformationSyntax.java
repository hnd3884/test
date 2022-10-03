package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.DirectoryString;
import org.bouncycastle.asn1.ASN1Object;

public class AdditionalInformationSyntax extends ASN1Object
{
    private DirectoryString information;
    
    public static AdditionalInformationSyntax getInstance(final Object o) {
        if (o instanceof AdditionalInformationSyntax) {
            return (AdditionalInformationSyntax)o;
        }
        if (o != null) {
            return new AdditionalInformationSyntax(DirectoryString.getInstance(o));
        }
        return null;
    }
    
    private AdditionalInformationSyntax(final DirectoryString information) {
        this.information = information;
    }
    
    public AdditionalInformationSyntax(final String s) {
        this(new DirectoryString(s));
    }
    
    public DirectoryString getInformation() {
        return this.information;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.information.toASN1Primitive();
    }
}
