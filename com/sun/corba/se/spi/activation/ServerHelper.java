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

public abstract class ServerHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Server server) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, server);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Server extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServerHelper.__typeCode == null) {
            ServerHelper.__typeCode = ORB.init().create_interface_tc(id(), "Server");
        }
        return ServerHelper.__typeCode;
    }
    
    public static String id() {
        return ServerHelper._id;
    }
    
    public static Server read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_ServerStub.class));
    }
    
    public static void write(final OutputStream outputStream, final Server server) {
        outputStream.write_Object(server);
    }
    
    public static Server narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Server) {
            return (Server)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _ServerStub serverStub = new _ServerStub();
        serverStub._set_delegate(get_delegate);
        return serverStub;
    }
    
    public static Server unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Server) {
            return (Server)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _ServerStub serverStub = new _ServerStub();
        serverStub._set_delegate(get_delegate);
        return serverStub;
    }
    
    static {
        ServerHelper._id = "IDL:activation/Server:1.0";
        ServerHelper.__typeCode = null;
    }
}
