package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class InitializerSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Initializer[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Initializer[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InitializerSeqHelper.__typeCode == null) {
            InitializerSeqHelper.__typeCode = InitializerHelper.type();
            InitializerSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, InitializerSeqHelper.__typeCode);
            InitializerSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "InitializerSeq", InitializerSeqHelper.__typeCode);
        }
        return InitializerSeqHelper.__typeCode;
    }
    
    public static String id() {
        return InitializerSeqHelper._id;
    }
    
    public static Initializer[] read(final InputStream inputStream) {
        final Initializer[] array = new Initializer[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = InitializerHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final Initializer[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            InitializerHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        InitializerSeqHelper._id = "IDL:omg.org/CORBA/InitializerSeq:1.0";
        InitializerSeqHelper.__typeCode = null;
    }
}
