package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class DefinitionKindHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final DefinitionKind definitionKind) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, definitionKind);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static DefinitionKind extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DefinitionKindHelper.__typeCode == null) {
            DefinitionKindHelper.__typeCode = ORB.init().create_enum_tc(id(), "DefinitionKind", new String[] { "dk_none", "dk_all", "dk_Attribute", "dk_Constant", "dk_Exception", "dk_Interface", "dk_Module", "dk_Operation", "dk_Typedef", "dk_Alias", "dk_Struct", "dk_Union", "dk_Enum", "dk_Primitive", "dk_String", "dk_Sequence", "dk_Array", "dk_Repository", "dk_Wstring", "dk_Fixed", "dk_Value", "dk_ValueBox", "dk_ValueMember", "dk_Native" });
        }
        return DefinitionKindHelper.__typeCode;
    }
    
    public static String id() {
        return DefinitionKindHelper._id;
    }
    
    public static DefinitionKind read(final InputStream inputStream) {
        return DefinitionKind.from_int(inputStream.read_long());
    }
    
    public static void write(final OutputStream outputStream, final DefinitionKind definitionKind) {
        outputStream.write_long(definitionKind.value());
    }
    
    static {
        DefinitionKindHelper._id = "IDL:omg.org/CORBA/DefinitionKind:1.0";
        DefinitionKindHelper.__typeCode = null;
    }
}
