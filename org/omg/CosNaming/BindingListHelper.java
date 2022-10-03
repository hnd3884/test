package org.omg.CosNaming;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class BindingListHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Binding[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Binding[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (BindingListHelper.__typeCode == null) {
            BindingListHelper.__typeCode = BindingHelper.type();
            BindingListHelper.__typeCode = ORB.init().create_sequence_tc(0, BindingListHelper.__typeCode);
            BindingListHelper.__typeCode = ORB.init().create_alias_tc(id(), "BindingList", BindingListHelper.__typeCode);
        }
        return BindingListHelper.__typeCode;
    }
    
    public static String id() {
        return BindingListHelper._id;
    }
    
    public static Binding[] read(final InputStream inputStream) {
        final Binding[] array = new Binding[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = BindingHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final Binding[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            BindingHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        BindingListHelper._id = "IDL:omg.org/CosNaming/BindingList:1.0";
        BindingListHelper.__typeCode = null;
    }
}
