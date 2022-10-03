package sun.security.x509;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerOutputStream;
import java.security.cert.CRLReason;

public class CRLReasonCodeExtension extends Extension implements CertAttrSet<String>
{
    public static final String NAME = "CRLReasonCode";
    public static final String REASON = "reason";
    private static CRLReason[] values;
    private int reasonCode;
    
    private void encodeThis() throws IOException {
        if (this.reasonCode == 0) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putEnumerated(this.reasonCode);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public CRLReasonCodeExtension(final int n) throws IOException {
        this(false, n);
    }
    
    public CRLReasonCodeExtension(final boolean critical, final int reasonCode) throws IOException {
        this.reasonCode = 0;
        this.extensionId = PKIXExtensions.ReasonCode_Id;
        this.critical = critical;
        this.reasonCode = reasonCode;
        this.encodeThis();
    }
    
    public CRLReasonCodeExtension(final Boolean b, final Object o) throws IOException {
        this.reasonCode = 0;
        this.extensionId = PKIXExtensions.ReasonCode_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        this.reasonCode = new DerValue(this.extensionValue).getEnumerated();
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof Integer)) {
            throw new IOException("Attribute must be of type Integer.");
        }
        if (s.equalsIgnoreCase("reason")) {
            this.reasonCode = (int)o;
            this.encodeThis();
            return;
        }
        throw new IOException("Name not supported by CRLReasonCodeExtension");
    }
    
    @Override
    public Integer get(final String s) throws IOException {
        if (s.equalsIgnoreCase("reason")) {
            return new Integer(this.reasonCode);
        }
        throw new IOException("Name not supported by CRLReasonCodeExtension");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("reason")) {
            this.reasonCode = 0;
            this.encodeThis();
            return;
        }
        throw new IOException("Name not supported by CRLReasonCodeExtension");
    }
    
    @Override
    public String toString() {
        return super.toString() + "    Reason Code: " + this.getReasonCode();
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.ReasonCode_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("reason");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "CRLReasonCode";
    }
    
    public CRLReason getReasonCode() {
        if (this.reasonCode > 0 && this.reasonCode < CRLReasonCodeExtension.values.length) {
            return CRLReasonCodeExtension.values[this.reasonCode];
        }
        return CRLReason.UNSPECIFIED;
    }
    
    static {
        CRLReasonCodeExtension.values = CRLReason.values();
    }
}
