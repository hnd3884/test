package sun.security.x509;

import java.util.Enumeration;
import java.io.OutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;

public class AuthorityKeyIdentifierExtension extends Extension implements CertAttrSet<String>
{
    public static final String IDENT = "x509.info.extensions.AuthorityKeyIdentifier";
    public static final String NAME = "AuthorityKeyIdentifier";
    public static final String KEY_ID = "key_id";
    public static final String AUTH_NAME = "auth_name";
    public static final String SERIAL_NUMBER = "serial_number";
    private static final byte TAG_ID = 0;
    private static final byte TAG_NAMES = 1;
    private static final byte TAG_SERIAL_NUM = 2;
    private KeyIdentifier id;
    private GeneralNames names;
    private SerialNumber serialNum;
    
    private void encodeThis() throws IOException {
        if (this.id == null && this.names == null && this.serialNum == null) {
            this.extensionValue = null;
            return;
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        if (this.id != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            this.id.encode(derOutputStream3);
            derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)0), derOutputStream3);
        }
        try {
            if (this.names != null) {
                final DerOutputStream derOutputStream4 = new DerOutputStream();
                this.names.encode(derOutputStream4);
                derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream4);
            }
        }
        catch (final Exception ex) {
            throw new IOException(ex.toString());
        }
        if (this.serialNum != null) {
            final DerOutputStream derOutputStream5 = new DerOutputStream();
            this.serialNum.encode(derOutputStream5);
            derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)2), derOutputStream5);
        }
        derOutputStream.write((byte)48, derOutputStream2);
        this.extensionValue = derOutputStream.toByteArray();
    }
    
    public AuthorityKeyIdentifierExtension(final KeyIdentifier id, final GeneralNames names, final SerialNumber serialNum) throws IOException {
        this.id = null;
        this.names = null;
        this.serialNum = null;
        this.id = id;
        this.names = names;
        this.serialNum = serialNum;
        this.extensionId = PKIXExtensions.AuthorityKey_Id;
        this.critical = false;
        this.encodeThis();
    }
    
    public AuthorityKeyIdentifierExtension(final Boolean b, final Object o) throws IOException {
        this.id = null;
        this.names = null;
        this.serialNum = null;
        this.extensionId = PKIXExtensions.AuthorityKey_Id;
        this.critical = b;
        this.extensionValue = (byte[])o;
        final DerValue derValue = new DerValue(this.extensionValue);
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for AuthorityKeyIdentifierExtension.");
        }
        while (derValue.data != null && derValue.data.available() != 0) {
            final DerValue derValue2 = derValue.data.getDerValue();
            if (derValue2.isContextSpecific((byte)0) && !derValue2.isConstructed()) {
                if (this.id != null) {
                    throw new IOException("Duplicate KeyIdentifier in AuthorityKeyIdentifier.");
                }
                derValue2.resetTag((byte)4);
                this.id = new KeyIdentifier(derValue2);
            }
            else if (derValue2.isContextSpecific((byte)1) && derValue2.isConstructed()) {
                if (this.names != null) {
                    throw new IOException("Duplicate GeneralNames in AuthorityKeyIdentifier.");
                }
                derValue2.resetTag((byte)48);
                this.names = new GeneralNames(derValue2);
            }
            else {
                if (!derValue2.isContextSpecific((byte)2) || derValue2.isConstructed()) {
                    throw new IOException("Invalid encoding of AuthorityKeyIdentifierExtension.");
                }
                if (this.serialNum != null) {
                    throw new IOException("Duplicate SerialNumber in AuthorityKeyIdentifier.");
                }
                derValue2.resetTag((byte)2);
                this.serialNum = new SerialNumber(derValue2);
            }
        }
    }
    
    @Override
    public String toString() {
        String s = super.toString() + "AuthorityKeyIdentifier [\n";
        if (this.id != null) {
            s += this.id.toString();
        }
        if (this.names != null) {
            s = s + this.names.toString() + "\n";
        }
        if (this.serialNum != null) {
            s = s + this.serialNum.toString() + "\n";
        }
        return s + "]\n";
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        if (this.extensionValue == null) {
            this.extensionId = PKIXExtensions.AuthorityKey_Id;
            this.critical = false;
            this.encodeThis();
        }
        super.encode(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    @Override
    public void set(final String s, final Object o) throws IOException {
        if (s.equalsIgnoreCase("key_id")) {
            if (!(o instanceof KeyIdentifier)) {
                throw new IOException("Attribute value should be of type KeyIdentifier.");
            }
            this.id = (KeyIdentifier)o;
        }
        else if (s.equalsIgnoreCase("auth_name")) {
            if (!(o instanceof GeneralNames)) {
                throw new IOException("Attribute value should be of type GeneralNames.");
            }
            this.names = (GeneralNames)o;
        }
        else {
            if (!s.equalsIgnoreCase("serial_number")) {
                throw new IOException("Attribute name not recognized by CertAttrSet:AuthorityKeyIdentifier.");
            }
            if (!(o instanceof SerialNumber)) {
                throw new IOException("Attribute value should be of type SerialNumber.");
            }
            this.serialNum = (SerialNumber)o;
        }
        this.encodeThis();
    }
    
    @Override
    public Object get(final String s) throws IOException {
        if (s.equalsIgnoreCase("key_id")) {
            return this.id;
        }
        if (s.equalsIgnoreCase("auth_name")) {
            return this.names;
        }
        if (s.equalsIgnoreCase("serial_number")) {
            return this.serialNum;
        }
        throw new IOException("Attribute name not recognized by CertAttrSet:AuthorityKeyIdentifier.");
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (s.equalsIgnoreCase("key_id")) {
            this.id = null;
        }
        else if (s.equalsIgnoreCase("auth_name")) {
            this.names = null;
        }
        else {
            if (!s.equalsIgnoreCase("serial_number")) {
                throw new IOException("Attribute name not recognized by CertAttrSet:AuthorityKeyIdentifier.");
            }
            this.serialNum = null;
        }
        this.encodeThis();
    }
    
    @Override
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("key_id");
        attributeNameEnumeration.addElement("auth_name");
        attributeNameEnumeration.addElement("serial_number");
        return attributeNameEnumeration.elements();
    }
    
    @Override
    public String getName() {
        return "AuthorityKeyIdentifier";
    }
    
    public byte[] getEncodedKeyIdentifier() throws IOException {
        if (this.id != null) {
            final DerOutputStream derOutputStream = new DerOutputStream();
            this.id.encode(derOutputStream);
            return derOutputStream.toByteArray();
        }
        return null;
    }
}
