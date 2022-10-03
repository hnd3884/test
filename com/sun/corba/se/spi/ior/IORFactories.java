package com.sun.corba.se.spi.ior;

import java.io.Serializable;
import org.omg.CORBA.portable.ValueFactory;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.CORBA.Object;
import com.sun.corba.se.impl.ior.ObjectKeyFactoryImpl;
import com.sun.corba.se.impl.ior.ObjectReferenceFactoryImpl;
import com.sun.corba.se.impl.ior.ObjectReferenceProducerBase;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.CORBA.BAD_PARAM;
import com.sun.corba.se.impl.ior.ObjectReferenceTemplateImpl;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import com.sun.corba.se.impl.ior.IORTemplateListImpl;
import com.sun.corba.se.impl.ior.IORTemplateImpl;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.impl.ior.IORImpl;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.ior.ObjectKeyImpl;
import com.sun.corba.se.impl.ior.ObjectIdImpl;

public class IORFactories
{
    private IORFactories() {
    }
    
    public static ObjectId makeObjectId(final byte[] array) {
        return new ObjectIdImpl(array);
    }
    
    public static ObjectKey makeObjectKey(final ObjectKeyTemplate objectKeyTemplate, final ObjectId objectId) {
        return new ObjectKeyImpl(objectKeyTemplate, objectId);
    }
    
    public static IOR makeIOR(final ORB orb, final String s) {
        return new IORImpl(orb, s);
    }
    
    public static IOR makeIOR(final ORB orb) {
        return new IORImpl(orb);
    }
    
    public static IOR makeIOR(final InputStream inputStream) {
        return new IORImpl(inputStream);
    }
    
    public static IORTemplate makeIORTemplate(final ObjectKeyTemplate objectKeyTemplate) {
        return new IORTemplateImpl(objectKeyTemplate);
    }
    
    public static IORTemplate makeIORTemplate(final InputStream inputStream) {
        return new IORTemplateImpl(inputStream);
    }
    
    public static IORTemplateList makeIORTemplateList() {
        return new IORTemplateListImpl();
    }
    
    public static IORTemplateList makeIORTemplateList(final InputStream inputStream) {
        return new IORTemplateListImpl(inputStream);
    }
    
    public static IORFactory getIORFactory(final ObjectReferenceTemplate objectReferenceTemplate) {
        if (objectReferenceTemplate instanceof ObjectReferenceTemplateImpl) {
            return ((ObjectReferenceTemplateImpl)objectReferenceTemplate).getIORFactory();
        }
        throw new BAD_PARAM();
    }
    
    public static IORTemplateList getIORTemplateList(final ObjectReferenceFactory objectReferenceFactory) {
        if (objectReferenceFactory instanceof ObjectReferenceProducerBase) {
            return ((ObjectReferenceProducerBase)objectReferenceFactory).getIORTemplateList();
        }
        throw new BAD_PARAM();
    }
    
    public static ObjectReferenceTemplate makeObjectReferenceTemplate(final ORB orb, final IORTemplate iorTemplate) {
        return new ObjectReferenceTemplateImpl(orb, iorTemplate);
    }
    
    public static ObjectReferenceFactory makeObjectReferenceFactory(final ORB orb, final IORTemplateList list) {
        return new ObjectReferenceFactoryImpl(orb, list);
    }
    
    public static ObjectKeyFactory makeObjectKeyFactory(final ORB orb) {
        return new ObjectKeyFactoryImpl(orb);
    }
    
    public static IOR getIOR(final org.omg.CORBA.Object object) {
        return ORBUtility.getIOR(object);
    }
    
    public static org.omg.CORBA.Object makeObjectReference(final IOR ior) {
        return ORBUtility.makeObjectReference(ior);
    }
    
    public static void registerValueFactories(final ORB orb) {
        orb.register_value_factory("IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0", new ValueFactory() {
            @Override
            public Serializable read_value(final InputStream inputStream) {
                return new ObjectReferenceTemplateImpl(inputStream);
            }
        });
        orb.register_value_factory("IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0", new ValueFactory() {
            @Override
            public Serializable read_value(final InputStream inputStream) {
                return new ObjectReferenceFactoryImpl(inputStream);
            }
        });
    }
}
