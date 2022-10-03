package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class WrongTransactionHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    private static boolean __active;
    
    public static void insert(final Any any, final WrongTransaction wrongTransaction) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, wrongTransaction);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static WrongTransaction extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (WrongTransactionHelper.__typeCode == null) {
            synchronized (TypeCode.class) {
                if (WrongTransactionHelper.__typeCode == null) {
                    if (WrongTransactionHelper.__active) {
                        return ORB.init().create_recursive_tc(WrongTransactionHelper._id);
                    }
                    WrongTransactionHelper.__active = true;
                    WrongTransactionHelper.__typeCode = ORB.init().create_exception_tc(id(), "WrongTransaction", new StructMember[0]);
                    WrongTransactionHelper.__active = false;
                }
            }
        }
        return WrongTransactionHelper.__typeCode;
    }
    
    public static String id() {
        return WrongTransactionHelper._id;
    }
    
    public static WrongTransaction read(final InputStream inputStream) {
        final WrongTransaction wrongTransaction = new WrongTransaction();
        inputStream.read_string();
        return wrongTransaction;
    }
    
    public static void write(final OutputStream outputStream, final WrongTransaction wrongTransaction) {
        outputStream.write_string(id());
    }
    
    static {
        WrongTransactionHelper._id = "IDL:omg.org/CORBA/WrongTransaction:1.0";
        WrongTransactionHelper.__typeCode = null;
        WrongTransactionHelper.__active = false;
    }
}
