package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NoSuchEndPointHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final NoSuchEndPoint noSuchEndPoint) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, noSuchEndPoint);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NoSuchEndPoint extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NoSuchEndPointHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (NoSuchEndPointHelper.__typeCode == null) {
                    if (NoSuchEndPointHelper.__active) {
                        return ORB.init().create_recursive_tc(NoSuchEndPointHelper._id);
                    }
                    NoSuchEndPointHelper.__active = true;
                    NoSuchEndPointHelper.__typeCode = ORB.init().create_exception_tc(id(), "NoSuchEndPoint", new StructMember[0]);
                    NoSuchEndPointHelper.__active = false;
                }
            }
        }
        return NoSuchEndPointHelper.__typeCode;
    }
    
    public static String id() {
        return NoSuchEndPointHelper._id;
    }
    
    public static NoSuchEndPoint read(final InputStream inputStream) {
        final NoSuchEndPoint noSuchEndPoint = new NoSuchEndPoint();
        inputStream.read_string();
        return noSuchEndPoint;
    }
    
    public static void write(final OutputStream outputStream, final NoSuchEndPoint noSuchEndPoint) {
        outputStream.write_string(id());
    }
    
    static {
        NoSuchEndPointHelper._id = "IDL:activation/NoSuchEndPoint:1.0";
        NoSuchEndPointHelper.__typeCode = null;
        NoSuchEndPointHelper.__active = false;
    }
}
