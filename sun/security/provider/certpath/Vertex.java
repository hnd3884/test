package sun.security.provider.certpath;

import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import java.io.IOException;
import sun.security.x509.KeyIdentifier;
import java.security.cert.CertificateException;
import sun.security.x509.X509CertImpl;
import java.security.cert.X509Certificate;
import sun.security.util.Debug;

public class Vertex
{
    private static final Debug debug;
    private X509Certificate cert;
    private int index;
    private Throwable throwable;
    
    Vertex(final X509Certificate cert) {
        this.cert = cert;
        this.index = -1;
    }
    
    public X509Certificate getCertificate() {
        return this.cert;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    void setIndex(final int index) {
        this.index = index;
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
    
    void setThrowable(final Throwable throwable) {
        this.throwable = throwable;
    }
    
    @Override
    public String toString() {
        return this.certToString() + this.throwableToString() + this.indexToString();
    }
    
    public String certToString() {
        final StringBuilder sb = new StringBuilder();
        X509CertImpl impl;
        try {
            impl = X509CertImpl.toImpl(this.cert);
        }
        catch (final CertificateException ex) {
            if (Vertex.debug != null) {
                Vertex.debug.println("Vertex.certToString() unexpected exception");
                ex.printStackTrace();
            }
            return sb.toString();
        }
        sb.append("Issuer:     ").append(impl.getIssuerX500Principal()).append("\n");
        sb.append("Subject:    ").append(impl.getSubjectX500Principal()).append("\n");
        sb.append("SerialNum:  ").append(impl.getSerialNumber().toString(16)).append("\n");
        sb.append("Expires:    ").append(impl.getNotAfter().toString()).append("\n");
        final boolean[] issuerUniqueID = impl.getIssuerUniqueID();
        if (issuerUniqueID != null) {
            sb.append("IssuerUID:  ");
            final boolean[] array = issuerUniqueID;
            for (int length = array.length, i = 0; i < length; ++i) {
                sb.append(array[i] ? 1 : 0);
            }
            sb.append("\n");
        }
        final boolean[] subjectUniqueID = impl.getSubjectUniqueID();
        if (subjectUniqueID != null) {
            sb.append("SubjectUID: ");
            final boolean[] array2 = subjectUniqueID;
            for (int length2 = array2.length, j = 0; j < length2; ++j) {
                sb.append(array2[j] ? 1 : 0);
            }
            sb.append("\n");
        }
        try {
            final SubjectKeyIdentifierExtension subjectKeyIdentifierExtension = impl.getSubjectKeyIdentifierExtension();
            if (subjectKeyIdentifierExtension != null) {
                sb.append("SubjKeyID:  ").append(subjectKeyIdentifierExtension.get("key_id").toString());
            }
            final AuthorityKeyIdentifierExtension authorityKeyIdentifierExtension = impl.getAuthorityKeyIdentifierExtension();
            if (authorityKeyIdentifierExtension != null) {
                sb.append("AuthKeyID:  ").append(((KeyIdentifier)authorityKeyIdentifierExtension.get("key_id")).toString());
            }
        }
        catch (final IOException ex2) {
            if (Vertex.debug != null) {
                Vertex.debug.println("Vertex.certToString() unexpected exception");
                ex2.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    public String throwableToString() {
        final StringBuilder sb = new StringBuilder("Exception:  ");
        if (this.throwable != null) {
            sb.append(this.throwable.toString());
        }
        else {
            sb.append("null");
        }
        sb.append("\n");
        return sb.toString();
    }
    
    public String moreToString() {
        final StringBuilder sb = new StringBuilder("Last cert?  ");
        sb.append((this.index == -1) ? "Yes" : "No");
        sb.append("\n");
        return sb.toString();
    }
    
    public String indexToString() {
        return "Index:      " + this.index + "\n";
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}
