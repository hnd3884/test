package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerIdsHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final int[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static int[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerIdsHelper.__typeCode == null) {
            ServerIdsHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
            ServerIdsHelper.__typeCode = ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", ServerIdsHelper.__typeCode);
            ServerIdsHelper.__typeCode = ORB.init().create_sequence_tc(0, ServerIdsHelper.__typeCode);
            ServerIdsHelper.__typeCode = ORB.init().create_alias_tc(id(), "ServerIds", ServerIdsHelper.__typeCode);
        }
        return ServerIdsHelper.__typeCode;
    }
    
    public static String id() {
        return ServerIdsHelper._id;
    }
    
    public static int[] read(final InputStream inputStream) {
        final int[] array = new int[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ServerIdHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final int[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            ServerIdHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        ServerIdsHelper._id = "IDL:activation/ServerIds:1.0";
        ServerIdsHelper.__typeCode = null;
    }
}
