package org.omg.IOP;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class MultipleComponentProfileHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final TaggedComponent[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static TaggedComponent[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (MultipleComponentProfileHelper.__typeCode == null) {
            MultipleComponentProfileHelper.__typeCode = TaggedComponentHelper.type();
            MultipleComponentProfileHelper.__typeCode = ORB.init().create_sequence_tc(0, MultipleComponentProfileHelper.__typeCode);
            MultipleComponentProfileHelper.__typeCode = ORB.init().create_alias_tc(id(), "MultipleComponentProfile", MultipleComponentProfileHelper.__typeCode);
        }
        return MultipleComponentProfileHelper.__typeCode;
    }
    
    public static String id() {
        return MultipleComponentProfileHelper._id;
    }
    
    public static TaggedComponent[] read(final InputStream inputStream) {
        final TaggedComponent[] array = new TaggedComponent[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = TaggedComponentHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final TaggedComponent[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            TaggedComponentHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        MultipleComponentProfileHelper._id = "IDL:omg.org/IOP/MultipleComponentProfile:1.0";
        MultipleComponentProfileHelper.__typeCode = null;
    }
}
