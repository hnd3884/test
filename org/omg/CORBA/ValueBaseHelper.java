package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import java.io.Serializable;

public abstract class ValueBaseHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Serializable s) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, s);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Serializable extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ValueBaseHelper.__typeCode == null) {
            ValueBaseHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_value);
        }
        return ValueBaseHelper.__typeCode;
    }
    
    public static String id() {
        return ValueBaseHelper._id;
    }
    
    public static Serializable read(final InputStream inputStream) {
        return ((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value();
    }
    
    public static void write(final OutputStream outputStream, final Serializable s) {
        ((org.omg.CORBA_2_3.portable.OutputStream)outputStream).write_value(s);
    }
    
    static {
        ValueBaseHelper._id = "IDL:omg.org/CORBA/ValueBase:1.0";
        ValueBaseHelper.__typeCode = null;
    }
}
