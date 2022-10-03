package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.portable.InputStream;
import org.omg.IOP.IORHelper;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class IORAddressingInfoHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final IORAddressingInfo iorAddressingInfo) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, iorAddressingInfo);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static IORAddressingInfo extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (IORAddressingInfoHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (IORAddressingInfoHelper.__typeCode == null) {
                    if (IORAddressingInfoHelper.__active) {
                        return ORB.init().create_recursive_tc(IORAddressingInfoHelper._id);
                    }
                    IORAddressingInfoHelper.__active = true;
                    IORAddressingInfoHelper.__typeCode = ORB.init().create_struct_tc(id(), "IORAddressingInfo", new StructMember[] { new StructMember("selected_profile_index", ORB.init().get_primitive_tc(TCKind.tk_ulong), null), new StructMember("ior", IORHelper.type(), null) });
                    IORAddressingInfoHelper.__active = false;
                }
            }
        }
        return IORAddressingInfoHelper.__typeCode;
    }
    
    public static String id() {
        return IORAddressingInfoHelper._id;
    }
    
    public static IORAddressingInfo read(final InputStream inputStream) {
        final IORAddressingInfo iorAddressingInfo = new IORAddressingInfo();
        iorAddressingInfo.selected_profile_index = inputStream.read_ulong();
        iorAddressingInfo.ior = IORHelper.read(inputStream);
        return iorAddressingInfo;
    }
    
    public static void write(final OutputStream outputStream, final IORAddressingInfo iorAddressingInfo) {
        outputStream.write_ulong(iorAddressingInfo.selected_profile_index);
        IORHelper.write(outputStream, iorAddressingInfo.ior);
    }
    
    static {
        IORAddressingInfoHelper._id = "IDL:messages/IORAddressingInfo:1.0";
        IORAddressingInfoHelper.__typeCode = null;
        IORAddressingInfoHelper.__active = false;
    }
}
