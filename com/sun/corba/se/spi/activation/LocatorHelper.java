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

public abstract class LocatorHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Locator locator) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, locator);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Locator extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (LocatorHelper.__typeCode == null) {
            LocatorHelper.__typeCode = ORB.init().create_interface_tc(id(), "Locator");
        }
        return LocatorHelper.__typeCode;
    }
    
    public static String id() {
        return LocatorHelper._id;
    }
    
    public static Locator read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_LocatorStub.class));
    }
    
    public static void write(final OutputStream outputStream, final Locator locator) {
        outputStream.write_Object(locator);
    }
    
    public static Locator narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Locator) {
            return (Locator)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _LocatorStub locatorStub = new _LocatorStub();
        locatorStub._set_delegate(get_delegate);
        return locatorStub;
    }
    
    public static Locator unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Locator) {
            return (Locator)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _LocatorStub locatorStub = new _LocatorStub();
        locatorStub._set_delegate(get_delegate);
        return locatorStub;
    }
    
    static {
        LocatorHelper._id = "IDL:activation/Locator:1.0";
        LocatorHelper.__typeCode = null;
    }
}
