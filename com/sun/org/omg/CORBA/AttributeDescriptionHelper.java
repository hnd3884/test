package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class AttributeDescriptionHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final AttributeDescription attributeDescription) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, attributeDescription);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static AttributeDescription extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (AttributeDescriptionHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (AttributeDescriptionHelper.__typeCode == null) {
                    if (AttributeDescriptionHelper.__active) {
                        return ORB.init().create_recursive_tc(AttributeDescriptionHelper._id);
                    }
                    AttributeDescriptionHelper.__active = true;
                    AttributeDescriptionHelper.__typeCode = ORB.init().create_struct_tc(id(), "AttributeDescription", new StructMember[] { new StructMember("name", ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", ORB.init().create_string_tc(0)), null), new StructMember("id", ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", ORB.init().create_string_tc(0)), null), new StructMember("defined_in", ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", ORB.init().create_string_tc(0)), null), new StructMember("version", ORB.init().create_alias_tc(VersionSpecHelper.id(), "VersionSpec", ORB.init().create_string_tc(0)), null), new StructMember("type", ORB.init().get_primitive_tc(TCKind.tk_TypeCode), null), new StructMember("mode", AttributeModeHelper.type(), null) });
                    AttributeDescriptionHelper.__active = false;
                }
            }
        }
        return AttributeDescriptionHelper.__typeCode;
    }
    
    public static String id() {
        return AttributeDescriptionHelper._id;
    }
    
    public static AttributeDescription read(final InputStream inputStream) {
        final AttributeDescription attributeDescription = new AttributeDescription();
        attributeDescription.name = inputStream.read_string();
        attributeDescription.id = inputStream.read_string();
        attributeDescription.defined_in = inputStream.read_string();
        attributeDescription.version = inputStream.read_string();
        attributeDescription.type = inputStream.read_TypeCode();
        attributeDescription.mode = AttributeModeHelper.read(inputStream);
        return attributeDescription;
    }
    
    public static void write(final OutputStream outputStream, final AttributeDescription attributeDescription) {
        outputStream.write_string(attributeDescription.name);
        outputStream.write_string(attributeDescription.id);
        outputStream.write_string(attributeDescription.defined_in);
        outputStream.write_string(attributeDescription.version);
        outputStream.write_TypeCode(attributeDescription.type);
        AttributeModeHelper.write(outputStream, attributeDescription.mode);
    }
    
    static {
        AttributeDescriptionHelper._id = "IDL:omg.org/CORBA/AttributeDescription:1.0";
        AttributeDescriptionHelper.__typeCode = null;
        AttributeDescriptionHelper.__active = false;
    }
}
