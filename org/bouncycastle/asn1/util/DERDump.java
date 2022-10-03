package org.bouncycastle.asn1.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;

public class DERDump extends ASN1Dump
{
    public static String dumpAsString(final ASN1Primitive asn1Primitive) {
        final StringBuffer sb = new StringBuffer();
        ASN1Dump._dumpAsString("", false, asn1Primitive, sb);
        return sb.toString();
    }
    
    public static String dumpAsString(final ASN1Encodable asn1Encodable) {
        final StringBuffer sb = new StringBuffer();
        ASN1Dump._dumpAsString("", false, asn1Encodable.toASN1Primitive(), sb);
        return sb.toString();
    }
}
