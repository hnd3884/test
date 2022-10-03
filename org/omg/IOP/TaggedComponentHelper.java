package org.omg.IOP;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class TaggedComponentHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final TaggedComponent taggedComponent) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, taggedComponent);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static TaggedComponent extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (TaggedComponentHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (TaggedComponentHelper.__typeCode == null) {
                    if (TaggedComponentHelper.__active) {
                        return ORB.init().create_recursive_tc(TaggedComponentHelper._id);
                    }
                    TaggedComponentHelper.__active = true;
                    TaggedComponentHelper.__typeCode = ORB.init().create_struct_tc(id(), "TaggedComponent", new StructMember[] { new StructMember("tag", ORB.init().create_alias_tc(ComponentIdHelper.id(), "ComponentId", ORB.init().get_primitive_tc(TCKind.tk_ulong)), null), new StructMember("component_data", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_octet)), null) });
                    TaggedComponentHelper.__active = false;
                }
            }
        }
        return TaggedComponentHelper.__typeCode;
    }
    
    public static String id() {
        return TaggedComponentHelper._id;
    }
    
    public static TaggedComponent read(final InputStream inputStream) {
        final TaggedComponent taggedComponent = new TaggedComponent();
        taggedComponent.tag = inputStream.read_ulong();
        final int read_long = inputStream.read_long();
        inputStream.read_octet_array(taggedComponent.component_data = new byte[read_long], 0, read_long);
        return taggedComponent;
    }
    
    public static void write(final OutputStream outputStream, final TaggedComponent taggedComponent) {
        outputStream.write_ulong(taggedComponent.tag);
        outputStream.write_long(taggedComponent.component_data.length);
        outputStream.write_octet_array(taggedComponent.component_data, 0, taggedComponent.component_data.length);
    }
    
    static {
        TaggedComponentHelper._id = "IDL:omg.org/IOP/TaggedComponent:1.0";
        TaggedComponentHelper.__typeCode = null;
        TaggedComponentHelper.__active = false;
    }
}
