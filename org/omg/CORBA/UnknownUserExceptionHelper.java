package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class UnknownUserExceptionHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final UnknownUserException ex) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, ex);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static UnknownUserException extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (UnknownUserExceptionHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (UnknownUserExceptionHelper.__typeCode == null) {
                    if (UnknownUserExceptionHelper.__active) {
                        return ORB.init().create_recursive_tc(UnknownUserExceptionHelper._id);
                    }
                    UnknownUserExceptionHelper.__active = true;
                    UnknownUserExceptionHelper.__typeCode = ORB.init().create_exception_tc(id(), "UnknownUserException", new StructMember[] { new StructMember("except", ORB.init().get_primitive_tc(TCKind.tk_any), null) });
                    UnknownUserExceptionHelper.__active = false;
                }
            }
        }
        return UnknownUserExceptionHelper.__typeCode;
    }
    
    public static String id() {
        return UnknownUserExceptionHelper._id;
    }
    
    public static UnknownUserException read(final InputStream inputStream) {
        final UnknownUserException ex = new UnknownUserException();
        inputStream.read_string();
        ex.except = inputStream.read_any();
        return ex;
    }
    
    public static void write(final OutputStream outputStream, final UnknownUserException ex) {
        outputStream.write_string(id());
        outputStream.write_any(ex.except);
    }
    
    static {
        UnknownUserExceptionHelper._id = "IDL:omg.org/CORBA/UnknownUserException:1.0";
        UnknownUserExceptionHelper.__typeCode = null;
        UnknownUserExceptionHelper.__active = false;
    }
}
