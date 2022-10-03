package sun.security.x509;

import java.util.Enumeration;
import java.io.OutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerOutputStream;

public class BasicConstraintsExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.BasicConstraints";
    public static final String NAME = "BasicConstraints";
    public static final String IS_CA = "is_ca";
    public static final String PATH_LEN = "path_len";
    private boolean ca;
    private int pathLen;
    
    private void encodeThis() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        if (this.ca) {
            derOutputStream2.putBoolean(this.ca);
            if (this.pathLen >= 0) {
                derOutputStream2.putInteger(this.pathLen);
            }
        }
        derOutputStream.write((byte)48, derOutputStream2);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public BasicConstraintsExtension(final boolean b, final int n) throws IOException {
        this(b, b, n);
    }
    
    public BasicConstraintsExtension(final Boolean b, final boolean ca, final int pathLen) throws IOException {
        this.ca = false;
        this.pathLen = -1;
        this.ca = ca;
        this.pathLen = pathLen;
        this.extensionId = PKIXExtensions.BasicConstraints_Id;
        this.critical = b;
        this.encodeThis();
    }
    
    public BasicConstraintsExtension(final Boolean b, final Object o) throws IOException {
        this.ca = false;
        this.pathLen = -1;
        this.extensionId = PKIXExtensions.BasicConstraints_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding of BasicConstraints");
        }
        if (derValue.data == null || derValue.data.available() == 0) {
            return;
        }
        final DerValue derValue2 = derValue.data.getDerValue();
        if (derValue2.tag != 1) {
            return;
        }
        this.ca = derValue2.getBoolean();
        if (derValue.data.available() == 0) {
            this.pathLen = Integer.MAX_VALUE;
            return;
        }
        final DerValue derValue3 = derValue.data.getDerValue();
        if (derValue3.tag != 2) {
            throw new IOException("Invalid encoding of BasicConstraints");
        }
        this.pathLen = derValue3.getInteger();
    }
    
    @Override
    public String toString() {
        final String string = super.toString() + "BasicConstraints:[\n" + (this.ca ? "  CA:true" : "  CA:false") + "\n";
        String s;
        if (this.pathLen >= 0) {
            s = string + "  PathLen:" + this.pathLen + "\n";
        }
        else {
            s = string + "  PathLen: undefined\n";
        }
        return s + "]\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.BasicConstraints_Id;
            if (this.ca) {
                this.critical = true;
            }
            else {
                this.critical = false;
            }
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (s.equalsIgnoreCase("is_ca")) {
            if (!(o instanceof Boolean)) {
                throw new IOException("Attribute value should be of type Boolean.");
            }
            this.ca = (boolean)o;
        }
        else {
            if (!s.equalsIgnoreCase("path_len")) {
                throw new IOException("Attribute name not recognized by CertAttrSet:BasicConstraints.");
            }
            if (!(o instanceof Integer)) {
                throw new IOException("Attribute value should be of type Integer.");
            }
            this.pathLen = (int)o;
        }
        this.encodeThis();
    }
    
    @Override
    public Object get(final String s) throws IOException {
        if (s.equalsIgnoreCase("is_ca")) {
            return this.ca;
        }
        if (s.equalsIgnoreCase("path_len")) {
            return this.pathLen;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:BasicConstraints.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("is_ca")) {
            this.ca = false;
        }
        else {
            if (!s.equalsIgnoreCase("path_len")) {
                throw new IOException("Attribute name not recognized by CertAttrSet:BasicConstraints.");
            }
            this.pathLen = -1;
        }
        this.encodeThis();
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("is_ca");
        attributeNameEnumeration.addElement("path_len");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "BasicConstraints";
    }
}
