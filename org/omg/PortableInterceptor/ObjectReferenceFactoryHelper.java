package org.omg.PortableInterceptor;

import java.io.Serializable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ObjectReferenceFactoryHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ObjectReferenceFactory objectReferenceFactory) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, objectReferenceFactory);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ObjectReferenceFactory extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ObjectReferenceFactoryHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ObjectReferenceFactoryHelper.__typeCode == null) {
                    if (ObjectReferenceFactoryHelper.__active) {
                        return ORB.init().create_recursive_tc(ObjectReferenceFactoryHelper._id);
                    }
                    ObjectReferenceFactoryHelper.__active = true;
                    ObjectReferenceFactoryHelper.__typeCode = ORB.init().create_value_tc(ObjectReferenceFactoryHelper._id, "ObjectReferenceFactory", (short)2, null, new ValueMember[0]);
                    ObjectReferenceFactoryHelper.__active = false;
                }
            }
        }
        return ObjectReferenceFactoryHelper.__typeCode;
    }
    
    public static String id() {
        return ObjectReferenceFactoryHelper._id;
    }
    
    public static ObjectReferenceFactory read(final InputStream inputStream) {
        return (ObjectReferenceFactory)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value(id());
    }
    
    public static void write(final OutputStream outputStream, final ObjectReferenceFactory objectReferenceFactory) {
        ((org.omg.CORBA_2_3.portable.OutputStream)outputStream).write_value(objectReferenceFactory, id());
    }
    
    static {
        ObjectReferenceFactoryHelper._id = "IDL:omg.org/PortableInterceptor/ObjectReferenceFactory:1.0";
        ObjectReferenceFactoryHelper.__typeCode = null;
        ObjectReferenceFactoryHelper.__active = false;
    }
}
