package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CosNaming.NameComponentHelper;
import org.omg.CosNaming.NameHelper;
import org.omg.CORBA.IDLType;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class CannotProceedHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final CannotProceed cannotProceed) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, cannotProceed);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static CannotProceed extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (CannotProceedHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (CannotProceedHelper.__typeCode == null) {
                    if (CannotProceedHelper.__active) {
                        return ORB.init().create_recursive_tc(CannotProceedHelper._id);
                    }
                    CannotProceedHelper.__active = true;
                    CannotProceedHelper.__typeCode = ORB.init().create_exception_tc(id(), "CannotProceed", new StructMember[] { new StructMember("cxt", NamingContextHelper.type(), null), new StructMember("rest_of_name", ORB.init().create_alias_tc(NameHelper.id(), "Name", ORB.init().create_sequence_tc(0, NameComponentHelper.type())), null) });
                    CannotProceedHelper.__active = false;
                }
            }
        }
        return CannotProceedHelper.__typeCode;
    }
    
    public static String id() {
        return CannotProceedHelper._id;
    }
    
    public static CannotProceed read(final InputStream inputStream) {
        final CannotProceed cannotProceed = new CannotProceed();
        inputStream.read_string();
        cannotProceed.cxt = NamingContextHelper.read(inputStream);
        cannotProceed.rest_of_name = NameHelper.read(inputStream);
        return cannotProceed;
    }
    
    public static void write(final OutputStream outputStream, final CannotProceed cannotProceed) {
        outputStream.write_string(id());
        NamingContextHelper.write(outputStream, cannotProceed.cxt);
        NameHelper.write(outputStream, cannotProceed.rest_of_name);
    }
    
    static {
        CannotProceedHelper._id = "IDL:omg.org/CosNaming/NamingContext/CannotProceed:1.0";
        CannotProceedHelper.__typeCode = null;
        CannotProceedHelper.__active = false;
    }
}
