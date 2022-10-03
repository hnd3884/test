package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CharSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final char[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static char[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (CharSeqHelper.__typeCode == null) {
            CharSeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_char);
            CharSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, CharSeqHelper.__typeCode);
            CharSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "CharSeq", CharSeqHelper.__typeCode);
        }
        return CharSeqHelper.__typeCode;
    }
    
    public static String id() {
        return CharSeqHelper._id;
    }
    
    public static char[] read(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final char[] array = new char[read_long];
        inputStream.read_char_array(array, 0, read_long);
        return array;
    }
    
    public static void write(final OutputStream outputStream, final char[] array) {
        outputStream.write_long(array.length);
        outputStream.write_char_array(array, 0, array.length);
    }
    
    static {
        CharSeqHelper._id = "IDL:omg.org/CORBA/CharSeq:1.0";
        CharSeqHelper.__typeCode = null;
    }
}
