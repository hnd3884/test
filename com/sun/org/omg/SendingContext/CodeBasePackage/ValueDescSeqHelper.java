package com.sun.org.omg.SendingContext.CodeBasePackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import org.omg.CORBA.portable.OutputStream;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class ValueDescSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final FullValueDescription[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static FullValueDescription[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ValueDescSeqHelper.__typeCode == null) {
            ValueDescSeqHelper.__typeCode = FullValueDescriptionHelper.type();
            ValueDescSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, ValueDescSeqHelper.__typeCode);
            ValueDescSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "ValueDescSeq", ValueDescSeqHelper.__typeCode);
        }
        return ValueDescSeqHelper.__typeCode;
    }
    
    public static String id() {
        return ValueDescSeqHelper._id;
    }
    
    public static FullValueDescription[] read(final InputStream inputStream) {
        final FullValueDescription[] array = new FullValueDescription[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = FullValueDescriptionHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final FullValueDescription[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            FullValueDescriptionHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        ValueDescSeqHelper._id = "IDL:omg.org/SendingContext/CodeBase/ValueDescSeq:1.0";
        ValueDescSeqHelper.__typeCode = null;
    }
}
