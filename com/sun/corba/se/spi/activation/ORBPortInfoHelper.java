package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ORBPortInfoHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ORBPortInfo orbPortInfo) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, orbPortInfo);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ORBPortInfo extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ORBPortInfoHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ORBPortInfoHelper.__typeCode == null) {
                    if (ORBPortInfoHelper.__active) {
                        return ORB.init().create_recursive_tc(ORBPortInfoHelper._id);
                    }
                    ORBPortInfoHelper.__active = true;
                    ORBPortInfoHelper.__typeCode = ORB.init().create_struct_tc(id(), "ORBPortInfo", new StructMember[] { new StructMember("orbId", ORB.init().create_alias_tc(ORBidHelper.id(), "ORBid", ORB.init().create_string_tc(0)), null), new StructMember("port", ORB.init().create_alias_tc(TCPPortHelper.id(), "TCPPort", ORB.init().get_primitive_tc(TCKind.tk_long)), null) });
                    ORBPortInfoHelper.__active = false;
                }
            }
        }
        return ORBPortInfoHelper.__typeCode;
    }
    
    public static String id() {
        return ORBPortInfoHelper._id;
    }
    
    public static ORBPortInfo read(final InputStream inputStream) {
        final ORBPortInfo orbPortInfo = new ORBPortInfo();
        orbPortInfo.orbId = inputStream.read_string();
        orbPortInfo.port = inputStream.read_long();
        return orbPortInfo;
    }
    
    public static void write(final OutputStream outputStream, final ORBPortInfo orbPortInfo) {
        outputStream.write_string(orbPortInfo.orbId);
        outputStream.write_long(orbPortInfo.port);
    }
    
    static {
        ORBPortInfoHelper._id = "IDL:activation/ORBPortInfo:1.0";
        ORBPortInfoHelper.__typeCode = null;
        ORBPortInfoHelper.__active = false;
    }
}
