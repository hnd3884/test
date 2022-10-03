package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyTypeHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final int n) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, n);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static int extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (PolicyTypeHelper.__typeCode == null) {
            PolicyTypeHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
            PolicyTypeHelper.__typeCode = ORB.init().create_alias_tc(id(), "PolicyType", PolicyTypeHelper.__typeCode);
        }
        return PolicyTypeHelper.__typeCode;
    }
    
    public static String id() {
        return PolicyTypeHelper._id;
    }
    
    public static int read(final InputStream inputStream) {
        return inputStream.read_ulong();
    }
    
    public static void write(final OutputStream outputStream, final int n) {
        outputStream.write_ulong(n);
    }
    
    static {
        PolicyTypeHelper._id = "IDL:omg.org/CORBA/PolicyType:1.0";
        PolicyTypeHelper.__typeCode = null;
    }
}
