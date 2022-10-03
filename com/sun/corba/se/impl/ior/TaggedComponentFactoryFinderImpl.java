package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.TaggedComponentHelper;
import sun.corba.OutputStreamFactory;
import org.omg.IOP.TaggedComponent;
import com.sun.corba.se.spi.ior.Identifiable;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;

public class TaggedComponentFactoryFinderImpl extends IdentifiableFactoryFinderBase implements TaggedComponentFactoryFinder
{
    public TaggedComponentFactoryFinderImpl(final ORB orb) {
        super(orb);
    }
    
    @Override
    public Identifiable handleMissingFactory(final int n, final InputStream inputStream) {
        return new GenericTaggedComponent(n, inputStream);
    }
    
    @Override
    public com.sun.corba.se.spi.ior.TaggedComponent create(final org.omg.CORBA.ORB orb, final TaggedComponent taggedComponent) {
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)orb);
        TaggedComponentHelper.write(encapsOutputStream, taggedComponent);
        final InputStream inputStream = (InputStream)encapsOutputStream.create_input_stream();
        inputStream.read_ulong();
        return (com.sun.corba.se.spi.ior.TaggedComponent)this.create(taggedComponent.tag, inputStream);
    }
}
