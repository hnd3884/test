package org.omg.DynamicAny;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NameValuePairSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final NameValuePair[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NameValuePair[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NameValuePairSeqHelper.__typeCode == null) {
            NameValuePairSeqHelper.__typeCode = NameValuePairHelper.type();
            NameValuePairSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, NameValuePairSeqHelper.__typeCode);
            NameValuePairSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "NameValuePairSeq", NameValuePairSeqHelper.__typeCode);
        }
        return NameValuePairSeqHelper.__typeCode;
    }
    
    public static String id() {
        return NameValuePairSeqHelper._id;
    }
    
    public static NameValuePair[] read(final InputStream inputStream) {
        final NameValuePair[] array = new NameValuePair[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = NameValuePairHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final NameValuePair[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            NameValuePairHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        NameValuePairSeqHelper._id = "IDL:omg.org/DynamicAny/NameValuePairSeq:1.0";
        NameValuePairSeqHelper.__typeCode = null;
    }
}
