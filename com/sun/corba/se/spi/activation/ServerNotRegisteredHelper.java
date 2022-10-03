package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerNotRegisteredHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServerNotRegistered serverNotRegistered) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverNotRegistered);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerNotRegistered extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerNotRegisteredHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServerNotRegisteredHelper.__typeCode == null) {
                    if (ServerNotRegisteredHelper.__active) {
                        return ORB.init().create_recursive_tc(ServerNotRegisteredHelper._id);
                    }
                    ServerNotRegisteredHelper.__active = true;
                    ServerNotRegisteredHelper.__typeCode = ORB.init().create_exception_tc(id(), "ServerNotRegistered", new StructMember[] { new StructMember("serverId", ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", ORB.init().get_primitive_tc(TCKind.tk_long)), null) });
                    ServerNotRegisteredHelper.__active = false;
                }
            }
        }
        return ServerNotRegisteredHelper.__typeCode;
    }
    
    public static String id() {
        return ServerNotRegisteredHelper._id;
    }
    
    public static ServerNotRegistered read(final InputStream inputStream) {
        final ServerNotRegistered serverNotRegistered = new ServerNotRegistered();
        inputStream.read_string();
        serverNotRegistered.serverId = inputStream.read_long();
        return serverNotRegistered;
    }
    
    public static void write(final OutputStream outputStream, final ServerNotRegistered serverNotRegistered) {
        outputStream.write_string(id());
        outputStream.write_long(serverNotRegistered.serverId);
    }
    
    static {
        ServerNotRegisteredHelper._id = "IDL:activation/ServerNotRegistered:1.0";
        ServerNotRegisteredHelper.__typeCode = null;
        ServerNotRegisteredHelper.__active = false;
    }
}
