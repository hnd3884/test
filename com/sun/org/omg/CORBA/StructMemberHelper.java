package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class StructMemberHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final StructMember structMember) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, structMember);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static StructMember extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (StructMemberHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (StructMemberHelper.__typeCode == null) {
                    if (StructMemberHelper.__active) {
                        return ORB.init().create_recursive_tc(StructMemberHelper._id);
                    }
                    StructMemberHelper.__active = true;
                    StructMemberHelper.__typeCode = ORB.init().create_struct_tc(id(), "StructMember", new StructMember[] { new StructMember("name", ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", ORB.init().create_string_tc(0)), null), new StructMember("type", ORB.init().get_primitive_tc(TCKind.tk_TypeCode), null), new StructMember("type_def", IDLTypeHelper.type(), null) });
                    StructMemberHelper.__active = false;
                }
            }
        }
        return StructMemberHelper.__typeCode;
    }
    
    public static String id() {
        return StructMemberHelper._id;
    }
    
    public static StructMember read(final InputStream inputStream) {
        final StructMember structMember = new StructMember();
        structMember.name = inputStream.read_string();
        structMember.type = inputStream.read_TypeCode();
        structMember.type_def = IDLTypeHelper.read(inputStream);
        return structMember;
    }
    
    public static void write(final OutputStream outputStream, final StructMember structMember) {
        outputStream.write_string(structMember.name);
        outputStream.write_TypeCode(structMember.type);
        IDLTypeHelper.write(outputStream, structMember.type_def);
    }
    
    static {
        StructMemberHelper._id = "IDL:omg.org/CORBA/StructMember:1.0";
        StructMemberHelper.__typeCode = null;
        StructMemberHelper.__active = false;
    }
}
