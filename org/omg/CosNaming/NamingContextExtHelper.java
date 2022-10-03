package org.omg.CosNaming;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NamingContextExtHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final NamingContextExt namingContextExt) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, namingContextExt);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NamingContextExt extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NamingContextExtHelper.__typeCode == null) {
            NamingContextExtHelper.__typeCode = ORB.init().create_interface_tc(id(), "NamingContextExt");
        }
        return NamingContextExtHelper.__typeCode;
    }
    
    public static String id() {
        return NamingContextExtHelper._id;
    }
    
    public static NamingContextExt read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_NamingContextExtStub.class));
    }
    
    public static void write(final OutputStream outputStream, final NamingContextExt namingContextExt) {
        outputStream.write_Object(namingContextExt);
    }
    
    public static NamingContextExt narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof NamingContextExt) {
            return (NamingContextExt)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _NamingContextExtStub namingContextExtStub = new _NamingContextExtStub();
        namingContextExtStub._set_delegate(get_delegate);
        return namingContextExtStub;
    }
    
    public static NamingContextExt unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof NamingContextExt) {
            return (NamingContextExt)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _NamingContextExtStub namingContextExtStub = new _NamingContextExtStub();
        namingContextExtStub._set_delegate(get_delegate);
        return namingContextExtStub;
    }
    
    static {
        NamingContextExtHelper._id = "IDL:omg.org/CosNaming/NamingContextExt:1.0";
        NamingContextExtHelper.__typeCode = null;
    }
}
