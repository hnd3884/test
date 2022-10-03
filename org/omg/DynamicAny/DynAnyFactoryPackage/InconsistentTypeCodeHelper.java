package org.omg.DynamicAny.DynAnyFactoryPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class InconsistentTypeCodeHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final InconsistentTypeCode inconsistentTypeCode) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, inconsistentTypeCode);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static InconsistentTypeCode extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (InconsistentTypeCodeHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (InconsistentTypeCodeHelper.__typeCode == null) {
                    if (InconsistentTypeCodeHelper.__active) {
                        return ORB.init().create_recursive_tc(InconsistentTypeCodeHelper._id);
                    }
                    InconsistentTypeCodeHelper.__active = true;
                    InconsistentTypeCodeHelper.__typeCode = ORB.init().create_exception_tc(id(), "InconsistentTypeCode", new StructMember[0]);
                    InconsistentTypeCodeHelper.__active = false;
                }
            }
        }
        return InconsistentTypeCodeHelper.__typeCode;
    }
    
    public static String id() {
        return InconsistentTypeCodeHelper._id;
    }
    
    public static InconsistentTypeCode read(final InputStream inputStream) {
        final InconsistentTypeCode inconsistentTypeCode = new InconsistentTypeCode();
        inputStream.read_string();
        return inconsistentTypeCode;
    }
    
    public static void write(final OutputStream outputStream, final InconsistentTypeCode inconsistentTypeCode) {
        outputStream.write_string(id());
    }
    
    static {
        InconsistentTypeCodeHelper._id = "IDL:omg.org/DynamicAny/DynAnyFactory/InconsistentTypeCode:1.0";
        InconsistentTypeCodeHelper.__typeCode = null;
        InconsistentTypeCodeHelper.__active = false;
    }
}
