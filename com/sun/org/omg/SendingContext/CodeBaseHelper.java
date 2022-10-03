package com.sun.org.omg.SendingContext;

import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class CodeBaseHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final CodeBase codeBase) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, codeBase);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static CodeBase extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (CodeBaseHelper.__typeCode == null) {
            CodeBaseHelper.__typeCode = ORB.init().create_interface_tc(id(), "CodeBase");
        }
        return CodeBaseHelper.__typeCode;
    }
    
    public static String id() {
        return CodeBaseHelper._id;
    }
    
    public static CodeBase read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_CodeBaseStub.class));
    }
    
    public static void write(final OutputStream outputStream, final CodeBase codeBase) {
        outputStream.write_Object(codeBase);
    }
    
    public static CodeBase narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof CodeBase) {
            return (CodeBase)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        return new _CodeBaseStub(((ObjectImpl)object)._get_delegate());
    }
    
    static {
        CodeBaseHelper._id = "IDL:omg.org/SendingContext/CodeBase:1.0";
        CodeBaseHelper.__typeCode = null;
    }
}
