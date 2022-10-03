package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ParameterModeHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final ParameterMode parameterMode) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, parameterMode);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ParameterMode extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ParameterModeHelper.__typeCode == null) {
            ParameterModeHelper.__typeCode = ORB.init().create_enum_tc(id(), "ParameterMode", new String[] { "PARAM_IN", "PARAM_OUT", "PARAM_INOUT" });
        }
        return ParameterModeHelper.__typeCode;
    }
    
    public static String id() {
        return ParameterModeHelper._id;
    }
    
    public static ParameterMode read(final InputStream inputStream) {
        return ParameterMode.from_int(inputStream.read_long());
    }
    
    public static void write(final OutputStream outputStream, final ParameterMode parameterMode) {
        outputStream.write_long(parameterMode.value());
    }
    
    static {
        ParameterModeHelper._id = "IDL:omg.org/CORBA/ParameterMode:1.0";
        ParameterModeHelper.__typeCode = null;
    }
}
