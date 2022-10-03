package org.bouncycastle.asn1;

import org.bouncycastle.util.Arrays;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.IOException;
import java.util.Vector;
import org.bouncycastle.util.Iterable;

public abstract class ASN1Set extends ASN1Primitive implements Iterable<ASN1Encodable>
{
    private Vector set;
    private boolean isSorted;
    
    public static ASN1Set getInstance(final Object o) {
        if (o == null || o instanceof ASN1Set) {
            return (ASN1Set)o;
        }
        if (o instanceof ASN1SetParser) {
            return getInstance(((ASN1SetParser)o).toASN1Primitive());
        }
        if (o instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("failed to construct set from byte[]: " + ex.getMessage());
            }
        }
        if (o instanceof ASN1Encodable) {
            final ASN1Primitive asn1Primitive = ((ASN1Encodable)o).toASN1Primitive();
            if (asn1Primitive instanceof ASN1Set) {
                return (ASN1Set)asn1Primitive;
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + o.getClass().getName());
    }
    
    public static ASN1Set getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        if (b) {
            if (!asn1TaggedObject.isExplicit()) {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }
            return (ASN1Set)asn1TaggedObject.getObject();
        }
        else {
            final ASN1Primitive object = asn1TaggedObject.getObject();
            if (asn1TaggedObject.isExplicit()) {
                if (asn1TaggedObject instanceof BERTaggedObject) {
                    return new BERSet(object);
                }
                return new DLSet(object);
            }
            else {
                if (object instanceof ASN1Set) {
                    return (ASN1Set)object;
                }
                if (!(object instanceof ASN1Sequence)) {
                    throw new IllegalArgumentException("unknown object in getInstance: " + asn1TaggedObject.getClass().getName());
                }
                final ASN1Sequence asn1Sequence = (ASN1Sequence)object;
                if (asn1TaggedObject instanceof BERTaggedObject) {
                    return new BERSet(asn1Sequence.toArray());
                }
                return new DLSet(asn1Sequence.toArray());
            }
        }
    }
    
    protected ASN1Set() {
        this.set = new Vector();
        this.isSorted = false;
    }
    
    protected ASN1Set(final ASN1Encodable asn1Encodable) {
        this.set = new Vector();
        this.isSorted = false;
        this.set.addElement(asn1Encodable);
    }
    
    protected ASN1Set(final ASN1EncodableVector asn1EncodableVector, final boolean b) {
        this.set = new Vector();
        this.isSorted = false;
        for (int i = 0; i != asn1EncodableVector.size(); ++i) {
            this.set.addElement(asn1EncodableVector.get(i));
        }
        if (b) {
            this.sort();
        }
    }
    
    protected ASN1Set(final ASN1Encodable[] array, final boolean b) {
        this.set = new Vector();
        this.isSorted = false;
        for (int i = 0; i != array.length; ++i) {
            this.set.addElement(array[i]);
        }
        if (b) {
            this.sort();
        }
    }
    
    public Enumeration getObjects() {
        return this.set.elements();
    }
    
    public ASN1Encodable getObjectAt(final int n) {
        return this.set.elementAt(n);
    }
    
    public int size() {
        return this.set.size();
    }
    
    public ASN1Encodable[] toArray() {
        final ASN1Encodable[] array = new ASN1Encodable[this.size()];
        for (int i = 0; i != this.size(); ++i) {
            array[i] = this.getObjectAt(i);
        }
        return array;
    }
    
    public ASN1SetParser parser() {
        return new ASN1SetParser() {
            private final int max = ASN1Set.this.size();
            private int index;
            
            public ASN1Encodable readObject() throws IOException {
                if (this.index == this.max) {
                    return null;
                }
                final ASN1Encodable object = ASN1Set.this.getObjectAt(this.index++);
                if (object instanceof ASN1Sequence) {
                    return ((ASN1Sequence)object).parser();
                }
                if (object instanceof ASN1Set) {
                    return ((ASN1Set)object).parser();
                }
                return object;
            }
            
            public ASN1Primitive getLoadedObject() {
                return ASN1Set.this;
            }
            
            public ASN1Primitive toASN1Primitive() {
                return ASN1Set.this;
            }
        };
    }
    
    @Override
    public int hashCode() {
        final Enumeration objects = this.getObjects();
        int size = this.size();
        while (objects.hasMoreElements()) {
            size = (size * 17 ^ this.getNext(objects).hashCode());
        }
        return size;
    }
    
    @Override
    ASN1Primitive toDERObject() {
        if (this.isSorted) {
            final DERSet set = new DERSet();
            set.set = this.set;
            return set;
        }
        final Vector set2 = new Vector();
        for (int i = 0; i != this.set.size(); ++i) {
            set2.addElement(this.set.elementAt(i));
        }
        final DERSet set3 = new DERSet();
        set3.set = set2;
        set3.sort();
        return set3;
    }
    
    @Override
    ASN1Primitive toDLObject() {
        final DLSet set = new DLSet();
        set.set = this.set;
        return set;
    }
    
    @Override
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        if (!(asn1Primitive instanceof ASN1Set)) {
            return false;
        }
        final ASN1Set set = (ASN1Set)asn1Primitive;
        if (this.size() != set.size()) {
            return false;
        }
        final Enumeration objects = this.getObjects();
        final Enumeration objects2 = set.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1Encodable next = this.getNext(objects);
            final ASN1Encodable next2 = this.getNext(objects2);
            final ASN1Primitive asn1Primitive2 = next.toASN1Primitive();
            final ASN1Primitive asn1Primitive3 = next2.toASN1Primitive();
            if (asn1Primitive2 != asn1Primitive3) {
                if (asn1Primitive2.equals(asn1Primitive3)) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }
    
    private ASN1Encodable getNext(final Enumeration enumeration) {
        final ASN1Encodable asn1Encodable = enumeration.nextElement();
        if (asn1Encodable == null) {
            return DERNull.INSTANCE;
        }
        return asn1Encodable;
    }
    
    private boolean lessThanOrEqual(final byte[] array, final byte[] array2) {
        final int min = Math.min(array.length, array2.length);
        for (int i = 0; i != min; ++i) {
            if (array[i] != array2[i]) {
                return (array[i] & 0xFF) < (array2[i] & 0xFF);
            }
        }
        return min == array.length;
    }
    
    private byte[] getDEREncoded(final ASN1Encodable asn1Encodable) {
        try {
            return asn1Encodable.toASN1Primitive().getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("cannot encode object added to SET");
        }
    }
    
    protected void sort() {
        if (!this.isSorted) {
            this.isSorted = true;
            if (this.set.size() > 1) {
                int i = 1;
                int n = this.set.size() - 1;
                while (i != 0) {
                    int j = 0;
                    int n2 = 0;
                    byte[] derEncoded = this.getDEREncoded(this.set.elementAt(0));
                    i = 0;
                    while (j != n) {
                        final byte[] derEncoded2 = this.getDEREncoded(this.set.elementAt(j + 1));
                        if (this.lessThanOrEqual(derEncoded, derEncoded2)) {
                            derEncoded = derEncoded2;
                        }
                        else {
                            final Object element = this.set.elementAt(j);
                            this.set.setElementAt(this.set.elementAt(j + 1), j);
                            this.set.setElementAt(element, j + 1);
                            i = 1;
                            n2 = j;
                        }
                        ++j;
                    }
                    n = n2;
                }
            }
        }
    }
    
    @Override
    boolean isConstructed() {
        return true;
    }
    
    @Override
    abstract void encode(final ASN1OutputStream p0) throws IOException;
    
    @Override
    public String toString() {
        return this.set.toString();
    }
    
    public Iterator<ASN1Encodable> iterator() {
        return new Arrays.Iterator<ASN1Encodable>(this.toArray());
    }
}
