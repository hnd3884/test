package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyErrorHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final PolicyError policyError) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, policyError);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static PolicyError extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (PolicyErrorHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (PolicyErrorHelper.__typeCode == null) {
                    if (PolicyErrorHelper.__active) {
                        return ORB.init().create_recursive_tc(PolicyErrorHelper._id);
                    }
                    PolicyErrorHelper.__active = true;
                    PolicyErrorHelper.__typeCode = ORB.init().create_exception_tc(id(), "PolicyError", new StructMember[] { new StructMember("reason", ORB.init().create_alias_tc(PolicyErrorCodeHelper.id(), "PolicyErrorCode", ORB.init().get_primitive_tc(TCKind.tk_short)), null) });
                    PolicyErrorHelper.__active = false;
                }
            }
        }
        return PolicyErrorHelper.__typeCode;
    }
    
    public static String id() {
        return PolicyErrorHelper._id;
    }
    
    public static PolicyError read(final InputStream inputStream) {
        final PolicyError policyError = new PolicyError();
        inputStream.read_string();
        policyError.reason = inputStream.read_short();
        return policyError;
    }
    
    public static void write(final OutputStream outputStream, final PolicyError policyError) {
        outputStream.write_string(id());
        outputStream.write_short(policyError.reason);
    }
    
    static {
        PolicyErrorHelper._id = "IDL:omg.org/CORBA/PolicyError:1.0";
        PolicyErrorHelper.__typeCode = null;
        PolicyErrorHelper.__active = false;
    }
}
