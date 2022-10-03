package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class UnionMemberHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final UnionMember unionMember) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, unionMember);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static UnionMember extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (UnionMemberHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (UnionMemberHelper.__typeCode == null) {
                    if (UnionMemberHelper.__active) {
                        return ORB.init().create_recursive_tc(UnionMemberHelper._id);
                    }
                    UnionMemberHelper.__active = true;
                    UnionMemberHelper.__typeCode = ORB.init().create_struct_tc(id(), "UnionMember", new StructMember[] { new StructMember("name", ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", ORB.init().create_string_tc(0)), null), new StructMember("label", ORB.init().get_primitive_tc(TCKind.tk_any), null), new StructMember("type", ORB.init().get_primitive_tc(TCKind.tk_TypeCode), null), new StructMember("type_def", IDLTypeHelper.type(), null) });
                    UnionMemberHelper.__active = false;
                }
            }
        }
        return UnionMemberHelper.__typeCode;
    }
    
    public static String id() {
        return UnionMemberHelper._id;
    }
    
    public static UnionMember read(final InputStream inputStream) {
        final UnionMember unionMember = new UnionMember();
        unionMember.name = inputStream.read_string();
        unionMember.label = inputStream.read_any();
        unionMember.type = inputStream.read_TypeCode();
        unionMember.type_def = IDLTypeHelper.read(inputStream);
        return unionMember;
    }
    
    public static void write(final OutputStream outputStream, final UnionMember unionMember) {
        outputStream.write_string(unionMember.name);
        outputStream.write_any(unionMember.label);
        outputStream.write_TypeCode(unionMember.type);
        IDLTypeHelper.write(outputStream, unionMember.type_def);
    }
    
    static {
        UnionMemberHelper._id = "IDL:omg.org/CORBA/UnionMember:1.0";
        UnionMemberHelper.__typeCode = null;
        UnionMemberHelper.__active = false;
    }
}
