package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class InitializerHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final Initializer initializer) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, initializer);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static Initializer extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InitializerHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (InitializerHelper.__typeCode == null) {
                    if (InitializerHelper.__active) {
                        return ORB.init().create_recursive_tc(InitializerHelper._id);
                    }
                    InitializerHelper.__active = true;
                    InitializerHelper.__typeCode = ORB.init().create_struct_tc(id(), "Initializer", new StructMember[] { new StructMember("members", ORB.init().create_alias_tc(StructMemberSeqHelper.id(), "StructMemberSeq", ORB.init().create_sequence_tc(0, StructMemberHelper.type())), null), new StructMember("name", ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", ORB.init().create_string_tc(0)), null) });
                    InitializerHelper.__active = false;
                }
            }
        }
        return InitializerHelper.__typeCode;
    }
    
    public static String id() {
        return InitializerHelper._id;
    }
    
    public static Initializer read(final InputStream inputStream) {
        final Initializer initializer = new Initializer();
        initializer.members = StructMemberSeqHelper.read(inputStream);
        initializer.name = inputStream.read_string();
        return initializer;
    }
    
    public static void write(final OutputStream outputStream, final Initializer initializer) {
        StructMemberSeqHelper.write(outputStream, initializer.members);
        outputStream.write_string(initializer.name);
    }
    
    static {
        InitializerHelper._id = "IDL:omg.org/CORBA/Initializer:1.0";
        InitializerHelper.__typeCode = null;
        InitializerHelper.__active = false;
    }
}
