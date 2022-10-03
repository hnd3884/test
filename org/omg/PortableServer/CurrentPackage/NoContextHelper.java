package org.omg.PortableServer.CurrentPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NoContextHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final NoContext noContext) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, noContext);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NoContext extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NoContextHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (NoContextHelper.__typeCode == null) {
                    if (NoContextHelper.__active) {
                        return ORB.init().create_recursive_tc(NoContextHelper._id);
                    }
                    NoContextHelper.__active = true;
                    NoContextHelper.__typeCode = ORB.init().create_exception_tc(id(), "NoContext", new StructMember[0]);
                    NoContextHelper.__active = false;
                }
            }
        }
        return NoContextHelper.__typeCode;
    }
    
    public static String id() {
        return NoContextHelper._id;
    }
    
    public static NoContext read(final InputStream inputStream) {
        final NoContext noContext = new NoContext();
        inputStream.read_string();
        return noContext;
    }
    
    public static void write(final OutputStream outputStream, final NoContext noContext) {
        outputStream.write_string(id());
    }
    
    static {
        NoContextHelper._id = "IDL:omg.org/PortableServer/Current/NoContext:1.0";
        NoContextHelper.__typeCode = null;
        NoContextHelper.__active = false;
    }
}
