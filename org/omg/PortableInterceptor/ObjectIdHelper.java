package org.omg.PortableInterceptor;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.OctetSeqHelper;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public abstract class ObjectIdHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final byte[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static byte[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ObjectIdHelper.__typeCode == null) {
            ObjectIdHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
            ObjectIdHelper.__typeCode = ORB.init().create_sequence_tc(0, ObjectIdHelper.__typeCode);
            ObjectIdHelper.__typeCode = ORB.init().create_alias_tc(OctetSeqHelper.id(), "OctetSeq", ObjectIdHelper.__typeCode);
            ObjectIdHelper.__typeCode = ORB.init().create_alias_tc(id(), "ObjectId", ObjectIdHelper.__typeCode);
        }
        return ObjectIdHelper.__typeCode;
    }
    
    public static String id() {
        return ObjectIdHelper._id;
    }
    
    public static byte[] read(final InputStream inputStream) {
        return OctetSeqHelper.read(inputStream);
    }
    
    public static void write(final OutputStream outputStream, final byte[] array) {
        OctetSeqHelper.write(outputStream, array);
    }
    
    static {
        ObjectIdHelper._id = "IDL:omg.org/PortableInterceptor/ObjectId:1.0";
        ObjectIdHelper.__typeCode = null;
    }
}
