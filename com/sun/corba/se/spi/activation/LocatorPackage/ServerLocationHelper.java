package com.sun.corba.se.spi.activation.LocatorPackage;

import org.omg.CORBA.portable.InputStream;
import com.sun.corba.se.spi.activation.ORBPortInfoHelper;
import com.sun.corba.se.spi.activation.ORBPortInfoListHelper;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerLocationHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServerLocation serverLocation) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverLocation);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerLocation extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerLocationHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServerLocationHelper.__typeCode == null) {
                    if (ServerLocationHelper.__active) {
                        return ORB.init().create_recursive_tc(ServerLocationHelper._id);
                    }
                    ServerLocationHelper.__active = true;
                    ServerLocationHelper.__typeCode = ORB.init().create_struct_tc(id(), "ServerLocation", new StructMember[] { new StructMember("hostname", ORB.init().create_string_tc(0), null), new StructMember("ports", ORB.init().create_alias_tc(ORBPortInfoListHelper.id(), "ORBPortInfoList", ORB.init().create_sequence_tc(0, ORBPortInfoHelper.type())), null) });
                    ServerLocationHelper.__active = false;
                }
            }
        }
        return ServerLocationHelper.__typeCode;
    }
    
    public static String id() {
        return ServerLocationHelper._id;
    }
    
    public static ServerLocation read(final InputStream inputStream) {
        final ServerLocation serverLocation = new ServerLocation();
        serverLocation.hostname = inputStream.read_string();
        serverLocation.ports = ORBPortInfoListHelper.read(inputStream);
        return serverLocation;
    }
    
    public static void write(final OutputStream outputStream, final ServerLocation serverLocation) {
        outputStream.write_string(serverLocation.hostname);
        ORBPortInfoListHelper.write(outputStream, serverLocation.ports);
    }
    
    static {
        ServerLocationHelper._id = "IDL:activation/Locator/ServerLocation:1.0";
        ServerLocationHelper.__typeCode = null;
        ServerLocationHelper.__active = false;
    }
}
