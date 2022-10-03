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

public abstract class DynSequenceHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DynSequence dynSequence) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, dynSequence);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DynSequence extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DynSequenceHelper.__typeCode == null) {
            DynSequenceHelper.__typeCode = ORB.init().create_interface_tc(id(), "DynSequence");
        }
        return DynSequenceHelper.__typeCode;
    }
    
    public static String id() {
        return DynSequenceHelper._id;
    }
    
    public static DynSequence read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final DynSequence dynSequence) {
        throw new MARSHAL();
    }
    
    public static DynSequence narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynSequence) {
            return (DynSequence)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynSequenceStub dynSequenceStub = new _DynSequenceStub();
        dynSequenceStub._set_delegate(get_delegate);
        return dynSequenceStub;
    }
    
    public static DynSequence unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DynSequence) {
            return (DynSequence)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _DynSequenceStub dynSequenceStub = new _DynSequenceStub();
        dynSequenceStub._set_delegate(get_delegate);
        return dynSequenceStub;
    }
    
    static {
        DynSequenceHelper._id = "IDL:omg.org/DynamicAny/DynSequence:1.0";
        DynSequenceHelper.__typeCode = null;
    }
}
