package com.sun.corba.se.impl.corba;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class AnyImplHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Any any2) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, any2);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Any extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AnyImplHelper.__typeCode == null) {
            AnyImplHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_any);
        }
        return AnyImplHelper.__typeCode;
    }
    
    public static String id() {
        return AnyImplHelper._id;
    }
    
    public static Any read(final InputStream inputStream) {
        return inputStream.read_any();
    }
    
    public static void write(final OutputStream outputStream, final Any any) {
        outputStream.write_any(any);
    }
    
    static {
        AnyImplHelper._id = "IDL:omg.org/CORBA/Any:1.0";
        AnyImplHelper.__typeCode = null;
    }
}
