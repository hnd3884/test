package com.sun.corba.se.spi.activation;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import java.util.Hashtable;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;

public abstract class _ServerImplBase extends ObjectImpl implements Server, InvokeHandler
{
    private static Hashtable _methods;
    private static String[] __ids;
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        final Integer n = _ServerImplBase._methods.get(s);
        if (n == null) {
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
        }
        OutputStream outputStream = null;
        switch (n) {
            case 0: {
                this.shutdown();
                outputStream = responseHandler.createReply();
                break;
            }
            case 1: {
                this.install();
                outputStream = responseHandler.createReply();
                break;
            }
            case 2: {
                this.uninstall();
                outputStream = responseHandler.createReply();
                break;
            }
            default: {
                throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
            }
        }
        return outputStream;
    }
    
    @Override
    public String[] _ids() {
        return _ServerImplBase.__ids.clone();
    }
    
    static {
        (_ServerImplBase._methods = new Hashtable()).put("shutdown", new Integer(0));
        _ServerImplBase._methods.put("install", new Integer(1));
        _ServerImplBase._methods.put("uninstall", new Integer(2));
        _ServerImplBase.__ids = new String[] { "IDL:activation/Server:1.0" };
    }
}
