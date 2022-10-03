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

public abstract class DynUnionHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DynUnion dynUnion) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, dynUnion);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DynUnion extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DynUnionHelper.__typeCode == null) {
            DynUnionHelper.__typeCode = ORB.init().create_interface_tc(id(), "DynUnion");
        }
        return DynUnionHelper.__typeCode;
    }
    
    public static String id() {
        return DynUnionHelper._id;
    }
    
    public static DynUnion read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final DynUnion dynUnion) {
        throw new MARSHAL();
    }
    
    public static DynUnion narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynUnion) {
            return (DynUnion)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynUnionStub dynUnionStub = new _DynUnionStub();
        dynUnionStub._set_delegate(get_delegate);
        return dynUnionStub;
    }
    
    public static DynUnion unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynUnion) {
            return (DynUnion)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynUnionStub dynUnionStub = new _DynUnionStub();
        dynUnionStub._set_delegate(get_delegate);
        return dynUnionStub;
    }
    
    static {
        DynUnionHelper._id = "IDL:omg.org/DynamicAny/DynUnion:1.0";
        DynUnionHelper.__typeCode = null;
    }
}
