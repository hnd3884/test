package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class POPODecKeyChallContent extends ASN1Object
{
    private ASN1Sequence content;
    
    private POPODecKeyChallContent(final ASN1Sequence content) {
        this.content = content;
    }
    
    public static POPODecKeyChallContent getInstance(final Object o) {
        if (o instanceof POPODecKeyChallContent) {
            return (POPODecKeyChallContent)o;
        }
        if (o != null) {
            return new POPODecKeyChallContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public Challenge[] toChallengeArray() {
        final Challenge[] array = new Challenge[this.content.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = Challenge.getInstance(this.content.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
