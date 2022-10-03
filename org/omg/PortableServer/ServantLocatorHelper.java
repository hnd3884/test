package org.omg.PortableServer;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServantLocatorHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final ServantLocator servantLocator) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, servantLocator);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServantLocator extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServantLocatorHelper.__typeCode == null) {
            ServantLocatorHelper.__typeCode = ORB.init().create_interface_tc(id(), "ServantLocator");
        }
        return ServantLocatorHelper.__typeCode;
    }
    
    public static String id() {
        return ServantLocatorHelper._id;
    }
    
    public static ServantLocator read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final ServantLocator servantLocator) {
        throw new MARSHAL();
    }
    
    public static ServantLocator narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof ServantLocator) {
            return (ServantLocator)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _ServantLocatorStub servantLocatorStub = new _ServantLocatorStub();
        servantLocatorStub._set_delegate(get_delegate);
        return servantLocatorStub;
    }
    
    public static ServantLocator unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof ServantLocator) {
            return (ServantLocator)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _ServantLocatorStub servantLocatorStub = new _ServantLocatorStub();
        servantLocatorStub._set_delegate(get_delegate);
        return servantLocatorStub;
    }
    
    static {
        ServantLocatorHelper._id = "IDL:omg.org/PortableServer/ServantLocator:1.0";
        ServantLocatorHelper.__typeCode = null;
    }
}
