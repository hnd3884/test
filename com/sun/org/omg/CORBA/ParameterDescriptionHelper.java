package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class ParameterDescriptionHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final ParameterDescription parameterDescription) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, parameterDescription);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ParameterDescription extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ParameterDescriptionHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (ParameterDescriptionHelper.__typeCode == null) {
                    if (ParameterDescriptionHelper.__active) {
                        return ORB.init().create_recursive_tc(ParameterDescriptionHelper._id);
                    }
                    ParameterDescriptionHelper.__active = true;
                    ParameterDescriptionHelper.__typeCode = ORB.init().create_struct_tc(id(), "ParameterDescription", new StructMember[] { new StructMember("name", ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", ORB.init().create_string_tc(0)), null), new StructMember("type", ORB.init().get_primitive_tc(TCKind.tk_TypeCode), null), new StructMember("type_def", IDLTypeHelper.type(), null), new StructMember("mode", ParameterModeHelper.type(), null) });
                    ParameterDescriptionHelper.__active = false;
                }
            }
        }
        return ParameterDescriptionHelper.__typeCode;
    }
    
    public static String id() {
        return ParameterDescriptionHelper._id;
    }
    
    public static ParameterDescription read(final InputStream inputStream) {
        final ParameterDescription parameterDescription = new ParameterDescription();
        parameterDescription.name = inputStream.read_string();
        parameterDescription.type = inputStream.read_TypeCode();
        parameterDescription.type_def = IDLTypeHelper.read(inputStream);
        parameterDescription.mode = ParameterModeHelper.read(inputStream);
        return parameterDescription;
    }
    
    public static void write(final OutputStream outputStream, final ParameterDescription parameterDescription) {
        outputStream.write_string(parameterDescription.name);
        outputStream.write_TypeCode(parameterDescription.type);
        IDLTypeHelper.write(outputStream, parameterDescription.type_def);
        ParameterModeHelper.write(outputStream, parameterDescription.mode);
    }
    
    static {
        ParameterDescriptionHelper._id = "IDL:omg.org/CORBA/ParameterDescription:1.0";
        ParameterDescriptionHelper.__typeCode = null;
        ParameterDescriptionHelper.__active = false;
    }
}
