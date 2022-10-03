package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class StringSeqHelper
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
        if (StringSeqHelper.__typeCode == null) {
            StringSeqHelper.__typeCode = ORB.init().create_string_tc(0);
            StringSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, StringSeqHelper.__typeCode);
            StringSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "StringSeq", StringSeqHelper.__typeCode);
        }
        return StringSeqHelper.__typeCode;
    }
    
    public static String id() {
        return StringSeqHelper._id;
    }
    
    public static String[] read(final InputStream inputStream) {
        final String[] array = new String[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = inputStream.read_string();
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final String[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            outputStream.write_string(array[i]);
        }
    }
    
    static {
        StringSeqHelper._id = "IDL:omg.org/CORBA/StringSeq:1.0";
        StringSeqHelper.__typeCode = null;
    }
}
