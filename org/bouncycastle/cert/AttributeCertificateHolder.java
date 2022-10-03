package org.bouncycastle.cert;

import java.io.OutputStream;
import org.bouncycastle.operator.DigestCalculator;
import java.util.ArrayList;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.ObjectDigestInfo;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.ASN1Integer;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Holder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.util.Selector;

public class AttributeCertificateHolder implements Selector
{
    private static DigestCalculatorProvider digestCalculatorProvider;
    final Holder holder;
    
    AttributeCertificateHolder(final ASN1Sequence asn1Sequence) {
        this.holder = Holder.getInstance((Object)asn1Sequence);
    }
    
    public AttributeCertificateHolder(final X500Name x500Name, final BigInteger bigInteger) {
        this.holder = new Holder(new IssuerSerial(this.generateGeneralNames(x500Name), new ASN1Integer(bigInteger)));
    }
    
    public AttributeCertificateHolder(final X509CertificateHolder x509CertificateHolder) {
        this.holder = new Holder(new IssuerSerial(this.generateGeneralNames(x509CertificateHolder.getIssuer()), new ASN1Integer(x509CertificateHolder.getSerialNumber())));
    }
    
    public AttributeCertificateHolder(final X500Name x500Name) {
        this.holder = new Holder(this.generateGeneralNames(x500Name));
    }
    
    public AttributeCertificateHolder(final int n, final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1ObjectIdentifier asn1ObjectIdentifier2, final byte[] array) {
        this.holder = new Holder(new ObjectDigestInfo(n, asn1ObjectIdentifier2, new AlgorithmIdentifier(asn1ObjectIdentifier), Arrays.clone(array)));
    }
    
    public int getDigestedObjectType() {
        if (this.holder.getObjectDigestInfo() != null) {
            return this.holder.getObjectDigestInfo().getDigestedObjectType().getValue().intValue();
        }
        return -1;
    }
    
    public AlgorithmIdentifier getDigestAlgorithm() {
        if (this.holder.getObjectDigestInfo() != null) {
            return this.holder.getObjectDigestInfo().getDigestAlgorithm();
        }
        return null;
    }
    
    public byte[] getObjectDigest() {
        if (this.holder.getObjectDigestInfo() != null) {
            return this.holder.getObjectDigestInfo().getObjectDigest().getBytes();
        }
        return null;
    }
    
    public ASN1ObjectIdentifier getOtherObjectTypeID() {
        if (this.holder.getObjectDigestInfo() != null) {
            new ASN1ObjectIdentifier(this.holder.getObjectDigestInfo().getOtherObjectTypeID().getId());
        }
        return null;
    }
    
    private GeneralNames generateGeneralNames(final X500Name x500Name) {
        return new GeneralNames(new GeneralName(x500Name));
    }
    
    private boolean matchesDN(final X500Name x500Name, final GeneralNames generalNames) {
        final GeneralName[] names = generalNames.getNames();
        for (int i = 0; i != names.length; ++i) {
            final GeneralName generalName = names[i];
            if (generalName.getTagNo() == 4 && X500Name.getInstance((Object)generalName.getName()).equals((Object)x500Name)) {
                return true;
            }
        }
        return false;
    }
    
    private X500Name[] getPrincipals(final GeneralName[] array) {
        final ArrayList list = new ArrayList(array.length);
        for (int i = 0; i != array.length; ++i) {
            if (array[i].getTagNo() == 4) {
                list.add(X500Name.getInstance((Object)array[i].getName()));
            }
        }
        return (X500Name[])list.toArray(new X500Name[list.size()]);
    }
    
    public X500Name[] getEntityNames() {
        if (this.holder.getEntityName() != null) {
            return this.getPrincipals(this.holder.getEntityName().getNames());
        }
        return null;
    }
    
    public X500Name[] getIssuer() {
        if (this.holder.getBaseCertificateID() != null) {
            return this.getPrincipals(this.holder.getBaseCertificateID().getIssuer().getNames());
        }
        return null;
    }
    
    public BigInteger getSerialNumber() {
        if (this.holder.getBaseCertificateID() != null) {
            return this.holder.getBaseCertificateID().getSerial().getValue();
        }
        return null;
    }
    
    public Object clone() {
        return new AttributeCertificateHolder((ASN1Sequence)this.holder.toASN1Primitive());
    }
    
    public boolean match(final Object o) {
        if (!(o instanceof X509CertificateHolder)) {
            return false;
        }
        final X509CertificateHolder x509CertificateHolder = (X509CertificateHolder)o;
        if (this.holder.getBaseCertificateID() != null) {
            return this.holder.getBaseCertificateID().getSerial().getValue().equals(x509CertificateHolder.getSerialNumber()) && this.matchesDN(x509CertificateHolder.getIssuer(), this.holder.getBaseCertificateID().getIssuer());
        }
        if (this.holder.getEntityName() != null && this.matchesDN(x509CertificateHolder.getSubject(), this.holder.getEntityName())) {
            return true;
        }
        if (this.holder.getObjectDigestInfo() != null) {
            try {
                final DigestCalculator value = AttributeCertificateHolder.digestCalculatorProvider.get(this.holder.getObjectDigestInfo().getDigestAlgorithm());
                final OutputStream outputStream = value.getOutputStream();
                switch (this.getDigestedObjectType()) {
                    case 0: {
                        outputStream.write(x509CertificateHolder.getSubjectPublicKeyInfo().getEncoded());
                        break;
                    }
                    case 1: {
                        outputStream.write(x509CertificateHolder.getEncoded());
                        break;
                    }
                }
                outputStream.close();
                if (!Arrays.areEqual(value.getDigest(), this.getObjectDigest())) {
                    return false;
                }
            }
            catch (final Exception ex) {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof AttributeCertificateHolder && this.holder.equals((Object)((AttributeCertificateHolder)o).holder));
    }
    
    @Override
    public int hashCode() {
        return this.holder.hashCode();
    }
    
    public static void setDigestCalculatorProvider(final DigestCalculatorProvider digestCalculatorProvider) {
        AttributeCertificateHolder.digestCalculatorProvider = digestCalculatorProvider;
    }
}
