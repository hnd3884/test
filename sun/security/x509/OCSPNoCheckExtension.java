package sun.security.x509;

import java.util.Enumeration;
import java.io.IOException;

public class OCSPNoCheckExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.OCSPNoCheck";
    public static final String NAME = "OCSPNoCheck";
    
    public OCSPNoCheckExtension() throws IOException {
        this.extensionId = PKIXExtensions.OCSPNoCheck_Id;
        this.critical = false;
        this.extensionValue = new byte[0];
    }
    
    public OCSPNoCheckExtension(final Boolean b, final Object o) throws IOException {
        this.extensionId = PKIXExtensions.OCSPNoCheck_Id;
        this.critical = b;
        this.extensionValue = new byte[0];
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        throw new IOException("No attribute is allowed by CertAttrSet:OCSPNoCheckExtension.");
    }
    
    @Override
    public Object get(final String s) throws IOException {
        throw new IOException("No attribute is allowed by CertAttrSet:OCSPNoCheckExtension.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        throw new IOException("No attribute is allowed by CertAttrSet:OCSPNoCheckExtension.");
    }
    
    @Override
    public Enumeration<String> getElements() {
        return new AttributeNameEnumeration().elements();
    }
    
    @Override
    public String getName() {
        return "OCSPNoCheck";
    }
}
