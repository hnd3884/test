package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerAlreadyInstalledHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServerAlreadyInstalled serverAlreadyInstalled) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverAlreadyInstalled);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerAlreadyInstalled extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerAlreadyInstalledHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServerAlreadyInstalledHelper.__typeCode == null) {
                    if (ServerAlreadyInstalledHelper.__active) {
                        return ORB.init().create_recursive_tc(ServerAlreadyInstalledHelper._id);
                    }
                    ServerAlreadyInstalledHelper.__active = true;
                    ServerAlreadyInstalledHelper.__typeCode = ORB.init().create_exception_tc(id(), "ServerAlreadyInstalled", new StructMember[] { new StructMember("serverId", ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", ORB.init().get_primitive_tc(TCKind.tk_long)), null) });
                    ServerAlreadyInstalledHelper.__active = false;
                }
            }
        }
        return ServerAlreadyInstalledHelper.__typeCode;
    }
    
    public static String id() {
        return ServerAlreadyInstalledHelper._id;
    }
    
    public static ServerAlreadyInstalled read(final InputStream inputStream) {
        final ServerAlreadyInstalled serverAlreadyInstalled = new ServerAlreadyInstalled();
        inputStream.read_string();
        serverAlreadyInstalled.serverId = inputStream.read_long();
        return serverAlreadyInstalled;
    }
    
    public static void write(final OutputStream outputStream, final ServerAlreadyInstalled serverAlreadyInstalled) {
        outputStream.write_string(id());
        outputStream.write_long(serverAlreadyInstalled.serverId);
    }
    
    static {
        ServerAlreadyInstalledHelper._id = "IDL:activation/ServerAlreadyInstalled:1.0";
        ServerAlreadyInstalledHelper.__typeCode = null;
        ServerAlreadyInstalledHelper.__active = false;
    }
}
