package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.Debug;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import java.io.IOException;
import sun.security.util.DerOutputStream;
import java.math.BigInteger;

public class CRLNumberExtension extends Extension implements CertAttrSet<String>
{
    public static final String NAME = "CRLNumber";
    public static final String NUMBER = "value";
    private static final String LABEL = "CRL Number";
    private BigInteger crlNumber;
    private String extensionName;
    private String extensionLabel;
    
    private void encodeThis() throws IOException {
        if (this.crlNumber == null) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(this.crlNumber);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public CRLNumberExtension(final int n) throws IOException {
        this(PKIXExtensions.CRLNumber_Id, false, BigInteger.valueOf(n), "CRLNumber", "CRL Number");
    }
    
    public CRLNumberExtension(final BigInteger bigInteger) throws IOException {
        this(PKIXExtensions.CRLNumber_Id, false, bigInteger, "CRLNumber", "CRL Number");
    }
    
    protected CRLNumberExtension(final ObjectIdentifier extensionId, final boolean critical, final BigInteger crlNumber, final String extensionName, final String extensionLabel) throws IOException {
        this.crlNumber = null;
        this.extensionId = extensionId;
        this.critical = critical;
        this.crlNumber = crlNumber;
        this.extensionName = extensionName;
        this.extensionLabel = extensionLabel;
        this.encodeThis();
    }
    
    public CRLNumberExtension(final Boolean b, final Object o) throws IOException {
        this(PKIXExtensions.CRLNumber_Id, b, o, "CRLNumber", "CRL Number");
    }
    
    protected CRLNumberExtension(final ObjectIdentifier extensionId, final Boolean b, final Object o, final String extensionName, final String extensionLabel) throws IOException {
        this.crlNumber = null;
        this.extensionId = extensionId;
        this.critical = b;
        this.extensionValue = (byte[])o;
        this.crlNumber = new DerValue(this.extensionValue).getBigInteger();
        this.extensionName = extensionName;
        this.extensionLabel = extensionLabel;
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!s.equalsIgnoreCase("value")) {
            throw new IOException("Attribute name not recognized by CertAttrSet:" + this.extensionName + ".");
        }
        if (!(o instanceof BigInteger)) {
            throw new IOException("Attribute must be of type BigInteger.");
        }
        this.crlNumber = (BigInteger)o;
        this.encodeThis();
    }
    
    @Override
    public BigInteger get(final String s) throws IOException {
        if (s.equalsIgnoreCase("value")) {
            return this.crlNumber;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:" + this.extensionName + '.');
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("value")) {
            this.crlNumber = null;
            this.encodeThis();
            return;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:" + this.extensionName + ".");
    }
    
    @Override
    public String toString() {
        return super.toString() + this.extensionLabel + ": " + ((this.crlNumber == null) ? "" : Debug.toHexString(this.crlNumber)) + "\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        this.encode(outputStream, PKIXExtensions.CRLNumber_Id, true);
    }
    
    protected void encode(final OutputStream outputStream, final ObjectIdentifier extensionId, final boolean critical) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = extensionId;
            this.critical = critical;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("value");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return this.extensionName;
    }
}
