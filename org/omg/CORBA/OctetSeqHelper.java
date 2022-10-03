package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class OctetSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final byte[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static byte[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (OctetSeqHelper.__typeCode == null) {
            OctetSeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
            OctetSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, OctetSeqHelper.__typeCode);
            OctetSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "OctetSeq", OctetSeqHelper.__typeCode);
        }
        return OctetSeqHelper.__typeCode;
    }
    
    public static String id() {
        return OctetSeqHelper._id;
    }
    
    public static byte[] read(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final byte[] array = new byte[read_long];
        inputStream.read_octet_array(array, 0, read_long);
        return array;
    }
    
    public static void write(final OutputStream outputStream, final byte[] array) {
        outputStream.write_long(array.length);
        outputStream.write_octet_array(array, 0, array.length);
    }
    
    static {
        OctetSeqHelper._id = "IDL:omg.org/CORBA/OctetSeq:1.0";
        OctetSeqHelper.__typeCode = null;
    }
}
