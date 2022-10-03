package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class IDLTypeHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final IDLType idlType) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, idlType);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static IDLType extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (IDLTypeHelper.__typeCode == null) {
            IDLTypeHelper.__typeCode = ORB.init().create_interface_tc(id(), "IDLType");
        }
        return IDLTypeHelper.__typeCode;
    }
    
    public static String id() {
        return IDLTypeHelper._id;
    }
    
    public static IDLType read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_IDLTypeStub.class));
    }
    
    public static void write(final OutputStream outputStream, final IDLType idlType) {
        outputStream.write_Object(idlType);
    }
    
    public static IDLType narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof IDLType) {
            return (IDLType)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        return new _IDLTypeStub(((ObjectImpl)object)._get_delegate());
    }
    
    static {
        IDLTypeHelper._id = "IDL:omg.org/CORBA/IDLType:1.0";
        IDLTypeHelper.__typeCode = null;
    }
}
