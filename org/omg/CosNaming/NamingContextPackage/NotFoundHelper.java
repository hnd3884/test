package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.portable.InputStream;
import org.omg.CosNaming.NameComponentHelper;
import org.omg.CosNaming.NameHelper;
import org.omg.CORBA.IDLType;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class NotFoundHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final NotFound notFound) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, notFound);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static NotFound extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (NotFoundHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (NotFoundHelper.__typeCode == null) {
                    if (NotFoundHelper.__active) {
                        return ORB.init().create_recursive_tc(NotFoundHelper._id);
                    }
                    NotFoundHelper.__active = true;
                    NotFoundHelper.__typeCode = ORB.init().create_exception_tc(id(), "NotFound", new StructMember[] { new StructMember("why", NotFoundReasonHelper.type(), null), new StructMember("rest_of_name", ORB.init().create_alias_tc(NameHelper.id(), "Name", ORB.init().create_sequence_tc(0, NameComponentHelper.type())), null) });
                    NotFoundHelper.__active = false;
                }
            }
        }
        return NotFoundHelper.__typeCode;
    }
    
    public static String id() {
        return NotFoundHelper._id;
    }
    
    public static NotFound read(final InputStream inputStream) {
        final NotFound notFound = new NotFound();
        inputStream.read_string();
        notFound.why = NotFoundReasonHelper.read(inputStream);
        notFound.rest_of_name = NameHelper.read(inputStream);
        return notFound;
    }
    
    public static void write(final OutputStream outputStream, final NotFound notFound) {
        outputStream.write_string(id());
        NotFoundReasonHelper.write(outputStream, notFound.why);
        NameHelper.write(outputStream, notFound.rest_of_name);
    }
    
    static {
        NotFoundHelper._id = "IDL:omg.org/CosNaming/NamingContext/NotFound:1.0";
        NotFoundHelper.__typeCode = null;
        NotFoundHelper.__active = false;
    }
}
