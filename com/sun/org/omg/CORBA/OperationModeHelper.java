package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class OperationModeHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final OperationMode operationMode) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, operationMode);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static OperationMode extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (OperationModeHelper.__typeCode == null) {
            OperationModeHelper.__typeCode = ORB.init().create_enum_tc(id(), "OperationMode", new String[] { "OP_NORMAL", "OP_ONEWAY" });
        }
        return OperationModeHelper.__typeCode;
    }
    
    public static String id() {
        return OperationModeHelper._id;
    }
    
    public static OperationMode read(final InputStream inputStream) {
        return OperationMode.from_int(inputStream.read_long());
    }
    
    public static void write(final OutputStream outputStream, final OperationMode operationMode) {
        outputStream.write_long(operationMode.value());
    }
    
    static {
        OperationModeHelper._id = "IDL:omg.org/CORBA/OperationMode:1.0";
        OperationModeHelper.__typeCode = null;
    }
}
