package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class AttrDescriptionSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final AttributeDescription[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static AttributeDescription[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AttrDescriptionSeqHelper.__typeCode == null) {
            AttrDescriptionSeqHelper.__typeCode = AttributeDescriptionHelper.type();
            AttrDescriptionSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, AttrDescriptionSeqHelper.__typeCode);
            AttrDescriptionSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "AttrDescriptionSeq", AttrDescriptionSeqHelper.__typeCode);
        }
        return AttrDescriptionSeqHelper.__typeCode;
    }
    
    public static String id() {
        return AttrDescriptionSeqHelper._id;
    }
    
    public static AttributeDescription[] read(final InputStream inputStream) {
        final AttributeDescription[] array = new AttributeDescription[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = AttributeDescriptionHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final AttributeDescription[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            AttributeDescriptionHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        AttrDescriptionSeqHelper._id = "IDL:omg.org/CORBA/AttrDescriptionSeq:1.0";
        AttrDescriptionSeqHelper.__typeCode = null;
    }
}
