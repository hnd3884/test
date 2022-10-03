package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import org.omg.IOP.TaggedProfileHelper;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.TaggedProfile;

public class GenericTaggedProfile extends GenericIdentifiable implements TaggedProfile
{
    private ORB orb;
    
    public GenericTaggedProfile(final int n, final InputStream inputStream) {
        super(n, inputStream);
        this.orb = (ORB)inputStream.orb();
    }
    
    public GenericTaggedProfile(final ORB orb, final int n, final byte[] array) {
        super(n, array);
        this.orb = orb;
    }
    
    @Override
    public TaggedProfileTemplate getTaggedProfileTemplate() {
        return null;
    }
    
    @Override
    public ObjectId getObjectId() {
        return null;
    }
    
    @Override
    public ObjectKeyTemplate getObjectKeyTemplate() {
        return null;
    }
    
    @Override
    public ObjectKey getObjectKey() {
        return null;
    }
    
    @Override
    public boolean isEquivalent(final TaggedProfile taggedProfile) {
        return this.equals(taggedProfile);
    }
    
    @Override
    public void makeImmutable() {
    }
    
    @Override
    public boolean isLocal() {
        return false;
    }
    
    @Override
    public org.omg.IOP.TaggedProfile getIOPProfile() {
        final EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.orb);
        this.write(encapsOutputStream);
        return TaggedProfileHelper.read(encapsOutputStream.create_input_stream());
    }
}
