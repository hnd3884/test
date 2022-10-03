package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;

public class GeneralName
{
    private GeneralNameInterface name;
    
    public GeneralName(final GeneralNameInterface name) {
        this.name = null;
        if (name == null) {
            throw new NullPointerException("GeneralName must not be null");
        }
        this.name = name;
    }
    
    public GeneralName(final DerValue derValue) throws IOException {
        this(derValue, false);
    }
    
    public GeneralName(final DerValue derValue, final boolean b) throws IOException {
        this.name = null;
        final short n = (byte)(derValue.tag & 0x1F);
        switch (n) {
            case 0: {
                if (derValue.isContextSpecific() && derValue.isConstructed()) {
                    derValue.resetTag((byte)48);
                    this.name = new OtherName(derValue);
                    break;
                }
                throw new IOException("Invalid encoding of Other-Name");
            }
            case 1: {
                if (derValue.isContextSpecific() && !derValue.isConstructed()) {
                    derValue.resetTag((byte)22);
                    this.name = new RFC822Name(derValue);
                    break;
                }
                throw new IOException("Invalid encoding of RFC822 name");
            }
            case 2: {
                if (derValue.isContextSpecific() && !derValue.isConstructed()) {
                    derValue.resetTag((byte)22);
                    this.name = new DNSName(derValue);
                    break;
                }
                throw new IOException("Invalid encoding of DNSName");
            }
            case 6: {
                if (derValue.isContextSpecific() && !derValue.isConstructed()) {
                    derValue.resetTag((byte)22);
                    this.name = (b ? URIName.nameConstraint(derValue) : new URIName(derValue));
                    break;
                }
                throw new IOException("Invalid encoding of URI");
            }
            case 7: {
                if (derValue.isContextSpecific() && !derValue.isConstructed()) {
                    derValue.resetTag((byte)4);
                    this.name = new IPAddressName(derValue);
                    break;
                }
                throw new IOException("Invalid encoding of IP address");
            }
            case 8: {
                if (derValue.isContextSpecific() && !derValue.isConstructed()) {
                    derValue.resetTag((byte)6);
                    this.name = new OIDName(derValue);
                    break;
                }
                throw new IOException("Invalid encoding of OID name");
            }
            case 4: {
                if (derValue.isContextSpecific() && derValue.isConstructed()) {
                    this.name = new X500Name(derValue.getData());
                    break;
                }
                throw new IOException("Invalid encoding of Directory name");
            }
            case 5: {
                if (derValue.isContextSpecific() && derValue.isConstructed()) {
                    derValue.resetTag((byte)48);
                    this.name = new EDIPartyName(derValue);
                    break;
                }
                throw new IOException("Invalid encoding of EDI name");
            }
            default: {
                throw new IOException("Unrecognized GeneralName tag, (" + n + ")");
            }
        }
    }
    
    public int getType() {
        return this.name.getType();
    }
    
    public GeneralNameInterface getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return this.name.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GeneralName)) {
            return false;
        }
        final GeneralNameInterface name = ((GeneralName)o).name;
        try {
            return this.name.constrains(name) == 0;
        }
        catch (final UnsupportedOperationException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        this.name.encode(derOutputStream2);
        final int type = this.name.getType();
        if (type == 0 || type == 3 || type == 5) {
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), true, (byte)type), derOutputStream2);
        }
        else if (type == 4) {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)type), derOutputStream2);
        }
        else {
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)type), derOutputStream2);
        }
    }
}
