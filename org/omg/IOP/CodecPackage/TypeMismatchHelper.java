package org.omg.IOP.CodecPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class TypeMismatchHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final TypeMismatch typeMismatch) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, typeMismatch);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static TypeMismatch extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (TypeMismatchHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (TypeMismatchHelper.__typeCode == null) {
                    if (TypeMismatchHelper.__active) {
                        return ORB.init().create_recursive_tc(TypeMismatchHelper._id);
                    }
                    TypeMismatchHelper.__active = true;
                    TypeMismatchHelper.__typeCode = ORB.init().create_exception_tc(id(), "TypeMismatch", new StructMember[0]);
                    TypeMismatchHelper.__active = false;
                }
            }
        }
        return TypeMismatchHelper.__typeCode;
    }
    
    public static String id() {
        return TypeMismatchHelper._id;
    }
    
    public static TypeMismatch read(final InputStream inputStream) {
        final TypeMismatch typeMismatch = new TypeMismatch();
        inputStream.read_string();
        return typeMismatch;
    }
    
    public static void write(final OutputStream outputStream, final TypeMismatch typeMismatch) {
        outputStream.write_string(id());
    }
    
    static {
        TypeMismatchHelper._id = "IDL:omg.org/IOP/Codec/TypeMismatch:1.0";
        TypeMismatchHelper.__typeCode = null;
        TypeMismatchHelper.__active = false;
    }
}
