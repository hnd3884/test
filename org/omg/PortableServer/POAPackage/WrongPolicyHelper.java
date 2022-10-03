package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class WrongPolicyHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final WrongPolicy wrongPolicy) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, wrongPolicy);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static WrongPolicy extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (WrongPolicyHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (WrongPolicyHelper.__typeCode == null) {
                    if (WrongPolicyHelper.__active) {
                        return ORB.init().create_recursive_tc(WrongPolicyHelper._id);
                    }
                    WrongPolicyHelper.__active = true;
                    WrongPolicyHelper.__typeCode = ORB.init().create_exception_tc(id(), "WrongPolicy", new StructMember[0]);
                    WrongPolicyHelper.__active = false;
                }
            }
        }
        return WrongPolicyHelper.__typeCode;
    }
    
    public static String id() {
        return WrongPolicyHelper._id;
    }
    
    public static WrongPolicy read(final InputStream inputStream) {
        final WrongPolicy wrongPolicy = new WrongPolicy();
        inputStream.read_string();
        return wrongPolicy;
    }
    
    public static void write(final OutputStream outputStream, final WrongPolicy wrongPolicy) {
        outputStream.write_string(id());
    }
    
    static {
        WrongPolicyHelper._id = "IDL:omg.org/PortableServer/POA/WrongPolicy:1.0";
        WrongPolicyHelper.__typeCode = null;
        WrongPolicyHelper.__active = false;
    }
}
