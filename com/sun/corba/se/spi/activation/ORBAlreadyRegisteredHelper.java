package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ORBAlreadyRegisteredHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ORBAlreadyRegistered orbAlreadyRegistered) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, orbAlreadyRegistered);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ORBAlreadyRegistered extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ORBAlreadyRegisteredHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ORBAlreadyRegisteredHelper.__typeCode == null) {
                    if (ORBAlreadyRegisteredHelper.__active) {
                        return ORB.init().create_recursive_tc(ORBAlreadyRegisteredHelper._id);
                    }
                    ORBAlreadyRegisteredHelper.__active = true;
                    ORBAlreadyRegisteredHelper.__typeCode = ORB.init().create_exception_tc(id(), "ORBAlreadyRegistered", new StructMember[] { new StructMember("orbId", ORB.init().create_alias_tc(ORBidHelper.id(), "ORBid", ORB.init().create_string_tc(0)), null) });
                    ORBAlreadyRegisteredHelper.__active = false;
                }
            }
        }
        return ORBAlreadyRegisteredHelper.__typeCode;
    }
    
    public static String id() {
        return ORBAlreadyRegisteredHelper._id;
    }
    
    public static ORBAlreadyRegistered read(final InputStream inputStream) {
        final ORBAlreadyRegistered orbAlreadyRegistered = new ORBAlreadyRegistered();
        inputStream.read_string();
        orbAlreadyRegistered.orbId = inputStream.read_string();
        return orbAlreadyRegistered;
    }
    
    public static void write(final OutputStream outputStream, final ORBAlreadyRegistered orbAlreadyRegistered) {
        outputStream.write_string(id());
        outputStream.write_string(orbAlreadyRegistered.orbId);
    }
    
    static {
        ORBAlreadyRegisteredHelper._id = "IDL:activation/ORBAlreadyRegistered:1.0";
        ORBAlreadyRegisteredHelper.__typeCode = null;
        ORBAlreadyRegisteredHelper.__active = false;
    }
}
