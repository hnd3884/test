package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class WStringSeqHelper
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
        if (WStringSeqHelper.__typeCode == null) {
            WStringSeqHelper.__typeCode = ORB.init().create_wstring_tc(0);
            WStringSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, WStringSeqHelper.__typeCode);
            WStringSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "WStringSeq", WStringSeqHelper.__typeCode);
        }
        return WStringSeqHelper.__typeCode;
    }
    
    public static String id() {
        return WStringSeqHelper._id;
    }
    
    public static String[] read(final InputStream inputStream) {
        final String[] array = new String[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = inputStream.read_wstring();
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final String[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            outputStream.write_wstring(array[i]);
        }
    }
    
    static {
        WStringSeqHelper._id = "IDL:omg.org/CORBA/WStringSeq:1.0";
        WStringSeqHelper.__typeCode = null;
    }
}
