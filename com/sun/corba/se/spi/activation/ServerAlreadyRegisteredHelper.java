package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerAlreadyRegisteredHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServerAlreadyRegistered serverAlreadyRegistered) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverAlreadyRegistered);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerAlreadyRegistered extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerAlreadyRegisteredHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServerAlreadyRegisteredHelper.__typeCode == null) {
                    if (ServerAlreadyRegisteredHelper.__active) {
                        return ORB.init().create_recursive_tc(ServerAlreadyRegisteredHelper._id);
                    }
                    ServerAlreadyRegisteredHelper.__active = true;
                    ServerAlreadyRegisteredHelper.__typeCode = ORB.init().create_exception_tc(id(), "ServerAlreadyRegistered", new StructMember[] { new StructMember("serverId", ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", ORB.init().get_primitive_tc(TCKind.tk_long)), null) });
                    ServerAlreadyRegisteredHelper.__active = false;
                }
            }
        }
        return ServerAlreadyRegisteredHelper.__typeCode;
    }
    
    public static String id() {
        return ServerAlreadyRegisteredHelper._id;
    }
    
    public static ServerAlreadyRegistered read(final InputStream inputStream) {
        final ServerAlreadyRegistered serverAlreadyRegistered = new ServerAlreadyRegistered();
        inputStream.read_string();
        serverAlreadyRegistered.serverId = inputStream.read_long();
        return serverAlreadyRegistered;
    }
    
    public static void write(final OutputStream outputStream, final ServerAlreadyRegistered serverAlreadyRegistered) {
        outputStream.write_string(id());
        outputStream.write_long(serverAlreadyRegistered.serverId);
    }
    
    static {
        ServerAlreadyRegisteredHelper._id = "IDL:activation/ServerAlreadyRegistered:1.0";
        ServerAlreadyRegisteredHelper.__typeCode = null;
        ServerAlreadyRegisteredHelper.__active = false;
    }
}
