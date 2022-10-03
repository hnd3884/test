package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;

public class GeneralSubtree
{
    private static final byte TAG_MIN = 0;
    private static final byte TAG_MAX = 1;
    private static final int MIN_DEFAULT = 0;
    private GeneralName name;
    private int minimum;
    private int maximum;
    private int myhash;
    
    public GeneralSubtree(final GeneralName name, final int minimum, final int maximum) {
        this.minimum = 0;
        this.maximum = -1;
        this.myhash = -1;
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
    }
    
    public GeneralSubtree(final DerValue derValue) throws IOException {
        this.minimum = 0;
        this.maximum = -1;
        this.myhash = -1;
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding for GeneralSubtree.");
        }
        this.name = new GeneralName(derValue.data.getDerValue(), true);
        while (derValue.data.available() != 0) {
            final DerValue derValue2 = derValue.data.getDerValue();
            if (derValue2.isContextSpecific((byte)0) && !derValue2.isConstructed()) {
                derValue2.resetTag((byte)2);
                this.minimum = derValue2.getInteger();
            }
            else {
                if (!derValue2.isContextSpecific((byte)1) || derValue2.isConstructed()) {
                    throw new IOException("Invalid encoding of GeneralSubtree.");
                }
                derValue2.resetTag((byte)2);
                this.maximum = derValue2.getInteger();
            }
        }
    }
    
    public GeneralName getName() {
        return this.name;
    }
    
    public int getMinimum() {
        return this.minimum;
    }
    
    public int getMaximum() {
        return this.maximum;
    }
    
    @Override
    public String toString() {
        final String string = "\n   GeneralSubtree: [\n    GeneralName: " + ((this.name == null) ? "" : this.name.toString()) + "\n    Minimum: " + this.minimum;
        String s;
        if (this.maximum == -1) {
            s = string + "\t    Maximum: undefined";
        }
        else {
            s = string + "\t    Maximum: " + this.maximum;
        }
        return s + "    ]\n";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GeneralSubtree)) {
            return false;
        }
        final GeneralSubtree generalSubtree = (GeneralSubtree)o;
        if (this.name == null) {
            if (generalSubtree.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(generalSubtree.name)) {
            return false;
        }
        return this.minimum == generalSubtree.minimum && this.maximum == generalSubtree.maximum;
    }
    
    @Override
    public int hashCode() {
        if (this.myhash == -1) {
            this.myhash = 17;
            if (this.name != null) {
                this.myhash = 37 * this.myhash + this.name.hashCode();
            }
            if (this.minimum != 0) {
                this.myhash = 37 * this.myhash + this.minimum;
            }
            if (this.maximum != -1) {
                this.myhash = 37 * this.myhash + this.maximum;
            }
        }
        return this.myhash;
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        this.name.encode(derOutputStream2);
        if (this.minimum != 0) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putInteger(this.minimum);
            derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)0), derOutputStream3);
        }
        if (this.maximum != -1) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.putInteger(this.maximum);
            derOutputStream2.writeImplicit(DerValue.createTag((byte)(-128), false, (byte)1), derOutputStream4);
        }
        derOutputStream.write((byte)48, derOutputStream2);
    }
}
