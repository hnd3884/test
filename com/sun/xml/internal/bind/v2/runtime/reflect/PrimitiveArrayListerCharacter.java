package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

final class PrimitiveArrayListerCharacter<BeanT> extends Lister<BeanT, char[], Character, CharacterArrayPack>
{
    private PrimitiveArrayListerCharacter() {
    }
    
    static void register() {
        Lister.primitiveArrayListers.put(Character.TYPE, new PrimitiveArrayListerCharacter());
    }
    
    @Override
    public ListIterator<Character> iterator(final char[] objects, final XMLSerializer context) {
        return new ListIterator<Character>() {
            int idx = 0;
            
            @Override
            public boolean hasNext() {
                return this.idx < objects.length;
            }
            
            @Override
            public Character next() {
                return objects[this.idx++];
            }
        };
    }
    
    @Override
    public CharacterArrayPack startPacking(final BeanT current, final Accessor<BeanT, char[]> acc) {
        return new CharacterArrayPack();
    }
    
    @Override
    public void addToPack(final CharacterArrayPack objects, final Character o) {
        objects.add(o);
    }
    
    @Override
    public void endPacking(final CharacterArrayPack pack, final BeanT bean, final Accessor<BeanT, char[]> acc) throws AccessorException {
        acc.set(bean, pack.build());
    }
    
    @Override
    public void reset(final BeanT o, final Accessor<BeanT, char[]> acc) throws AccessorException {
        acc.set(o, new char[0]);
    }
    
    static final class CharacterArrayPack
    {
        char[] buf;
        int size;
        
        CharacterArrayPack() {
            this.buf = new char[16];
        }
        
        void add(final Character b) {
            if (this.buf.length == this.size) {
                final char[] nb = new char[this.buf.length * 2];
                System.arraycopy(this.buf, 0, nb, 0, this.buf.length);
                this.buf = nb;
            }
            if (b != null) {
                this.buf[this.size++] = b;
            }
        }
        
        char[] build() {
            if (this.buf.length == this.size) {
                return this.buf;
            }
            final char[] r = new char[this.size];
            System.arraycopy(this.buf, 0, r, 0, this.size);
            return r;
        }
    }
}
