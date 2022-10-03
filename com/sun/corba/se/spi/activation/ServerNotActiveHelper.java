package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerNotActiveHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServerNotActive serverNotActive) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverNotActive);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerNotActive extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerNotActiveHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServerNotActiveHelper.__typeCode == null) {
                    if (ServerNotActiveHelper.__active) {
                        return ORB.init().create_recursive_tc(ServerNotActiveHelper._id);
                    }
                    ServerNotActiveHelper.__active = true;
                    ServerNotActiveHelper.__typeCode = ORB.init().create_exception_tc(id(), "ServerNotActive", new StructMember[] { new StructMember("serverId", ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", ORB.init().get_primitive_tc(TCKind.tk_long)), null) });
                    ServerNotActiveHelper.__active = false;
                }
            }
        }
        return ServerNotActiveHelper.__typeCode;
    }
    
    public static String id() {
        return ServerNotActiveHelper._id;
    }
    
    public static ServerNotActive read(final InputStream inputStream) {
        final ServerNotActive serverNotActive = new ServerNotActive();
        inputStream.read_string();
        serverNotActive.serverId = inputStream.read_long();
        return serverNotActive;
    }
    
    public static void write(final OutputStream outputStream, final ServerNotActive serverNotActive) {
        outputStream.write_string(id());
        outputStream.write_long(serverNotActive.serverId);
    }
    
    static {
        ServerNotActiveHelper._id = "IDL:activation/ServerNotActive:1.0";
        ServerNotActiveHelper.__typeCode = null;
        ServerNotActiveHelper.__active = false;
    }
}
