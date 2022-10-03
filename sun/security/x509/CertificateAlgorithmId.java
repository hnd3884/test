package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.InputStream;
import java.io.IOException;
import sun.security.util.DerInputStream;

public class CertificateAlgorithmId implements CertAttrSet<String>
{
    private AlgorithmId algId;
    public static final String IDENT = "x509.info.algorithmID";
    public static final String NAME = "algorithmID";
    public static final String ALGORITHM = "algorithm";
    
    public CertificateAlgorithmId(final AlgorithmId algId) {
        this.algId = algId;
    }
    
    public CertificateAlgorithmId(final DerInputStream derInputStream) throws IOException {
        this.algId = AlgorithmId.parse(derInputStream.getDerValue());
    }
    
    public CertificateAlgorithmId(final InputStream inputStream) throws IOException {
        this.algId = AlgorithmId.parse(new DerValue(inputStream));
    }
    
    @Override
    public String toString() {
        if (this.algId == null) {
            return "";
        }
        return this.algId.toString() + ", OID = " + this.algId.getOID().toString() + "\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        this.algId.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof AlgorithmId)) {
            throw new IOException("Attribute must be of type AlgorithmId.");
        }
        if (s.equalsIgnoreCase("algorithm")) {
            this.algId = (AlgorithmId)o;
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateAlgorithmId.");
    }
    
    @Override
    public AlgorithmId get(final String s) throws IOException {
        if (s.equalsIgnoreCase("algorithm")) {
            return this.algId;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateAlgorithmId.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("algorithm")) {
            this.algId = null;
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:CertificateAlgorithmId.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("algorithm");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "algorithmID";
    }
}
