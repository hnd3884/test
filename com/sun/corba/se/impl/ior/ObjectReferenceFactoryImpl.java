package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IORFactory;
import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.spi.ior.IORFactories;
import org.omg.PortableInterceptor.ObjectReferenceFactoryHelper;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.ior.IORTemplateList;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.PortableInterceptor.ObjectReferenceFactory;

public class ObjectReferenceFactoryImpl extends ObjectReferenceProducerBase implements ObjectReferenceFactory, StreamableValue
{
    private transient IORTemplateList iorTemplates;
    public static final String repositoryId = "IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0";
    
    public ObjectReferenceFactoryImpl(final InputStream inputStream) {
        super((ORB)inputStream.orb());
        this._read(inputStream);
    }
    
    public ObjectReferenceFactoryImpl(final ORB orb, final IORTemplateList iorTemplates) {
        super(orb);
        this.iorTemplates = iorTemplates;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ObjectReferenceFactoryImpl)) {
            return false;
        }
        final ObjectReferenceFactoryImpl objectReferenceFactoryImpl = (ObjectReferenceFactoryImpl)o;
        return this.iorTemplates != null && this.iorTemplates.equals(objectReferenceFactoryImpl.iorTemplates);
    }
    
    @Override
    public int hashCode() {
        return this.iorTemplates.hashCode();
    }
    
    @Override
    public String[] _truncatable_ids() {
        return new String[] { "IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0" };
    }
    
    @Override
    public TypeCode _type() {
        return ObjectReferenceFactoryHelper.type();
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.iorTemplates = IORFactories.makeIORTemplateList((org.omg.CORBA_2_3.portable.InputStream)inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        this.iorTemplates.write((org.omg.CORBA_2_3.portable.OutputStream)outputStream);
    }
    
    @Override
    public IORFactory getIORFactory() {
        return this.iorTemplates;
    }
    
    @Override
    public IORTemplateList getIORTemplateList() {
        return this.iorTemplates;
    }
}
