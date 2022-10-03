package org.omg.CosNaming;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import java.util.Hashtable;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.PortableServer.Servant;

public abstract class BindingIteratorPOA extends Servant implements BindingIteratorOperations, InvokeHandler
{
    private static Hashtable _methods;
    private static String[] __ids;
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        final Integer n = BindingIteratorPOA._methods.get(s);
        if (n == null) {
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
        }
        OutputStream outputStream = null;
        switch (n) {
            case 0: {
                final BindingHolder bindingHolder = new BindingHolder();
                final boolean next_one = this.next_one(bindingHolder);
                outputStream = responseHandler.createReply();
                outputStream.write_boolean(next_one);
                BindingHelper.write(outputStream, bindingHolder.value);
                break;
            }
            case 1: {
                final int read_ulong = inputStream.read_ulong();
                final BindingListHolder bindingListHolder = new BindingListHolder();
                final boolean next_n = this.next_n(read_ulong, bindingListHolder);
                outputStream = responseHandler.createReply();
                outputStream.write_boolean(next_n);
                BindingListHelper.write(outputStream, bindingListHolder.value);
                break;
            }
            case 2: {
                this.destroy();
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
    public String[] _all_interfaces(final POA poa, final byte[] array) {
        return BindingIteratorPOA.__ids.clone();
    }
    
    public BindingIterator _this() {
        return BindingIteratorHelper.narrow(super._this_object());
    }
    
    public BindingIterator _this(final ORB orb) {
        return BindingIteratorHelper.narrow(super._this_object(orb));
    }
    
    static {
        (BindingIteratorPOA._methods = new Hashtable()).put("next_one", new Integer(0));
        BindingIteratorPOA._methods.put("next_n", new Integer(1));
        BindingIteratorPOA._methods.put("destroy", new Integer(2));
        BindingIteratorPOA.__ids = new String[] { "IDL:omg.org/CosNaming/BindingIterator:1.0" };
    }
}
