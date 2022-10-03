package org.omg.PortableServer.POAManagerPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class AdapterInactiveHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final AdapterInactive adapterInactive) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, adapterInactive);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static AdapterInactive extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AdapterInactiveHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (AdapterInactiveHelper.__typeCode == null) {
                    if (AdapterInactiveHelper.__active) {
                        return ORB.init().create_recursive_tc(AdapterInactiveHelper._id);
                    }
                    AdapterInactiveHelper.__active = true;
                    AdapterInactiveHelper.__typeCode = ORB.init().create_exception_tc(id(), "AdapterInactive", new StructMember[0]);
                    AdapterInactiveHelper.__active = false;
                }
            }
        }
        return AdapterInactiveHelper.__typeCode;
    }
    
    public static String id() {
        return AdapterInactiveHelper._id;
    }
    
    public static AdapterInactive read(final InputStream inputStream) {
        final AdapterInactive adapterInactive = new AdapterInactive();
        inputStream.read_string();
        return adapterInactive;
    }
    
    public static void write(final OutputStream outputStream, final AdapterInactive adapterInactive) {
        outputStream.write_string(id());
    }
    
    static {
        AdapterInactiveHelper._id = "IDL:omg.org/PortableServer/POAManager/AdapterInactive:1.0";
        AdapterInactiveHelper.__typeCode = null;
        AdapterInactiveHelper.__active = false;
    }
}
