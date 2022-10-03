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

public abstract class NamingContextHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final NamingContext namingContext) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, namingContext);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NamingContext extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NamingContextHelper.__typeCode == null) {
            NamingContextHelper.__typeCode = ORB.init().create_interface_tc(id(), "NamingContext");
        }
        return NamingContextHelper.__typeCode;
    }
    
    public static String id() {
        return NamingContextHelper._id;
    }
    
    public static NamingContext read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_NamingContextStub.class));
    }
    
    public static void write(final OutputStream outputStream, final NamingContext namingContext) {
        outputStream.write_Object(namingContext);
    }
    
    public static NamingContext narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof NamingContext) {
            return (NamingContext)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _NamingContextStub namingContextStub = new _NamingContextStub();
        namingContextStub._set_delegate(get_delegate);
        return namingContextStub;
    }
    
    public static NamingContext unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof NamingContext) {
            return (NamingContext)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _NamingContextStub namingContextStub = new _NamingContextStub();
        namingContextStub._set_delegate(get_delegate);
        return namingContextStub;
    }
    
    static {
        NamingContextHelper._id = "IDL:omg.org/CosNaming/NamingContext:1.0";
        NamingContextHelper.__typeCode = null;
    }
}
