package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class InvalidPolicyHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final InvalidPolicy invalidPolicy) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, invalidPolicy);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static InvalidPolicy extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InvalidPolicyHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (InvalidPolicyHelper.__typeCode == null) {
                    if (InvalidPolicyHelper.__active) {
                        return ORB.init().create_recursive_tc(InvalidPolicyHelper._id);
                    }
                    InvalidPolicyHelper.__active = true;
                    InvalidPolicyHelper.__typeCode = ORB.init().create_exception_tc(id(), "InvalidPolicy", new StructMember[] { new StructMember("index", ORB.init().get_primitive_tc(TCKind.tk_ushort), null) });
                    InvalidPolicyHelper.__active = false;
                }
            }
        }
        return InvalidPolicyHelper.__typeCode;
    }
    
    public static String id() {
        return InvalidPolicyHelper._id;
    }
    
    public static InvalidPolicy read(final InputStream inputStream) {
        final InvalidPolicy invalidPolicy = new InvalidPolicy();
        inputStream.read_string();
        invalidPolicy.index = inputStream.read_ushort();
        return invalidPolicy;
    }
    
    public static void write(final OutputStream outputStream, final InvalidPolicy invalidPolicy) {
        outputStream.write_string(id());
        outputStream.write_ushort(invalidPolicy.index);
    }
    
    static {
        InvalidPolicyHelper._id = "IDL:omg.org/PortableServer/POA/InvalidPolicy:1.0";
        InvalidPolicyHelper.__typeCode = null;
        InvalidPolicyHelper.__active = false;
    }
}
