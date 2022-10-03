package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ORBidHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final String s) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, s);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static String extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ORBidHelper.__typeCode == null) {
            ORBidHelper.__typeCode = ORB.init().create_string_tc(0);
            ORBidHelper.__typeCode = ORB.init().create_alias_tc(id(), "ORBid", ORBidHelper.__typeCode);
        }
        return ORBidHelper.__typeCode;
    }
    
    public static String id() {
        return ORBidHelper._id;
    }
    
    public static String read(final InputStream inputStream) {
        return inputStream.read_string();
    }
    
    public static void write(final OutputStream outputStream, final String s) {
        outputStream.write_string(s);
    }
    
    static {
        ORBidHelper._id = "IDL:activation/ORBid:1.0";
        ORBidHelper.__typeCode = null;
    }
}
