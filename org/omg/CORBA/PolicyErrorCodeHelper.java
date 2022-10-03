package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyErrorCodeHelper
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
        if (PolicyErrorCodeHelper.__typeCode == null) {
            PolicyErrorCodeHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
            PolicyErrorCodeHelper.__typeCode = ORB.init().create_alias_tc(id(), "PolicyErrorCode", PolicyErrorCodeHelper.__typeCode);
        }
        return PolicyErrorCodeHelper.__typeCode;
    }
    
    public static String id() {
        return PolicyErrorCodeHelper._id;
    }
    
    public static short read(final InputStream inputStream) {
        return inputStream.read_short();
    }
    
    public static void write(final OutputStream outputStream, final short n) {
        outputStream.write_short(n);
    }
    
    static {
        PolicyErrorCodeHelper._id = "IDL:omg.org/CORBA/PolicyErrorCode:1.0";
        PolicyErrorCodeHelper.__typeCode = null;
    }
}
