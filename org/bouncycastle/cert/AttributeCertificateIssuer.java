package org.bouncycastle.cert;

import java.util.ArrayList;
import org.bouncycastle.asn1.x509.V2Form;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.util.Selector;

public class AttributeCertificateIssuer implements Selector
{
    final ASN1Encodable form;
    
    public AttributeCertificateIssuer(final AttCertIssuer attCertIssuer) {
        this.form = attCertIssuer.getIssuer();
    }
    
    public AttributeCertificateIssuer(final X500Name x500Name) {
        this.form = (ASN1Encodable)new V2Form(new GeneralNames(new GeneralName(x500Name)));
    }
    
    public X500Name[] getNames() {
        GeneralNames issuerName;
        if (this.form instanceof V2Form) {
            issuerName = ((V2Form)this.form).getIssuerName();
        }
        else {
            issuerName = (GeneralNames)this.form;
        }
        final GeneralName[] names = issuerName.getNames();
        final ArrayList list = new ArrayList(names.length);
        for (int i = 0; i != names.length; ++i) {
            if (names[i].getTagNo() == 4) {
                list.add((Object)X500Name.getInstance((Object)names[i].getName()));
            }
        }
        return (X500Name[])list.toArray((Object[])new X500Name[list.size()]);
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
    
    public Object clone() {
        return new AttributeCertificateIssuer(AttCertIssuer.getInstance((Object)this.form));
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof AttributeCertificateIssuer && this.form.equals(((AttributeCertificateIssuer)o).form));
    }
    
    @Override
    public int hashCode() {
        return this.form.hashCode();
    }
    
    public boolean match(final Object o) {
        if (!(o instanceof X509CertificateHolder)) {
            return false;
        }
        final X509CertificateHolder x509CertificateHolder = (X509CertificateHolder)o;
        if (this.form instanceof V2Form) {
            final V2Form v2Form = (V2Form)this.form;
            if (v2Form.getBaseCertificateID() != null) {
                return v2Form.getBaseCertificateID().getSerial().getValue().equals(x509CertificateHolder.getSerialNumber()) && this.matchesDN(x509CertificateHolder.getIssuer(), v2Form.getBaseCertificateID().getIssuer());
            }
            if (this.matchesDN(x509CertificateHolder.getSubject(), v2Form.getIssuerName())) {
                return true;
            }
        }
        else if (this.matchesDN(x509CertificateHolder.getSubject(), (GeneralNames)this.form)) {
            return true;
        }
        return false;
    }
}
