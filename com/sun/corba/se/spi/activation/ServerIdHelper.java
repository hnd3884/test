package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerIdHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final int n) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, n);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static int extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerIdHelper.__typeCode == null) {
            ServerIdHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
            ServerIdHelper.__typeCode = ORB.init().create_alias_tc(id(), "ServerId", ServerIdHelper.__typeCode);
        }
        return ServerIdHelper.__typeCode;
    }
    
    public static String id() {
        return ServerIdHelper._id;
    }
    
    public static int read(final InputStream inputStream) {
        return inputStream.read_long();
    }
    
    public static void write(final OutputStream outputStream, final int n) {
        outputStream.write_long(n);
    }
    
    static {
        ServerIdHelper._id = "IDL:activation/ServerId:1.0";
        ServerIdHelper.__typeCode = null;
    }
}
