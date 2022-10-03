package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerAlreadyUninstalledHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServerAlreadyUninstalled serverAlreadyUninstalled) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverAlreadyUninstalled);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerAlreadyUninstalled extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerAlreadyUninstalledHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServerAlreadyUninstalledHelper.__typeCode == null) {
                    if (ServerAlreadyUninstalledHelper.__active) {
                        return ORB.init().create_recursive_tc(ServerAlreadyUninstalledHelper._id);
                    }
                    ServerAlreadyUninstalledHelper.__active = true;
                    ServerAlreadyUninstalledHelper.__typeCode = ORB.init().create_exception_tc(id(), "ServerAlreadyUninstalled", new StructMember[] { new StructMember("serverId", ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", ORB.init().get_primitive_tc(TCKind.tk_long)), null) });
                    ServerAlreadyUninstalledHelper.__active = false;
                }
            }
        }
        return ServerAlreadyUninstalledHelper.__typeCode;
    }
    
    public static String id() {
        return ServerAlreadyUninstalledHelper._id;
    }
    
    public static ServerAlreadyUninstalled read(final InputStream inputStream) {
        final ServerAlreadyUninstalled serverAlreadyUninstalled = new ServerAlreadyUninstalled();
        inputStream.read_string();
        serverAlreadyUninstalled.serverId = inputStream.read_long();
        return serverAlreadyUninstalled;
    }
    
    public static void write(final OutputStream outputStream, final ServerAlreadyUninstalled serverAlreadyUninstalled) {
        outputStream.write_string(id());
        outputStream.write_long(serverAlreadyUninstalled.serverId);
    }
    
    static {
        ServerAlreadyUninstalledHelper._id = "IDL:activation/ServerAlreadyUninstalled:1.0";
        ServerAlreadyUninstalledHelper.__typeCode = null;
        ServerAlreadyUninstalledHelper.__active = false;
    }
}
