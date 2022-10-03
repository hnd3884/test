package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class CurrentHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Current current) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, current);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Current extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (CurrentHelper.__typeCode == null) {
            CurrentHelper.__typeCode = ORB.init().create_interface_tc(id(), "Current");
        }
        return CurrentHelper.__typeCode;
    }
    
    public static String id() {
        return CurrentHelper._id;
    }
    
    public static Current read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final Current current) {
        throw new MARSHAL();
    }
    
    public static Current narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Current) {
            return (Current)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        return null;
    }
    
    static {
        CurrentHelper._id = "IDL:omg.org/PortableServer/Current:2.3";
        CurrentHelper.__typeCode = null;
    }
}
