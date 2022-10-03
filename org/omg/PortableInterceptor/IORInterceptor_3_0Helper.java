package org.omg.PortableInterceptor;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class IORInterceptor_3_0Helper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final IORInterceptor_3_0 iorInterceptor_3_0) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, iorInterceptor_3_0);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static IORInterceptor_3_0 extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (IORInterceptor_3_0Helper.__typeCode == null) {
            IORInterceptor_3_0Helper.__typeCode = ORB.init().create_interface_tc(id(), "IORInterceptor_3_0");
        }
        return IORInterceptor_3_0Helper.__typeCode;
    }
    
    public static String id() {
        return IORInterceptor_3_0Helper._id;
    }
    
    public static IORInterceptor_3_0 read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final IORInterceptor_3_0 iorInterceptor_3_0) {
        throw new MARSHAL();
    }
    
    public static IORInterceptor_3_0 narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof IORInterceptor_3_0) {
            return (IORInterceptor_3_0)object;
        }
        throw new BAD_PARAM();
    }
    
    public static IORInterceptor_3_0 unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof IORInterceptor_3_0) {
            return (IORInterceptor_3_0)object;
        }
        throw new BAD_PARAM();
    }
    
    static {
        IORInterceptor_3_0Helper._id = "IDL:omg.org/PortableInterceptor/IORInterceptor_3_0:1.0";
        IORInterceptor_3_0Helper.__typeCode = null;
    }
}
