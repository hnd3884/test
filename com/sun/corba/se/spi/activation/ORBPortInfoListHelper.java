package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ORBPortInfoListHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final ORBPortInfo[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ORBPortInfo[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ORBPortInfoListHelper.__typeCode == null) {
            ORBPortInfoListHelper.__typeCode = ORBPortInfoHelper.type();
            ORBPortInfoListHelper.__typeCode = ORB.init().create_sequence_tc(0, ORBPortInfoListHelper.__typeCode);
            ORBPortInfoListHelper.__typeCode = ORB.init().create_alias_tc(id(), "ORBPortInfoList", ORBPortInfoListHelper.__typeCode);
        }
        return ORBPortInfoListHelper.__typeCode;
    }
    
    public static String id() {
        return ORBPortInfoListHelper._id;
    }
    
    public static ORBPortInfo[] read(final InputStream inputStream) {
        final ORBPortInfo[] array = new ORBPortInfo[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ORBPortInfoHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final ORBPortInfo[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            ORBPortInfoHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        ORBPortInfoListHelper._id = "IDL:activation/ORBPortInfoList:1.0";
        ORBPortInfoListHelper.__typeCode = null;
    }
}
