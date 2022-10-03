package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.portable.InputStream;
import org.omg.IOP.TaggedProfileHelper;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class TargetAddressHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final TargetAddress targetAddress) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, targetAddress);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static TargetAddress extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (TargetAddressHelper.__typeCode == null) {
            final TypeCode create_alias_tc = ORB.init().create_alias_tc(AddressingDispositionHelper.id(), "AddressingDisposition", ORB.init().get_primitive_tc(TCKind.tk_short));
            final UnionMember[] array = new UnionMember[3];
            final Any create_any = ORB.init().create_any();
            create_any.insert_short((short)0);
            array[0] = new UnionMember("object_key", create_any, ORB.init().create_sequence_tc(0, ORB.init().get_primitive_tc(TCKind.tk_octet)), null);
            final Any create_any2 = ORB.init().create_any();
            create_any2.insert_short((short)1);
            array[1] = new UnionMember("profile", create_any2, TaggedProfileHelper.type(), null);
            final Any create_any3 = ORB.init().create_any();
            create_any3.insert_short((short)2);
            array[2] = new UnionMember("ior", create_any3, IORAddressingInfoHelper.type(), null);
            TargetAddressHelper.__typeCode = ORB.init().create_union_tc(id(), "TargetAddress", create_alias_tc, array);
        }
        return TargetAddressHelper.__typeCode;
    }
    
    public static String id() {
        return TargetAddressHelper._id;
    }
    
    public static TargetAddress read(final InputStream inputStream) {
        final TargetAddress targetAddress = new TargetAddress();
        switch (inputStream.read_short()) {
            case 0: {
                final int read_long = inputStream.read_long();
                final byte[] array = new byte[read_long];
                inputStream.read_octet_array(array, 0, read_long);
                targetAddress.object_key(array);
                break;
            }
            case 1: {
                targetAddress.profile(TaggedProfileHelper.read(inputStream));
                break;
            }
            case 2: {
                targetAddress.ior(IORAddressingInfoHelper.read(inputStream));
                break;
            }
            default: {
                throw new BAD_OPERATION();
            }
        }
        return targetAddress;
    }
    
    public static void write(final OutputStream outputStream, final TargetAddress targetAddress) {
        outputStream.write_short(targetAddress.discriminator());
        switch (targetAddress.discriminator()) {
            case 0: {
                outputStream.write_long(targetAddress.object_key().length);
                outputStream.write_octet_array(targetAddress.object_key(), 0, targetAddress.object_key().length);
                break;
            }
            case 1: {
                TaggedProfileHelper.write(outputStream, targetAddress.profile());
                break;
            }
            case 2: {
                IORAddressingInfoHelper.write(outputStream, targetAddress.ior());
                break;
            }
            default: {
                throw new BAD_OPERATION();
            }
        }
    }
    
    static {
        TargetAddressHelper._id = "IDL:messages/TargetAddress:1.0";
        TargetAddressHelper.__typeCode = null;
    }
}
