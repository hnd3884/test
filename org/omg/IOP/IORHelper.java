package org.omg.IOP;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class IORHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final IOR ior) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, ior);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static IOR extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (IORHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (IORHelper.__typeCode == null) {
                    if (IORHelper.__active) {
                        return ORB.init().create_recursive_tc(IORHelper._id);
                    }
                    IORHelper.__active = true;
                    IORHelper.__typeCode = ORB.init().create_struct_tc(id(), "IOR", new StructMember[] { new StructMember("type_id", ORB.init().create_string_tc(0), null), new StructMember("profiles", ORB.init().create_sequence_tc(0, TaggedProfileHelper.type()), null) });
                    IORHelper.__active = false;
                }
            }
        }
        return IORHelper.__typeCode;
    }
    
    public static String id() {
        return IORHelper._id;
    }
    
    public static IOR read(final InputStream inputStream) {
        final IOR ior = new IOR();
        ior.type_id = inputStream.read_string();
        ior.profiles = new TaggedProfile[inputStream.read_long()];
        for (int i = 0; i < ior.profiles.length; ++i) {
            ior.profiles[i] = TaggedProfileHelper.read(inputStream);
        }
        return ior;
    }
    
    public static void write(final OutputStream outputStream, final IOR ior) {
        outputStream.write_string(ior.type_id);
        outputStream.write_long(ior.profiles.length);
        for (int i = 0; i < ior.profiles.length; ++i) {
            TaggedProfileHelper.write(outputStream, ior.profiles[i]);
        }
    }
    
    static {
        IORHelper._id = "IDL:omg.org/IOP/IOR:1.0";
        IORHelper.__typeCode = null;
        IORHelper.__active = false;
    }
}
