package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import org.omg.IOP.TaggedComponentHelper;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;
import org.omg.CORBA.ORB;

public abstract class TaggedComponentBase extends IdentifiableBase implements TaggedComponent
{
    @Override
    public org.omg.IOP.TaggedComponent getIOPComponent(final ORB orb) {
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((com.sun.corba.se.spi.orb.ORB)orb);
        this.write(encapsOutputStream);
        return TaggedComponentHelper.read(encapsOutputStream.create_input_stream());
    }
}
