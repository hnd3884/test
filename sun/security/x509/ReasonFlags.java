package sun.security.x509;

import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.DerInputStream;
import sun.security.util.BitArray;
import java.io.IOException;

public class ReasonFlags
{
    public static final String UNUSED = "unused";
    public static final String KEY_COMPROMISE = "key_compromise";
    public static final String CA_COMPROMISE = "ca_compromise";
    public static final String AFFILIATION_CHANGED = "affiliation_changed";
    public static final String SUPERSEDED = "superseded";
    public static final String CESSATION_OF_OPERATION = "cessation_of_operation";
    public static final String CERTIFICATE_HOLD = "certificate_hold";
    public static final String PRIVILEGE_WITHDRAWN = "privilege_withdrawn";
    public static final String AA_COMPROMISE = "aa_compromise";
    private static final String[] NAMES;
    private boolean[] bitString;
    
    private static int name2Index(final String s) throws IOException {
        for (int i = 0; i < ReasonFlags.NAMES.length; ++i) {
            if (ReasonFlags.NAMES[i].equalsIgnoreCase(s)) {
                return i;
            }
        }
        throw new IOException("Name not recognized by ReasonFlags");
    }
    
    private boolean isSet(final int n) {
        return n < this.bitString.length && this.bitString[n];
    }
    
    private void set(final int n, final boolean b) {
        if (n >= this.bitString.length) {
            final boolean[] bitString = new boolean[n + 1];
            System.arraycopy(this.bitString, 0, bitString, 0, this.bitString.length);
            this.bitString = bitString;
        }
        this.bitString[n] = b;
    }
    
    public ReasonFlags(final byte[] array) {
        this.bitString = new BitArray(array.length * 8, array).toBooleanArray();
    }
    
    public ReasonFlags(final boolean[] bitString) {
        this.bitString = bitString;
    }
    
    public ReasonFlags(final BitArray bitArray) {
        this.bitString = bitArray.toBooleanArray();
    }
    
    public ReasonFlags(final DerInputStream derInputStream) throws IOException {
        this.bitString = derInputStream.getDerValue().getUnalignedBitString(true).toBooleanArray();
    }
    
    public ReasonFlags(final DerValue derValue) throws IOException {
        this.bitString = derValue.getUnalignedBitString(true).toBooleanArray();
    }
    
    public boolean[] getFlags() {
        return this.bitString;
    }
    
    public void set(final String s, final Object o) throws IOException {
        if (!(o instanceof Boolean)) {
            throw new IOException("Attribute must be of type Boolean.");
        }
        this.set(name2Index(s), (boolean)o);
    }
    
    public Object get(final String s) throws IOException {
        return this.isSet(name2Index(s));
    }
    
    public void delete(final String s) throws IOException {
        this.set(s, Boolean.FALSE);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Reason Flags [\n");
        if (this.isSet(0)) {
            sb.append("  Unused\n");
        }
        if (this.isSet(1)) {
            sb.append("  Key Compromise\n");
        }
        if (this.isSet(2)) {
            sb.append("  CA Compromise\n");
        }
        if (this.isSet(3)) {
            sb.append("  Affiliation_Changed\n");
        }
        if (this.isSet(4)) {
            sb.append("  Superseded\n");
        }
        if (this.isSet(5)) {
            sb.append("  Cessation Of Operation\n");
        }
        if (this.isSet(6)) {
            sb.append("  Certificate Hold\n");
        }
        if (this.isSet(7)) {
            sb.append("  Privilege Withdrawn\n");
        }
        if (this.isSet(8)) {
            sb.append("  AA Compromise\n");
        }
        sb.append("]\n");
        return sb.toString();
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.putTruncatedUnalignedBitString(new BitArray(this.bitString));
    }
    
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        for (int i = 0; i < ReasonFlags.NAMES.length; ++i) {
            attributeNameEnumeration.addElement(ReasonFlags.NAMES[i]);
        }
        return attributeNameEnumeration.elements();
    }
    
    static {
        NAMES = new String[] { "unused", "key_compromise", "ca_compromise", "affiliation_changed", "superseded", "cessation_of_operation", "certificate_hold", "privilege_withdrawn", "aa_compromise" };
    }
}
