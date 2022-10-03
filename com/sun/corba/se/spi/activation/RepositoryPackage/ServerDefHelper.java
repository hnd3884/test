package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerDefHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServerDef serverDef) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverDef);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerDef extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerDefHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServerDefHelper.__typeCode == null) {
                    if (ServerDefHelper.__active) {
                        return ORB.init().create_recursive_tc(ServerDefHelper._id);
                    }
                    ServerDefHelper.__active = true;
                    ServerDefHelper.__typeCode = ORB.init().create_struct_tc(id(), "ServerDef", new StructMember[] { new StructMember("applicationName", ORB.init().create_string_tc(0), null), new StructMember("serverName", ORB.init().create_string_tc(0), null), new StructMember("serverClassPath", ORB.init().create_string_tc(0), null), new StructMember("serverArgs", ORB.init().create_string_tc(0), null), new StructMember("serverVmArgs", ORB.init().create_string_tc(0), null) });
                    ServerDefHelper.__active = false;
                }
            }
        }
        return ServerDefHelper.__typeCode;
    }
    
    public static String id() {
        return ServerDefHelper._id;
    }
    
    public static ServerDef read(final InputStream inputStream) {
        final ServerDef serverDef = new ServerDef();
        serverDef.applicationName = inputStream.read_string();
        serverDef.serverName = inputStream.read_string();
        serverDef.serverClassPath = inputStream.read_string();
        serverDef.serverArgs = inputStream.read_string();
        serverDef.serverVmArgs = inputStream.read_string();
        return serverDef;
    }
    
    public static void write(final OutputStream outputStream, final ServerDef serverDef) {
        outputStream.write_string(serverDef.applicationName);
        outputStream.write_string(serverDef.serverName);
        outputStream.write_string(serverDef.serverClassPath);
        outputStream.write_string(serverDef.serverArgs);
        outputStream.write_string(serverDef.serverVmArgs);
    }
    
    static {
        ServerDefHelper._id = "IDL:activation/Repository/ServerDef:1.0";
        ServerDefHelper.__typeCode = null;
        ServerDefHelper.__active = false;
    }
}
