package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NotEmptyHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final NotEmpty notEmpty) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, notEmpty);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NotEmpty extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NotEmptyHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (NotEmptyHelper.__typeCode == null) {
                    if (NotEmptyHelper.__active) {
                        return ORB.init().create_recursive_tc(NotEmptyHelper._id);
                    }
                    NotEmptyHelper.__active = true;
                    NotEmptyHelper.__typeCode = ORB.init().create_exception_tc(id(), "NotEmpty", new StructMember[0]);
                    NotEmptyHelper.__active = false;
                }
            }
        }
        return NotEmptyHelper.__typeCode;
    }
    
    public static String id() {
        return NotEmptyHelper._id;
    }
    
    public static NotEmpty read(final InputStream inputStream) {
        final NotEmpty notEmpty = new NotEmpty();
        inputStream.read_string();
        return notEmpty;
    }
    
    public static void write(final OutputStream outputStream, final NotEmpty notEmpty) {
        outputStream.write_string(id());
    }
    
    static {
        NotEmptyHelper._id = "IDL:omg.org/CosNaming/NamingContext/NotEmpty:1.0";
        NotEmptyHelper.__typeCode = null;
        NotEmptyHelper.__active = false;
    }
}
