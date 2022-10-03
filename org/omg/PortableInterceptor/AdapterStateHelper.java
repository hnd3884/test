package org.omg.PortableInterceptor;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class AdapterStateHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final short n) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, n);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static short extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AdapterStateHelper.__typeCode == null) {
            AdapterStateHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
            AdapterStateHelper.__typeCode = ORB.init().create_alias_tc(id(), "AdapterState", AdapterStateHelper.__typeCode);
        }
        return AdapterStateHelper.__typeCode;
    }
    
    public static String id() {
        return AdapterStateHelper._id;
    }
    
    public static short read(final InputStream inputStream) {
        return inputStream.read_short();
    }
    
    public static void write(final OutputStream outputStream, final short n) {
        outputStream.write_short(n);
    }
    
    static {
        AdapterStateHelper._id = "IDL:omg.org/PortableInterceptor/AdapterState:1.0";
        AdapterStateHelper.__typeCode = null;
    }
}
