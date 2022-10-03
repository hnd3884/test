package org.omg.IOP.CodecPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class InvalidTypeForEncodingHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final InvalidTypeForEncoding invalidTypeForEncoding) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, invalidTypeForEncoding);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static InvalidTypeForEncoding extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InvalidTypeForEncodingHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (InvalidTypeForEncodingHelper.__typeCode == null) {
                    if (InvalidTypeForEncodingHelper.__active) {
                        return ORB.init().create_recursive_tc(InvalidTypeForEncodingHelper._id);
                    }
                    InvalidTypeForEncodingHelper.__active = true;
                    InvalidTypeForEncodingHelper.__typeCode = ORB.init().create_exception_tc(id(), "InvalidTypeForEncoding", new StructMember[0]);
                    InvalidTypeForEncodingHelper.__active = false;
                }
            }
        }
        return InvalidTypeForEncodingHelper.__typeCode;
    }
    
    public static String id() {
        return InvalidTypeForEncodingHelper._id;
    }
    
    public static InvalidTypeForEncoding read(final InputStream inputStream) {
        final InvalidTypeForEncoding invalidTypeForEncoding = new InvalidTypeForEncoding();
        inputStream.read_string();
        return invalidTypeForEncoding;
    }
    
    public static void write(final OutputStream outputStream, final InvalidTypeForEncoding invalidTypeForEncoding) {
        outputStream.write_string(id());
    }
    
    static {
        InvalidTypeForEncodingHelper._id = "IDL:omg.org/IOP/Codec/InvalidTypeForEncoding:1.0";
        InvalidTypeForEncodingHelper.__typeCode = null;
        InvalidTypeForEncodingHelper.__active = false;
    }
}
