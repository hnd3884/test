package cryptix.jce.provider.asn;

import java.util.Vector;
import java.io.IOException;

public final class AsnSequence extends AsnObject
{
    private final AsnObject[] vals;
    
    public String toString(final String indent) {
        String s = indent + "SEQUENCE (" + this.vals.length + " elements):";
        for (int i = 0; i < this.vals.length; ++i) {
            s = s + "\n" + this.vals[i].toString(indent + "    ");
        }
        return s;
    }
    
    public AsnObject get(final int index) {
        return this.vals[index];
    }
    
    public int size() {
        return this.vals.length;
    }
    
    protected void encodePayload(final AsnOutputStream os) throws IOException {
        for (int i = 0; i < this.vals.length; ++i) {
            os.write(this.vals[i]);
        }
    }
    
    protected int getEncodedLengthOfPayload(final AsnOutputStream os) {
        int len = 0;
        for (int i = 0; i < this.vals.length; ++i) {
            len += this.vals[i].getEncodedLength(os);
        }
        return len;
    }
    
    AsnSequence(final AsnInputStream is) throws IOException {
        super((byte)48);
        final int len = is.readLength();
        final AsnInputStream sub_is = is.getSubStream(len);
        final Vector vec = new Vector(3);
        while (sub_is.available() > 0) {
            vec.addElement(sub_is.read());
        }
        vec.copyInto(this.vals = new AsnObject[vec.size()]);
    }
    
    public AsnSequence(final AsnObject[] vals) {
        super((byte)48);
        this.vals = vals.clone();
    }
    
    public AsnSequence(final AsnObject a, final AsnObject b) {
        super((byte)48);
        final AsnObject[] objs = { a, b };
        this.vals = objs;
    }
}
