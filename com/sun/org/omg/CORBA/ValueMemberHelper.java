package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class ValueMemberHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ValueMember valueMember) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, valueMember);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ValueMember extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ValueMemberHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ValueMemberHelper.__typeCode == null) {
                    if (ValueMemberHelper.__active) {
                        return ORB.init().create_recursive_tc(ValueMemberHelper._id);
                    }
                    ValueMemberHelper.__active = true;
                    ValueMemberHelper.__typeCode = ORB.init().create_struct_tc(id(), "ValueMember", new StructMember[] { new StructMember("name", ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", ORB.init().create_string_tc(0)), null), new StructMember("id", ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", ORB.init().create_string_tc(0)), null), new StructMember("defined_in", ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", ORB.init().create_string_tc(0)), null), new StructMember("version", ORB.init().create_alias_tc(VersionSpecHelper.id(), "VersionSpec", ORB.init().create_string_tc(0)), null), new StructMember("type", ORB.init().get_primitive_tc(TCKind.tk_TypeCode), null), new StructMember("type_def", IDLTypeHelper.type(), null), new StructMember("access", ORB.init().create_alias_tc(VisibilityHelper.id(), "Visibility", ORB.init().get_primitive_tc(TCKind.tk_short)), null) });
                    ValueMemberHelper.__active = false;
                }
            }
        }
        return ValueMemberHelper.__typeCode;
    }
    
    public static String id() {
        return ValueMemberHelper._id;
    }
    
    public static ValueMember read(final InputStream inputStream) {
        final ValueMember valueMember = new ValueMember();
        valueMember.name = inputStream.read_string();
        valueMember.id = inputStream.read_string();
        valueMember.defined_in = inputStream.read_string();
        valueMember.version = inputStream.read_string();
        valueMember.type = inputStream.read_TypeCode();
        valueMember.type_def = IDLTypeHelper.read(inputStream);
        valueMember.access = inputStream.read_short();
        return valueMember;
    }
    
    public static void write(final OutputStream outputStream, final ValueMember valueMember) {
        outputStream.write_string(valueMember.name);
        outputStream.write_string(valueMember.id);
        outputStream.write_string(valueMember.defined_in);
        outputStream.write_string(valueMember.version);
        outputStream.write_TypeCode(valueMember.type);
        IDLTypeHelper.write(outputStream, valueMember.type_def);
        outputStream.write_short(valueMember.access);
    }
    
    static {
        ValueMemberHelper._id = "IDL:omg.org/CORBA/ValueMember:1.0";
        ValueMemberHelper.__typeCode = null;
        ValueMemberHelper.__active = false;
    }
}
