package org.bouncycastle.asn1.util;

import org.bouncycastle.util.encoders.Hex;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import java.util.Enumeration;
import org.bouncycastle.asn1.DERExternal;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.BERApplicationSpecific;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERVideotexString;
import org.bouncycastle.asn1.DERGraphicString;
import org.bouncycastle.asn1.DERT61String;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERVisibleString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.ASN1Primitive;

public class ASN1Dump
{
    private static final String TAB = "    ";
    private static final int SAMPLE_SIZE = 32;
    
    static void _dumpAsString(final String s, final boolean b, final ASN1Primitive asn1Primitive, final StringBuffer sb) {
        final String lineSeparator = Strings.lineSeparator();
        if (asn1Primitive instanceof ASN1Sequence) {
            final Enumeration objects = ((ASN1Sequence)asn1Primitive).getObjects();
            final String string = s + "    ";
            sb.append(s);
            if (asn1Primitive instanceof BERSequence) {
                sb.append("BER Sequence");
            }
            else if (asn1Primitive instanceof DERSequence) {
                sb.append("DER Sequence");
            }
            else {
                sb.append("Sequence");
            }
            sb.append(lineSeparator);
            while (objects.hasMoreElements()) {
                final Object nextElement = objects.nextElement();
                if (nextElement == null || nextElement.equals(DERNull.INSTANCE)) {
                    sb.append(string);
                    sb.append("NULL");
                    sb.append(lineSeparator);
                }
                else if (nextElement instanceof ASN1Primitive) {
                    _dumpAsString(string, b, (ASN1Primitive)nextElement, sb);
                }
                else {
                    _dumpAsString(string, b, ((ASN1Encodable)nextElement).toASN1Primitive(), sb);
                }
            }
        }
        else if (asn1Primitive instanceof ASN1TaggedObject) {
            final String string2 = s + "    ";
            sb.append(s);
            if (asn1Primitive instanceof BERTaggedObject) {
                sb.append("BER Tagged [");
            }
            else {
                sb.append("Tagged [");
            }
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Primitive;
            sb.append(Integer.toString(asn1TaggedObject.getTagNo()));
            sb.append(']');
            if (!asn1TaggedObject.isExplicit()) {
                sb.append(" IMPLICIT ");
            }
            sb.append(lineSeparator);
            if (asn1TaggedObject.isEmpty()) {
                sb.append(string2);
                sb.append("EMPTY");
                sb.append(lineSeparator);
            }
            else {
                _dumpAsString(string2, b, asn1TaggedObject.getObject(), sb);
            }
        }
        else if (asn1Primitive instanceof ASN1Set) {
            final Enumeration objects2 = ((ASN1Set)asn1Primitive).getObjects();
            final String string3 = s + "    ";
            sb.append(s);
            if (asn1Primitive instanceof BERSet) {
                sb.append("BER Set");
            }
            else {
                sb.append("DER Set");
            }
            sb.append(lineSeparator);
            while (objects2.hasMoreElements()) {
                final Object nextElement2 = objects2.nextElement();
                if (nextElement2 == null) {
                    sb.append(string3);
                    sb.append("NULL");
                    sb.append(lineSeparator);
                }
                else if (nextElement2 instanceof ASN1Primitive) {
                    _dumpAsString(string3, b, (ASN1Primitive)nextElement2, sb);
                }
                else {
                    _dumpAsString(string3, b, ((ASN1Encodable)nextElement2).toASN1Primitive(), sb);
                }
            }
        }
        else if (asn1Primitive instanceof ASN1OctetString) {
            final ASN1OctetString asn1OctetString = (ASN1OctetString)asn1Primitive;
            if (asn1Primitive instanceof BEROctetString) {
                sb.append(s + "BER Constructed Octet String[" + asn1OctetString.getOctets().length + "] ");
            }
            else {
                sb.append(s + "DER Octet String[" + asn1OctetString.getOctets().length + "] ");
            }
            if (b) {
                sb.append(dumpBinaryDataAsString(s, asn1OctetString.getOctets()));
            }
            else {
                sb.append(lineSeparator);
            }
        }
        else if (asn1Primitive instanceof ASN1ObjectIdentifier) {
            sb.append(s + "ObjectIdentifier(" + ((ASN1ObjectIdentifier)asn1Primitive).getId() + ")" + lineSeparator);
        }
        else if (asn1Primitive instanceof ASN1Boolean) {
            sb.append(s + "Boolean(" + ((ASN1Boolean)asn1Primitive).isTrue() + ")" + lineSeparator);
        }
        else if (asn1Primitive instanceof ASN1Integer) {
            sb.append(s + "Integer(" + ((ASN1Integer)asn1Primitive).getValue() + ")" + lineSeparator);
        }
        else if (asn1Primitive instanceof DERBitString) {
            final DERBitString derBitString = (DERBitString)asn1Primitive;
            sb.append(s + "DER Bit String[" + derBitString.getBytes().length + ", " + derBitString.getPadBits() + "] ");
            if (b) {
                sb.append(dumpBinaryDataAsString(s, derBitString.getBytes()));
            }
            else {
                sb.append(lineSeparator);
            }
        }
        else if (asn1Primitive instanceof DERIA5String) {
            sb.append(s + "IA5String(" + ((DERIA5String)asn1Primitive).getString() + ") " + lineSeparator);
        }
        else if (asn1Primitive instanceof DERUTF8String) {
            sb.append(s + "UTF8String(" + ((DERUTF8String)asn1Primitive).getString() + ") " + lineSeparator);
        }
        else if (asn1Primitive instanceof DERPrintableString) {
            sb.append(s + "PrintableString(" + ((DERPrintableString)asn1Primitive).getString() + ") " + lineSeparator);
        }
        else if (asn1Primitive instanceof DERVisibleString) {
            sb.append(s + "VisibleString(" + ((DERVisibleString)asn1Primitive).getString() + ") " + lineSeparator);
        }
        else if (asn1Primitive instanceof DERBMPString) {
            sb.append(s + "BMPString(" + ((DERBMPString)asn1Primitive).getString() + ") " + lineSeparator);
        }
        else if (asn1Primitive instanceof DERT61String) {
            sb.append(s + "T61String(" + ((DERT61String)asn1Primitive).getString() + ") " + lineSeparator);
        }
        else if (asn1Primitive instanceof DERGraphicString) {
            sb.append(s + "GraphicString(" + ((DERGraphicString)asn1Primitive).getString() + ") " + lineSeparator);
        }
        else if (asn1Primitive instanceof DERVideotexString) {
            sb.append(s + "VideotexString(" + ((DERVideotexString)asn1Primitive).getString() + ") " + lineSeparator);
        }
        else if (asn1Primitive instanceof ASN1UTCTime) {
            sb.append(s + "UTCTime(" + ((ASN1UTCTime)asn1Primitive).getTime() + ") " + lineSeparator);
        }
        else if (asn1Primitive instanceof ASN1GeneralizedTime) {
            sb.append(s + "GeneralizedTime(" + ((ASN1GeneralizedTime)asn1Primitive).getTime() + ") " + lineSeparator);
        }
        else if (asn1Primitive instanceof BERApplicationSpecific) {
            sb.append(outputApplicationSpecific("BER", s, b, asn1Primitive, lineSeparator));
        }
        else if (asn1Primitive instanceof DERApplicationSpecific) {
            sb.append(outputApplicationSpecific("DER", s, b, asn1Primitive, lineSeparator));
        }
        else if (asn1Primitive instanceof ASN1Enumerated) {
            sb.append(s + "DER Enumerated(" + ((ASN1Enumerated)asn1Primitive).getValue() + ")" + lineSeparator);
        }
        else if (asn1Primitive instanceof DERExternal) {
            final DERExternal derExternal = (DERExternal)asn1Primitive;
            sb.append(s + "External " + lineSeparator);
            final String string4 = s + "    ";
            if (derExternal.getDirectReference() != null) {
                sb.append(string4 + "Direct Reference: " + derExternal.getDirectReference().getId() + lineSeparator);
            }
            if (derExternal.getIndirectReference() != null) {
                sb.append(string4 + "Indirect Reference: " + derExternal.getIndirectReference().toString() + lineSeparator);
            }
            if (derExternal.getDataValueDescriptor() != null) {
                _dumpAsString(string4, b, derExternal.getDataValueDescriptor(), sb);
            }
            sb.append(string4 + "Encoding: " + derExternal.getEncoding() + lineSeparator);
            _dumpAsString(string4, b, derExternal.getExternalContent(), sb);
        }
        else {
            sb.append(s + asn1Primitive.toString() + lineSeparator);
        }
    }
    
