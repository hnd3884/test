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

public abstract class ActivatorHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Activator activator) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, activator);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Activator extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ActivatorHelper.__typeCode == null) {
            ActivatorHelper.__typeCode = ORB.init().create_interface_tc(id(), "Activator");
        }
        return ActivatorHelper.__typeCode;
    }
    
    public static String id() {
        return ActivatorHelper._id;
    }
    
    public static Activator read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_ActivatorStub.class));
    }
    
    public static void write(final OutputStream outputStream, final Activator activator) {
        outputStream.write_Object(activator);
    }
    
    public static Activator narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Activator) {
            return (Activator)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _ActivatorStub activatorStub = new _ActivatorStub();
        activatorStub._set_delegate(get_delegate);
        return activatorStub;
    }
    
    public static Activator unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Activator) {
            return (Activator)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _ActivatorStub activatorStub = new _ActivatorStub();
        activatorStub._set_delegate(get_delegate);
        return activatorStub;
    }
    
    static {
        ActivatorHelper._id = "IDL:activation/Activator:1.0";
        ActivatorHelper.__typeCode = null;
    }
}
