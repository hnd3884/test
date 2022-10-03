package org.omg.CORBA;

import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Policy policy) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, policy);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Policy extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (PolicyHelper.__typeCode == null) {
            PolicyHelper.__typeCode = ORB.init().create_interface_tc(id(), "Policy");
        }
        return PolicyHelper.__typeCode;
    }
    
    public static String id() {
        return PolicyHelper._id;
    }
    
    public static Policy read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_PolicyStub.class));
    }
    
    public static void write(final OutputStream outputStream, final Policy policy) {
        outputStream.write_Object(policy);
    }
    
    public static Policy narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Policy) {
            return (Policy)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        return new _PolicyStub(((ObjectImpl)object)._get_delegate());
    }
    
    static {
        PolicyHelper._id = "IDL:omg.org/CORBA/Policy:1.0";
        PolicyHelper.__typeCode = null;
    }
}
