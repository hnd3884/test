package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class FloatSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final float[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static float[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (FloatSeqHelper.__typeCode == null) {
            FloatSeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_float);
            FloatSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, FloatSeqHelper.__typeCode);
            FloatSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "FloatSeq", FloatSeqHelper.__typeCode);
        }
        return FloatSeqHelper.__typeCode;
    }
    
    public static String id() {
        return FloatSeqHelper._id;
    }
    
    public static float[] read(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final float[] array = new float[read_long];
        inputStream.read_float_array(array, 0, read_long);
        return array;
    }
    
    public static void write(final OutputStream outputStream, final float[] array) {
        outputStream.write_long(array.length);
        outputStream.write_float_array(array, 0, array.length);
    }
    
    static {
        FloatSeqHelper._id = "IDL:omg.org/CORBA/FloatSeq:1.0";
        FloatSeqHelper.__typeCode = null;
    }
}
