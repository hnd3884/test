package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class RepositoryHelper
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
            RepositoryHelper.__typeCode = ORB.init().create_interface_tc(id(), "Repository");
        }
        return RepositoryHelper.__typeCode;
    }
    
    public static String id() {
        return RepositoryHelper._id;
    }
    
    public static Repository read(final InputStream inputStream) {
        return narrow(inputStream.read_Object(_RepositoryStub.class));
    }
    
    public static void write(final OutputStream outputStream, final Repository repository) {
        outputStream.write_Object(repository);
    }
    
    public static Repository narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Repository) {
            return (Repository)object;
        }
        if (!object._is_a(id())) {
            throw new BAD_PARAM();
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _RepositoryStub repositoryStub = new _RepositoryStub();
        repositoryStub._set_delegate(get_delegate);
        return repositoryStub;
    }
    
    public static Repository unchecked_narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Repository) {
            return (Repository)object;
        }
        final Delegate get_delegate = ((ObjectImpl)object)._get_delegate();
        final _RepositoryStub repositoryStub = new _RepositoryStub();
        repositoryStub._set_delegate(get_delegate);
        return repositoryStub;
    }
    
    static {
        RepositoryHelper._id = "IDL:activation/Repository:1.0";
        RepositoryHelper.__typeCode = null;
    }
}
