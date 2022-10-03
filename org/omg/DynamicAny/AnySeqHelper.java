package org.omg.DynamicAny;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class AnySeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Any[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Any[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AnySeqHelper.__typeCode == null) {
            AnySeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_any);
            AnySeqHelper.__typeCode = ORB.init().create_sequence_tc(0, AnySeqHelper.__typeCode);
            AnySeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "AnySeq", AnySeqHelper.__typeCode);
        }
        return AnySeqHelper.__typeCode;
    }
    
    public static String id() {
        return AnySeqHelper._id;
    }
    
    public static Any[] read(final InputStream inputStream) {
        final Any[] array = new Any[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = inputStream.read_any();
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final Any[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            outputStream.write_any(array[i]);
        }
    }
    
    static {
        AnySeqHelper._id = "IDL:omg.org/DynamicAny/AnySeq:1.0";
        AnySeqHelper.__typeCode = null;
    }
}
