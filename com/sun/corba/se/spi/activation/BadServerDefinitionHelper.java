package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class BadServerDefinitionHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final BadServerDefinition badServerDefinition) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, badServerDefinition);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static BadServerDefinition extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (BadServerDefinitionHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (BadServerDefinitionHelper.__typeCode == null) {
                    if (BadServerDefinitionHelper.__active) {
                        return ORB.init().create_recursive_tc(BadServerDefinitionHelper._id);
                    }
                    BadServerDefinitionHelper.__active = true;
                    BadServerDefinitionHelper.__typeCode = ORB.init().create_exception_tc(id(), "BadServerDefinition", new StructMember[] { new StructMember("reason", ORB.init().create_string_tc(0), null) });
                    BadServerDefinitionHelper.__active = false;
                }
            }
        }
        return BadServerDefinitionHelper.__typeCode;
    }
    
    public static String id() {
        return BadServerDefinitionHelper._id;
    }
    
    public static BadServerDefinition read(final InputStream inputStream) {
        final BadServerDefinition badServerDefinition = new BadServerDefinition();
        inputStream.read_string();
        badServerDefinition.reason = inputStream.read_string();
        return badServerDefinition;
    }
    
    public static void write(final OutputStream outputStream, final BadServerDefinition badServerDefinition) {
        outputStream.write_string(id());
        outputStream.write_string(badServerDefinition.reason);
    }
    
    static {
        BadServerDefinitionHelper._id = "IDL:activation/BadServerDefinition:1.0";
        BadServerDefinitionHelper.__typeCode = null;
        BadServerDefinitionHelper.__active = false;
    }
}
