package sun.security.x509;

import java.util.Objects;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;

public class DistributionPointName
{
    private static final byte TAG_FULL_NAME = 0;
    private static final byte TAG_RELATIVE_NAME = 1;
    private GeneralNames fullName;
    private RDN relativeName;
    private volatile int hashCode;
    
    public DistributionPointName(final GeneralNames fullName) {
        this.fullName = null;
        this.relativeName = null;
        if (fullName == null) {
            throw new IllegalArgumentException("fullName must not be null");
        }
        this.fullName = fullName;
    }
    
    public DistributionPointName(final RDN relativeName) {
        this.fullName = null;
        this.relativeName = null;
        if (relativeName == null) {
            throw new IllegalArgumentException("relativeName must not be null");
        }
        this.relativeName = relativeName;
    }
    
    public DistributionPointName(final DerValue derValue) throws IOException {
        this.fullName = null;
        this.relativeName = null;
        if (derValue.isContextSpecific((byte)0) && derValue.isConstructed()) {
            derValue.resetTag((byte)48);
            this.fullName = new GeneralNames(derValue);
        }
        else {
            if (!derValue.isContextSpecific((byte)1) || !derValue.isConstructed()) {
                throw new IOException("Invalid encoding for DistributionPointName");
            }
            derValue.resetTag((byte)49);
            this.relativeName = new RDN(derValue);
        }
    }
    
    public GeneralNames getFullName() {
        return this.fullName;
    }
    
    public RDN getRelativeName() {
        return this.relativeName;
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        if (this.fullName != null) {
            this.fullName.encode(derOutputStream2);
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        }
        else {
            this.relativeName.encode(derOutputStream2);
            derOutputStream.writeImplicit(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream2);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DistributionPointName)) {
            return false;
        }
        final DistributionPointName distributionPointName = (DistributionPointName)o;
        return Objects.equals(this.fullName, distributionPointName.fullName) && Objects.equals(this.relativeName, distributionPointName.relativeName);
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            final int n = 1;
            if (this.fullName != null) {
                hashCode = n + this.fullName.hashCode();
            }
            else {
                hashCode = n + this.relativeName.hashCode();
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.fullName != null) {
            sb.append("DistributionPointName:\n     " + this.fullName + "\n");
        }
        else {
            sb.append("DistributionPointName:\n     " + this.relativeName + "\n");
        }
        return sb.toString();
    }
}
