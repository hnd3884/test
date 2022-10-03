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

public abstract class DynArrayHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DynArray dynArray) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, dynArray);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DynArray extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DynArrayHelper.__typeCode == null) {
            DynArrayHelper.__typeCode = ORB.init().create_interface_tc(id(), "DynArray");
        }
        return DynArrayHelper.__typeCode;
    }
    
    public static String id() {
        return DynArrayHelper._id;
    }
    
    public static DynArray read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final DynArray dynArray) {
        throw new MARSHAL();
    }
    
    public static DynArray narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynArray) {
            return (DynArray)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynArrayStub dynArrayStub = new _DynArrayStub();
        dynArrayStub._set_delegate(get_delegate);
        return dynArrayStub;
    }
    
    public static DynArray unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynArray) {
            return (DynArray)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynArrayStub dynArrayStub = new _DynArrayStub();
        dynArrayStub._set_delegate(get_delegate);
        return dynArrayStub;
    }
    
    static {
        DynArrayHelper._id = "IDL:omg.org/DynamicAny/DynArray:1.0";
        DynArrayHelper.__typeCode = null;
    }
}
