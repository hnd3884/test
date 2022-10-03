package org.omg.IOP;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class CodecFactoryHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final CodecFactory codecFactory) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, codecFactory);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static CodecFactory extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (CodecFactoryHelper.__typeCode == null) {
            CodecFactoryHelper.__typeCode = ORB.init().create_interface_tc(id(), "CodecFactory");
        }
        return CodecFactoryHelper.__typeCode;
    }
    
    public static String id() {
        return CodecFactoryHelper._id;
    }
    
    public static CodecFactory read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final CodecFactory codecFactory) {
        throw new MARSHAL();
    }
    
    public static CodecFactory narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof CodecFactory) {
            return (CodecFactory)object;
        }
        throw new BAD_PARAM();
    }
    
    public static CodecFactory unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof CodecFactory) {
            return (CodecFactory)object;
        }
        throw new BAD_PARAM();
    }
    
    static {
        CodecFactoryHelper._id = "IDL:omg.org/IOP/CodecFactory:1.0";
        CodecFactoryHelper.__typeCode = null;
    }
}
