package org.bouncycastle.x509;

import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.security.Principal;
import java.util.ArrayList;
import org.bouncycastle.asn1.x509.V2Form;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x509.GeneralName;
import java.io.IOException;
import org.bouncycastle.jce.X509Principal;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.util.Selector;
import java.security.cert.CertSelector;

public class AttributeCertificateIssuer implements CertSelector, Selector
{
    final ASN1Encodable form;
    
    public AttributeCertificateIssuer(final AttCertIssuer attCertIssuer) {
        this.form = attCertIssuer.getIssuer();
    }
    
    public AttributeCertificateIssuer(final X500Principal x500Principal) throws IOException {
        this(new X509Principal(x500Principal.getEncoded()));
    }
    
    public AttributeCertificateIssuer(final X509Principal x509Principal) {
        this.form = new V2Form(GeneralNames.getInstance(new DERSequence(new GeneralName(x509Principal))));
    }
    
    private Object[] getNames() {
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
                try {
                    list.add((Object)new X500Principal(names[i].getName().toASN1Primitive().getEncoded()));
                }
                catch (final IOException ex) {
                    throw new RuntimeException("badly formed Name object");
                }
            }
        }
        return list.toArray(new Object[list.size()]);
    }
    
    public Principal[] getPrincipals() {
        final Object[] names = this.getNames();
        final ArrayList list = new ArrayList();
        for (int i = 0; i != names.length; ++i) {
            if (names[i] instanceof Principal) {
                list.add(names[i]);
            }
        }
        return (Principal[])list.toArray(new Principal[list.size()]);
    }
    
    private boolean matchesDN(final X500Principal x500Principal, final GeneralNames generalNames) {
        final GeneralName[] names = generalNames.getNames();
        for (int i = 0; i != names.length; ++i) {
            final GeneralName generalName = names[i];
            if (generalName.getTagNo() == 4) {
                try {
                    if (new X500Principal(generalName.getName().toASN1Primitive().getEncoded()).equals(x500Principal)) {
                        return true;
                    }
                }
                catch (final IOException ex) {}
            }
        }
        return false;
    }
    
    public Object clone() {
        return new AttributeCertificateIssuer(AttCertIssuer.getInstance(this.form));
    }
    
    public boolean match(final Certificate certificate) {
        if (!(certificate instanceof X509Certificate)) {
            return false;
        }
        final X509Certificate x509Certificate = (X509Certificate)certificate;
        if (this.form instanceof V2Form) {
            final V2Form v2Form = (V2Form)this.form;
            if (v2Form.getBaseCertificateID() != null) {
                return v2Form.getBaseCertificateID().getSerial().getValue().equals(x509Certificate.getSerialNumber()) && this.matchesDN(x509Certificate.getIssuerX500Principal(), v2Form.getBaseCertificateID().getIssuer());
            }
            if (this.matchesDN(x509Certificate.getSubjectX500Principal(), v2Form.getIssuerName())) {
                return true;
            }
        }
        else if (this.matchesDN(x509Certificate.getSubjectX500Principal(), (GeneralNames)this.form)) {
            return true;
        }
        return false;
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
        return o instanceof X509Certificate && this.match((Certificate)o);
    }
}
