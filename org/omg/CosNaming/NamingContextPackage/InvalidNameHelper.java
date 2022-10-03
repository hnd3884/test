package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class InvalidNameHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final InvalidName invalidName) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, invalidName);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static InvalidName extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InvalidNameHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (InvalidNameHelper.__typeCode == null) {
                    if (InvalidNameHelper.__active) {
                        return ORB.init().create_recursive_tc(InvalidNameHelper._id);
                    }
                    InvalidNameHelper.__active = true;
                    InvalidNameHelper.__typeCode = ORB.init().create_exception_tc(id(), "InvalidName", new StructMember[0]);
                    InvalidNameHelper.__active = false;
                }
            }
        }
        return InvalidNameHelper.__typeCode;
    }
    
    public static String id() {
        return InvalidNameHelper._id;
    }
    
    public static InvalidName read(final InputStream inputStream) {
        final InvalidName invalidName = new InvalidName();
        inputStream.read_string();
        return invalidName;
    }
    
    public static void write(final OutputStream outputStream, final InvalidName invalidName) {
        outputStream.write_string(id());
    }
    
    static {
        InvalidNameHelper._id = "IDL:omg.org/CosNaming/NamingContext/InvalidName:1.0";
        InvalidNameHelper.__typeCode = null;
        InvalidNameHelper.__active = false;
    }
}
