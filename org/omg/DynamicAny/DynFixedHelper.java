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

public abstract class DynFixedHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DynFixed dynFixed) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, dynFixed);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DynFixed extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DynFixedHelper.__typeCode == null) {
            DynFixedHelper.__typeCode = ORB.init().create_interface_tc(id(), "DynFixed");
        }
        return DynFixedHelper.__typeCode;
    }
    
    public static String id() {
        return DynFixedHelper._id;
    }
    
    public static DynFixed read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final DynFixed dynFixed) {
        throw new MARSHAL();
    }
    
    public static DynFixed narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynFixed) {
            return (DynFixed)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynFixedStub dynFixedStub = new _DynFixedStub();
        dynFixedStub._set_delegate(get_delegate);
        return dynFixedStub;
    }
    
    public static DynFixed unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynFixed) {
            return (DynFixed)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynFixedStub dynFixedStub = new _DynFixedStub();
        dynFixedStub._set_delegate(get_delegate);
        return dynFixedStub;
    }
    
    static {
        DynFixedHelper._id = "IDL:omg.org/DynamicAny/DynFixed:1.0";
        DynFixedHelper.__typeCode = null;
    }
}
