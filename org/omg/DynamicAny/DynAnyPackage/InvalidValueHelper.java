package org.omg.DynamicAny.DynAnyPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class InvalidValueHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final InvalidValue invalidValue) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, invalidValue);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static InvalidValue extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InvalidValueHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (InvalidValueHelper.__typeCode == null) {
                    if (InvalidValueHelper.__active) {
                        return ORB.init().create_recursive_tc(InvalidValueHelper._id);
                    }
                    InvalidValueHelper.__active = true;
                    InvalidValueHelper.__typeCode = ORB.init().create_exception_tc(id(), "InvalidValue", new StructMember[0]);
                    InvalidValueHelper.__active = false;
                }
            }
        }
        return InvalidValueHelper.__typeCode;
    }
    
    public static String id() {
        return InvalidValueHelper._id;
    }
    
    public static InvalidValue read(final InputStream inputStream) {
        final InvalidValue invalidValue = new InvalidValue();
        inputStream.read_string();
        return invalidValue;
    }
    
    public static void write(final OutputStream outputStream, final InvalidValue invalidValue) {
        outputStream.write_string(id());
    }
    
    static {
        InvalidValueHelper._id = "IDL:omg.org/DynamicAny/DynAny/InvalidValue:1.0";
        InvalidValueHelper.__typeCode = null;
        InvalidValueHelper.__active = false;
    }
}
