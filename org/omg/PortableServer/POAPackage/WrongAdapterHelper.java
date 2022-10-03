package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class WrongAdapterHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final WrongAdapter wrongAdapter) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, wrongAdapter);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static WrongAdapter extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (WrongAdapterHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (WrongAdapterHelper.__typeCode == null) {
                    if (WrongAdapterHelper.__active) {
                        return ORB.init().create_recursive_tc(WrongAdapterHelper._id);
                    }
                    WrongAdapterHelper.__active = true;
                    WrongAdapterHelper.__typeCode = ORB.init().create_exception_tc(id(), "WrongAdapter", new StructMember[0]);
                    WrongAdapterHelper.__active = false;
                }
            }
        }
        return WrongAdapterHelper.__typeCode;
    }
    
    public static String id() {
        return WrongAdapterHelper._id;
    }
    
    public static WrongAdapter read(final InputStream inputStream) {
        final WrongAdapter wrongAdapter = new WrongAdapter();
        inputStream.read_string();
        return wrongAdapter;
    }
    
    public static void write(final OutputStream outputStream, final WrongAdapter wrongAdapter) {
        outputStream.write_string(id());
    }
    
    static {
        WrongAdapterHelper._id = "IDL:omg.org/PortableServer/POA/WrongAdapter:1.0";
        WrongAdapterHelper.__typeCode = null;
        WrongAdapterHelper.__active = false;
    }
}
