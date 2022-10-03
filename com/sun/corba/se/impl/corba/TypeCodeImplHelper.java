package com.sun.corba.se.impl.corba;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class TypeCodeImplHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final TypeCode typeCode) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, typeCode);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static TypeCode extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (TypeCodeImplHelper.__typeCode == null) {
            TypeCodeImplHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
        }
        return TypeCodeImplHelper.__typeCode;
    }
    
    public static String id() {
        return TypeCodeImplHelper._id;
    }
    
    public static TypeCode read(final InputStream inputStream) {
        return inputStream.read_TypeCode();
    }
    
    public static void write(final OutputStream outputStream, final TypeCode typeCode) {
        outputStream.write_TypeCode(typeCode);
    }
    
    public static void write(final OutputStream outputStream, final TypeCodeImpl typeCodeImpl) {
        outputStream.write_TypeCode(typeCodeImpl);
    }
    
    static {
        TypeCodeImplHelper._id = "IDL:omg.org/CORBA/TypeCode:1.0";
        TypeCodeImplHelper.__typeCode = null;
    }
}
