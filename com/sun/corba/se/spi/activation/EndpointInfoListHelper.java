package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class EndpointInfoListHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final EndPointInfo[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static EndPointInfo[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (EndpointInfoListHelper.__typeCode == null) {
            EndpointInfoListHelper.__typeCode = EndPointInfoHelper.type();
            EndpointInfoListHelper.__typeCode = ORB.init().create_sequence_tc(0, EndpointInfoListHelper.__typeCode);
            EndpointInfoListHelper.__typeCode = ORB.init().create_alias_tc(id(), "EndpointInfoList", EndpointInfoListHelper.__typeCode);
        }
        return EndpointInfoListHelper.__typeCode;
    }
    
    public static String id() {
        return EndpointInfoListHelper._id;
    }
    
    public static EndPointInfo[] read(final InputStream inputStream) {
        final EndPointInfo[] array = new EndPointInfo[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = EndPointInfoHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final EndPointInfo[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            EndPointInfoHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        EndpointInfoListHelper._id = "IDL:activation/EndpointInfoList:1.0";
        EndpointInfoListHelper.__typeCode = null;
    }
}
