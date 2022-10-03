package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ObjectNotActiveHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ObjectNotActive objectNotActive) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, objectNotActive);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ObjectNotActive extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ObjectNotActiveHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ObjectNotActiveHelper.__typeCode == null) {
                    if (ObjectNotActiveHelper.__active) {
                        return ORB.init().create_recursive_tc(ObjectNotActiveHelper._id);
                    }
                    ObjectNotActiveHelper.__active = true;
                    ObjectNotActiveHelper.__typeCode = ORB.init().create_exception_tc(id(), "ObjectNotActive", new StructMember[0]);
                    ObjectNotActiveHelper.__active = false;
                }
            }
        }
        return ObjectNotActiveHelper.__typeCode;
    }
    
    public static String id() {
        return ObjectNotActiveHelper._id;
    }
    
    public static ObjectNotActive read(final InputStream inputStream) {
        final ObjectNotActive objectNotActive = new ObjectNotActive();
        inputStream.read_string();
        return objectNotActive;
    }
    
    public static void write(final OutputStream outputStream, final ObjectNotActive objectNotActive) {
        outputStream.write_string(id());
    }
    
    static {
        ObjectNotActiveHelper._id = "IDL:omg.org/PortableServer/POA/ObjectNotActive:1.0";
        ObjectNotActiveHelper.__typeCode = null;
        ObjectNotActiveHelper.__active = false;
    }
}
