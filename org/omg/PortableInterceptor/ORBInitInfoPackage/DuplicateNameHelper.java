package org.omg.PortableInterceptor.ORBInitInfoPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class DuplicateNameHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final DuplicateName duplicateName) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, duplicateName);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DuplicateName extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DuplicateNameHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (DuplicateNameHelper.__typeCode == null) {
                    if (DuplicateNameHelper.__active) {
                        return ORB.init().create_recursive_tc(DuplicateNameHelper._id);
                    }
                    DuplicateNameHelper.__active = true;
                    DuplicateNameHelper.__typeCode = ORB.init().create_exception_tc(id(), "DuplicateName", new StructMember[] { new StructMember("name", ORB.init().create_string_tc(0), null) });
                    DuplicateNameHelper.__active = false;
                }
            }
        }
        return DuplicateNameHelper.__typeCode;
    }
    
    public static String id() {
        return DuplicateNameHelper._id;
    }
    
    public static DuplicateName read(final InputStream inputStream) {
        final DuplicateName duplicateName = new DuplicateName();
        inputStream.read_string();
        duplicateName.name = inputStream.read_string();
        return duplicateName;
    }
    
    public static void write(final OutputStream outputStream, final DuplicateName duplicateName) {
        outputStream.write_string(id());
        outputStream.write_string(duplicateName.name);
    }
    
    static {
        DuplicateNameHelper._id = "IDL:omg.org/PortableInterceptor/ORBInitInfo/DuplicateName:1.0";
        DuplicateNameHelper.__typeCode = null;
        DuplicateNameHelper.__active = false;
    }
}
