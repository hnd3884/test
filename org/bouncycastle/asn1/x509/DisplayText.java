package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERVisibleString;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class DisplayText extends ASN1Object implements ASN1Choice
{
    public static final int CONTENT_TYPE_IA5STRING = 0;
    public static final int CONTENT_TYPE_BMPSTRING = 1;
    public static final int CONTENT_TYPE_UTF8STRING = 2;
    public static final int CONTENT_TYPE_VISIBLESTRING = 3;
    public static final int DISPLAY_TEXT_MAXIMUM_SIZE = 200;
    int contentType;
    ASN1String contents;
    
    public DisplayText(final int contentType, String substring) {
        if (substring.length() > 200) {
            substring = substring.substring(0, 200);
        }
        switch (this.contentType = contentType) {
            case 0: {
                this.contents = new DERIA5String(substring);
                break;
            }
            case 2: {
                this.contents = new DERUTF8String(substring);
                break;
            }
            case 3: {
                this.contents = new DERVisibleString(substring);
                break;
            }
            case 1: {
                this.contents = new DERBMPString(substring);
                break;
            }
            default: {
                this.contents = new DERUTF8String(substring);
                break;
            }
        }
    }
    
    public DisplayText(String substring) {
        if (substring.length() > 200) {
            substring = substring.substring(0, 200);
        }
        this.contentType = 2;
        this.contents = new DERUTF8String(substring);
    }
    
    private DisplayText(final ASN1String contents) {
        this.contents = contents;
        if (contents instanceof DERUTF8String) {
            this.contentType = 2;
        }
        else if (contents instanceof DERBMPString) {
            this.contentType = 1;
        }
        else if (contents instanceof DERIA5String) {
            this.contentType = 0;
        }
        else {
            if (!(contents instanceof DERVisibleString)) {
                throw new IllegalArgumentException("unknown STRING type in DisplayText");
            }
            this.contentType = 3;
        }
    }
    
    public static DisplayText getInstance(final Object o) {
        if (o instanceof ASN1String) {
            return new DisplayText((ASN1String)o);
        }
        if (o == null || o instanceof DisplayText) {
            return (DisplayText)o;
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    public static DisplayText getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return (ASN1Primitive)this.contents;
    }
    
    public String getString() {
        return this.contents.getString();
    }
}
