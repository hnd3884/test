package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ORBidListHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final String[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static String[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ORBidListHelper.__typeCode == null) {
            ORBidListHelper.__typeCode = ORB.init().create_string_tc(0);
            ORBidListHelper.__typeCode = ORB.init().create_alias_tc(ORBidHelper.id(), "ORBid", ORBidListHelper.__typeCode);
            ORBidListHelper.__typeCode = ORB.init().create_sequence_tc(0, ORBidListHelper.__typeCode);
            ORBidListHelper.__typeCode = ORB.init().create_alias_tc(id(), "ORBidList", ORBidListHelper.__typeCode);
        }
        return ORBidListHelper.__typeCode;
    }
    
    public static String id() {
        return ORBidListHelper._id;
    }
    
    public static String[] read(final InputStream inputStream) {
        final String[] array = new String[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ORBidHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final String[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            ORBidHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        ORBidListHelper._id = "IDL:activation/ORBidList:1.0";
        ORBidListHelper.__typeCode = null;
    }
}
