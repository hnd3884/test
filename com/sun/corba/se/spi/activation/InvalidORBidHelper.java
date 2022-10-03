package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class InvalidORBidHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final InvalidORBid invalidORBid) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, invalidORBid);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static InvalidORBid extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InvalidORBidHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (InvalidORBidHelper.__typeCode == null) {
                    if (InvalidORBidHelper.__active) {
                        return ORB.init().create_recursive_tc(InvalidORBidHelper._id);
                    }
                    InvalidORBidHelper.__active = true;
                    InvalidORBidHelper.__typeCode = ORB.init().create_exception_tc(id(), "InvalidORBid", new StructMember[0]);
                    InvalidORBidHelper.__active = false;
                }
            }
        }
        return InvalidORBidHelper.__typeCode;
    }
    
    public static String id() {
        return InvalidORBidHelper._id;
    }
    
    public static InvalidORBid read(final InputStream inputStream) {
        final InvalidORBid invalidORBid = new InvalidORBid();
        inputStream.read_string();
        return invalidORBid;
    }
    
    public static void write(final OutputStream outputStream, final InvalidORBid invalidORBid) {
        outputStream.write_string(id());
    }
    
    static {
        InvalidORBidHelper._id = "IDL:activation/InvalidORBid:1.0";
        InvalidORBidHelper.__typeCode = null;
        InvalidORBidHelper.__active = false;
    }
}
