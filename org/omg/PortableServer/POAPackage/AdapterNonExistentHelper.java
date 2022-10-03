package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class AdapterNonExistentHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final AdapterNonExistent adapterNonExistent) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, adapterNonExistent);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static AdapterNonExistent extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AdapterNonExistentHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (AdapterNonExistentHelper.__typeCode == null) {
                    if (AdapterNonExistentHelper.__active) {
                        return ORB.init().create_recursive_tc(AdapterNonExistentHelper._id);
                    }
                    AdapterNonExistentHelper.__active = true;
                    AdapterNonExistentHelper.__typeCode = ORB.init().create_exception_tc(id(), "AdapterNonExistent", new StructMember[0]);
                    AdapterNonExistentHelper.__active = false;
                }
            }
        }
        return AdapterNonExistentHelper.__typeCode;
    }
    
    public static String id() {
        return AdapterNonExistentHelper._id;
    }
    
    public static AdapterNonExistent read(final InputStream inputStream) {
        final AdapterNonExistent adapterNonExistent = new AdapterNonExistent();
        inputStream.read_string();
        return adapterNonExistent;
    }
    
    public static void write(final OutputStream outputStream, final AdapterNonExistent adapterNonExistent) {
        outputStream.write_string(id());
    }
    
    static {
        AdapterNonExistentHelper._id = "IDL:omg.org/PortableServer/POA/AdapterNonExistent:1.0";
        AdapterNonExistentHelper.__typeCode = null;
        AdapterNonExistentHelper.__active = false;
    }
}
