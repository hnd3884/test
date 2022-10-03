package org.omg.DynamicAny;

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

public abstract class DynAnyFactoryHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DynAnyFactory dynAnyFactory) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, dynAnyFactory);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DynAnyFactory extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DynAnyFactoryHelper.__typeCode == null) {
            DynAnyFactoryHelper.__typeCode = ORB.init().create_interface_tc(id(), "DynAnyFactory");
        }
        return DynAnyFactoryHelper.__typeCode;
    }
    
    public static String id() {
        return DynAnyFactoryHelper._id;
    }
    
    public static DynAnyFactory read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final DynAnyFactory dynAnyFactory) {
        throw new MARSHAL();
    }
    
    public static DynAnyFactory narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynAnyFactory) {
            return (DynAnyFactory)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynAnyFactoryStub dynAnyFactoryStub = new _DynAnyFactoryStub();
        dynAnyFactoryStub._set_delegate(get_delegate);
        return dynAnyFactoryStub;
    }
    
    public static DynAnyFactory unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynAnyFactory) {
            return (DynAnyFactory)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynAnyFactoryStub dynAnyFactoryStub = new _DynAnyFactoryStub();
        dynAnyFactoryStub._set_delegate(get_delegate);
        return dynAnyFactoryStub;
    }
    
    static {
        DynAnyFactoryHelper._id = "IDL:omg.org/DynamicAny/DynAnyFactory:1.0";
        DynAnyFactoryHelper.__typeCode = null;
    }
}
