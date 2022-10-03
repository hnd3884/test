package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerAlreadyActiveHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServerAlreadyActive serverAlreadyActive) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverAlreadyActive);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerAlreadyActive extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerAlreadyActiveHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServerAlreadyActiveHelper.__typeCode == null) {
                    if (ServerAlreadyActiveHelper.__active) {
                        return ORB.init().create_recursive_tc(ServerAlreadyActiveHelper._id);
                    }
                    ServerAlreadyActiveHelper.__active = true;
                    ServerAlreadyActiveHelper.__typeCode = ORB.init().create_exception_tc(id(), "ServerAlreadyActive", new StructMember[] { new StructMember("serverId", ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", ORB.init().get_primitive_tc(TCKind.tk_long)), null) });
                    ServerAlreadyActiveHelper.__active = false;
                }
            }
        }
        return ServerAlreadyActiveHelper.__typeCode;
    }
    
    public static String id() {
        return ServerAlreadyActiveHelper._id;
    }
    
    public static ServerAlreadyActive read(final InputStream inputStream) {
        final ServerAlreadyActive serverAlreadyActive = new ServerAlreadyActive();
        inputStream.read_string();
        serverAlreadyActive.serverId = inputStream.read_long();
        return serverAlreadyActive;
    }
    
    public static void write(final OutputStream outputStream, final ServerAlreadyActive serverAlreadyActive) {
        outputStream.write_string(id());
        outputStream.write_long(serverAlreadyActive.serverId);
    }
    
    static {
        ServerAlreadyActiveHelper._id = "IDL:activation/ServerAlreadyActive:1.0";
        ServerAlreadyActiveHelper.__typeCode = null;
        ServerAlreadyActiveHelper.__active = false;
    }
}
