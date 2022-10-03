package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class RepositoryIdSeqHelper
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
        if (RepositoryIdSeqHelper.__typeCode == null) {
            RepositoryIdSeqHelper.__typeCode = ORB.init().create_string_tc(0);
            RepositoryIdSeqHelper.__typeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", RepositoryIdSeqHelper.__typeCode);
            RepositoryIdSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, RepositoryIdSeqHelper.__typeCode);
            RepositoryIdSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "RepositoryIdSeq", RepositoryIdSeqHelper.__typeCode);
        }
        return RepositoryIdSeqHelper.__typeCode;
    }
    
    public static String id() {
        return RepositoryIdSeqHelper._id;
    }
    
    public static String[] read(final InputStream inputStream) {
        final String[] array = new String[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = RepositoryIdHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final String[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            RepositoryIdHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        RepositoryIdSeqHelper._id = "IDL:omg.org/CORBA/RepositoryIdSeq:1.0";
        RepositoryIdSeqHelper.__typeCode = null;
    }
}
