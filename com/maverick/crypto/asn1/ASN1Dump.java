package com.maverick.crypto.asn1;

import java.util.Enumeration;
import com.maverick.crypto.encoders.Hex;

public class ASN1Dump
{
    private static String b;
    
    public static String _dumpAsString(final String s, final DERObject derObject) {
        if (derObject instanceof ASN1Sequence) {
            final StringBuffer sb = new StringBuffer();
            final Enumeration objects = ((ASN1Sequence)derObject).getObjects();
            final String string = s + ASN1Dump.b;
            sb.append(s);
            if (derObject instanceof BERConstructedSequence) {
                sb.append("BER ConstructedSequence");
            }
            else if (derObject instanceof DERConstructedSequence) {
                sb.append("DER ConstructedSequence");
            }
            else if (derObject instanceof DERSequence) {
                sb.append("DER Sequence");
            }
            else if (derObject instanceof BERSequence) {
                sb.append("BER Sequence");
            }
            else {
                sb.append("Sequence");
            }
            sb.append(System.getProperty("line.separator"));
            while (objects.hasMoreElements()) {
                final Object nextElement = objects.nextElement();
                if (nextElement == null || nextElement.equals(new DERNull())) {
                    sb.append(string);
                    sb.append("NULL");
                    sb.append(System.getProperty("line.separator"));
                }
                else if (nextElement instanceof DERObject) {
                    sb.append(_dumpAsString(string, (DERObject)nextElement));
                }
                else {
                    sb.append(_dumpAsString(string, ((DEREncodable)nextElement).getDERObject()));
                }
            }
            return sb.toString();
        }
        if (derObject instanceof DERTaggedObject) {
            final StringBuffer sb2 = new StringBuffer();
            final String string2 = s + ASN1Dump.b;
            sb2.append(s);
            if (derObject instanceof BERTaggedObject) {
                sb2.append("BER Tagged [");
            }
            else {
                sb2.append("Tagged [");
            }
            final DERTaggedObject derTaggedObject = (DERTaggedObject)derObject;
            sb2.append(Integer.toString(derTaggedObject.getTagNo()));
            sb2.append("]");
            if (!derTaggedObject.isExplicit()) {
                sb2.append(" IMPLICIT ");
            }
            sb2.append(System.getProperty("line.separator"));
            if (derTaggedObject.isEmpty()) {
                sb2.append(string2);
                sb2.append("EMPTY");
                sb2.append(System.getProperty("line.separator"));
            }
            else {
                sb2.append(_dumpAsString(string2, derTaggedObject.getObject()));
            }
            return sb2.toString();
        }
        if (derObject instanceof DERConstructedSet) {
            final StringBuffer sb3 = new StringBuffer();
            final Enumeration objects2 = ((ASN1Set)derObject).getObjects();
            final String string3 = s + ASN1Dump.b;
            sb3.append(s);
            sb3.append("ConstructedSet");
            sb3.append(System.getProperty("line.separator"));
            while (objects2.hasMoreElements()) {
                final Object nextElement2 = objects2.nextElement();
                if (nextElement2 == null) {
                    sb3.append(string3);
                    sb3.append("NULL");
                    sb3.append(System.getProperty("line.separator"));
                }
                else if (nextElement2 instanceof DERObject) {
                    sb3.append(_dumpAsString(string3, (DERObject)nextElement2));
                }
                else {
                    sb3.append(_dumpAsString(string3, ((DEREncodable)nextElement2).getDERObject()));
                }
            }
            return sb3.toString();
        }
        if (derObject instanceof BERSet) {
            final StringBuffer sb4 = new StringBuffer();
            final Enumeration objects3 = ((ASN1Set)derObject).getObjects();
            final String string4 = s + ASN1Dump.b;
            sb4.append(s);
            sb4.append("BER Set");
            sb4.append(System.getProperty("line.separator"));
            while (objects3.hasMoreElements()) {
                final Object nextElement3 = objects3.nextElement();
                if (nextElement3 == null) {
                    sb4.append(string4);
                    sb4.append("NULL");
                    sb4.append(System.getProperty("line.separator"));
                }
                else if (nextElement3 instanceof DERObject) {
                    sb4.append(_dumpAsString(string4, (DERObject)nextElement3));
                }
                else {
                    sb4.append(_dumpAsString(string4, ((DEREncodable)nextElement3).getDERObject()));
                }
            }
            return sb4.toString();
        }
        if (derObject instanceof DERSet) {
            final StringBuffer sb5 = new StringBuffer();
            final Enumeration objects4 = ((ASN1Set)derObject).getObjects();
            final String string5 = s + ASN1Dump.b;
            sb5.append(s);
            sb5.append("DER Set");
            sb5.append(System.getProperty("line.separator"));
            while (objects4.hasMoreElements()) {
                final Object nextElement4 = objects4.nextElement();
                if (nextElement4 == null) {
                    sb5.append(string5);
                    sb5.append("NULL");
                    sb5.append(System.getProperty("line.separator"));
                }
                else if (nextElement4 instanceof DERObject) {
                    sb5.append(_dumpAsString(string5, (DERObject)nextElement4));
                }
                else {
                    sb5.append(_dumpAsString(string5, ((DEREncodable)nextElement4).getDERObject()));
                }
            }
            return sb5.toString();
        }
        if (derObject instanceof DERObjectIdentifier) {
            return s + "ObjectIdentifier(" + ((DERObjectIdentifier)derObject).getId() + ")" + System.getProperty("line.separator");
        }
        if (derObject instanceof DERBoolean) {
            return s + "Boolean(" + ((DERBoolean)derObject).isTrue() + ")" + System.getProperty("line.separator");
        }
        if (derObject instanceof DERInteger) {
            return s + "Integer(" + ((DERInteger)derObject).getValue() + ")" + System.getProperty("line.separator");
        }
        if (derObject instanceof DEROctetString) {
            return s + derObject.toString() + "[" + ((ASN1OctetString)derObject).getOctets().length + "] " + System.getProperty("line.separator");
        }
        if (derObject instanceof DERIA5String) {
            return s + "IA5String(" + ((DERIA5String)derObject).getString() + ") " + System.getProperty("line.separator");
        }
        if (derObject instanceof DERPrintableString) {
            return s + "PrintableString(" + ((DERPrintableString)derObject).getString() + ") " + System.getProperty("line.separator");
        }
        if (derObject instanceof DERVisibleString) {
            return s + "VisibleString(" + ((DERVisibleString)derObject).getString() + ") " + System.getProperty("line.separator");
        }
        if (derObject instanceof DERBMPString) {
            return s + "BMPString(" + ((DERBMPString)derObject).getString() + ") " + System.getProperty("line.separator");
        }
        if (derObject instanceof DERT61String) {
            return s + "T61String(" + ((DERT61String)derObject).getString() + ") " + System.getProperty("line.separator");
        }
        if (derObject instanceof DERUTCTime) {
            return s + "UTCTime(" + ((DERUTCTime)derObject).getTime() + ") " + System.getProperty("line.separator");
        }
        if (derObject instanceof DERUnknownTag) {
            return s + "Unknown " + Integer.toString(((DERUnknownTag)derObject).getTag(), 16) + " " + new String(Hex.encode(((DERUnknownTag)derObject).getData())) + System.getProperty("line.separator");
        }
        return s + derObject.toString() + System.getProperty("line.separator");
    }
    
    public static String dumpAsString(final Object o) {
        if (o instanceof DERObject) {
            return _dumpAsString("", (DERObject)o);
        }
        if (o instanceof DEREncodable) {
            return _dumpAsString("", ((DEREncodable)o).getDERObject());
        }
        return "unknown object type " + o.toString();
    }
    
    static {
        ASN1Dump.b = "    ";
    }
}
