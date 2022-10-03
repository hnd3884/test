package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;
import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBoundHelper;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import java.util.Hashtable;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;

public abstract class _InitialNameServiceImplBase extends ObjectImpl implements InitialNameService, InvokeHandler
{
    private static Hashtable _methods;
    private static String[] __ids;
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        final Integer n = _InitialNameServiceImplBase._methods.get(s);
        if (n == null) {
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
        }
        switch (n) {
            case 0: {
                OutputStream outputStream;
                try {
                    this.bind(inputStream.read_string(), ObjectHelper.read(inputStream), inputStream.read_boolean());
                    outputStream = responseHandler.createReply();
                }
                catch (final NameAlreadyBound nameAlreadyBound) {
                    outputStream = responseHandler.createExceptionReply();
                    NameAlreadyBoundHelper.write(outputStream, nameAlreadyBound);
                }
                return outputStream;
            }
            default: {
                throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
            }
        }
    }
    
    @Override
    public String[] _ids() {
        return _InitialNameServiceImplBase.__ids.clone();
    }
    
    static {
        (_InitialNameServiceImplBase._methods = new Hashtable()).put("bind", new Integer(0));
        _InitialNameServiceImplBase.__ids = new String[] { "IDL:activation/InitialNameService:1.0" };
    }
}
