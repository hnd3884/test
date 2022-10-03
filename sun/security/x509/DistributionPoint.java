package sun.security.x509;

import java.util.Arrays;
import java.util.Objects;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;

public class DistributionPoint
{
    public static final int KEY_COMPROMISE = 1;
    public static final int CA_COMPROMISE = 2;
    public static final int AFFILIATION_CHANGED = 3;
    public static final int SUPERSEDED = 4;
    public static final int CESSATION_OF_OPERATION = 5;
    public static final int CERTIFICATE_HOLD = 6;
    public static final int PRIVILEGE_WITHDRAWN = 7;
    public static final int AA_COMPROMISE = 8;
    private static final String[] REASON_STRINGS;
    private static final byte TAG_DIST_PT = 0;
    private static final byte TAG_REASONS = 1;
    private static final byte TAG_ISSUER = 2;
    private static final byte TAG_FULL_NAME = 0;
    private static final byte TAG_REL_NAME = 1;
    private GeneralNames fullName;
    private RDN relativeName;
    private boolean[] reasonFlags;
    private GeneralNames crlIssuer;
    private volatile int hashCode;
    
    public DistributionPoint(final GeneralNames fullName, final boolean[] reasonFlags, final GeneralNames crlIssuer) {
        if (fullName == null && crlIssuer == null) {
            throw new IllegalArgumentException("fullName and crlIssuer may not both be null");
        }
        this.fullName = fullName;
        this.reasonFlags = reasonFlags;
        this.crlIssuer = crlIssuer;
    }
    
    public DistributionPoint(final RDN relativeName, final boolean[] reasonFlags, final GeneralNames crlIssuer) {
        if (relativeName == null && crlIssuer == null) {
            throw new IllegalArgumentException("relativeName and crlIssuer may not both be null");
        }
        this.relativeName = relativeName;
        this.reasonFlags = reasonFlags;
        this.crlIssuer = crlIssuer;
    }
    
    public DistributionPoint(final DerValue derValue) throws IOException {
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding of DistributionPoint.");
        }
        while (derValue.data != null && derValue.data.available() != 0) {
            final DerValue derValue2 = derValue.data.getDerValue();
            if (derValue2.isContextSpecific((byte)0) && derValue2.isConstructed()) {
                if (this.fullName != null || this.relativeName != null) {
                    throw new IOException("Duplicate DistributionPointName in DistributionPoint.");
                }
                final DerValue derValue3 = derValue2.data.getDerValue();
                if (derValue3.isContextSpecific((byte)0) && derValue3.isConstructed()) {
                    derValue3.resetTag((byte)48);
                    this.fullName = new GeneralNames(derValue3);
                }
                else {
                    if (!derValue3.isContextSpecific((byte)1) || !derValue3.isConstructed()) {
                        throw new IOException("Invalid DistributionPointName in DistributionPoint");
                    }
                    derValue3.resetTag((byte)49);
                    this.relativeName = new RDN(derValue3);
                }
            }
            else if (derValue2.isContextSpecific((byte)1) && !derValue2.isConstructed()) {
                if (this.reasonFlags != null) {
                    throw new IOException("Duplicate Reasons in DistributionPoint.");
                }
                derValue2.resetTag((byte)3);
                this.reasonFlags = derValue2.getUnalignedBitString().toBooleanArray();
            }
            else {
                if (!derValue2.isContextSpecific((byte)2) || !derValue2.isConstructed()) {
                    throw new IOException("Invalid encoding of DistributionPoint.");
                }
                if (this.crlIssuer != null) {
                    throw new IOException("Duplicate CRLIssuer in DistributionPoint.");
                }
                derValue2.resetTag((byte)48);
                this.crlIssuer = new GeneralNames(derValue2);
            }
        }
        if (this.crlIssuer == null && this.fullName == null && this.relativeName == null) {
            throw new IOException("One of fullName, relativeName,  and crlIssuer has to be set");
        }
    }
    
    public GeneralNames getFullName() {
        return this.fullName;
    }
    
    public RDN getRelativeName() {
        return this.relativeName;
    }
    
    public boolean[] getReasonFlags() {
        return this.reasonFlags;
    }
    
    public GeneralNames getCRLIssuer() {
        return this.crlIssuer;
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        if (this.fullName != null || this.relativeName != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            if (this.fullName != null) {
                final DerOutputStream derOutputStream4 = new DerOutputStream();
                this.fullName.encode(derOutputStream4);
                derOutputStream3.writeImplicit(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream4);
            }
            else if (this.relativeName != null) {
                final DerOutputStream derOutputStream5 = new DerOutputStream();
                this.relativeName.encode(derOutputStream5);
                derOutputStream3.writeImplicit(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream5);
            }
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream3);
        }
        if (this.reasonFlags != null) {
            final DerOutputStream derOutputStream6 = new DerOutputStream();
            derOutputStream6.putTruncatedUnalignedBitString(new BitArray(this.reasonFlags));
            derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)1), derOutputStream6);
        }
        if (this.crlIssuer != null) {
            final DerOutputStream derOutputStream7 = new DerOutputStream();
            this.crlIssuer.encode(derOutputStream7);
            derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream7);
        }
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DistributionPoint)) {
            return false;
        }
        final DistributionPoint distributionPoint = (DistributionPoint)o;
        return Objects.equals(this.fullName, distributionPoint.fullName) && Objects.equals(this.relativeName, distributionPoint.relativeName) && Objects.equals(this.crlIssuer, distributionPoint.crlIssuer) && Arrays.equals(this.reasonFlags, distributionPoint.reasonFlags);
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.hashCode;
        if (hashCode == 0) {
            hashCode = 1;
            if (this.fullName != null) {
                hashCode += this.fullName.hashCode();
            }
            if (this.relativeName != null) {
                hashCode += this.relativeName.hashCode();
            }
            if (this.crlIssuer != null) {
                hashCode += this.crlIssuer.hashCode();
            }
            if (this.reasonFlags != null) {
                for (int i = 0; i < this.reasonFlags.length; ++i) {
                    if (this.reasonFlags[i]) {
                        hashCode += i;
                    }
                }
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }
    
    private static String reasonToString(final int n) {
        if (n > 0 && n < DistributionPoint.REASON_STRINGS.length) {
            return DistributionPoint.REASON_STRINGS[n];
        }
        return "Unknown reason " + n;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (this.fullName != null) {
            sb.append("DistributionPoint:\n     " + this.fullName + "\n");
        }
        if (this.relativeName != null) {
            sb.append("DistributionPoint:\n     " + this.relativeName + "\n");
        }
        if (this.reasonFlags != null) {
            sb.append("   ReasonFlags:\n");
            for (int i = 0; i < this.reasonFlags.length; ++i) {
                if (this.reasonFlags[i]) {
                    sb.append("    " + reasonToString(i) + "\n");
                }
            }
        }
        if (this.crlIssuer != null) {
            sb.append("   CRLIssuer:" + this.crlIssuer + "\n");
        }
        return sb.toString();
    }
    
    static {
        REASON_STRINGS = new String[] { null, "key compromise", "CA compromise", "affiliation changed", "superseded", "cessation of operation", "certificate hold", "privilege withdrawn", "AA compromise" };
    }
}
