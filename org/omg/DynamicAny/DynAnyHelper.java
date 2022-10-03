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

public abstract class DynAnyHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DynAny dynAny) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, dynAny);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DynAny extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DynAnyHelper.__typeCode == null) {
            DynAnyHelper.__typeCode = ORB.init().create_interface_tc(id(), "DynAny");
        }
        return DynAnyHelper.__typeCode;
    }
    
    public static String id() {
        return DynAnyHelper._id;
    }
    
    public static DynAny read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final DynAny dynAny) {
        throw new MARSHAL();
    }
    
    public static DynAny narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynAny) {
            return (DynAny)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynAnyStub dynAnyStub = new _DynAnyStub();
        dynAnyStub._set_delegate(get_delegate);
        return dynAnyStub;
    }
    
    public static DynAny unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynAny) {
            return (DynAny)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynAnyStub dynAnyStub = new _DynAnyStub();
        dynAnyStub._set_delegate(get_delegate);
        return dynAnyStub;
    }
    
    static {
        DynAnyHelper._id = "IDL:omg.org/DynamicAny/DynAny:1.0";
        DynAnyHelper.__typeCode = null;
    }
}
