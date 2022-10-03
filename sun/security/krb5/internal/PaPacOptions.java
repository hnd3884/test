package sun.security.krb5.internal;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.internal.util.KerberosFlags;

public class PaPacOptions
{
    private static final int CLAIMS = 0;
    private static final int BRANCH_AWARE = 1;
    private static final int FORWARD_TO_FULL_DC = 2;
    private static final int RESOURCE_BASED_CONSTRAINED_DELEGATION = 3;
    private KerberosFlags flags;
    
    public PaPacOptions() {
        this.flags = new KerberosFlags(32);
    }
    
    public PaPacOptions(final DerValue derValue) throws Asn1Exception, IOException {
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) == 0x0) {
            this.flags = new KDCOptions(derValue2.getData().getDerValue());
            return;
        }
        throw new Asn1Exception(906);
    }
    
    public PaPacOptions setClaims(final boolean b) {
        this.flags.set(0, b);
        return this;
    }
    
    public boolean getClaims() {
        return this.flags.get(0);
    }
    
    public PaPacOptions setBranchAware(final boolean b) {
        this.flags.set(1, b);
        return this;
    }
    
    public boolean getBranchAware() {
        return this.flags.get(1);
    }
    
    public PaPacOptions setForwardToFullDC(final boolean b) {
        this.flags.set(2, b);
        return this;
    }
    
    public boolean getForwardToFullDC() {
        return this.flags.get(2);
    }
    
    public PaPacOptions setResourceBasedConstrainedDelegation(final boolean b) {
        this.flags.set(3, b);
        return this;
    }
    
    public boolean getResourceBasedConstrainedDelegation() {
        return this.flags.get(3);
    }
    
    public byte[] asn1Encode() throws IOException {
        byte[] byteArray = null;
        try (final DerOutputStream derOutputStream = new DerOutputStream()) {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), this.flags.asn1Encode());
            byteArray = derOutputStream.toByteArray();
        }
        try (final DerOutputStream derOutputStream2 = new DerOutputStream()) {
            derOutputStream2.write((byte)48, byteArray);
            return derOutputStream2.toByteArray();
        }
    }
    
    @Override
    public String toString() {
        return this.flags.toString();
    }
}
