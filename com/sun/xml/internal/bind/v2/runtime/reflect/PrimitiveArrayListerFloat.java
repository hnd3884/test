package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerFloat<BeanT> extends Lister<BeanT, float[], Float, FloatArrayPack>
{
    private PrimitiveArrayListerFloat() {
    }
    
    static void register() {
        Lister.primitiveArrayListers.put(Float.TYPE, new PrimitiveArrayListerFloat());
    }
    
    @Override
    public ListIterator<Float> iterator(final float[] objects, final XMLSerializer context) {
        return new ListIterator<Float>() {
            int idx = 0;
            
            @Override
            public boolean hasNext() {
                return this.idx < objects.length;
            }
            
            @Override
            public Float next() {
                return objects[this.idx++];
            }
        };
    }
    
    @Override
    public FloatArrayPack startPacking(final BeanT current, final Accessor<BeanT, float[]> acc) {
        return new FloatArrayPack();
    }
    
    @Override
    public void addToPack(final FloatArrayPack objects, final Float o) {
        objects.add(o);
    }
    
    @Override
    public void endPacking(final FloatArrayPack pack, final BeanT bean, final Accessor<BeanT, float[]> acc) throws AccessorException {
        acc.set(bean, pack.build());
    }
    
    @Override
    public void reset(final BeanT o, final Accessor<BeanT, float[]> acc) throws AccessorException {
        acc.set(o, new float[0]);
    }
    
    static final class FloatArrayPack
    {
        float[] buf;
        int size;
        
        FloatArrayPack() {
            this.buf = new float[16];
        }
        
        void add(final Float b) {
            if (this.buf.length == this.size) {
                final float[] nb = new float[this.buf.length * 2];
                System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
                this.buf = nb;
            }
            if (b != null) {
                this.buf[this.size++] = b;
            }
        }
        
        float[] build() {
            if (this.buf.length == this.size) {
                return this.buf;
            }
            final float[] r = new float[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
        }
    }
}
