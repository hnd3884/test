package org.omg.DynamicAny;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NameDynAnyPairSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final NameDynAnyPair[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NameDynAnyPair[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NameDynAnyPairSeqHelper.__typeCode == null) {
            NameDynAnyPairSeqHelper.__typeCode = NameDynAnyPairHelper.type();
            NameDynAnyPairSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, NameDynAnyPairSeqHelper.__typeCode);
            NameDynAnyPairSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "NameDynAnyPairSeq", NameDynAnyPairSeqHelper.__typeCode);
        }
        return NameDynAnyPairSeqHelper.__typeCode;
    }
    
    public static String id() {
        return NameDynAnyPairSeqHelper._id;
    }
    
    public static NameDynAnyPair[] read(final InputStream inputStream) {
        final NameDynAnyPair[] array = new NameDynAnyPair[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = NameDynAnyPairHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final NameDynAnyPair[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            NameDynAnyPairHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        NameDynAnyPairSeqHelper._id = "IDL:omg.org/DynamicAny/NameDynAnyPairSeq:1.0";
        NameDynAnyPairSeqHelper.__typeCode = null;
    }
}
