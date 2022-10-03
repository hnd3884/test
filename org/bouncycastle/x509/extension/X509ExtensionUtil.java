package org.bouncycastle.x509.extension;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Sequence;
import java.util.ArrayList;
import java.util.Collections;
import java.security.cert.CertificateParsingException;
import org.bouncycastle.asn1.x509.X509Extension;
import java.util.Collection;
import java.security.cert.X509Certificate;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;

public class X509ExtensionUtil
{
    public static ASN1Primitive fromExtensionValue(final byte[] array) throws IOException {
        return ASN1Primitive.fromByteArray(((ASN1OctetString)ASN1Primitive.fromByteArray(array)).getOctets());
    }
    
    public static Collection getIssuerAlternativeNames(final X509Certificate x509Certificate) throws CertificateParsingException {
        return getAlternativeNames(x509Certificate.getExtensionValue(X509Extension.issuerAlternativeName.getId()));
    }
    
    public static Collection getSubjectAlternativeNames(final X509Certificate x509Certificate) throws CertificateParsingException {
        return getAlternativeNames(x509Certificate.getExtensionValue(X509Extension.subjectAlternativeName.getId()));
    }
    
    private static Collection getAlternativeNames(final byte[] array) throws CertificateParsingException {
        if (array == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            final ArrayList list = new ArrayList();
            final Enumeration objects = ASN1Sequence.getInstance(fromExtensionValue(array)).getObjects();
            while (objects.hasMoreElements()) {
                final GeneralName instance = GeneralName.getInstance(objects.nextElement());
                final ArrayList list2 = new ArrayList();
                list2.add(Integers.valueOf(instance.getTagNo()));
                switch (instance.getTagNo()) {
                    case 0:
                    case 3:
                    case 5: {
                        list2.add(instance.getName().toASN1Primitive());
                        break;
                    }
                    case 4: {
                        list2.add(X500Name.getInstance(instance.getName()).toString());
                        break;
                    }
                    case 1:
                    case 2:
                    case 6: {
                        list2.add(((ASN1String)instance.getName()).getString());
                        break;
                    }
                    case 8: {
                        list2.add(ASN1ObjectIdentifier.getInstance(instance.getName()).getId());
                        break;
                    }
                    case 7: {
                        list2.add(ASN1OctetString.getInstance(instance.getName()).getOctets());
                        break;
                    }
                    default: {
                        throw new IOException("Bad tag number: " + instance.getTagNo());
                    }
                }
                list.add(list2);
            }
            return Collections.unmodifiableCollection((Collection<?>)list);
        }
        catch (final Exception ex) {
            throw new CertificateParsingException(ex.getMessage());
        }
    }
}
