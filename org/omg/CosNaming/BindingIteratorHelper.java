package org.omg.CosNaming;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class BindingIteratorHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final BindingIterator bindingIterator) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, bindingIterator);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static BindingIterator extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (BindingIteratorHelper.__typeCode == null) {
            BindingIteratorHelper.__typeCode = ORB.init().create_interface_tc(id(), "BindingIterator");
        }
        return BindingIteratorHelper.__typeCode;
    }
    
    public static String id() {
        return BindingIteratorHelper._id;
    }
    
    public static BindingIterator read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_BindingIteratorStub.class));
    }
    
    public static void write(final OutputStream outputStream, final BindingIterator bindingIterator) {
        outputStream.write_Object(bindingIterator);
    }
    
    public static BindingIterator narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof BindingIterator) {
            return (BindingIterator)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _BindingIteratorStub bindingIteratorStub = new _BindingIteratorStub();
        bindingIteratorStub._set_delegate(get_delegate);
        return bindingIteratorStub;
    }
    
    public static BindingIterator unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof BindingIterator) {
            return (BindingIterator)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _BindingIteratorStub bindingIteratorStub = new _BindingIteratorStub();
        bindingIteratorStub._set_delegate(get_delegate);
        return bindingIteratorStub;
    }
    
    static {
        BindingIteratorHelper._id = "IDL:omg.org/CosNaming/BindingIterator:1.0";
        BindingIteratorHelper.__typeCode = null;
    }
}
