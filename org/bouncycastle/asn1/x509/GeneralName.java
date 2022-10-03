package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERTaggedObject;
import java.util.StringTokenizer;
import org.bouncycastle.util.IPAddress;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class GeneralName extends ASN1Object implements ASN1Choice
{
    public static final int otherName = 0;
    public static final int rfc822Name = 1;
    public static final int dNSName = 2;
    public static final int x400Address = 3;
    public static final int directoryName = 4;
    public static final int ediPartyName = 5;
    public static final int uniformResourceIdentifier = 6;
    public static final int iPAddress = 7;
    public static final int registeredID = 8;
    private ASN1Encodable obj;
    private int tag;
    
    @Deprecated
    public GeneralName(final X509Name x509Name) {
        this.obj = X500Name.getInstance(x509Name);
        this.tag = 4;
    }
    
    public GeneralName(final X500Name obj) {
        this.obj = obj;
        this.tag = 4;
    }
    
    public GeneralName(final int tag, final ASN1Encodable obj) {
        this.obj = obj;
        this.tag = tag;
    }
    
    public GeneralName(final int tag, final String s) {
        this.tag = tag;
        if (tag == 1 || tag == 2 || tag == 6) {
            this.obj = new DERIA5String(s);
        }
        else if (tag == 8) {
            this.obj = new ASN1ObjectIdentifier(s);
        }
        else if (tag == 4) {
            this.obj = new X500Name(s);
        }
        else {
            if (tag != 7) {
                throw new IllegalArgumentException("can't process String for tag: " + tag);
            }
            final byte[] generalNameEncoding = this.toGeneralNameEncoding(s);
            if (generalNameEncoding == null) {
                throw new IllegalArgumentException("IP Address is invalid");
            }
            this.obj = new DEROctetString(generalNameEncoding);
        }
    }
    
    public static GeneralName getInstance(final Object o) {
        if (o == null || o instanceof GeneralName) {
            return (GeneralName)o;
        }
        if (o instanceof ASN1TaggedObject) {
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)o;
            final int tagNo = asn1TaggedObject.getTagNo();
            switch (tagNo) {
                case 0: {
                    return new GeneralName(tagNo, ASN1Sequence.getInstance(asn1TaggedObject, false));
                }
                case 1: {
                    return new GeneralName(tagNo, DERIA5String.getInstance(asn1TaggedObject, false));
                }
                case 2: {
                    return new GeneralName(tagNo, DERIA5String.getInstance(asn1TaggedObject, false));
                }
                case 3: {
                    throw new IllegalArgumentException("unknown tag: " + tagNo);
                }
                case 4: {
                    return new GeneralName(tagNo, X500Name.getInstance(asn1TaggedObject, true));
                }
                case 5: {
                    return new GeneralName(tagNo, ASN1Sequence.getInstance(asn1TaggedObject, false));
                }
                case 6: {
                    return new GeneralName(tagNo, DERIA5String.getInstance(asn1TaggedObject, false));
                }
                case 7: {
                    return new GeneralName(tagNo, ASN1OctetString.getInstance(asn1TaggedObject, false));
                }
                case 8: {
                    return new GeneralName(tagNo, ASN1ObjectIdentifier.getInstance(asn1TaggedObject, false));
                }
            }
        }
        if (o instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("unable to parse encoded general name");
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + o.getClass().getName());
    }
    
    public static GeneralName getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1TaggedObject.getInstance(asn1TaggedObject, true));
    }
    
    public int getTagNo() {
        return this.tag;
    }
    
    public ASN1Encodable getName() {
        return this.obj;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.tag);
        sb.append(": ");
        switch (this.tag) {
            case 1:
            case 2:
            case 6: {
                sb.append(DERIA5String.getInstance(this.obj).getString());
                break;
            }
            case 4: {
                sb.append(X500Name.getInstance(this.obj).toString());
                break;
            }
            default: {
                sb.append(this.obj.toString());
                break;
            }
        }
        return sb.toString();
    }
    
    private byte[] toGeneralNameEncoding(final String s) {
        if (IPAddress.isValidIPv6WithNetmask(s) || IPAddress.isValidIPv6(s)) {
            final int index = s.indexOf(47);
            if (index < 0) {
                final byte[] array = new byte[16];
                this.copyInts(this.parseIPv6(s), array, 0);
                return array;
            }
            final byte[] array2 = new byte[32];
            this.copyInts(this.parseIPv6(s.substring(0, index)), array2, 0);
            final String substring = s.substring(index + 1);
            int[] array3;
            if (substring.indexOf(58) > 0) {
                array3 = this.parseIPv6(substring);
            }
            else {
                array3 = this.parseMask(substring);
            }
            this.copyInts(array3, array2, 16);
            return array2;
        }
        else {
            if (!IPAddress.isValidIPv4WithNetmask(s) && !IPAddress.isValidIPv4(s)) {
                return null;
            }
            final int index2 = s.indexOf(47);
            if (index2 < 0) {
                final byte[] array4 = new byte[4];
                this.parseIPv4(s, array4, 0);
                return array4;
            }
            final byte[] array5 = new byte[8];
            this.parseIPv4(s.substring(0, index2), array5, 0);
            final String substring2 = s.substring(index2 + 1);
            if (substring2.indexOf(46) > 0) {
                this.parseIPv4(substring2, array5, 4);
            }
            else {
                this.parseIPv4Mask(substring2, array5, 4);
            }
            return array5;
        }
    }
    
    private void parseIPv4Mask(final String s, final byte[] array, final int n) {
        for (int int1 = Integer.parseInt(s), i = 0; i != int1; ++i) {
            final int n2 = i / 8 + n;
            array[n2] |= (byte)(1 << 7 - i % 8);
        }
    }
    
    private void parseIPv4(final String s, final byte[] array, final int n) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, "./");
        int n2 = 0;
        while (stringTokenizer.hasMoreTokens()) {
            array[n + n2++] = (byte)Integer.parseInt(stringTokenizer.nextToken());
        }
    }
    
    private int[] parseMask(final String s) {
        final int[] array = new int[8];
        for (int int1 = Integer.parseInt(s), i = 0; i != int1; ++i) {
            final int[] array2 = array;
            final int n = i / 16;
            array2[n] |= 1 << 15 - i % 16;
        }
        return array;
    }
    
    private void copyInts(final int[] array, final byte[] array2, final int n) {
        for (int i = 0; i != array.length; ++i) {
            array2[i * 2 + n] = (byte)(array[i] >> 8);
            array2[i * 2 + 1 + n] = (byte)array[i];
        }
    }
    
    private int[] parseIPv6(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ":", true);
        int n = 0;
        final int[] array = new int[8];
        if (s.charAt(0) == ':' && s.charAt(1) == ':') {
            stringTokenizer.nextToken();
        }
        int n2 = -1;
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            if (nextToken.equals(":")) {
                n2 = n;
                array[n++] = 0;
            }
            else if (nextToken.indexOf(46) < 0) {
                array[n++] = Integer.parseInt(nextToken, 16);
                if (!stringTokenizer.hasMoreTokens()) {
                    continue;
                }
                stringTokenizer.nextToken();
            }
            else {
                final StringTokenizer stringTokenizer2 = new StringTokenizer(nextToken, ".");
                array[n++] = (Integer.parseInt(stringTokenizer2.nextToken()) << 8 | Integer.parseInt(stringTokenizer2.nextToken()));
                array[n++] = (Integer.parseInt(stringTokenizer2.nextToken()) << 8 | Integer.parseInt(stringTokenizer2.nextToken()));
            }
        }
        if (n != array.length) {
            System.arraycopy(array, n2, array, array.length - (n - n2), n - n2);
            for (int i = n2; i != array.length - (n - n2); ++i) {
                array[i] = 0;
            }
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.tag == 4) {
            return new DERTaggedObject(true, this.tag, this.obj);
        }
        return new DERTaggedObject(false, this.tag, this.obj);
    }
}
