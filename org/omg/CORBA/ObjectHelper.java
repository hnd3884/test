package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final org.omg.CORBA.Object object) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, object);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static org.omg.CORBA.Object extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ObjectHelper.__typeCode == null) {
            ObjectHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_objref);
        }
        return ObjectHelper.__typeCode;
    }
    
    public static String id() {
        return ObjectHelper._id;
    }
    
    public static org.omg.CORBA.Object read(final InputStream inputStream) {
        return inputStream.read_Object();
    }
    
    public static void write(final OutputStream outputStream, final org.omg.CORBA.Object object) {
        outputStream.write_Object(object);
    }
    
    static {
        ObjectHelper._id = "";
        ObjectHelper.__typeCode = null;
    }
}
