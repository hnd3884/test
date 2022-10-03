package org.omg.IOP.CodecPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class FormatMismatchHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final FormatMismatch formatMismatch) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, formatMismatch);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static FormatMismatch extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (FormatMismatchHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (FormatMismatchHelper.__typeCode == null) {
                    if (FormatMismatchHelper.__active) {
                        return ORB.init().create_recursive_tc(FormatMismatchHelper._id);
                    }
                    FormatMismatchHelper.__active = true;
                    FormatMismatchHelper.__typeCode = ORB.init().create_exception_tc(id(), "FormatMismatch", new StructMember[0]);
                    FormatMismatchHelper.__active = false;
                }
            }
        }
        return FormatMismatchHelper.__typeCode;
    }
    
    public static String id() {
        return FormatMismatchHelper._id;
    }
    
    public static FormatMismatch read(final InputStream inputStream) {
        final FormatMismatch formatMismatch = new FormatMismatch();
        inputStream.read_string();
        return formatMismatch;
    }
    
    public static void write(final OutputStream outputStream, final FormatMismatch formatMismatch) {
        outputStream.write_string(id());
    }
    
    static {
        FormatMismatchHelper._id = "IDL:omg.org/IOP/Codec/FormatMismatch:1.0";
        FormatMismatchHelper.__typeCode = null;
        FormatMismatchHelper.__active = false;
    }
}
