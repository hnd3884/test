package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NotFoundReasonHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final NotFoundReason notFoundReason) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, notFoundReason);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NotFoundReason extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NotFoundReasonHelper.__typeCode == null) {
            NotFoundReasonHelper.__typeCode = ORB.init().create_enum_tc(id(), "NotFoundReason", new String[] { "missing_node", "not_context", "not_object" });
        }
        return NotFoundReasonHelper.__typeCode;
    }
    
    public static String id() {
        return NotFoundReasonHelper._id;
    }
    
    public static NotFoundReason read(final InputStream inputStream) {
        return NotFoundReason.from_int(inputStream.read_long());
    }
    
    public static void write(final OutputStream outputStream, final NotFoundReason notFoundReason) {
        outputStream.write_long(notFoundReason.value());
    }
    
    static {
        NotFoundReasonHelper._id = "IDL:omg.org/CosNaming/NamingContext/NotFoundReason:1.0";
        NotFoundReasonHelper.__typeCode = null;
    }
}
