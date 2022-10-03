package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;

public class X400Address implements GeneralNameInterface
{
    byte[] nameValue;
    
    public X400Address(final byte[] nameValue) {
        this.nameValue = null;
        this.nameValue = nameValue;
    }
    
    public X400Address(final DerValue derValue) throws IOException {
        this.nameValue = null;
        this.nameValue = derValue.toByteArray();
    }
    
    @Override
    public int getType() {
        return 3;
    }
    
    @Override
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.putDerValue(new DerValue(this.nameValue));
    }
    
    @Override
    public String toString() {
        return "X400Address: <DER-encoded value>";
    }
    
    @Override
    public int constrains(final GeneralNameInterface generalNameInterface) throws UnsupportedOperationException {
        int n;
        if (generalNameInterface == null) {
            n = -1;
        }
        else {
            if (generalNameInterface.getType() == 3) {
                throw new UnsupportedOperationException("Narrowing, widening, and match are not supported for X400Address.");
            }
            n = -1;
        }
        return n;
    }
    
    @Override
    public int subtreeDepth() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("subtreeDepth not supported for X400Address");
    }
}
