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

public abstract class ServantActivatorHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final ServantActivator servantActivator) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, servantActivator);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServantActivator extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServantActivatorHelper.__typeCode == null) {
            ServantActivatorHelper.__typeCode = ORB.init().create_interface_tc(id(), "ServantActivator");
        }
        return ServantActivatorHelper.__typeCode;
    }
    
    public static String id() {
        return ServantActivatorHelper._id;
    }
    
    public static ServantActivator read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final ServantActivator servantActivator) {
        throw new MARSHAL();
    }
    
    public static ServantActivator narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof ServantActivator) {
            return (ServantActivator)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _ServantActivatorStub servantActivatorStub = new _ServantActivatorStub();
        servantActivatorStub._set_delegate(get_delegate);
        return servantActivatorStub;
    }
    
    public static ServantActivator unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof ServantActivator) {
            return (ServantActivator)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _ServantActivatorStub servantActivatorStub = new _ServantActivatorStub();
        servantActivatorStub._set_delegate(get_delegate);
        return servantActivatorStub;
    }
    
    static {
        ServantActivatorHelper._id = "IDL:omg.org/PortableServer/ServantActivator:2.3";
        ServantActivatorHelper.__typeCode = null;
    }
}
