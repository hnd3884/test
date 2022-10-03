package com.sun.corba.se.spi.ior;

import java.util.Iterator;
import org.omg.IOP.TaggedComponent;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.ior.EncapsulationUtility;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class TaggedProfileTemplateBase extends IdentifiableContainerBase implements TaggedProfileTemplate
{
    @Override
    public void write(final OutputStream outputStream) {
        EncapsulationUtility.writeEncapsulation(this, outputStream);
    }
    
    @Override
    public TaggedComponent[] getIOPComponents(final ORB orb, final int n) {
        int n2 = 0;
        final Iterator iteratorById = this.iteratorById(n);
        while (iteratorById.hasNext()) {
            iteratorById.next();
            ++n2;
        }
        final TaggedComponent[] array = new TaggedComponent[n2];
        int n3 = 0;
        final Iterator iteratorById2 = this.iteratorById(n);
        while (iteratorById2.hasNext()) {
            array[n3++] = ((com.sun.corba.se.spi.ior.TaggedComponent)iteratorById2.next()).getIOPComponent(orb);
        }
        return array;
    }
}
