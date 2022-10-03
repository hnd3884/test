package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERPrintableString;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.ASN1Primitive;

public abstract class X509NameEntryConverter
{
    protected ASN1Primitive convertHexEncoded(String lowerCase, final int n) throws IOException {
        lowerCase = Strings.toLowerCase(lowerCase);
        final byte[] array = new byte[(lowerCase.length() - n) / 2];
        for (int i = 0; i != array.length; ++i) {
            final char char1 = lowerCase.charAt(i * 2 + n);
            final char char2 = lowerCase.charAt(i * 2 + n + 1);
            if (char1 < 'a') {
                array[i] = (byte)(char1 - '0' << 4);
            }
            else {
                array[i] = (byte)(char1 - 'a' + 10 << 4);
            }
            if (char2 < 'a') {
                final byte[] array2 = array;
                final int n2 = i;
                array2[n2] |= (byte)(char2 - '0');
            }
            else {
                final byte[] array3 = array;
                final int n3 = i;
                array3[n3] |= (byte)(char2 - 'a' + 10);
            }
        }
        return new ASN1InputStream(array).readObject();
    }
    
    protected boolean canBePrintable(final String s) {
        return DERPrintableString.isPrintableString(s);
    }
    
    public abstract ASN1Primitive getConvertedValue(final ASN1ObjectIdentifier p0, final String p1);
}
