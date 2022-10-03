package com.sun.org.omg.SendingContext.CodeBasePackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class URLSeqHelper
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
        if (URLSeqHelper.__typeCode == null) {
            URLSeqHelper.__typeCode = ORB.init().create_string_tc(0);
            URLSeqHelper.__typeCode = ORB.init().create_alias_tc(URLHelper.id(), "URL", URLSeqHelper.__typeCode);
            URLSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, URLSeqHelper.__typeCode);
            URLSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "URLSeq", URLSeqHelper.__typeCode);
        }
        return URLSeqHelper.__typeCode;
    }
    
    public static String id() {
        return URLSeqHelper._id;
    }
    
    public static String[] read(final InputStream inputStream) {
        final String[] array = new String[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = URLHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final String[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            URLHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        URLSeqHelper._id = "IDL:omg.org/SendingContext/CodeBase/URLSeq:1.0";
        URLSeqHelper.__typeCode = null;
    }
}
