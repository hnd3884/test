package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ServantNotActiveHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ServantNotActive servantNotActive) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, servantNotActive);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ServantNotActive extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ServantNotActiveHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ServantNotActiveHelper.__typeCode == null) {
                    if (ServantNotActiveHelper.__active) {
                        return ORB.init().create_recursive_tc(ServantNotActiveHelper._id);
                    }
                    ServantNotActiveHelper.__active = true;
                    ServantNotActiveHelper.__typeCode = ORB.init().create_exception_tc(id(), "ServantNotActive", new StructMember[0]);
                    ServantNotActiveHelper.__active = false;
                }
            }
        }
        return ServantNotActiveHelper.__typeCode;
    }
    
    public static String id() {
        return ServantNotActiveHelper._id;
    }
    
    public static ServantNotActive read(final InputStream inputStream) {
        final ServantNotActive servantNotActive = new ServantNotActive();
        inputStream.read_string();
        return servantNotActive;
    }
    
    public static void write(final OutputStream outputStream, final ServantNotActive servantNotActive) {
        outputStream.write_string(id());
    }
    
    static {
        ServantNotActiveHelper._id = "IDL:omg.org/PortableServer/POA/ServantNotActive:1.0";
        ServantNotActiveHelper.__typeCode = null;
        ServantNotActiveHelper.__active = false;
    }
}
