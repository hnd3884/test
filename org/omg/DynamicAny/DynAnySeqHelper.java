package org.omg.DynamicAny;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class DynAnySeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DynAny[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DynAny[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DynAnySeqHelper.__typeCode == null) {
            DynAnySeqHelper.__typeCode = DynAnyHelper.type();
            DynAnySeqHelper.__typeCode = ORB.init().create_sequence_tc(0, DynAnySeqHelper.__typeCode);
            DynAnySeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "DynAnySeq", DynAnySeqHelper.__typeCode);
        }
        return DynAnySeqHelper.__typeCode;
    }
    
    public static String id() {
        return DynAnySeqHelper._id;
    }
    
    public static DynAny[] read(final InputStream inputStream) {
        final DynAny[] array = new DynAny[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = DynAnyHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final DynAny[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            DynAnyHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        DynAnySeqHelper._id = "IDL:omg.org/DynamicAny/DynAnySeq:1.0";
        DynAnySeqHelper.__typeCode = null;
    }
}
