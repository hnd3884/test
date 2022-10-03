package org.omg.IOP;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class TaggedProfileHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final TaggedProfile taggedProfile) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, taggedProfile);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static TaggedProfile extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (TaggedProfileHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (TaggedProfileHelper.__typeCode == null) {
                    if (TaggedProfileHelper.__active) {
                        return ORB.init().create_recursive_tc(TaggedProfileHelper._id);
                    }
                    TaggedProfileHelper.__active = true;
                    TaggedProfileHelper.__typeCode = ORB.init().create_struct_tc(id(), "TaggedProfile", new StructMember[] { new StructMember("tag", ORB.init().create_alias_tc(ProfileIdHelper.id(), "ProfileId", ORB.init().get_primitive_tc(TCKind.tk_ulong)), null), new StructMember("profile_data", ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_octet)), null) });
                    TaggedProfileHelper.__active = false;
                }
            }
        }
        return TaggedProfileHelper.__typeCode;
    }
    
    public static String id() {
        return TaggedProfileHelper._id;
    }
    
    public static TaggedProfile read(final InputStream inputStream) {
        final TaggedProfile taggedProfile = new TaggedProfile();
        taggedProfile.tag = inputStream.read_ulong();
        final int read_long = inputStream.read_long();
        inputStream.read_octet_array(taggedProfile.profile_data = new byte[read_long], 0, read_long);
        return taggedProfile;
    }
    
    public static void write(final OutputStream outputStream, final TaggedProfile taggedProfile) {
        outputStream.write_ulong(taggedProfile.tag);
        outputStream.write_long(taggedProfile.profile_data.length);
        outputStream.write_octet_array(taggedProfile.profile_data, 0, taggedProfile.profile_data.length);
    }
    
    static {
        TaggedProfileHelper._id = "IDL:omg.org/IOP/TaggedProfile:1.0";
        TaggedProfileHelper.__typeCode = null;
        TaggedProfileHelper.__active = false;
    }
}
