package org.omg.PortableServer;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class POAHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final POA poa) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, poa);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static POA extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (POAHelper.__typeCode == null) {
            POAHelper.__typeCode = ORB.init().create_interface_tc(id(), "POA");
        }
        return POAHelper.__typeCode;
    }
    
    public static String id() {
        return POAHelper._id;
    }
    
    public static POA read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final POA poa) {
        throw new MARSHAL();
    }
    
    public static POA narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof POA) {
            return (POA)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        return null;
    }
    
    static {
        POAHelper._id = "IDL:omg.org/PortableServer/POA:2.3";
        POAHelper.__typeCode = null;
    }
}
