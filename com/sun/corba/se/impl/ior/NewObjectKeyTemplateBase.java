package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;

public abstract class NewObjectKeyTemplateBase extends ObjectKeyTemplateBase
{
    public NewObjectKeyTemplateBase(final ORB orb, final int n, final int n2, final int n3, final String s, final ObjectAdapterId objectAdapterId) {
        super(orb, n, n2, n3, s, objectAdapterId);
        if (n != -1347695872) {
            throw this.wrapper.badMagic(new Integer(n));
        }
    }
    
    @Override
    public void write(final ObjectId objectId, final OutputStream outputStream) {
        super.write(objectId, outputStream);
        this.getORBVersion().write(outputStream);
    }
    
    @Override
    public void write(final OutputStream outputStream) {
        super.write(outputStream);
        this.getORBVersion().write(outputStream);
    }
    
    protected void setORBVersion(final InputStream inputStream) {
        this.setORBVersion(ORBVersionFactory.create(inputStream));
    }
}
