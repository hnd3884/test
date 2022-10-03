package com.sun.org.omg.SendingContext;

import com.sun.org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLSeqHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import com.sun.org.omg.CORBA.RepositoryHelper;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import java.util.Hashtable;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;

public abstract class _CodeBaseImplBase extends ObjectImpl implements CodeBase, InvokeHandler
{
    private static Hashtable _methods;
    private static String[] __ids;
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        final OutputStream reply = responseHandler.createReply();
        final Integer n = _CodeBaseImplBase._methods.get(s);
        if (n == null) {
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
        }
        switch (n) {
            case 0: {
                RepositoryHelper.write(reply, this.get_ir());
                break;
            }
            case 1: {
                reply.write_string(this.implementation(RepositoryIdHelper.read(inputStream)));
                break;
            }
            case 2: {
                URLSeqHelper.write(reply, this.implementations(RepositoryIdSeqHelper.read(inputStream)));
                break;
            }
            case 3: {
                FullValueDescriptionHelper.write(reply, this.meta(RepositoryIdHelper.read(inputStream)));
                break;
            }
            case 4: {
                ValueDescSeqHelper.write(reply, this.metas(RepositoryIdSeqHelper.read(inputStream)));
                break;
            }
            case 5: {
                RepositoryIdSeqHelper.write(reply, this.bases(RepositoryIdHelper.read(inputStream)));
                break;
            }
            default: {
                throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
            }
        }
        return reply;
    }
    
    @Override
    public String[] _ids() {
        return _CodeBaseImplBase.__ids.clone();
    }
    
    static {
        (_CodeBaseImplBase._methods = new Hashtable()).put("get_ir", new Integer(0));
        _CodeBaseImplBase._methods.put("implementation", new Integer(1));
        _CodeBaseImplBase._methods.put("implementations", new Integer(2));
        _CodeBaseImplBase._methods.put("meta", new Integer(3));
        _CodeBaseImplBase._methods.put("metas", new Integer(4));
        _CodeBaseImplBase._methods.put("bases", new Integer(5));
        _CodeBaseImplBase.__ids = new String[] { "IDL:omg.org/SendingContext/CodeBase:1.0", "IDL:omg.org/SendingContext/RunTime:1.0" };
    }
}
