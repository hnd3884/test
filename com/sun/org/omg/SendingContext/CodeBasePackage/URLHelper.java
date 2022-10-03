package com.sun.org.omg.SendingContext.CodeBasePackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class URLHelper
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
        if (URLHelper.__typeCode == null) {
            URLHelper.__typeCode = ORB.init().create_string_tc(0);
            URLHelper.__typeCode = ORB.init().create_alias_tc(id(), "URL", URLHelper.__typeCode);
        }
        return URLHelper.__typeCode;
    }
    
    public static String id() {
        return URLHelper._id;
    }
    
    public static String read(final InputStream inputStream) {
        return inputStream.read_string();
    }
    
    public static void write(final OutputStream outputStream, final String s) {
        outputStream.write_string(s);
    }
    
    static {
        URLHelper._id = "IDL:omg.org/SendingContext/CodeBase/URL:1.0";
        URLHelper.__typeCode = null;
    }
}
