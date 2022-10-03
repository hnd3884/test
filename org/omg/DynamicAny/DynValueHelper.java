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

public abstract class DynValueHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DynValue dynValue) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, dynValue);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DynValue extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DynValueHelper.__typeCode == null) {
            DynValueHelper.__typeCode = ORB.init().create_interface_tc(id(), "DynValue");
        }
        return DynValueHelper.__typeCode;
    }
    
    public static String id() {
        return DynValueHelper._id;
    }
    
    public static DynValue read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final DynValue dynValue) {
        throw new MARSHAL();
    }
    
    public static DynValue narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynValue) {
            return (DynValue)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynValueStub dynValueStub = new _DynValueStub();
        dynValueStub._set_delegate(get_delegate);
        return dynValueStub;
    }
    
    public static DynValue unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynValue) {
            return (DynValue)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynValueStub dynValueStub = new _DynValueStub();
        dynValueStub._set_delegate(get_delegate);
        return dynValueStub;
    }
    
    static {
        DynValueHelper._id = "IDL:omg.org/DynamicAny/DynValue:1.0";
        DynValueHelper.__typeCode = null;
    }
}
