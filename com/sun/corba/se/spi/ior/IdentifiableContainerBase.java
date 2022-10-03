package com.sun.corba.se.spi.ior;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.sun.corba.se.impl.ior.FreezableList;

public class IdentifiableContainerBase extends FreezableList
{
    public IdentifiableContainerBase() {
        super(new ArrayList());
    }
    
    public Iterator iteratorById(final int n) {
        return new Iterator() {
            Iterator iter = IdentifiableContainerBase.this.iterator();
            Object current = this.advance();
            
            private Object advance() {
                while (this.iter.hasNext()) {
                    final Identifiable identifiable = this.iter.next();
                    if (identifiable.getId() == n) {
                        return identifiable;
                    }
                }
                return null;
            }
            
            @Override
            public boolean hasNext() {
                return this.current != null;
            }
            
            @Override
            public Object next() {
                final Object current = this.current;
                this.current = this.advance();
                return current;
            }
            
            @Override
            public void remove() {
                this.iter.remove();
            }
        };
    }
}
