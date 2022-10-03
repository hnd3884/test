package org.omg.CosNaming;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NameHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final NameComponent[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NameComponent[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NameHelper.__typeCode == null) {
            NameHelper.__typeCode = NameComponentHelper.type();
            NameHelper.__typeCode = ORB.init().create_sequence_tc(0, NameHelper.__typeCode);
            NameHelper.__typeCode = ORB.init().create_alias_tc(id(), "Name", NameHelper.__typeCode);
        }
        return NameHelper.__typeCode;
    }
    
    public static String id() {
        return NameHelper._id;
    }
    
    public static NameComponent[] read(final InputStream inputStream) {
        final NameComponent[] array = new NameComponent[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = NameComponentHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final NameComponent[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            NameComponentHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        NameHelper._id = "IDL:omg.org/CosNaming/Name:1.0";
        NameHelper.__typeCode = null;
    }
}
