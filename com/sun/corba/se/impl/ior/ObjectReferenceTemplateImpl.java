package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.IORFactory;
import org.omg.CORBA.portable.OutputStream;
import com.sun.corba.se.spi.ior.IORFactories;
import org.omg.PortableInterceptor.ObjectReferenceTemplateHelper;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.ior.IORTemplate;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public class ObjectReferenceTemplateImpl extends ObjectReferenceProducerBase implements ObjectReferenceTemplate, StreamableValue
{
    private transient IORTemplate iorTemplate;
    public static final String repositoryId = "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0";
    
    public ObjectReferenceTemplateImpl(final InputStream inputStream) {
        super((ORB)inputStream.orb());
        this._read(inputStream);
    }
    
    public ObjectReferenceTemplateImpl(final ORB orb, final IORTemplate iorTemplate) {
        super(orb);
        this.iorTemplate = iorTemplate;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ObjectReferenceTemplateImpl)) {
            return false;
        }
        final ObjectReferenceTemplateImpl objectReferenceTemplateImpl = (ObjectReferenceTemplateImpl)o;
        return this.iorTemplate != null && this.iorTemplate.equals(objectReferenceTemplateImpl.iorTemplate);
    }
    
    @Override
    public int hashCode() {
        return this.iorTemplate.hashCode();
    }
    
    @Override
    public String[] _truncatable_ids() {
        return new String[] { "IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0" };
    }
    
    @Override
    public TypeCode _type() {
        return ObjectReferenceTemplateHelper.type();
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        final org.omg.CORBA_2_3.portable.InputStream inputStream2 = (org.omg.CORBA_2_3.portable.InputStream)inputStream;
        this.iorTemplate = IORFactories.makeIORTemplate(inputStream2);
        this.orb = (ORB)inputStream2.orb();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        this.iorTemplate.write((org.omg.CORBA_2_3.portable.OutputStream)outputStream);
    }
    
    @Override
    public String server_id() {
        return Integer.toString(this.iorTemplate.getObjectKeyTemplate().getServerId());
    }
    
    @Override
    public String orb_id() {
        return this.iorTemplate.getObjectKeyTemplate().getORBId();
    }
    
    @Override
    public String[] adapter_name() {
        return this.iorTemplate.getObjectKeyTemplate().getObjectAdapterId().getAdapterName();
    }
    
    @Override
    public IORFactory getIORFactory() {
        return this.iorTemplate;
    }
    
    @Override
    public IORTemplateList getIORTemplateList() {
        final IORTemplateList iorTemplateList = IORFactories.makeIORTemplateList();
        iorTemplateList.add(this.iorTemplate);
        iorTemplateList.makeImmutable();
        return iorTemplateList;
    }
}
