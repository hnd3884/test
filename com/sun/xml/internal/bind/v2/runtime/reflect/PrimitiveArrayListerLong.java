package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerLong<BeanT> extends Lister<BeanT, long[], Long, LongArrayPack>
{
    private PrimitiveArrayListerLong() {
    }
    
    static void register() {
        Lister.primitiveArrayListers.put(Long.TYPE, new PrimitiveArrayListerLong());
    }
    
    @Override
    public ListIterator<Long> iterator(final long[] objects, final XMLSerializer context) {
        return new ListIterator<Long>() {
            int idx = 0;
            
            @Override
            public boolean hasNext() {
                return this.idx < objects.length;
            }
            
            @Override
            public Long next() {
                return objects[this.idx++];
            }
        };
    }
    
    @Override
    public LongArrayPack startPacking(final BeanT current, final Accessor<BeanT, long[]> acc) {
        return new LongArrayPack();
    }
    
    @Override
    public void addToPack(final LongArrayPack objects, final Long o) {
        objects.add(o);
    }
    
    @Override
    public void endPacking(final LongArrayPack pack, final BeanT bean, final Accessor<BeanT, long[]> acc) throws AccessorException {
        acc.set(bean, pack.build());
    }
    
    @Override
    public void reset(final BeanT o, final Accessor<BeanT, long[]> acc) throws AccessorException {
        acc.set(o, new long[0]);
    }
    
    static final class LongArrayPack
    {
        long[] buf;
        int size;
        
        LongArrayPack() {
            this.buf = new long[16];
        }
        
        void add(final Long b) {
            if (this.buf.length == this.size) {
                final long[] nb = new long[this.buf.length * 2];
                System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
                this.buf = nb;
            }
            if (b != null) {
                this.buf[this.size++] = b;
            }
        }
        
        long[] build() {
            if (this.buf.length == this.size) {
                return this.buf;
            }
            final long[] r = new long[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
        }
    }
}
