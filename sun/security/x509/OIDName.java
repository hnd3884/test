package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class OIDName implements GeneralNameInterface
{
    private ObjectIdentifier oid;
    
    public OIDName(final DerValue derValue) throws IOException {
        this.oid = derValue.getOID();
    }
    
    public OIDName(final ObjectIdentifier oid) {
        this.oid = oid;
    }
    
    public OIDName(final String s) throws IOException {
        try {
            this.oid = new ObjectIdentifier(s);
        }
        catch (final Exception ex) {
            throw new IOException("Unable to create OIDName: " + ex);
        }
    }
    
    @Override
    public int getType() {
        return 8;
    }
    
    @Override
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.putOID(this.oid);
    }
    
    @Override
    public String toString() {
        return "OIDName: " + this.oid.toString();
    }
    
    public ObjectIdentifier getOID() {
        return this.oid;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof OIDName && this.oid.equals((Object)((OIDName)o).oid));
    }
    
    @Override
    public int hashCode() {
        return this.oid.hashCode();
    }
    
    @Override
    public int constrains(final GeneralNameInterface generalNameInterface) throws UnsupportedOperationException {
        int n;
        if (generalNameInterface == null) {
            n = -1;
        }
        else if (generalNameInterface.getType() != 8) {
            n = -1;
        }
        else {
            if (!this.equals(generalNameInterface)) {
                throw new UnsupportedOperationException("Narrowing and widening are not supported for OIDNames");
            }
            n = 0;
        }
        return n;
    }
    
    @Override
    public int subtreeDepth() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("subtreeDepth() not supported for OIDName.");
    }
}
