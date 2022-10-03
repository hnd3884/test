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

public abstract class DynEnumHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DynEnum dynEnum) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, dynEnum);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DynEnum extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DynEnumHelper.__typeCode == null) {
            DynEnumHelper.__typeCode = ORB.init().create_interface_tc(id(), "DynEnum");
        }
        return DynEnumHelper.__typeCode;
    }
    
    public static String id() {
        return DynEnumHelper._id;
    }
    
    public static DynEnum read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final DynEnum dynEnum) {
        throw new MARSHAL();
    }
    
    public static DynEnum narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynEnum) {
            return (DynEnum)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynEnumStub dynEnumStub = new _DynEnumStub();
        dynEnumStub._set_delegate(get_delegate);
        return dynEnumStub;
    }
    
    public static DynEnum unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynEnum) {
            return (DynEnum)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynEnumStub dynEnumStub = new _DynEnumStub();
        dynEnumStub._set_delegate(get_delegate);
        return dynEnumStub;
    }
    
    static {
        DynEnumHelper._id = "IDL:omg.org/DynamicAny/DynEnum:1.0";
        DynEnumHelper.__typeCode = null;
    }
}
