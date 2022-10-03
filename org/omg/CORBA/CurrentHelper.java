package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;

public abstract class CurrentHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final Current current) {
        throw new MARSHAL();
    }
    
    public static Current extract(final Any any) {
        throw new MARSHAL();
    }
    
    public static synchronized TypeCode type() {
        if (CurrentHelper.__typeCode == null) {
            CurrentHelper.__typeCode = ORB.init().create_interface_tc(id(), "Current");
        }
        return CurrentHelper.__typeCode;
    }
    
    public static String id() {
        return CurrentHelper._id;
    }
    
    public static Current read(final InputStream inputStream) {
        throw new MARSHAL();
    }
    
    public static void write(final OutputStream outputStream, final Current current) {
        throw new MARSHAL();
    }
    
    public static Current narrow(final org.omg.CORBA.Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Current) {
            return (Current)object;
        }
        throw new BAD_PARAM();
    }
    
    static {
        CurrentHelper._id = "IDL:omg.org/CORBA/Current:1.0";
        CurrentHelper.__typeCode = null;
    }
}
