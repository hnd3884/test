package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.BoxedValueHelper;

public class WStringValueHelper implements BoxedValueHelper
{
    private static String _id;
    private static WStringValueHelper _instance;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final String s) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, s);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static String extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (WStringValueHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (WStringValueHelper.__typeCode == null) {
                    if (WStringValueHelper.__active) {
                        return ORB.init().create_recursive_tc(WStringValueHelper._id);
                    }
                    WStringValueHelper.__active = true;
                    WStringValueHelper.__typeCode = ORB.init().create_wstring_tc(0);
                    WStringValueHelper.__typeCode = ORB.init().create_value_box_tc(WStringValueHelper._id, "WStringValue", WStringValueHelper.__typeCode);
                    WStringValueHelper.__active = false;
                }
            }
        }
        return WStringValueHelper.__typeCode;
    }
    
    public static String id() {
        return WStringValueHelper._id;
    }
    
    public static String read(final InputStream inputStream) {
        if (!(inputStream instanceof org.omg.CORBA_2_3.portable.InputStream)) {
            throw new BAD_PARAM();
        }
        return (String)((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value(WStringValueHelper._instance);
    }
    
    @Override
    public Serializable read_value(final InputStream inputStream) {
        return inputStream.read_wstring();
    }
    
    public static void write(final OutputStream outputStream, final String s) {
        if (!(outputStream instanceof org.omg.CORBA_2_3.portable.OutputStream)) {
            throw new BAD_PARAM();
        }
        ((org.omg.CORBA_2_3.portable.OutputStream)outputStream).write_value(s, WStringValueHelper._instance);
    }
    
    @Override
    public void write_value(final OutputStream outputStream, final Serializable s) {
        if (!(s instanceof String)) {
            throw new MARSHAL();
        }
        outputStream.write_wstring((String)s);
    }
    
    @Override
    public String get_id() {
        return WStringValueHelper._id;
    }
    
    static {
        WStringValueHelper._id = "IDL:omg.org/CORBA/WStringValue:1.0";
        WStringValueHelper._instance = new WStringValueHelper();
        WStringValueHelper.__typeCode = null;
        WStringValueHelper.__active = false;
    }
}
