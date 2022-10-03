package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NoServantHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final NoServant noServant) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, noServant);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NoServant extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NoServantHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (NoServantHelper.__typeCode == null) {
                    if (NoServantHelper.__active) {
                        return ORB.init().create_recursive_tc(NoServantHelper._id);
                    }
                    NoServantHelper.__active = true;
                    NoServantHelper.__typeCode = ORB.init().create_exception_tc(id(), "NoServant", new StructMember[0]);
                    NoServantHelper.__active = false;
                }
            }
        }
        return NoServantHelper.__typeCode;
    }
    
    public static String id() {
        return NoServantHelper._id;
    }
    
    public static NoServant read(final InputStream inputStream) {
        final NoServant noServant = new NoServant();
        inputStream.read_string();
        return noServant;
    }
    
    public static void write(final OutputStream outputStream, final NoServant noServant) {
        outputStream.write_string(id());
    }
    
    static {
        NoServantHelper._id = "IDL:omg.org/PortableServer/POA/NoServant:1.0";
        NoServantHelper.__typeCode = null;
        NoServantHelper.__active = false;
    }
}
