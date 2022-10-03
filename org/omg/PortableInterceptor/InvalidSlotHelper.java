package org.omg.PortableInterceptor;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class InvalidSlotHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final InvalidSlot invalidSlot) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, invalidSlot);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static InvalidSlot extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InvalidSlotHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (InvalidSlotHelper.__typeCode == null) {
                    if (InvalidSlotHelper.__active) {
                        return ORB.init().create_recursive_tc(InvalidSlotHelper._id);
                    }
                    InvalidSlotHelper.__active = true;
                    InvalidSlotHelper.__typeCode = ORB.init().create_exception_tc(id(), "InvalidSlot", new StructMember[0]);
                    InvalidSlotHelper.__active = false;
                }
            }
        }
        return InvalidSlotHelper.__typeCode;
    }
    
    public static String id() {
        return InvalidSlotHelper._id;
    }
    
    public static InvalidSlot read(final InputStream inputStream) {
        final InvalidSlot invalidSlot = new InvalidSlot();
        inputStream.read_string();
        return invalidSlot;
    }
    
    public static void write(final OutputStream outputStream, final InvalidSlot invalidSlot) {
        outputStream.write_string(id());
    }
    
    static {
        InvalidSlotHelper._id = "IDL:omg.org/PortableInterceptor/InvalidSlot:1.0";
        InvalidSlotHelper.__typeCode = null;
        InvalidSlotHelper.__active = false;
    }
}
