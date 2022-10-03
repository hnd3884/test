package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class SetOverrideTypeHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final SetOverrideType setOverrideType) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, setOverrideType);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static SetOverrideType extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (SetOverrideTypeHelper.__typeCode == null) {
            SetOverrideTypeHelper.__typeCode = ORB.init().create_enum_tc(id(), "SetOverrideType", new String[] { "SET_OVERRIDE", "ADD_OVERRIDE" });
        }
        return SetOverrideTypeHelper.__typeCode;
    }
    
    public static String id() {
        return SetOverrideTypeHelper._id;
    }
    
    public static SetOverrideType read(final InputStream inputStream) {
        return SetOverrideType.from_int(inputStream.read_long());
    }
    
    public static void write(final OutputStream outputStream, final SetOverrideType setOverrideType) {
        outputStream.write_long(setOverrideType.value());
    }
    
    static {
        SetOverrideTypeHelper._id = "IDL:omg.org/CORBA/SetOverrideType:1.0";
        SetOverrideTypeHelper.__typeCode = null;
    }
}
