package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ObjectAlreadyActiveHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ObjectAlreadyActive objectAlreadyActive) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, objectAlreadyActive);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ObjectAlreadyActive extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ObjectAlreadyActiveHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ObjectAlreadyActiveHelper.__typeCode == null) {
                    if (ObjectAlreadyActiveHelper.__active) {
                        return ORB.init().create_recursive_tc(ObjectAlreadyActiveHelper._id);
                    }
                    ObjectAlreadyActiveHelper.__active = true;
                    ObjectAlreadyActiveHelper.__typeCode = ORB.init().create_exception_tc(id(), "ObjectAlreadyActive", new StructMember[0]);
                    ObjectAlreadyActiveHelper.__active = false;
                }
            }
        }
        return ObjectAlreadyActiveHelper.__typeCode;
    }
    
    public static String id() {
        return ObjectAlreadyActiveHelper._id;
    }
    
    public static ObjectAlreadyActive read(final InputStream inputStream) {
        final ObjectAlreadyActive objectAlreadyActive = new ObjectAlreadyActive();
        inputStream.read_string();
        return objectAlreadyActive;
    }
    
    public static void write(final OutputStream outputStream, final ObjectAlreadyActive objectAlreadyActive) {
        outputStream.write_string(id());
    }
    
    static {
        ObjectAlreadyActiveHelper._id = "IDL:omg.org/PortableServer/POA/ObjectAlreadyActive:1.0";
        ObjectAlreadyActiveHelper.__typeCode = null;
        ObjectAlreadyActiveHelper.__active = false;
    }
}
