package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class AdapterAlreadyExistsHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final AdapterAlreadyExists adapterAlreadyExists) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, adapterAlreadyExists);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static AdapterAlreadyExists extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AdapterAlreadyExistsHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (AdapterAlreadyExistsHelper.__typeCode == null) {
                    if (AdapterAlreadyExistsHelper.__active) {
                        return ORB.init().create_recursive_tc(AdapterAlreadyExistsHelper._id);
                    }
                    AdapterAlreadyExistsHelper.__active = true;
                    AdapterAlreadyExistsHelper.__typeCode = ORB.init().create_exception_tc(id(), "AdapterAlreadyExists", new StructMember[0]);
                    AdapterAlreadyExistsHelper.__active = false;
                }
            }
        }
        return AdapterAlreadyExistsHelper.__typeCode;
    }
    
    public static String id() {
        return AdapterAlreadyExistsHelper._id;
    }
    
    public static AdapterAlreadyExists read(final InputStream inputStream) {
        final AdapterAlreadyExists adapterAlreadyExists = new AdapterAlreadyExists();
        inputStream.read_string();
        return adapterAlreadyExists;
    }
    
    public static void write(final OutputStream outputStream, final AdapterAlreadyExists adapterAlreadyExists) {
        outputStream.write_string(id());
    }
    
    static {
        AdapterAlreadyExistsHelper._id = "IDL:omg.org/PortableServer/POA/AdapterAlreadyExists:1.0";
        AdapterAlreadyExistsHelper.__typeCode = null;
        AdapterAlreadyExistsHelper.__active = false;
    }
}
