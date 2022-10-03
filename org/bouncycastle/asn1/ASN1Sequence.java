package org.bouncycastle.asn1;

import org.bouncycastle.util.Arrays;
import java.util.Iterator;
import java.util.Enumeration;
import java.io.IOException;
import java.util.Vector;
import org.bouncycastle.util.Iterable;

public abstract class ASN1Sequence extends ASN1Primitive implements Iterable<ASN1Encodable>
{
    protected Vector seq;
    
    public static ASN1Sequence getInstance(final Object o) {
        if (o == null || o instanceof ASN1Sequence) {
            return (ASN1Sequence)o;
        }
        if (o instanceof ASN1SequenceParser) {
            return getInstance(((ASN1SequenceParser)o).toASN1Primitive());
        }
        if (o instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("failed to construct sequence from byte[]: " + ex.getMessage());
            }
        }
        if (o instanceof ASN1Encodable) {
            final ASN1Primitive asn1Primitive = ((ASN1Encodable)o).toASN1Primitive();
            if (asn1Primitive instanceof ASN1Sequence) {
                return (ASN1Sequence)asn1Primitive;
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + o.getClass().getName());
    }
    
    public static ASN1Sequence getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        if (b) {
            if (!asn1TaggedObject.isExplicit()) {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }
            return getInstance(asn1TaggedObject.getObject().toASN1Primitive());
        }
        else {
            final ASN1Primitive object = asn1TaggedObject.getObject();
            if (asn1TaggedObject.isExplicit()) {
                if (asn1TaggedObject instanceof BERTaggedObject) {
                    return new BERSequence(object);
                }
                return new DLSequence(object);
            }
            else {
                if (object instanceof ASN1Sequence) {
                    return (ASN1Sequence)object;
                }
                throw new IllegalArgumentException("unknown object in getInstance: " + asn1TaggedObject.getClass().getName());
            }
        }
    }
    
    protected ASN1Sequence() {
        this.seq = new Vector();
    }
    
    protected ASN1Sequence(final ASN1Encodable asn1Encodable) {
        (this.seq = new Vector()).addElement(asn1Encodable);
    }
    
    protected ASN1Sequence(final ASN1EncodableVector asn1EncodableVector) {
        this.seq = new Vector();
        for (int i = 0; i != asn1EncodableVector.size(); ++i) {
            this.seq.addElement(asn1EncodableVector.get(i));
        }
    }
    
    protected ASN1Sequence(final ASN1Encodable[] array) {
        this.seq = new Vector();
        for (int i = 0; i != array.length; ++i) {
            this.seq.addElement(array[i]);
        }
    }
    
    public ASN1Encodable[] toArray() {
        final ASN1Encodable[] array = new ASN1Encodable[this.size()];
        for (int i = 0; i != this.size(); ++i) {
            array[i] = this.getObjectAt(i);
        }
        return array;
    }
    
    public Enumeration getObjects() {
        return this.seq.elements();
    }
    
    public ASN1SequenceParser parser() {
        return new ASN1SequenceParser() {
            private final int max = ASN1Sequence.this.size();
            private int index;
            
            public ASN1Encodable readObject() throws IOException {
                if (this.index == this.max) {
                    return null;
                }
                final ASN1Encodable object = ASN1Sequence.this.getObjectAt(this.index++);
                if (object instanceof ASN1Sequence) {
                    return ((ASN1Sequence)object).parser();
                }
                if (object instanceof ASN1Set) {
                    return ((ASN1Set)object).parser();
                }
                return object;
            }
            
            public ASN1Primitive getLoadedObject() {
                return ASN1Sequence.this;
            }
            
            public ASN1Primitive toASN1Primitive() {
                return ASN1Sequence.this;
            }
        };
    }
    
    public ASN1Encodable getObjectAt(final int n) {
        return this.seq.elementAt(n);
    }
    
    public int size() {
        return this.seq.size();
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
    boolean asn1Equals(final ASN1Primitive asn1Primitive) {
        if (!(asn1Primitive instanceof ASN1Sequence)) {
            return false;
        }
        final ASN1Sequence asn1Sequence = (ASN1Sequence)asn1Primitive;
        if (this.size() != asn1Sequence.size()) {
            return false;
        }
        final Enumeration objects = this.getObjects();
        final Enumeration objects2 = asn1Sequence.getObjects();
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
        return enumeration.nextElement();
    }
    
    @Override
    ASN1Primitive toDERObject() {
        final DERSequence derSequence = new DERSequence();
        derSequence.seq = this.seq;
        return derSequence;
    }
    
    @Override
    ASN1Primitive toDLObject() {
        final DLSequence dlSequence = new DLSequence();
        dlSequence.seq = this.seq;
        return dlSequence;
    }
    
    @Override
    boolean isConstructed() {
        return true;
    }
    
    @Override
    abstract void encode(final ASN1OutputStream p0) throws IOException;
    
    @Override
    public String toString() {
        return this.seq.toString();
    }
    
    public Iterator<ASN1Encodable> iterator() {
        return new Arrays.Iterator<ASN1Encodable>(this.toArray());
    }
}
