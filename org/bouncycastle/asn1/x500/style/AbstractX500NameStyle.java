package org.bouncycastle.asn1.x500.style;

import org.bouncycastle.asn1.DERUTF8String;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Encodable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.x500.X500NameStyle;

public abstract class AbstractX500NameStyle implements X500NameStyle
{
    public static Hashtable copyHashTable(final Hashtable hashtable) {
        final Hashtable hashtable2 = new Hashtable();
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            hashtable2.put(nextElement, hashtable.get(nextElement));
        }
        return hashtable2;
    }
    
    private int calcHashCode(final ASN1Encodable asn1Encodable) {
        return IETFUtils.canonicalize(IETFUtils.valueToString(asn1Encodable)).hashCode();
    }
    
    public int calculateHashCode(final X500Name x500Name) {
        int n = 0;
        final RDN[] rdNs = x500Name.getRDNs();
        for (int i = 0; i != rdNs.length; ++i) {
            if (rdNs[i].isMultiValued()) {
                final AttributeTypeAndValue[] typesAndValues = rdNs[i].getTypesAndValues();
                for (int j = 0; j != typesAndValues.length; ++j) {
                    n = (n ^ typesAndValues[j].getType().hashCode() ^ this.calcHashCode(typesAndValues[j].getValue()));
                }
            }
            else {
                n = (n ^ rdNs[i].getFirst().getType().hashCode() ^ this.calcHashCode(rdNs[i].getFirst().getValue()));
            }
        }
        return n;
    }
    
    public ASN1Encodable stringToValue(final ASN1ObjectIdentifier asn1ObjectIdentifier, String substring) {
        if (substring.length() != 0 && substring.charAt(0) == '#') {
            try {
                return IETFUtils.valueFromHexString(substring, 1);
            }
            catch (final IOException ex) {
                throw new ASN1ParsingException("can't recode value for oid " + asn1ObjectIdentifier.getId());
            }
        }
        if (substring.length() != 0 && substring.charAt(0) == '\\') {
            substring = substring.substring(1);
        }
        return this.encodeStringValue(asn1ObjectIdentifier, substring);
    }
    
    protected ASN1Encodable encodeStringValue(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        return new DERUTF8String(s);
    }
    
    public boolean areEqual(final X500Name x500Name, final X500Name x500Name2) {
        final RDN[] rdNs = x500Name.getRDNs();
        final RDN[] rdNs2 = x500Name2.getRDNs();
        if (rdNs.length != rdNs2.length) {
            return false;
        }
        boolean b = false;
        if (rdNs[0].getFirst() != null && rdNs2[0].getFirst() != null) {
            b = !rdNs[0].getFirst().getType().equals(rdNs2[0].getFirst().getType());
        }
        for (int i = 0; i != rdNs.length; ++i) {
            if (!this.foundMatch(b, rdNs[i], rdNs2)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean foundMatch(final boolean b, final RDN rdn, final RDN[] array) {
        if (b) {
            for (int i = array.length - 1; i >= 0; --i) {
                if (array[i] != null && this.rdnAreEqual(rdn, array[i])) {
                    array[i] = null;
                    return true;
                }
            }
        }
        else {
            for (int j = 0; j != array.length; ++j) {
                if (array[j] != null && this.rdnAreEqual(rdn, array[j])) {
                    array[j] = null;
                    return true;
                }
            }
        }
        return false;
    }
    
    protected boolean rdnAreEqual(final RDN rdn, final RDN rdn2) {
        return IETFUtils.rDNAreEqual(rdn, rdn2);
    }
}
