package org.omg.PortableInterceptor;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ForwardRequestHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ForwardRequest forwardRequest) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, forwardRequest);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ForwardRequest extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ForwardRequestHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ForwardRequestHelper.__typeCode == null) {
                    if (ForwardRequestHelper.__active) {
                        return ORB.init().create_recursive_tc(ForwardRequestHelper._id);
                    }
                    ForwardRequestHelper.__active = true;
                    ForwardRequestHelper.__typeCode = ORB.init().create_exception_tc(id(), "ForwardRequest", new StructMember[] { new StructMember("forward", ObjectHelper.type(), null) });
                    ForwardRequestHelper.__active = false;
                }
            }
        }
        return ForwardRequestHelper.__typeCode;
    }
    
    public static String id() {
        return ForwardRequestHelper._id;
    }
    
    public static ForwardRequest read(final InputStream inputStream) {
        final ForwardRequest forwardRequest = new ForwardRequest();
        inputStream.read_string();
        forwardRequest.forward = ObjectHelper.read(inputStream);
        return forwardRequest;
    }
    
    public static void write(final OutputStream outputStream, final ForwardRequest forwardRequest) {
        outputStream.write_string(id());
        ObjectHelper.write(outputStream, forwardRequest.forward);
    }
    
    static {
        ForwardRequestHelper._id = "IDL:omg.org/PortableInterceptor/ForwardRequest:1.0";
        ForwardRequestHelper.__typeCode = null;
        ForwardRequestHelper.__active = false;
    }
}
