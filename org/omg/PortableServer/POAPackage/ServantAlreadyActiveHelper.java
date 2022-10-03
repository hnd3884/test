package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServantAlreadyActiveHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServantAlreadyActive servantAlreadyActive) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, servantAlreadyActive);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServantAlreadyActive extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServantAlreadyActiveHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServantAlreadyActiveHelper.__typeCode == null) {
                    if (ServantAlreadyActiveHelper.__active) {
                        return ORB.init().create_recursive_tc(ServantAlreadyActiveHelper._id);
                    }
                    ServantAlreadyActiveHelper.__active = true;
                    ServantAlreadyActiveHelper.__typeCode = ORB.init().create_exception_tc(id(), "ServantAlreadyActive", new StructMember[0]);
                    ServantAlreadyActiveHelper.__active = false;
                }
            }
        }
        return ServantAlreadyActiveHelper.__typeCode;
    }
    
    public static String id() {
        return ServantAlreadyActiveHelper._id;
    }
    
    public static ServantAlreadyActive read(final InputStream inputStream) {
        final ServantAlreadyActive servantAlreadyActive = new ServantAlreadyActive();
        inputStream.read_string();
        return servantAlreadyActive;
    }
    
    public static void write(final OutputStream outputStream, final ServantAlreadyActive servantAlreadyActive) {
        outputStream.write_string(id());
    }
    
    static {
        ServantAlreadyActiveHelper._id = "IDL:omg.org/PortableServer/POA/ServantAlreadyActive:1.0";
        ServantAlreadyActiveHelper.__typeCode = null;
        ServantAlreadyActiveHelper.__active = false;
    }
}
