package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class InvalidAddressHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final InvalidAddress invalidAddress) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, invalidAddress);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static InvalidAddress extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InvalidAddressHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (InvalidAddressHelper.__typeCode == null) {
                    if (InvalidAddressHelper.__active) {
                        return ORB.init().create_recursive_tc(InvalidAddressHelper._id);
                    }
                    InvalidAddressHelper.__active = true;
                    InvalidAddressHelper.__typeCode = ORB.init().create_exception_tc(id(), "InvalidAddress", new StructMember[0]);
                    InvalidAddressHelper.__active = false;
                }
            }
        }
        return InvalidAddressHelper.__typeCode;
    }
    
    public static String id() {
        return InvalidAddressHelper._id;
    }
    
    public static InvalidAddress read(final InputStream inputStream) {
        final InvalidAddress invalidAddress = new InvalidAddress();
        inputStream.read_string();
        return invalidAddress;
    }
    
    public static void write(final OutputStream outputStream, final InvalidAddress invalidAddress) {
        outputStream.write_string(id());
    }
    
    static {
        InvalidAddressHelper._id = "IDL:omg.org/CosNaming/NamingContextExt/InvalidAddress:1.0";
        InvalidAddressHelper.__typeCode = null;
        InvalidAddressHelper.__active = false;
    }
}
