package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CompletionStatusHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final CompletionStatus completionStatus) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, completionStatus);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static CompletionStatus extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (CompletionStatusHelper.__typeCode == null) {
            CompletionStatusHelper.__typeCode = ORB.init().create_enum_tc(id(), "CompletionStatus", new String[] { "COMPLETED_YES", "COMPLETED_NO", "COMPLETED_MAYBE" });
        }
        return CompletionStatusHelper.__typeCode;
    }
    
    public static String id() {
        return CompletionStatusHelper._id;
    }
    
    public static CompletionStatus read(final InputStream inputStream) {
        return CompletionStatus.from_int(inputStream.read_long());
    }
    
    public static void write(final OutputStream outputStream, final CompletionStatus completionStatus) {
        outputStream.write_long(completionStatus.value());
    }
    
    static {
        CompletionStatusHelper._id = "IDL:omg.org/CORBA/CompletionStatus:1.0";
        CompletionStatusHelper.__typeCode = null;
    }
}
