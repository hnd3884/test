package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerByte<BeanT> extends Lister<BeanT, byte[], Byte, ByteArrayPack>
{
    private PrimitiveArrayListerByte() {
    }
    
    static void register() {
        Lister.primitiveArrayListers.put(Byte.TYPE, new PrimitiveArrayListerByte());
    }
    
    @Override
    public ListIterator<Byte> iterator(final byte[] objects, final XMLSerializer context) {
        return new ListIterator<Byte>() {
            int idx = 0;
            
            @Override
            public boolean hasNext() {
                return this.idx < objects.length;
            }
            
            @Override
            public Byte next() {
                return objects[this.idx++];
            }
        };
    }
    
    @Override
    public ByteArrayPack startPacking(final BeanT current, final Accessor<BeanT, byte[]> acc) {
        return new ByteArrayPack();
    }
    
    @Override
    public void addToPack(final ByteArrayPack objects, final Byte o) {
        objects.add(o);
    }
    
    @Override
    public void endPacking(final ByteArrayPack pack, final BeanT bean, final Accessor<BeanT, byte[]> acc) throws AccessorException {
        acc.set(bean, pack.build());
    }
    
    @Override
    public void reset(final BeanT o, final Accessor<BeanT, byte[]> acc) throws AccessorException {
        acc.set(o, new byte[0]);
    }
    
    static final class ByteArrayPack
    {
        byte[] buf;
        int size;
        
        ByteArrayPack() {
            this.buf = new byte[16];
        }
        
        void add(final Byte b) {
            if (this.buf.length == this.size) {
                final byte[] nb = new byte[this.buf.length * 2];
                System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
                this.buf = nb;
            }
            if (b != null) {
                this.buf[this.size++] = b;
            }
        }
        
        byte[] build() {
            if (this.buf.length == this.size) {
                return this.buf;
            }
            final byte[] r = new byte[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
        }
    }
}
