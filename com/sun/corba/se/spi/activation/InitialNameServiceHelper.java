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

public abstract class InitialNameServiceHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final InitialNameService initialNameService) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, initialNameService);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static InitialNameService extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InitialNameServiceHelper.__typeCode == null) {
            InitialNameServiceHelper.__typeCode = ORB.init().create_interface_tc(id(), "InitialNameService");
        }
        return InitialNameServiceHelper.__typeCode;
    }
    
    public static String id() {
        return InitialNameServiceHelper._id;
    }
    
    public static InitialNameService read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_InitialNameServiceStub.class));
    }
    
    public static void write(final OutputStream outputStream, final InitialNameService initialNameService) {
        outputStream.write_Object(initialNameService);
    }
    
    public static InitialNameService narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof InitialNameService) {
            return (InitialNameService)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _InitialNameServiceStub initialNameServiceStub = new _InitialNameServiceStub();
        initialNameServiceStub._set_delegate(get_delegate);
        return initialNameServiceStub;
    }
    
    public static InitialNameService unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof InitialNameService) {
            return (InitialNameService)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _InitialNameServiceStub initialNameServiceStub = new _InitialNameServiceStub();
        initialNameServiceStub._set_delegate(get_delegate);
        return initialNameServiceStub;
    }
    
    static {
        InitialNameServiceHelper._id = "IDL:activation/InitialNameService:1.0";
        InitialNameServiceHelper.__typeCode = null;
    }
}
