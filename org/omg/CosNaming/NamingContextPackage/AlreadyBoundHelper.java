package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class AlreadyBoundHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final AlreadyBound alreadyBound) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, alreadyBound);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static AlreadyBound extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AlreadyBoundHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (AlreadyBoundHelper.__typeCode == null) {
                    if (AlreadyBoundHelper.__active) {
                        return ORB.init().create_recursive_tc(AlreadyBoundHelper._id);
                    }
                    AlreadyBoundHelper.__active = true;
                    AlreadyBoundHelper.__typeCode = ORB.init().create_exception_tc(id(), "AlreadyBound", new StructMember[0]);
                    AlreadyBoundHelper.__active = false;
                }
            }
        }
        return AlreadyBoundHelper.__typeCode;
    }
    
    public static String id() {
        return AlreadyBoundHelper._id;
    }
    
    public static AlreadyBound read(final InputStream inputStream) {
        final AlreadyBound alreadyBound = new AlreadyBound();
        inputStream.read_string();
        return alreadyBound;
    }
    
    public static void write(final OutputStream outputStream, final AlreadyBound alreadyBound) {
        outputStream.write_string(id());
    }
    
    static {
        AlreadyBoundHelper._id = "IDL:omg.org/CosNaming/NamingContext/AlreadyBound:1.0";
        AlreadyBoundHelper.__typeCode = null;
        AlreadyBoundHelper.__active = false;
    }
}
