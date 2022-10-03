package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class AddressingDispositionHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final short n) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, n);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static short extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AddressingDispositionHelper.__typeCode == null) {
            AddressingDispositionHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
            AddressingDispositionHelper.__typeCode = ORB.init().create_alias_tc(id(), "AddressingDisposition", AddressingDispositionHelper.__typeCode);
        }
        return AddressingDispositionHelper.__typeCode;
    }
    
    public static String id() {
        return AddressingDispositionHelper._id;
    }
    
    public static short read(final InputStream inputStream) {
        return inputStream.read_short();
    }
    
    public static void write(final OutputStream outputStream, final short n) {
        outputStream.write_short(n);
    }
    
    static {
        AddressingDispositionHelper._id = "IDL:messages/AddressingDisposition:1.0";
        AddressingDispositionHelper.__typeCode = null;
    }
}
