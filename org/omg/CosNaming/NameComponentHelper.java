package org.omg.CosNaming;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NameComponentHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final NameComponent nameComponent) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, nameComponent);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NameComponent extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NameComponentHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (NameComponentHelper.__typeCode == null) {
                    if (NameComponentHelper.__active) {
                        return ORB.init().create_recursive_tc(NameComponentHelper._id);
                    }
                    NameComponentHelper.__active = true;
                    NameComponentHelper.__typeCode = ORB.init().create_struct_tc(id(), "NameComponent", new StructMember[] { new StructMember("id", ORB.init().create_alias_tc(IstringHelper.id(), "Istring", ORB.init().create_string_tc(0)), null), new StructMember("kind", ORB.init().create_alias_tc(IstringHelper.id(), "Istring", ORB.init().create_string_tc(0)), null) });
                    NameComponentHelper.__active = false;
                }
            }
        }
        return NameComponentHelper.__typeCode;
    }
    
    public static String id() {
        return NameComponentHelper._id;
    }
    
    public static NameComponent read(final InputStream inputStream) {
        final NameComponent nameComponent = new NameComponent();
        nameComponent.id = inputStream.read_string();
        nameComponent.kind = inputStream.read_string();
        return nameComponent;
    }
    
    public static void write(final OutputStream outputStream, final NameComponent nameComponent) {
        outputStream.write_string(nameComponent.id);
        outputStream.write_string(nameComponent.kind);
    }
    
    static {
        NameComponentHelper._id = "IDL:omg.org/CosNaming/NameComponent:1.0";
        NameComponentHelper.__typeCode = null;
        NameComponentHelper.__active = false;
    }
}
