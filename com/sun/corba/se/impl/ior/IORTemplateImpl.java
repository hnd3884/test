package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import org.omg.CORBA_2_3.portable.InputStream;
import java.util.List;
import org.omg.CORBA_2_3.portable.OutputStream;
import java.util.Iterator;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IdentifiableContainerBase;

public class IORTemplateImpl extends IdentifiableContainerBase implements IORTemplate
{
    private ObjectKeyTemplate oktemp;
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof IORTemplateImpl)) {
            return false;
        }
        final IORTemplateImpl iorTemplateImpl = (IORTemplateImpl)o;
        return super.equals(o) && this.oktemp.equals(iorTemplateImpl.getObjectKeyTemplate());
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ this.oktemp.hashCode();
    }
    
    @Override
    public ObjectKeyTemplate getObjectKeyTemplate() {
        return this.oktemp;
    }
    
    public IORTemplateImpl(final ObjectKeyTemplate oktemp) {
        this.oktemp = oktemp;
    }
    
    @Override
    public IOR makeIOR(final ORB orb, final String s, final ObjectId objectId) {
        return new IORImpl(orb, s, this, objectId);
    }
    
    @Override
    public boolean isEquivalent(final IORFactory iorFactory) {
        if (!(iorFactory instanceof IORTemplate)) {
            return false;
        }
        final IORTemplate iorTemplate = (IORTemplate)iorFactory;
        final Iterator<Object> iterator = this.iterator();
        final Iterator<Object> iterator2 = iorTemplate.iterator();
        while (iterator.hasNext() && iterator2.hasNext()) {
            if (!iterator.next().isEquivalent(iterator2.next())) {
                return false;
            }
        }
        return iterator.hasNext() == iterator2.hasNext() && this.getObjectKeyTemplate().equals(iorTemplate.getObjectKeyTemplate());
    }
    
    @Override
    public void makeImmutable() {
        this.makeElementsImmutable();
        super.makeImmutable();
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        this.oktemp.write(outputStream);
        EncapsulationUtility.writeIdentifiableSequence(this, outputStream);
    }
    
    public IORTemplateImpl(final InputStream inputStream) {
        final ORB orb = (ORB)inputStream.orb();
        final IdentifiableFactoryFinder taggedProfileTemplateFactoryFinder = orb.getTaggedProfileTemplateFactoryFinder();
        this.oktemp = orb.getObjectKeyFactory().createTemplate(inputStream);
        EncapsulationUtility.readIdentifiableSequence(this, taggedProfileTemplateFactoryFinder, inputStream);
        this.makeImmutable();
    }
}