    private static String outputApplicationSpecific(final String s, final String s2, final boolean b, final ASN1Primitive asn1Primitive, final String s3) {
        final ASN1ApplicationSpecific instance = ASN1ApplicationSpecific.getInstance(asn1Primitive);
        final StringBuffer sb = new StringBuffer();
        if (instance.isConstructed()) {
            try {
                final ASN1Sequence instance2 = ASN1Sequence.getInstance(instance.getObject(16));
                sb.append(s2 + s + " ApplicationSpecific[" + instance.getApplicationTag() + "]" + s3);
                final Enumeration objects = instance2.getObjects();
                while (objects.hasMoreElements()) {
                    _dumpAsString(s2 + "    ", b, (ASN1Primitive)objects.nextElement(), sb);
                }
            }
            catch (final IOException ex) {
                sb.append(ex);
            }
            return sb.toString();
        }
        return s2 + s + " ApplicationSpecific[" + instance.getApplicationTag() + "] (" + Strings.fromByteArray(Hex.encode(instance.getContents())) + ")" + s3;
    }
    
    public static String dumpAsString(final Object o) {
        return dumpAsString(o, false);
    }
    
    public static String dumpAsString(final Object o, final boolean b) {
        final StringBuffer sb = new StringBuffer();
        if (o instanceof ASN1Primitive) {
            _dumpAsString("", b, (ASN1Primitive)o, sb);
        }
        else {
            if (!(o instanceof ASN1Encodable)) {
                return "unknown object type " + o.toString();
            }
            _dumpAsString("", b, ((ASN1Encodable)o).toASN1Primitive(), sb);
        }
        return sb.toString();
    }
    
    private static String dumpBinaryDataAsString(String string, final byte[] array) {
        final String lineSeparator = Strings.lineSeparator();
        final StringBuffer sb = new StringBuffer();
        string += "    ";
        sb.append(lineSeparator);
        for (int i = 0; i < array.length; i += 32) {
            if (array.length - i > 32) {
                sb.append(string);
                sb.append(Strings.fromByteArray(Hex.encode(array, i, 32)));
                sb.append("    ");
                sb.append(calculateAscString(array, i, 32));
                sb.append(lineSeparator);
            }
            else {
                sb.append(string);
                sb.append(Strings.fromByteArray(Hex.encode(array, i, array.length - i)));
                for (int j = array.length - i; j != 32; ++j) {
                    sb.append("  ");
                }
                sb.append("    ");
                sb.append(calculateAscString(array, i, array.length - i));
                sb.append(lineSeparator);
            }
        }
        return sb.toString();
    }
    
    private static String calculateAscString(final byte[] array, final int n, final int n2) {
        final StringBuffer sb = new StringBuffer();
        for (int i = n; i != n + n2; ++i) {
            if (array[i] >= 32 && array[i] <= 126) {
                sb.append((char)array[i]);
            }
        }
        return sb.toString();
    }
}
