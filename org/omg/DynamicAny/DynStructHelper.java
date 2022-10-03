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

public abstract class DynStructHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DynStruct dynStruct) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, dynStruct);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DynStruct extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DynStructHelper.__typeCode == null) {
            DynStructHelper.__typeCode = ORB.init().create_interface_tc(id(), "DynStruct");
        }
        return DynStructHelper.__typeCode;
    }
    
    public static String id() {
        return DynStructHelper._id;
    }
    
    public static DynStruct read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final DynStruct dynStruct) {
        throw new MARSHAL();
    }
    
    public static DynStruct narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynStruct) {
            return (DynStruct)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynStructStub dynStructStub = new _DynStructStub();
        dynStructStub._set_delegate(get_delegate);
        return dynStructStub;
    }
    
    public static DynStruct unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynStruct) {
            return (DynStruct)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynStructStub dynStructStub = new _DynStructStub();
        dynStructStub._set_delegate(get_delegate);
        return dynStructStub;
    }
    
    static {
        DynStructHelper._id = "IDL:omg.org/DynamicAny/DynStruct:1.0";
        DynStructHelper.__typeCode = null;
    }
}
