package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServerManagerHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final ServerManager serverManager) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, serverManager);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServerManager extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerManagerHelper.__typeCode == null) {
            ServerManagerHelper.__typeCode = ORB.init().create_interface_tc(id(), "ServerManager");
        }
        return ServerManagerHelper.__typeCode;
    }
    
    public static String id() {
        return ServerManagerHelper._id;
    }
    
    public static ServerManager read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_ServerManagerStub.class));
    }
    
    public static void write(final OutputStream outputStream, final ServerManager serverManager) {
        outputStream.write_Object(serverManager);
    }
    
    public static ServerManager narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof ServerManager) {
            return (ServerManager)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _ServerManagerStub serverManagerStub = new _ServerManagerStub();
        serverManagerStub._set_delegate(get_delegate);
        return serverManagerStub;
    }
    
    public static ServerManager unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof ServerManager) {
            return (ServerManager)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _ServerManagerStub serverManagerStub = new _ServerManagerStub();
        serverManagerStub._set_delegate(get_delegate);
        return serverManagerStub;
    }
    
    static {
        ServerManagerHelper._id = "IDL:activation/ServerManager:1.0";
        ServerManagerHelper.__typeCode = null;
    }
}
