package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class RepositoryHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Repository repository) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, repository);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Repository extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (RepositoryHelper.__typeCode == null) {
            RepositoryHelper.__typeCode = ORB.init().create_string_tc(0);
            RepositoryHelper.__typeCode = ORB.init().create_alias_tc(id(), "Repository", RepositoryHelper.__typeCode);
        }
        return RepositoryHelper.__typeCode;
    }
    
    public static String id() {
        return RepositoryHelper._id;
    }
    
    public static Repository read(final InputStream inputStream) {
        inputStream.read_string();
        return null;
    }
    
    public static void write(final OutputStream outputStream, final Repository repository) {
        outputStream.write_string(null);
    }
    
    static {
        RepositoryHelper._id = "IDL:com.sun.omg.org/CORBA/Repository:3.0";
        RepositoryHelper.__typeCode = null;
    }
}
