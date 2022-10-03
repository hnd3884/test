package org.omg.CosNaming;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class BindingTypeHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final BindingType bindingType) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, bindingType);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static BindingType extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (BindingTypeHelper.__typeCode == null) {
            BindingTypeHelper.__typeCode = ORB.init().create_enum_tc(id(), "BindingType", new String[] { "nobject", "ncontext" });
        }
        return BindingTypeHelper.__typeCode;
    }
    
    public static String id() {
        return BindingTypeHelper._id;
    }
    
    public static BindingType read(final InputStream inputStream) {
        return BindingType.from_int(inputStream.read_long());
    }
    
    public static void write(final OutputStream outputStream, final BindingType bindingType) {
        outputStream.write_long(bindingType.value());
    }
    
    static {
        BindingTypeHelper._id = "IDL:omg.org/CosNaming/BindingType:1.0";
        BindingTypeHelper.__typeCode = null;
    }
}
