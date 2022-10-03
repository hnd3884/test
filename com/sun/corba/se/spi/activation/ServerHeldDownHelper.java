package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerHeldDownHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServerHeldDown serverHeldDown) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverHeldDown);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerHeldDown extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerHeldDownHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServerHeldDownHelper.__typeCode == null) {
                    if (ServerHeldDownHelper.__active) {
                        return ORB.init().create_recursive_tc(ServerHeldDownHelper._id);
                    }
                    ServerHeldDownHelper.__active = true;
                    ServerHeldDownHelper.__typeCode = ORB.init().create_exception_tc(id(), "ServerHeldDown", new StructMember[] { new StructMember("serverId", ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", ORB.init().get_primitive_tc(TCKind.tk_long)), null) });
                    ServerHeldDownHelper.__active = false;
                }
            }
        }
        return ServerHeldDownHelper.__typeCode;
    }
    
    public static String id() {
        return ServerHeldDownHelper._id;
    }
    
    public static ServerHeldDown read(final InputStream inputStream) {
        final ServerHeldDown serverHeldDown = new ServerHeldDown();
        inputStream.read_string();
        serverHeldDown.serverId = inputStream.read_long();
        return serverHeldDown;
    }
    
    public static void write(final OutputStream outputStream, final ServerHeldDown serverHeldDown) {
        outputStream.write_string(id());
        outputStream.write_long(serverHeldDown.serverId);
    }
    
    static {
        ServerHeldDownHelper._id = "IDL:activation/ServerHeldDown:1.0";
        ServerHeldDownHelper.__typeCode = null;
        ServerHeldDownHelper.__active = false;
    }
}
