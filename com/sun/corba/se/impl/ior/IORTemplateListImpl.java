package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.IORFactories;
import org.omg.CORBA_2_3.portable.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;

public class IORTemplateListImpl extends FreezableList implements IORTemplateList
{
    @Override
    public Object set(final int n, final Object o) {
        if (o instanceof IORTemplate) {
            return super.set(n, o);
        }
        if (o instanceof IORTemplateList) {
            final Object remove = this.remove(n);
            this.add(n, o);
            return remove;
        }
        throw new IllegalArgumentException();
    }
    
    @Override
    public void add(final int n, final Object o) {
        if (o instanceof IORTemplate) {
            super.add(n, o);
        }
        else {
            if (!(o instanceof IORTemplateList)) {
                throw new IllegalArgumentException();
            }
            this.addAll(n, (Collection<?>)o);
        }
    }
    
    public IORTemplateListImpl() {
        super(new ArrayList());
    }
    
    public IORTemplateListImpl(final InputStream inputStream) {
        this();
        for (int read_long = inputStream.read_long(), i = 0; i < read_long; ++i) {
            this.add(IORFactories.makeIORTemplate(inputStream));
        }
        this.makeImmutable();
    }
    
    @Override
    public void makeImmutable() {
        this.makeElementsImmutable();
        super.makeImmutable();
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        outputStream.write_long(this.size());
        final Iterator<Object> iterator = this.iterator();
        while (iterator.hasNext()) {
            iterator.next().write(outputStream);
        }
    }
    
    @Override
    public IOR makeIOR(final ORB orb, final String s, final ObjectId objectId) {
        return new IORImpl(orb, s, this, objectId);
    }
    
    @Override
    public boolean isEquivalent(final IORFactory iorFactory) {
        if (!(iorFactory instanceof IORTemplateList)) {
            return false;
        }
        final IORTemplateList list = (IORTemplateList)iorFactory;
        final Iterator<Object> iterator = this.iterator();
        final Iterator<Object> iterator2 = list.iterator();
        while (iterator.hasNext() && iterator2.hasNext()) {
            if (!iterator.next().isEquivalent(iterator2.next())) {
                return false;
            }
        }
        return iterator.hasNext() == iterator2.hasNext();
    }
}
