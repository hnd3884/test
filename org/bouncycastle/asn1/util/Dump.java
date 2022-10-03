package org.bouncycastle.asn1.util;

import org.bouncycastle.asn1.ASN1Primitive;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.FileInputStream;

public class Dump
{
    public static void main(final String[] array) throws Exception {
        ASN1Primitive object;
        while ((object = new ASN1InputStream(new FileInputStream(array[0])).readObject()) != null) {
            System.out.println(ASN1Dump.dumpAsString(object));
        }
    }
}
