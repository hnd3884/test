package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class ContextIdSeqHelper
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
        if (ContextIdSeqHelper.__typeCode == null) {
            ContextIdSeqHelper.__typeCode = ORB.init().create_string_tc(0);
            ContextIdSeqHelper.__typeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", ContextIdSeqHelper.__typeCode);
            ContextIdSeqHelper.__typeCode = ORB.init().create_alias_tc(ContextIdentifierHelper.id(), "ContextIdentifier", ContextIdSeqHelper.__typeCode);
            ContextIdSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, ContextIdSeqHelper.__typeCode);
            ContextIdSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "ContextIdSeq", ContextIdSeqHelper.__typeCode);
        }
        return ContextIdSeqHelper.__typeCode;
    }
    
    public static String id() {
        return ContextIdSeqHelper._id;
    }
    
    public static String[] read(final InputStream inputStream) {
        final String[] array = new String[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ContextIdentifierHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final String[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            ContextIdentifierHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        ContextIdSeqHelper._id = "IDL:omg.org/CORBA/ContextIdSeq:1.0";
        ContextIdSeqHelper.__typeCode = null;
    }
}
