package com.sun.corba.se.spi.activation.InitialNameServicePackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NameAlreadyBoundHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final NameAlreadyBound nameAlreadyBound) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, nameAlreadyBound);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NameAlreadyBound extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NameAlreadyBoundHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (NameAlreadyBoundHelper.__typeCode == null) {
                    if (NameAlreadyBoundHelper.__active) {
                        return ORB.init().create_recursive_tc(NameAlreadyBoundHelper._id);
                    }
                    NameAlreadyBoundHelper.__active = true;
                    NameAlreadyBoundHelper.__typeCode = ORB.init().create_exception_tc(id(), "NameAlreadyBound", new StructMember[0]);
                    NameAlreadyBoundHelper.__active = false;
                }
            }
        }
        return NameAlreadyBoundHelper.__typeCode;
    }
    
    public static String id() {
        return NameAlreadyBoundHelper._id;
    }
    
    public static NameAlreadyBound read(final InputStream inputStream) {
        final NameAlreadyBound nameAlreadyBound = new NameAlreadyBound();
        inputStream.read_string();
        return nameAlreadyBound;
    }
    
    public static void write(final OutputStream outputStream, final NameAlreadyBound nameAlreadyBound) {
        outputStream.write_string(id());
    }
    
    static {
        NameAlreadyBoundHelper._id = "IDL:activation/InitialNameService/NameAlreadyBound:1.0";
        NameAlreadyBoundHelper.__typeCode = null;
        NameAlreadyBoundHelper.__active = false;
    }
}
