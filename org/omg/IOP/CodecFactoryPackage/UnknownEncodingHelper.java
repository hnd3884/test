package org.omg.IOP.CodecFactoryPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class UnknownEncodingHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final UnknownEncoding unknownEncoding) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, unknownEncoding);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static UnknownEncoding extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (UnknownEncodingHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (UnknownEncodingHelper.__typeCode == null) {
                    if (UnknownEncodingHelper.__active) {
                        return ORB.init().create_recursive_tc(UnknownEncodingHelper._id);
                    }
                    UnknownEncodingHelper.__active = true;
                    UnknownEncodingHelper.__typeCode = ORB.init().create_exception_tc(id(), "UnknownEncoding", new StructMember[0]);
                    UnknownEncodingHelper.__active = false;
                }
            }
        }
        return UnknownEncodingHelper.__typeCode;
    }
    
    public static String id() {
        return UnknownEncodingHelper._id;
    }
    
    public static UnknownEncoding read(final InputStream inputStream) {
        final UnknownEncoding unknownEncoding = new UnknownEncoding();
        inputStream.read_string();
        return unknownEncoding;
    }
    
    public static void write(final OutputStream outputStream, final UnknownEncoding unknownEncoding) {
        outputStream.write_string(id());
    }
    
    static {
        UnknownEncodingHelper._id = "IDL:omg.org/IOP/CodecFactory/UnknownEncoding:1.0";
        UnknownEncodingHelper.__typeCode = null;
        UnknownEncodingHelper.__active = false;
    }
}
